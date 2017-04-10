/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javax.faces.event.AjaxBehaviorEvent;
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

    public String cityName;
    public String hotelName;
    public double rate;
    public String checkIn;
    public String checkOut;
    public List<Room> roomsFound;
    public boolean show;

    public List<String> allCities;

    public String guestName;
    public String phone;
    public String email;
    public String credit;

    public double totalAmount;

    public Room bookingRoom;

    public String error;
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
        out.println("TRY-CONNECT-HOTEL-SERVER:" + hotelName);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String connectInfo = reader.readLine();
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
        } catch (IOException ex) {
            Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
        out.println("QUERY-BROKER-CITY:ALL");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            if (!Utility.isEmpty(line)) {
                // cities' name are seperated by comma ','
                String[] cityNames = line.split(",");
                allCities.addAll(Arrays.asList(cityNames));
            }
        } catch (IOException ex) {
            Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
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
                out.println("QUERY-BROKER-HOTEL-FROM-CITY:" + cityName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = reader.readLine();
                if (!Utility.isEmpty(line)) {
                    // hotels' name are seperated by comma ','
                    String[] hotelNames = line.split(",");
                    lst.addAll(Arrays.asList(hotelNames));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lst;
    }

    public double getRate()
    {
        String rateStr = "0";
        if (!Utility.isEmpty(hotelName) && !Utility.isEmpty(cityName)) {
            out.println("QUERY-BROKER-HOTEL-RATE:" + hotelName + ":" + cityName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            try {
                rateStr = reader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.rate = Double.parseDouble(rateStr);
        return rate;
    }

    public List<Room> getRoomsFound()
    {

//        if (Utility.isEmpty(hotelName) || Utility.isEmpty(checkIn) || Utility.isEmpty(checkOut)) {
//            return roomsFound;
//        }
//        roomsFound.clear();
//        if (Utility.isDateFormat(checkIn) && Utility.isDateFormat(checkOut)) {
//            System.out.println("dddddd");
//            // hotelName could only be: HOTEL_BROKER / HOTEL_HILTON / HOTEL_CHEVRON / HOTEL_REGENT
//            String request = "QUERY-HOTEL-AVAILABLE-ROOMS:" + hotelName + ":" + checkIn + ":" + checkOut;
//            out.println(request);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            String roomFromServer;
//            try {
//                roomFromServer = reader.readLine();
//                if (!Utility.isEmpty(roomFromServer)) {
//                    String[] roomArray = roomFromServer.split(":");
//                    for (String roomSt : roomArray) {
//                        Room r = new Room();
//                        String[] singleRoom = roomSt.split("-");
//                        r.setRoomId(Integer.parseInt(singleRoom[0]));
//                        r.setDes(singleRoom[1]);
//                        roomsFound.add(r);
//                    }
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        return roomsFound;
    }

    public String makeBooking()
    {
        String request = "QUERY-HOTEL-SUBMIT-BOOKING:"
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
        String bookResponse;
        try {
            bookResponse = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ticket.xhtml";
    }

    public void getAvailableRooms()
    {
        if (Utility.isEmpty(hotelName) || Utility.isEmpty(checkIn) || Utility.isEmpty(checkOut)) {
            return;
        }
        roomsFound.clear();
        // validate checkin and checkout order
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        Date checkInDate;
        try {
            checkInDate = format.parse(checkIn);
            Date checkoutDate = format.parse(checkOut);
            // validate checkout > checkin date
            if (checkInDate.compareTo(checkoutDate) > 0) {
                setError("Check in date should be before check out date.");
                return;
            } else {
                setError("");
            }
        } catch (ParseException ex) {
            Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (Utility.isDateFormat(checkIn) && Utility.isDateFormat(checkOut)) {
            // hotelName could only be: HOTEL_BROKER / HOTEL_HILTON / HOTEL_CHEVRON / HOTEL_REGENT
            String request = "QUERY-HOTEL-AVAILABLE-ROOMS:" + hotelName + ":" + checkIn + ":" + checkOut;
            out.println(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String roomFromServer;
            try {
                roomFromServer = reader.readLine();
                if (!Utility.isEmpty(roomFromServer) && !Utility.isErrorFromServer(roomFromServer)) {
                    String[] roomArray = roomFromServer.split(":");
                    for (String roomSt : roomArray) {
                        Room r = new Room();
                        String[] singleRoom = roomSt.split("-");
                        r.setRoomId(Integer.parseInt(singleRoom[0]));
                        r.setDes(singleRoom[1]);
                        roomsFound.add(r);
                    }
                }else if(Utility.isErrorFromServer(roomFromServer)){
                    setError(roomFromServer);
                }
                
            } catch (IOException ex) {
                Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String tryBooking(Room room)
    {
        this.bookingRoom = room;
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        int diffDays = 1;
        try {
            Date checkInDate = format.parse(checkIn);
            Date checkOutDate = format.parse(checkOut);
            diffDays = (int) ((checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24));
//            totalAmount = Double.parseDouble(diffDays * rate);
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

    public void setError(String error)
    {
        this.error = error;
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
