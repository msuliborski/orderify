package com.amm.orderify.helpers;

import java.sql.*;

public class JBDCDriver
{
    public static Connection myConn;
    public static Statement myStatement;

    public static void Initiate() throws SQLException
    {
        myConn = DriverManager.getConnection("jdbc:mysql://192.168.1.100:3306/orderify", "root", "1234");
        myStatement = myConn.createStatement();
    }
    public static ResultSet ExecuteStatement(String query) throws SQLException
    {
        return myStatement.executeQuery(query);
    }


}
