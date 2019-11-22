package com.amm.orderify.helpers;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.amm.orderify.helpers.TimeAndDate.getCurrentDate;
import static com.amm.orderify.helpers.TimeAndDate.getCurrentTime;

public class LogFile {

    private static File queriesFromApp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "queriesFromApp.txt");

    public static void writeToLog(String query){
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

    public static void clearLog(){
        try {
            FileOutputStream fos = new FileOutputStream(queriesFromApp);
            fos.write("".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
