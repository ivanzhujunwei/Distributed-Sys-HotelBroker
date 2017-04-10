package guessNumber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import managedBeans.HotelBookClientBean;

/**
 *
 * @author nbuser
 */
@ManagedBean(name = "UserNumberBean")
@SessionScoped
public class UserNumberBean  {

    Integer randomInt;
    Integer userNumber;
    String response;
    
    public static final int ECHOPORT = 8888;

    public String cityName;
    public String hotelName;
    public String rate;
    public Date checkIn;

    // establish the inputstream and outputstream
    InputStream in = null;
    PrintStream out = null;

    /** Creates a new instance of UserNumberBean */
    public UserNumberBean() {
        Random randomGR = new Random();
        randomInt = new Integer(randomGR.nextInt(10));
        System.out.println("Duke's number: " + randomInt);
        
        cityName = "";
        hotelName = "";
        rate = "";
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

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }

    public String getResponse() {
        if ((userNumber != null) && (userNumber.compareTo(randomInt) == 0)) {
            //invalidate user session
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            session.invalidate();

            return "Yay! You got it!";
        } else {

            // including HTML requires that you set escape="false" in view
            return "<p>Sorry, " + userNumber + " isn't it.</p>"
                    + "<p>Guess again...</p>";
        }
    }
    
    /**
     * *
     * Get all city names
     *
     * @return city names in the form of a list
     */
    public List<String> getAllCities()
    {
        List<String> lst = new ArrayList<>();
        // send request to server: get all citites' name
        out.println("QUERY-FOR-CITY");
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
                out.println("QUERY-HOTEL-FROM-CITY:" + cityName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = reader.readLine();
                // hotels' name are seperated by comma ','
                String[] hotelNames = line.split(",");
                for (String hn : hotelNames) {
                    lst.add(hn);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lst;
    }

    public String getRate()
    {
        String rate = "0";
        if (hotelName!=null && !hotelName.equals("") && !cityName.equals("") ) {
            out.println("QUERY-HOTEL-RATE:" + hotelName + ":" + cityName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            try {
                rate = reader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(HotelBookClientBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rate;
    }
    
    public String searchAvailableRooms(){
        return "greeting.xhtml";
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

    public void setRate(String rate)
    {
        this.rate = rate;
    }

    public Date getCheckIn()
    {
        return checkIn;
    }

    public void setCheckIn(Date checkIn)
    {
        this.checkIn = checkIn;
    }

}
