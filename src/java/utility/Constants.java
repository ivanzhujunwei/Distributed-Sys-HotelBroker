/*
 * Syste contants
 */
package utility;

/**
 *
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class Constants
{

    /**
     * ********** options ***********
     */
    public static final int OP_ALL_CITIES = 1;

    public static final int OP_EXIT = 0;

    /**
     * *********** hotel names **********
     */
    public static final String HOTEL_BROKER = "broker";

    public static final String HOTEL_HILTON = "Hilton";

    public static final String HOTEL_CHEVRON = "Chevron";

    public static final String HOTEL_REGENT = "Regent";

    public static final String SEMI = ":";

    /**
     * *********** server ports **********
     */
    public static final int PORT_BROKER = 8880;

    public static final int PORT_HILTON = 8881;

    public static final int PORT_CHEVRON = 8882;

    public static final int PORT_REGENT = 8883;

    /**
     * *********** database connectionS **********
     */
    public static final String DB_BROKER = "jdbc:mysql://localhost:3306/hotelBroker?useSSL=true";

    public static final String DB_HILTON = "jdbc:mysql://localhost:3306/hotelHilton?useSSL=true";

    public static final String DB_CHEVRON = "jdbc:mysql://localhost:3306/hotelChevron?useSSL=true";

    public static final String DB_REGENT = "jdbc:mysql://localhost:3306/hotelRegent?useSSL=true";

    // error message from broker server
    public static final String ERR_BROKER_SERVER = "Error: connection lost, broker server is offline now.";

    public static final String ERR_IOEXCEPTION = "Error: IO Exception.";

    // Protocol
    // establish the connection from clients to hotel servers
    public static final String POC_CONNECT_HOTEL_SERER = "TRY-CONNECT-HOTEL-SERVER:";
    
    ///////////////////////////////////////////////////////////////
    // protocols that send straight to broker server
    ///////////////////////////////////////////////////////////////
    
    // protocols start with 'QUERY-BROKER' indicating broker server should handle the request
    public static final String POC_BROKER = "QUERY-BROKER";
    
    // query city info from broker server
    public static final String POC_BROKER_ALL_CITIES = "QUERY-BROKER-CITY:ALL";

    // query hotel list in a city from broker server
    public static final String POC_BROKER_HOTEL_FROM_CITY = "QUERY-BROKER-HOTEL-FROM-CITY:";

    // query hotel rate from broker server
    public static final String POC_BROKER_HOTEL_RATE = "QUERY-BROKER-HOTEL-RATE:";

    ///////////////////////////////////////////////////////////////
    // protocols that send towards hotel servers
    ///////////////////////////////////////////////////////////////
    
    // protocols start with 'QUERY-HOTEL' indicating hotel servers should handle the request
    public static final String POC_HOTEL = "QUERY-HOTEL";

    // submit booking request to hotel server through broker server
    public static final String POC_HOTEL_SUBMIT_BOOKING = "QUERY-HOTEL-SUBMIT-BOOKING:";

    // query available room from hotel servers through broker server
    public static final String POC_HOTEL_AVAILABLE_ROOMS = "QUERY-HOTEL-AVAILABLE-ROOMS:";

}
