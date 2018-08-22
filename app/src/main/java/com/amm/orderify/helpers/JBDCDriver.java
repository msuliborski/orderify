package com.amm.orderify.helpers;

import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.Statement;

import static com.amm.orderify.helpers.TimeAndDate.getCurrentDate;
import static com.amm.orderify.helpers.TimeAndDate.getCurrentTime;

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

        ip = "10.0.2.2";
        password = "";

//        ip = "192.168.1.100";
//        password = "1234";
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
        logQueriesFromApp(query);
        return myStatement.executeQuery(query);
    }

    public static void ExecuteUpdate(String query) throws SQLException {
        logQueriesFromApp(query);
        myStatement.executeUpdate(query);
    }

    public static Connection getConnection() {
        return connection;
    }

    private static void logQueriesFromApp(String query){
        File queriesFromApp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "queriesFromApp.txt");
        /*
        Environment.getExternalStorageState() - returns path to internal SD mount point like “/mnt/sdcard”

        getExternalFilesDir() - returns the path to files folder inside Android/data/data/application_package/ on the SD card.
        It is used to store any required files for your app (like images downloaded from web or cache files).
        Once the app is uninstalled, any data stored in this folder is gone too.

        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) - returns path to Downloads folder;
        * */

        try {
            if(!queriesFromApp.exists()){
                FileOutputStream fos = new FileOutputStream(queriesFromApp);
                fos.write("".getBytes());
                fos.close();
            }

            FileInputStream fis = new FileInputStream(queriesFromApp);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            reader.close();
            String queriesFromAppString = sb.toString();
            fis.close();

            queriesFromAppString += "Date: " + getCurrentDate() +", " + getCurrentTime() +"\n" + query + "\n\n";
            FileOutputStream fos = new FileOutputStream(queriesFromApp);
            fos.write(queriesFromAppString.getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}



