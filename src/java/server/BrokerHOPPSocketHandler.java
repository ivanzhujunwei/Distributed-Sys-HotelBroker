/*
 * Handle the incoming request from client side
 */
package server;

import entities.Hotel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.Constants;
import utility.DBConnection;
import utility.Utility;

/**
 *
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class BrokerHOPPSocketHandler implements Runnable
{

    // connection
    Connection conn;
    
    // incoming socket from client side
    Socket incoming;

    BufferedReader readerFromClient;
    PrintStream writerToClient;

    // inputstream and printstream for each hotel server
    InputStream inChevron;
    InputStream inHilton;
    InputStream inRegent;
    PrintStream writerHilton;
    PrintStream writerChevron;
    PrintStream writerRegent;

    // socket for each hotel server
    Socket socketHilton;
    Socket socketChevron;
    Socket socketRegent;

    /**
     * *
     * Store the city list and corresponding hotels  <City name, Hotel list>
     */
    private Map<String, List<Hotel>> hotels;

    // db connection
    DBConnection dBConn;

    BrokerHOPPSocketHandler(Socket incoming)
    {
        this.incoming = incoming;
        dBConn = new DBConnection();
        this.hotels = new HashMap<>();
    }

    /**
     * *
     * Get different hotel socket
     *
     * @param hotelName hotel name
     * @return hotel socket
     * @throws IOException
     */
    private void initHotelSocket(String hotelName) throws IOException
    {
        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.exit(2);
        }
        switch (hotelName) {
            case Constants.HOTEL_CHEVRON:
                socketChevron = new Socket(address, Constants.PORT_CHEVRON);
                inChevron = socketChevron.getInputStream();
                writerChevron = new PrintStream(socketChevron.getOutputStream());
                break;
            case Constants.HOTEL_HILTON:
                socketHilton = new Socket(address, Constants.PORT_HILTON);
                inHilton = socketHilton.getInputStream();
                writerHilton = new PrintStream(socketHilton.getOutputStream());
                break;
            case Constants.HOTEL_REGENT:
                socketRegent = new Socket(address, Constants.PORT_REGENT);
                inRegent = socketRegent.getInputStream();
                writerRegent = new PrintStream(socketRegent.getOutputStream());
            default:
                break;
        }
        System.out.println("✔ Connected to " + hotelName + " hotel server ");
    }

    @Override
    public void run()
    {
        try {
            readerFromClient = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            writerToClient = new PrintStream(incoming.getOutputStream());
            boolean done = true;
            while (done) {
                String clientRequest = readerFromClient.readLine();
                System.out.println("Client quest: " + clientRequest);
                if (Utility.isValidRequest(clientRequest)) {
                    writerToClient.println(getServerResponse(clientRequest));
                } 
                else {
                    writerToClient.println("Error invalid request from client");
                    break;
                }
            }
            incoming.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * *
     * Get server's response including hotel server and broker server
     *
     * @param clientRequest request from client
     * @return server's response
     */
    private String getServerResponse(String clientRequest)
    {
        if (clientRequest.startsWith(Constants.POC_HOTEL)) {
            // query from hotel servers
            return getHotelResponse(clientRequest);
        } else if (clientRequest.startsWith(Constants.POC_BROKER)) {
            // query from broker itself
            return getBrokerResponse(clientRequest);
        } else if (clientRequest.startsWith(Constants.POC_CONNECT_HOTEL_SERER)) {
            // try to establish connection to hotel server
            return connOrDisConnHotelServer(clientRequest);
        }
        return null;
    }

    /**
     * *
     * Connect or disconnect hotel server, establish or close the socket
     *
     * @param clientRequest client request
     * @return connect information, e.g. success or failure
     */
    private String connOrDisConnHotelServer(String clientRequest)
    {
        String[] parameter = clientRequest.split(Constants.SEMI);
        String hotelName = parameter[1];
        int port = getHotelPortByName(hotelName);
        if (Utility.isHostServerOnline(port)) {
            try {
                Socket s = getSocketByHotelName(hotelName);
                if (null != s && !s.isClosed()) {
                    // if socket is opened, then close it
                    getSocketByHotelName(hotelName).close();
                    removeHotelData(hotelName);
                    return "Disconnect successfully!";
                } else {
                    // if socket is not opened, then create it
                    initHotelSocket(hotelName);
                    loadHotelData(hotelName);
                    return "Connected successfully!";
                }
            } catch (IOException ex) {
                return "Error: Internet issue.";
            }
        } else {
            return "Error: " + hotelName + " server is offline, cannot be connected!";
        }
    }

    /**
     * *
     * Remove the hotel data in memory by server name (hotel name)
     *
     * @param serverName
     */
    private void removeHotelData(String serverName)
    {
        String cityName = "";
        for (List<Hotel> hs : this.hotels.values()) {
            Iterator<Hotel> iter = hs.iterator();
            while (iter.hasNext()) {
                Hotel h = iter.next();
                cityName = h.getCityName();
                if (h.getHotelName().equals(serverName)) {
                    iter.remove();
                }
            }
        }
        List<Hotel> hotelList = this.hotels.get(cityName);
        if (null != hotelList && hotelList.isEmpty()) {
            // if the hotel list size is still > 1, means there are other hotels in the city
            this.hotels.remove(cityName);
        }
    }

    /***
     * Load broker database based on which hotel server is connected
     * @param serverName hotel server name
     */
    private void loadHotelData(String serverName)
    {
        try {
            conn = dBConn.getConnections(Constants.HOTEL_BROKER);
            Statement stmtCity = conn.createStatement();
            // query hotel info
            // hotel name is unique in the system, even they are in same company, they will be identified with location, like 'Hilton Melbourne', 'Hilton Sydney'
            ResultSet rs = stmtCity.executeQuery("select h.id, h.hotel_name,h.address,h.rate,c.city_name"
                    + " from hotel h left join city c on c.city_id = h.city_id where h.hotel_name = '" + serverName + "'");
            while (rs.next()) {
                int hotelId = rs.getInt(1);
                String hotelName = rs.getString(2);
                String hotelAddress = rs.getString(3);
                double rate = rs.getDouble(4);
                String cityName = rs.getString(5);
                List<Hotel> iniHotels = this.hotels.get(cityName);
                if (null == iniHotels) {
                    iniHotels = new ArrayList<>();
                    this.hotels.put(cityName, iniHotels);
                }
                iniHotels.add(new Hotel(hotelId, hotelName, cityName, hotelAddress, rate));
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(BrokerHOPPSocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Send request to hotel server and get server response
     *
     * @param clientRequest
     * @return server response
     */
    private String getHotelResponse(String clientRequest)
    {
        // Protocol: all the request to server should have the <hotel name> as the second parameter 
        String[] queries = clientRequest.split(":");
        String hotelName = queries[1];
        PrintStream writer = getWriter(hotelName);
        InputStream input = getInPutStream(hotelName);
        if (null != writer && null != input) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            writer.println(clientRequest);
            try {
                String hotelResponse =  reader.readLine();
                return hotelResponse;
            } catch (SocketException ex) {
                return "Error: " + hotelName + " server is offline, cannot be connected!";
            } catch (IOException ex) {
                return "Error: Internet issue.";
            }
        }
        return null;
    }

    /***
     * Get broker response
     * @param clientRequest
     * @return broker response
     */
    private String getBrokerResponse(String clientRequest)
    {
        if (clientRequest.startsWith(Constants.POC_BROKER_ALL_CITIES)) {
            return getAllCities();
        }
        if (clientRequest.startsWith(Constants.POC_BROKER_HOTEL_FROM_CITY)) {
            String[] queries = clientRequest.split(":");
            String cityName = queries[1];
            return getHotelsInACity(cityName);
        }
        // QUERY-BROKER-HOTEL-RATE:<hotel name>:<city name>
        if (clientRequest.startsWith(Constants.POC_BROKER_HOTEL_RATE)) {
            String[] queries = clientRequest.split(":");
            String hotelName = queries[1];
            String cityName = queries[2];
            return getHotelRate(hotelName, cityName);
        }
        return null;
    }

    /**
     * *
     * Get hotel server port by hotel name
     *
     * @param hotelName hotel name
     * @return hotel server port
     */
    private int getHotelPortByName(String hotelName)
    {
        switch (hotelName) {
            case Constants.HOTEL_CHEVRON:
                return Constants.PORT_CHEVRON;
            case Constants.HOTEL_HILTON:
                return Constants.PORT_HILTON;
            case Constants.HOTEL_REGENT:
                return Constants.PORT_REGENT;
            default:
                return -1;
        }
    }

    /**
     * *
     * Get printStream for different servers
     *
     * @param hotelName hotel name
     * @return server's printStream
     */
    private PrintStream getWriter(String hotelName)
    {
        switch (hotelName) {
            case Constants.HOTEL_HILTON:
                return writerHilton;
            case Constants.HOTEL_CHEVRON:
                return writerChevron;
            case Constants.HOTEL_REGENT:
                return writerRegent;
            default:
                return null;
        }
    }

    private InputStream getInPutStream(String hotelName)
    {
        switch (hotelName) {
            case Constants.HOTEL_HILTON:
                return inHilton;
            case Constants.HOTEL_CHEVRON:
                return inChevron;
            case Constants.HOTEL_REGENT:
                return inRegent;
            default:
                return null;
        }
    }

    /**
     * *
     * Get socket by hotel name
     *
     * @param hotelName
     * @return
     */
    private Socket getSocketByHotelName(String hotelName)
    {
        switch (hotelName) {
            case Constants.HOTEL_CHEVRON:
                return socketChevron;
            case Constants.HOTEL_HILTON:
                return socketHilton;
            case Constants.HOTEL_REGENT:
                return socketRegent;
            default:
                return null;
        }
    }

    /**
     * *
     * get all city names
     *
     * @return
     */
    private String getAllCities()
    {
        StringBuilder sb = new StringBuilder();
        for (String key : this.hotels.keySet()) {
            sb.append(key).append(",");
        }
        return sb.toString();
    }

    /**
     * *
     * get all hotel names by city name
     *
     * @param cityName city name
     * @return hotel names
     */
    private String getHotelsInACity(String cityName)
    {
        List<Hotel> hotelInCity = hotels.get(cityName);
        StringBuilder hotelNames = new StringBuilder();
        if (null != hotelInCity) {
            for (Hotel h : hotelInCity) {
                hotelNames.append(h.getHotelName()).append(",");
            }
        }
        return hotelNames.toString();
    }

    /**
     * *
     * Get hotel rate
     * @param hotelName
     * @param cityName
     * @return
     */
    private String getHotelRate(String hotelName, String cityName)
    {
        List<Hotel> hotelInCity = hotels.get(cityName);
        if (null != hotelInCity) {
            for (Hotel h : hotelInCity) {
                if (h.getHotelName().equals(hotelName)) {
                    return h.getRate() + "";
                }
            }
        }
        return "0";
    }
}
