/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class Booking {
    
    // booking id
    private int bookingId;
    
    // booked rooms 
    private List<Room> bookedRooms;
    
    // name of customer who booked the room
    private String customerName;
    
    // customer email
    private String email;
    
    // customer phone
    private String phone;
    
    // booked hotel
    private Hotel hotel;
    
    // check in date
    private Date checkInDate;
    
    // check out date
    private Date checkOutDate;

    public int getBookingId()
    {
        return bookingId;
    }

    public void setBookingId(int bookingId)
    {
        this.bookingId = bookingId;
    }

    public List<Room> getBookedRooms()
    {
        return bookedRooms;
    }

    public void setBookedRooms(List<Room> bookedRooms)
    {
        this.bookedRooms = bookedRooms;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Hotel getHotel()
    {
        return hotel;
    }

    public void setHotel(Hotel hotel)
    {
        this.hotel = hotel;
    }

    public Date getCheckInDate()
    {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate)
    {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate()
    {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate)
    {
        this.checkOutDate = checkOutDate;
    }
    
    

}
