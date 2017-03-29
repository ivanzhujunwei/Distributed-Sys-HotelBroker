/*
 * 
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import utility.Constants;

/**
 *
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class SocketHandler implements Runnable
{

    Socket incoming;

    SocketHandler(Socket incoming)
    {
        this.incoming = incoming;
    }

    @Override
    public void run()
    {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            PrintStream out = new PrintStream(incoming.getOutputStream());
//            out.println(displayOptions());
            boolean done = true;
            while (done) {
                // str should be integer
                String str = reader.readLine();
                System.out.println(str);
                int option = Integer.parseInt(str);
                String infoFromServer = "";
                switch (option) {
                    case Constants.OP_ALL_CITIES:
                        infoFromServer = displayAllServices();
                        break;
                    case Constants.OP_EXIT:
                        done = false;
                        continue;
                    // if nothing is entered, let client input again
                    default:
                        break;
                }
                out.println(infoFromServer);
            }
            out.println(Constants.OP_EXIT);
            incoming.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String displayAllServices()
    {
        return "Melbourne,Sydney,Perth,Canberra,Brisbane";
    }
    
//    private String displayOptions(){
//        String options = "111sChoose number to view hotel information."
//                + "1 - Cities serviced by the system.";
//        return options;
//    }
}
