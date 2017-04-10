/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
