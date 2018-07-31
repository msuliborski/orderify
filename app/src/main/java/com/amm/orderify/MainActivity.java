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
import java.sql.SQLException;

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
            resultSet = ExecuteQuery("SELECT * FROM dishes");

            while (resultSet.next()) {
                message += (resultSet.getString(1) + " " + resultSet.getString(2) + "\n\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            RecordTextView.setText(e.getMessage());
        }

        Button GetButton = findViewById(R.id.GetButton);
        GetButton.setOnClickListener(e -> {
            RecordTextView.setText(message);
        });

        Button AddButton = findViewById(R.id.SendButton);
        AddButton.setOnClickListener(e -> {
            try {
                ExecuteQuery(
                        "INSERT INTO `dishes` (`name`, `descS`, `descL`, `categoryID`)\n" +
                        "VALUES (" + RecordTextView.getText() + ", 'short', 'long', 1);"
                );
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        Button GoToMenuButton = findViewById(R.id.GoToMenuButton);
        GoToMenuButton.setOnClickListener(e -> {
            Intent openMenuIntent = new Intent(MainActivity.this, MenuActivity.class);
            this.startActivity(openMenuIntent);
        });
    }
}
