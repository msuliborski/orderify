package com.amm.orderify.helpers;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import com.amm.orderify.MainActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.Statement;

public class JBDCDriver
{

    public static Connection myConn;
    public static Statement myStatement;
    public static String ipAddress = "192.168.1.100";
    public static String classs = "net.sourceforge.jtds.jdbc.Driver";
    //public static String classs = "com.mysql.jdbc.Driver";

    public static void Initiate() throws SQLException
    {


            myConn = DriverManager.getConnection("jdbc:mysql://"+ ipAddress +":3306/orderify", "root", "1234");
            myStatement = myConn.createStatement();

    }
    public static ResultSet ExecuteStatement(String query) throws SQLException
    {
        return myStatement.executeQuery(query);
    }

    @SuppressLint("NewApi")
    public static Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            //ConnURL = "jdbc:jtds:sqlserver://" + ipAddress + ";"
            //        + "databaseName=" + "orderify" + ";user=" + "root" + ";password="
            //        + "1234" + ";";
            ConnURL = "jdbc:mysql://192.168.1.100:3306/orderify?autoReconnect=true&useSSL=false";
            conn = DriverManager.getConnection(ConnURL, "root", "1234");
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
            MainActivity.error = se.getMessage();
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
            MainActivity.error = e.getMessage();
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
            MainActivity.error = e.getMessage();
        }
        return conn;
    }
}
