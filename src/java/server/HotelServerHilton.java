/*
 * Hilton hotel server
 */

package server;

import utility.Constants;

/**
 * 
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class HotelServerHilton extends HotelServer{

    public HotelServerHilton(int port)
    {
        super(port);
    }
    

    public static void main(String[] args)
    {
        HotelServer hs = new HotelServer(Constants.PORT_HILTON);
        new Thread(hs).start();
    }
}
