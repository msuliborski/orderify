package com.amm.orderify;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.amm.orderify.helpers.JBDCDriver;
import java.sql.Statement;





public class MainActivity extends AppCompatActivity {

    String tmp;
    TextView RecordTextView;
    public static String error = "";

    Connection myConn;
    Statement myStatement;
    ResultSet resultSet;
    public static String classs = "com.mysql.jdbc.Driver";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecordTextView = findViewById(R.id.RecordTextView);

        try
        {
            //JBDCDriver.Initiate();
            //ResultSet results = JBDCDriver.ExecuteStatement("SELECT * FROM tab");

           // tmp=results.getString(0)+" "+ results.getString(1);

           // Connection con = JBDCDriver.CONN();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);


            Class.forName(classs);

            myConn = DriverManager.getConnection("jdbc:mysql://192.168.1.100:3306/orderify", "root", "1234");
            myStatement = myConn.createStatement();
            resultSet = myStatement.executeQuery("SELECT * FROM tab");
            while (resultSet.next()) {
                RecordTextView.append((resultSet.getString(1) + " " + resultSet.getString(2)));
            }


        } catch (Exception e)
        {
           e.printStackTrace();
           RecordTextView.setText(e.getMessage());
        }

        Button GetButton = findViewById(R.id.GetButton);
        GetButton.setOnClickListener(e ->
        {


            RecordTextView.setText(tmp);

        });




    }


}
