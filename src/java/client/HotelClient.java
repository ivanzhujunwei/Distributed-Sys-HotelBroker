package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class HotelClient
{

    public static final int ECHOPORT = 8888;

    public static void main(String[] args)
    {
        // judge the parameter is valid or not
//        if (args.length != 1) {
//            System.err.println("Usage: Client address");
//            System.exit(1);
//            return;
//        }
        // get the internet address
        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
//            address = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(2);
            return;
        }
        // create socket and connect to corresponding server
        Socket sock = null;
        try {
            sock = new Socket(address, ECHOPORT);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
            return;
        }
        // establish the inputstream and outputstream
        InputStream in = null;
        PrintStream out = null;
        try {
            in = sock.getInputStream();
            out = new PrintStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
             System.exit(5);
            return;
        }
        System.out.println("Client Running");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Scanner console = new Scanner(System.in);
        String message = "";
        String line = "";
        while (!message.trim().equals("0")) {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                // System.exit(6);
                return;
            }
            System.out.println(line);
            message = console.nextLine();
            out.println(message);
            // System.exit(0);
        }
        System.out.println("Client Closed");
        return;
    }
} // HotelClient
