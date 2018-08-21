package com.amm.orderify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.amm.orderify.bar.TablesActivity;
import com.amm.orderify.client.MenuActivity;
import com.amm.orderify.maintenance.ChoseActivity;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main_activity);

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
