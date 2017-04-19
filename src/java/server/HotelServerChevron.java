/*
 * Chevron hotel server
 */

package server;

import utility.Constants;

/**
 * 
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class HotelServerChevron extends HotelServer{

    public HotelServerChevron(int port)
    {
        super(port);
    }
    

    public static void main(String[] args)
    {
        HotelServer hs = new HotelServer(Constants.PORT_CHEVRON);
        new Thread(hs).start();
    }
}
