package com.amm.orderify;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.amm.orderify.bar.TablesActivity;
import com.amm.orderify.client.MenuActivity;
import com.amm.orderify.helpers.data.Client;
import com.amm.orderify.maintenance.ChoseActivity;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MainActivity extends AppCompatActivity {


    public static Context context;
    public static int thisClientID = 1;
    public static Client client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main_activity);

        context = this.getApplicationContext();

        InitiateConnection();
        ConnectToDatabase();

        //get table
        //??

        //get client
        try {
            ResultSet clientRS = ExecuteQuery("SELECT * FROM clients WHERE ID = " + thisClientID);
            if(clientRS.next()) client = new Client(thisClientID, clientRS.getInt("number"), clientRS.getInt("state"), null);
        } catch (SQLException ignore) {}

        //ask for permissions
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);


        Button barButton = findViewById(R.id.BarButton);
        barButton.setOnClickListener(e -> {
            this.startActivity(new Intent(MainActivity.this, TablesActivity.class));

        });

        Button clientButton = findViewById(R.id.ClientButton);
        clientButton.setOnClickListener(e -> {
            this.startActivity(new Intent(MainActivity.this, MenuActivity.class));
        });

        Button addButton = findViewById(R.id.AddButton);
        addButton.setOnClickListener(e -> {
            this.startActivity(new Intent(MainActivity.this, ChoseActivity.class));
        });



    }

}
