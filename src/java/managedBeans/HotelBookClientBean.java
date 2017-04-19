/*
 * The class is coping with the client users' request
 * Then sending requests to broker server and get response from broker server.
 */
package managedBeans;

import entities.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import utility.Constants;
import utility.Utility;

/**
 *
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
@ManagedBean(name = "hotelBookBean")
@SessionScoped
public class HotelBookClientBean implements Serializable
{

    // city name
    public String cityName;
    
    // hotel name
    public String hotelName;
    
    // hotel room rate
    public double rate;
    
    // check in date
    public String checkIn;
    
    // check out date
    public String checkOut;
    
    // available rooms found
    public List<Room> roomsFound;
    public boolean show;

    // city list displayed in drop down list
    public List<String> allCities;

    // guest name
    public String guestName;
    
    // guest phone
    public String phone;
    
    // guest email
    public String email;
    
    // guest credit
    public String credit;

    // total amount
    public double totalAmount;

    // room which is being booked
    public Room bookingRoom;

    // error response from servers
    public String error;
    
    // different connection info for hotel servers
    public String connInfoHilton;
    public String connInfoChevron;
    public String connInfoRegent;

    // establish the inputstream and outputstream
    InputStream in = null;
    PrintStream out = null;

    /**
     * Creates a new instance of HotelBookBean
     */
    public HotelBookClientBean()
    {
        allCities = new ArrayList<>();
        connInfoChevron = "";
        connInfoHilton = "";
        connInfoRegent = "";

        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(2);
            return;
        }
        Socket sock = null;
        try {
            sock = new Socket(address, Constants.PORT_BROKER);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
            return;
        }
        try {
            in = sock.getInputStream();
            out = new PrintStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(5);
            return;
        }
        System.out.println("Client Running");
    }

    @PostConstruct
    public void init()
    {
        initSessionValue();
    }

    /***
     * Initialize value
     */
    private void initSessionValue()
    {
        error = "";
        cityName = "";
        hotelName = "";
        rate = 0;
        roomsFound = new ArrayList<>();
        bookingRoom = new Room();

    }

    /**
     * *
     * Connect to different hotel servers
     *
     * @param hotelName hotel name
     */
    public void connectHotelServer(String hotelName)
    {
        // send request to server: try to connect hotel server
        out.println(Constants.POC_CONNECT_HOTEL_SERER + hotelName);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String connectInfo = reader.readLine();
            setError("");
            switch (hotelName) {
                case Constants.HOTEL_CHEVRON:
                    connInfoChevron = connectInfo;
                    break;
                case Constants.HOTEL_HILTON:
                    connInfoHilton = connectInfo;
                    break;
                case Constants.HOTEL_REGENT:
                    connInfoRegent = connectInfo;
                    break;
                default:
                    break;
            }
        } catch (SocketException se) {
            setError("Error: connection lost, unable to connect to broker server.");
        } catch (IOException ex) {
            setError("Error: IO exception when trying to connecting to broker server.");
        }
    }

    /***
     * Find another room after booking successfully
     * @return 
     */
    public String findAnotherRoom()
    {
        initSessionValue();
        return "findAroom.xhtml";
    }

    /**
     * *
     * Get all city names
     *
     * @return city names in the form of a list
     */
    public List<String> getAllCities()
    {
        // send request to server: get all citites' name
        allCities.clear();
        out.println(Constants.POC_BROKER_ALL_CITIES);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            if (!Utility.isEmpty(line)) {
                // cities' name are seperated by comma ','
                String[] cityNames = line.split(",");
                allCities.addAll(Arrays.asList(cityNames));
            }
        } catch (SocketException se) {
            setError(Constants.ERR_BROKER_SERVER);
        } catch (IOException ex) {
            setError(Constants.ERR_IOEXCEPTION);
        }
        return allCities;
    }

    /**
     * *
     * Get hotel names by city name
     *
     * @return hotel name in the form of a list
     */
    public List<String> getHotelsInCity()
    {
        List<String> lst = new ArrayList<>();
        try {
            // correct city name
            if (cityName != null && !cityName.trim().equals("")) {
                out.println(Constants.POC_BROKER_HOTEL_FROM_CITY + cityName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = reader.readLine();
                clearError();
                if (!Utility.isEmpty(line)) {
                    // hotels' name are seperated by comma ','
                    String[] hotelNames = line.split(",");
                    lst.addAll(Arrays.asList(hotelNames));
                }
            }
        } catch (SocketException se) {
            setError(Constants.ERR_BROKER_SERVER);
        } catch (IOException ex) {
            setError(Constants.ERR_IOEXCEPTION);
        }
        return lst;
    }

    /**
     * *
     * Clear error context
     */
    private void clearError()
    {
        setError("");
    }

    /***
     * Get hotel rate
     * @return 
     */
    public double getRate()
    {
        String rateStr = "0";
        if (!Utility.isEmpty(hotelName) && !Utility.isEmpty(cityName)) {
            out.println(Constants.POC_BROKER_HOTEL_RATE + hotelName + ":" + cityName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            try {
                rateStr = reader.readLine();
                System.out.println("........"+rateStr);
                rate = Double.parseDouble(rateStr);
            } catch (SocketException se) {
                setError(Constants.ERR_BROKER_SERVER);
            } catch (IOException ex) {
                setError(Constants.ERR_IOEXCEPTION);
            }
        }
        return rate;
    }

    public List<Room> getRoomsFound()
    {
        return roomsFound;
    }

    /***
     * Make a booking 
     * @return 
     */
    public String makeBooking()
    {
        String request = Constants.POC_HOTEL_SUBMIT_BOOKING
                + hotelName + Constants.SEMI
                + checkIn + Constants.SEMI
                + checkOut + Constants.SEMI
                + bookingRoom.getRoomId() + Constants.SEMI
                + guestName + Constants.SEMI
                + phone + Constants.SEMI
                + email + Constants.SEMI
                + credit;
        out.println(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            String bookResponse = reader.readLine();
            // if hotel servers send response with failure information
            // booking operation will be prevented
            if (Utility.isErrorFromServer(bookResponse)) {
                setError(bookResponse);
                return "bookRoom.xhtml";
            } else {
                clearError();
            }
        } catch (SocketException se) {
            setError(Constants.ERR_BROKER_SERVER);
        } catch (IOException ex) {
            setError(Constants.ERR_IOEXCEPTION);
        }
        return "ticket.xhtml";
    }

    /***
     * Find available rooms
     */
    public void getAvailableRooms()
    {
        if (Utility.isEmpty(hotelName) || Utility.isEmpty(checkIn) || Utility.isEmpty(checkOut)) {
            return;
        }
        // validate checkin and checkout order
        if (!Utility.strDateCompare(checkIn, checkOut)) {
            setError("Check in date should be before check out date.");
            return;
        } else {
            clearError();
        }
        if (Utility.isDateFormat(checkIn) && Utility.isDateFormat(checkOut)) {
            // hotelName could only be: HOTEL_BROKER / HOTEL_HILTON / HOTEL_CHEVRON / HOTEL_REGENT
            String request = Constants.POC_HOTEL_AVAILABLE_ROOMS + hotelName + ":" + checkIn + ":" + checkOut;
            out.println(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String roomFromServer;
            try {
                roomsFound.clear();
                roomFromServer = reader.readLine();
                clearError();
                // check the response from server is empty or contains failure information
                if (!Utility.isEmpty(roomFromServer) && !Utility.isErrorFromServer(roomFromServer)) {
                    String[] roomArray = roomFromServer.split(":");
                    for (String roomSt : roomArray) {
                        String[] singleRoom = roomSt.split("-");
                        Room r = new Room(Integer.parseInt(singleRoom[0]), singleRoom[1]);
                        roomsFound.add(r);
                    }
                } else if (Utility.isErrorFromServer(roomFromServer)) {
                    setError(roomFromServer);
                }
            } catch (SocketException se) {
                setError(Constants.ERR_BROKER_SERVER);
            } catch (IOException ex) {
                setError(Constants.ERR_IOEXCEPTION);
            }
        }
    }

    /**
     * *
     * Go to booking page
     *
     * @param room the room is going to be booked
     * @return booking page
     */
    public String tryBooking(Room room)
    {
        this.bookingRoom = room;
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        int diffDays = 1;
        try {
            Date checkInDate = format.parse(checkIn);
            Date checkOutDate = format.parse(checkOut);
            diffDays = (int) ((checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24));
        } catch (ParseException ex) {
            Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        totalAmount = diffDays * rate;
        return "bookRoom.xhtml";
    }

    public String getCityName()
    {

        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    public String getHotelName()
    {
        return hotelName;
    }

    public void setHotelName(String hotelName)
    {
        this.hotelName = hotelName;
    }

    public String getCheckIn()
    {
        return checkIn;
    }

    public void setCheckIn(String checkIn)
    {
        this.checkIn = checkIn;
    }

    public String getCheckOut()
    {
        return checkOut;
    }

    public void setCheckOut(String checkOut)
    {
        this.checkOut = checkOut;
    }

    public void setRoomsFound(List<Room> roomsFound)
    {
        this.roomsFound = roomsFound;
    }

    public boolean isShow()
    {
        return show;
    }

    public void setShow(boolean show)
    {
        this.show = show;
    }

    public String getGuestName()
    {
        return guestName;
    }

    public void setGuestName(String guestName)
    {
        this.guestName = guestName;
    }

    public String getError()
    {
        return error;
    }

    /**
     * *
     * Set the error info if the error has been cleared
     *
     * @param error
     */
    public void setError(String error)
    {
//        if (Utility.isEmpty(this.error)) {
            this.error = error;
//        }
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getCredit()
    {
        return credit;
    }

    public void setCredit(String credit)
    {
        this.credit = credit;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Room getBookingRoom()
    {
        return bookingRoom;
    }

    public void setBookingRoom(Room bookingRoom)
    {
        this.bookingRoom = bookingRoom;
    }

    public double getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public void setAllCities(List<String> allCities)
    {
        this.allCities = allCities;
    }

    public String getConnInfoHilton()
    {
        return connInfoHilton;
    }

    public void setConnInfoHilton(String connInfoHilton)
    {
        this.connInfoHilton = connInfoHilton;
    }

    public String getConnInfoChevron()
    {
        return connInfoChevron;
    }

    public void setConnInfoChevron(String connInfoChevron)
    {
        this.connInfoChevron = connInfoChevron;
    }

    public String getConnInfoRegent()
    {
        return connInfoRegent;
    }

    public void setConnInfoRegent(String connInfoRegent)
    {
        this.connInfoRegent = connInfoRegent;
    }

}
