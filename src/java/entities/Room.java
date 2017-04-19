/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;

/**
 * 
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class Room implements Serializable{
    
    private int roomId;
    
    private String des;
    
    public Room(int roomId, String desc){
        this.roomId = roomId;
        this.des = desc;
    }

    public Room(){}
    
    public int getRoomId()
    {
        return roomId;
    }

    public void setRoomId(int roomId)
    {
        this.roomId = roomId;
    }

    public String getDes()
    {
        return des;
    }

    public void setDes(String des)
    {
        this.des = des;
    }
    
    
    
}
