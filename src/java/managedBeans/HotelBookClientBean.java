/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import utility.Constants;

/**
 *
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
@ManagedBean(name = "hotelBookBean")
@SessionScoped
public class HotelBookClientBean
{

    public static final int ECHOPORT = 8888;

    public String hotels = "";
    public String cityName = "";

    // establish the inputstream and outputstream
    InputStream in = null;
    PrintStream out = null;

    /**
     * Creates a new instance of HotelBookBean
     */
    public HotelBookClientBean()
    {
        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
//            address = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(2);
            return;
        }

        Socket sock = null;
        try {
            sock = new Socket(address, ECHOPORT);
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

    public String getHotels()
    {
        return "gethotels";
    }


    public void setHotels(String hotels)
    {
        this.hotels = hotels;
    }
    
    public void localeChanged(ValueChangeEvent e) {
      //assign new value to country
      cityName = e.getNewValue().toString(); 
   }

    /***
     * Get all city names
     * @return city names in the form of a list
     */
    public List<String> getAllCities()
    {
        List<String> lst = new ArrayList<>();
        // send request to server: get all citites' name
        out.println(Constants.OP_ALL_CITIES);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            // cities' name are seperated by comma ','
            String[] cityNames = line.split(",");
            for (String cn : cityNames) {
                lst.add(cn);
            }
        } catch (IOException ex) {
            Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lst;
    }
    
    /***
     * Get hotel names by city name
//     * @param cityName the name of city selected
     * @return hotel name in the form of a list
     */
//    public List<String> getHotelsInCity()
//    {
//        List<String> lst = new ArrayList<>();
////        UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
////        UIComponent m = view.findComponent("selecCity");
////        Map<String,Object> mm = m.getAttributes();
//        try {
////            out.println(cityName);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            String line = reader.readLine();
//            // hotels' name are seperated by comma ','
//            String[] hotelNames = line.split(",");
//            for (String hn : hotelNames) {
//                lst.add(hn);
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return lst;
//    }

    public String getCityName()
    {
        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }
    
    
}
