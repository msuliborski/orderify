package com.amm.orderify;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.sql.ResultSet;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MainActivity extends AppCompatActivity {

    String message;
    TextView RecordTextView;
    Button GoToMenuButton;

    ResultSet resultSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecordTextView = findViewById(R.id.RecordTextView);
        GoToMenuButton = findViewById(R.id.GoToMenuButton);





        try {
            InitiateConnection();
            ConnectToDatabase();
            resultSet = ExecuteQuery("SELECT * FROM tab");

            while (resultSet.next()) {
                message += (resultSet.getString(1) + " " + resultSet.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            RecordTextView.setText(e.getMessage());
        }

        Button GetButton = findViewById(R.id.GetButton);
        GetButton.setOnClickListener(e ->
        {
            RecordTextView.setText(message);
        });

        GoToMenuButton.setOnClickListener(e ->
        {
            Intent openMenuIntent = new Intent(MainActivity.this, MenuActivity.class);
            this.startActivity(openMenuIntent);
        });

    }


}
