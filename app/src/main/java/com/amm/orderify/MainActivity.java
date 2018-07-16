package com.amm.orderify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amm.orderify.helpers.JBDCDriver;

import java.sql.ResultSet;
import java.sql.SQLException;



public class MainActivity extends AppCompatActivity {

    String tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            JBDCDriver.Initiate();
            ResultSet results = JBDCDriver.ExecuteStatement("SELECT * FROM tab");


            while (results.next())
            {
                tmp+=(results.getString(0)+" "+ results.getString(1));
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        Button GetButton = (Button)findViewById(R.id.GetButton);
        GetButton.setOnClickListener(e ->
        {
            TextView RecordTextView = (TextView)findViewById(R.id.RecordTextView);

            RecordTextView.setText(tmp);

        });




    }


}
