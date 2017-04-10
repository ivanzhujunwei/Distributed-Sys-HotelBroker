/*
 * Hotel entity
 */

package entities;

/**
 * 
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class Hotel {
    
    private int hotelId;
    
    private String hotelName;
    
    private String cityName;
    
    private String address;
    
    private double rate;
    
    public Hotel(int hotelId, String hotelName, String cityName, String address, double rate){
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.cityName = cityName;
        this.address = address;
        this.rate = rate;
    }
    

    public int getHotelId()
    {
        return hotelId;
    }

    public void setHotelId(int hotelId)
    {
        this.hotelId = hotelId;
    }

    public String getHotelName()
    {
        return hotelName;
    }

    public void setHotelName(String hotelName)
    {
        this.hotelName = hotelName;
    }

    public String getCityName()
    {
        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public double getRate()
    {
        return rate;
    }

    public void setRate(double rate)
    {
        this.rate = rate;
    }
}
