package com.amm.orderify;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.amm.orderify.bar.TablesActivity;
import com.amm.orderify.client.MenuActivity;
import com.amm.orderify.maintenance.ChoseActivity;

import static com.amm.orderify.helpers.JBDCDriver.*;
import static com.amm.orderify.helpers.TimeAndDate.*;

public class MainActivity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main_activity);

        //ask for permissions
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);

        InitiateConnection();
        ConnectToDatabase();

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
