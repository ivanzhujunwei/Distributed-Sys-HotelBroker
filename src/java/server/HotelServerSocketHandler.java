/*
 * Handle all the requests from broker server
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.Constants;
import utility.DBConnection;
import utility.Utility;

/**
 *
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class HotelServerSocketHandler implements Runnable
{

    Connection conn;
    Socket incoming;

    DBConnection dBConn;

    HotelServerSocketHandler(Socket incoming)
    {
        this.incoming = incoming;
        dBConn = new DBConnection();
    }

    @Override
    public void run()
    {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            PrintStream out = new PrintStream(incoming.getOutputStream());
            boolean done = true;
            while (done) {
                String brokerRequest = reader.readLine();
                if (Utility.isValidRequest(brokerRequest)) {
                    System.out.println("Broker request: " + brokerRequest);
                    String response2Client = responseFromServer(brokerRequest);
                    out.println(response2Client);
                } else {
                    out.println("Error invalid request from broker");
                    break;
                }
            }
            incoming.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * get response from server
     * @param parameter
     * @return 
     */
    public String responseFromServer(String parameter)
    {
        // QUERY-AVAILABLE-ROOMS:<hotel name>:<city name>:<check in>:<check out>
        if (parameter.contains(Constants.POC_HOTEL_AVAILABLE_ROOMS)) {
            String[] queries = parameter.split(":");
            String hotelName = queries[1];
            String checkin = queries[2];
            String checkout = queries[3];
            return getAvailableRooms(hotelName, checkin, checkout);
        }
        // QUERY-BOOKING:<hotel name>:<check in>:<check out>:<roomid>
        //  String request = "BOOKING:" + hotelName + Constants.SEMI + checkIn + Constants.SEMI + checkOut + Constants.SEMI + bookingRoom.getRoomId();
        if (parameter.contains(Constants.POC_HOTEL_SUBMIT_BOOKING)) {
            String[] queries = parameter.split(":");
            String hotelName = queries[1];
            String checkin = queries[2];
            String checkout = queries[3];
            String roomIdStr = queries[4];

            String guestName = queries[5];
            String phone = queries[6];
            String email = queries[7];
            String credit = queries[8];
            return bookingRoom(hotelName, checkin, checkout, Integer.parseInt(roomIdStr), guestName, phone, email, credit);
        }
        return "BYE";
    }

    /**
     * *
     * Booking a room
     *
     * @param hotelName
     * @param checkIn
     * @param checkOut
     * @param roomId
     * @param guestName
     * @param phone
     * @param email
     * @param credit
     * @return
     */
    private String bookingRoom(String hotelName, String checkIn, String checkOut, int roomId, String guestName, String phone, String email, String credit)
    {
        try {
            conn = dBConn.getConnections(hotelName);
            Statement stmtBooking = conn.createStatement();
            String sql = "insert into booking"
                    + "(checkin, checkout, phone, email, guestname, credit_card, room_id)"
                    + "values"
                    + "(str_to_date('" + checkIn + "','%Y-%m-%d'),"
                    + "str_to_date('" + checkOut + "','%Y-%m-%d'),"
                    + "'" + phone + "','"
                    + email + "','"
                    + guestName + "','"
                    + credit + "',"
                    + roomId + ")";
//            System.out.println(sql);
            stmtBooking.executeUpdate(sql);
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(HotelServerSocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
        return "1";
    }

    // query available romms
    private String getAvailableRooms(String hotelName, String checkin, String checkout)
    {
        StringBuilder sb = new StringBuilder();
        try {
            conn = dBConn.getConnections(hotelName);
            Statement stmtCity = conn.createStatement();
            String sql = "select distinct rm.room_id, rm.desc from room rm "
                    + " where "
                    + // checkin and checkout restrict
                    " NOT EXISTS"
                    + "  (SELECT 1"
                    + "  FROM Booking b"
                    + "  WHERE b.room_id = rm.room_id"
                    + "  AND (('" + checkin + "' BETWEEN b.checkin AND b.checkout "
                    + "  OR '" + checkout + "' BETWEEN b.checkin AND b.checkout) "
                    + "  OR ( "
                    + "  str_to_date(' " + checkin + "','%Y-%m-%d')  <= b.checkin  "
                    + "  AND str_to_date('" + checkout + "','%Y-%m-%d') >= b.checkout )))";
//            System.out.println(sql);
            ResultSet roomRs = stmtCity.executeQuery(sql);
            while (roomRs.next()) {
                int roomId = roomRs.getInt(1);
                String roomDesc = roomRs.getString(2);
                // roomid-desc:roomid-desc:roomid-desc
                sb.append(roomId).append("-").append(roomDesc).append(":");
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(HotelServerSocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        String ret  = sb.toString();
        return ret;
    }

}
