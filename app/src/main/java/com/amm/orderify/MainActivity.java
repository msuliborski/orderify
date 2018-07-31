package com.amm.orderify;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MainActivity extends AppCompatActivity {

    String message;
    TextView RecordTextView;
    EditText RecordGetText;
    ResultSet resultSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecordTextView = findViewById(R.id.RecordTextView);
        RecordGetText = findViewById(R.id.RecordGetText);

        InitiateConnection();
        ConnectToDatabase();

        Button GetButton = findViewById(R.id.GetButton);
        GetButton.setOnClickListener(e -> {
            message = null;
            try {
                resultSet = ExecuteQuery("SELECT * FROM dishes");

                while (resultSet.next()) {
                    message += (resultSet.getString(1) + " " + resultSet.getString(2)) + "\n";
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                RecordTextView.setText(e2.getMessage());
            }
            RecordTextView.setText(message);
        });

        Button AddButton = findViewById(R.id.SendButton);
        AddButton.setOnClickListener(e -> {
            message = null;
            try {
                ExecuteUpdate(
                        "INSERT INTO dishes (name, descS, descL, categoryID) " +
                        "VALUES ('" + RecordGetText.getText().toString() + "', 'short', 'long', 1);");
                resultSet = ExecuteQuery("SELECT * FROM dishes");
                while (resultSet.next()) {
                    message += (resultSet.getString(1) + " " + resultSet.getString(2)) + "\n";
                }
                //message = RecordGetText.getText().toString();
            } catch (SQLException e1) {
                e1.printStackTrace();
                RecordTextView.setText(e1.getMessage());
            }
            if (message != null) RecordTextView.setText(message);
        });

        Button GoToMenuButton = findViewById(R.id.GoToMenuButton);
        GoToMenuButton.setOnClickListener(e -> {
            Intent openMenuIntent = new Intent(MainActivity.this, MenuActivity.class);
            this.startActivity(openMenuIntent);
        });
    }
}
