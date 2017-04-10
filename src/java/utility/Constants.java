/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

}
