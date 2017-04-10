/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.Constants;

/**
 *
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class HotelServer implements Runnable
{

    ServerSocket s;
    int port;

    public HotelServer(int port)
    {
        try {
            this.s = new ServerSocket(port);
            this.port = port;
        } catch (IOException ex) {
            Logger.getLogger(HotelServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run()
    {
        System.out.println("Port of " + port + ": " + getHotelName(port) + " hotel server starts running...");
        while (true) {
            Socket incoming = null;
            try {
                incoming = s.accept();
            } catch (IOException e) {
                System.out.println(e);
                break;
            }
            HotelServerSocketHandler hssh = new HotelServerSocketHandler(incoming);
            new Thread(hssh).start();
        }
    }

    private String getHotelName(int port)
    {
        switch (port) {
            case Constants.PORT_CHEVRON:
                return Constants.HOTEL_CHEVRON;
            case Constants.PORT_HILTON:
                return Constants.HOTEL_HILTON;
            case Constants.PORT_REGENT:
                return Constants.HOTEL_REGENT;
            default:
                return null;
        }
    }

//    private ServerSocket getHotelServerSocket(String hotelName) throws IOException
//    {
//        switch (hotelName) {
//            case Constants.HOTEL_CHEVRON:
//                return new ServerSocket(Constants.PORT_CHEVRON);
//            case Constants.HOTEL_HILTON:
//                return new ServerSocket(Constants.PORT_HILTON);
//            case Constants.HOTEL_REGENT:
//                return new ServerSocket(Constants.PORT_REGENT);
//            default:
//                return null;
//        }
//    }
}
