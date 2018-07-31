package com.amm.orderify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.sql.ResultSet;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MainActivity extends AppCompatActivity {

    String message;
    TextView RecordTextView;

    ResultSet resultSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecordTextView = findViewById(R.id.RecordTextView);

        try {
            InitiateConnection();
            ConnectToDatabase();
            resultSet = ExecuteQuery("SELECT * FROM names");

            while (resultSet.next()) {
                message += (resultSet.getString(1) + " " + resultSet.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            RecordTextView.setText(e.getMessage());
        }

        Button GetButton = findViewById(R.id.GetButton);
        GetButton.setOnClickListener(e -> {
            RecordTextView.setText(message);
        });
    }


}
