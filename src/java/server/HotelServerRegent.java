/*
 * Regent hotel server
 */

package server;

import utility.Constants;

/**
 * 
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class HotelServerRegent extends HotelServer{

    public HotelServerRegent(int port)
    {
        super(port);
    }
    

    public static void main(String[] args)
    {
        HotelServer hs = new HotelServer(Constants.PORT_REGENT);
        new Thread(hs).start();
    }
}
