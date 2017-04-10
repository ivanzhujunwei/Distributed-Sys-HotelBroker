/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ivan Zhu <ivanzhujunwei@gmail.com>
 */
public class DBConnection
{

    private static final String USERNAME = "root";
    private static final String PASSWORD = "zjw";

    public static Connection conn;

    /**
     * *
     * Connect to different hotel database
     *
     * @param hotelName hotel name as well as the database name
     * @return database connection
     * @throws SQLException
     */
    public Connection getConnections(String hotelName) throws SQLException
    {
        System.out.println("Trying to connect " + hotelName + " database!");
        switch (hotelName) {
            case Constants.HOTEL_BROKER:
                conn = DriverManager.getConnection(Constants.DB_BROKER, USERNAME, PASSWORD);
                break;
            case Constants.HOTEL_HILTON:
                conn = DriverManager.getConnection(Constants.DB_HILTON, USERNAME, PASSWORD);
                break;
            case Constants.HOTEL_CHEVRON:
                conn = DriverManager.getConnection(Constants.DB_CHEVRON, USERNAME, PASSWORD);
                break;
            case Constants.HOTEL_REGENT:
                conn = DriverManager.getConnection(Constants.DB_REGENT, USERNAME, PASSWORD);
                break;
            default:
                conn = DriverManager.getConnection(Constants.DB_BROKER, USERNAME, PASSWORD);
                break;
        }
        System.out.println("Connected!");
        return conn;
    }

    /***
     * Close database connection
     */
    public static void closeConnection()
    {
        try {
            conn.close();
            System.out.println("Connection is closed.");
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
