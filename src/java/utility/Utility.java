/*
 * System common utility methods
 */
package utility;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class Utility
{

    /*** 
     * Judge if the string is null or empty
     * @param str 
     * @return true if it is null or empty
     */
    public static boolean isEmpty(String str)
    {
        return str == null || str.equals("") || str.equals("null");
    }

    /**
     * *
     * Check if the string is in data format or not
     *
     * @param str string needs to be checked
     * @return return true if it's in data format, return false if not
     */
    public static boolean isDateFormat(String str)
    {
        return str.matches("\\d{4}[-]\\d{2}[-]\\d{2}");
    }

    /***
     * Check hotel server is online or available
     * @param port hotel server port
     * @return return true if it's online
     */
    public static boolean isHostServerOnline(int port)
    {
        try (Socket s = new Socket(InetAddress.getByName("localhost"), port)) {
            s.close();
            return true;
        } catch (IOException e) {
            System.out.println("The hotel server is offline.");
        }
        return false;
    }
    

    /***
     * Judge if the response from server contains error information
     * @param readLine response from server
     * @return true if there is no error
     */
    public static boolean isErrorFromServer(String readLine){
        return readLine.startsWith("Error");
    }
    
    /***
     * Judge if the context is a valid request from client or broker 
     * It is a invalid request if the context satisfy any one of the 2 rules
     * 1) empty
     * //2) equals 'BYE'
     * 3) the Array length after split by semicolon is less than or equals 1, which means the context does not contain useful request after the request protocol, e.g."QUERY-HOTEL:"
     * @param context request context
     * @return return true if it is a valid request context, return false if not
     */
    public static boolean isValidRequest(String context){
        if(isEmpty(context)||
//                context.equals("BYE")||
                context.split(Constants.SEMI).length<=1){
            return false;
        }
        return true;
    }
    
    /***
     * Compare two dates
     * @param before the previous date
     * @param after the later date
     * @return return true if 'before' is before 'after', return false if not
     */
    public static boolean strDateCompare(String before, String after){
        // validate checkin and checkout order
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date checkInDate;
        Date checkoutDate;
        try {
            checkInDate = format.parse(before);
            checkoutDate = format.parse(after);
            // validate checkout > checkin date
            if (checkInDate.compareTo(checkoutDate) <= 0) {
                return true;
            } 
        } catch (ParseException ex) {
            System.out.println("Error happen when comparing dates. "+ex.getMessage());
        }
        return false;
    }
}
