/*
 * This is server side for clients to handle Hotel Booking 
 * 1) Receive request from clients sides and send them to the server sides
 * 2) Receive response from server sides and send back to the client sides
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
public class BrokerHOPP
{

    public static void main(String[] args)
    {
        try {
            ServerSocket s = new ServerSocket(Constants.PORT_BROKER);
            System.out.println("Hotel booking server running");
            while (true) {
                Socket incoming = s.accept();
//                new BrokerHOPPSocketHandler(incoming).start();
//                new BrokerHOPPSocketHandler(incoming).run();
                BrokerHOPPSocketHandler bh = new BrokerHOPPSocketHandler(incoming);
                new Thread(bh).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(BrokerHOPP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
