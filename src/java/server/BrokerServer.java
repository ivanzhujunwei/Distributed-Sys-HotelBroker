/*
 * This is server side handling Hotel Booking
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class BrokerServer
{

    public static int MYECHOPORT = 8888;

    public static void main(String[] args)
    {
        try {
            ServerSocket s = null;
            s = new ServerSocket(MYECHOPORT);
            System.out.println("Hotel booking server running");
            while (true) {
                Socket incoming = null;
                incoming = s.accept();
                new SocketHandler(incoming).run();
            }
        } catch (IOException ex) {
            Logger.getLogger(BrokerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
