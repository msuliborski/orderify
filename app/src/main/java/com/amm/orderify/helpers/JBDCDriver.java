package com.amm.orderify.helpers;

import java.sql.*;


import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JBDCDriver
{

    public static Connection myConn;
    public static Statement myStatement;
    public static final String ipAddress = "192.168.43.14";

    public static void Initiate() throws SQLException
    {


            myConn = DriverManager.getConnection("jdbc:mysql://192.168.43.14:3306/orderify", "root", "1234");
            myStatement = myConn.createStatement();

    }
    public static ResultSet ExecuteStatement(String query) throws SQLException
    {
        return myStatement.executeQuery(query);
    }


}
