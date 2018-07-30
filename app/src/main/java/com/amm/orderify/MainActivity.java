package com.amm.orderify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.amm.orderify.helpers.JBDCDriver;






public class MainActivity extends AppCompatActivity {

    String tmp;
    TextView RecordTextView;
    public static String error = "";


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

            Connection con = JBDCDriver.CONN();
            if (con == null)
            {
                RecordTextView.append(" " + error);
            }
            else
                RecordTextView.setText("Polaczono");


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
