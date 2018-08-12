package com.amm.orderify.helpers;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.Statement;

public class JBDCDriver {

    public static Connection connection;
    private static Statement myStatement;

    private static String ip;
    private static String database;
    private static String user;
    private static String password;
    public static void InitiateConnection(String _ip, String _database, String _user, String _password) {
        ip = _ip;
        database = _database;
        user = _user;
        password = _password;
    }

    public static void InitiateConnection() {
        database = "Orderify";
        user = "root";

//        ip = "192.168.1.100";
//        password = "1234";

        ip = "10.21.21.100";
        password = "michal";
    }

    public static void ConnectToDatabase() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/" + database, user, password);
            myStatement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet ExecuteQuery(String query) throws SQLException {
        return myStatement.executeQuery(query);
    }
    public static void ExecuteUpdate(String query) throws SQLException {
        myStatement.executeUpdate(query);
    }

    public static Connection getConnection() {
        return connection;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        JBDCDriver.ip = ip;
    }

    public static String getDatabase() {
        return database;
    }

    public static void setDatabase(String database) {
        JBDCDriver.database = database;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        JBDCDriver.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        JBDCDriver.password = password;
    }
}
