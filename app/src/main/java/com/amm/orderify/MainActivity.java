package com.amm.orderify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.amm.orderify.bar.OrdersActivity;
import com.amm.orderify.client.MenuActivity;
import com.amm.orderify.client.SummaryActivity;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main_activity);

        InitiateConnection();
        ConnectToDatabase();

        Button BarButton = findViewById(R.id.BarButton);
        BarButton.setOnClickListener(e -> {
            this.startActivity(new Intent(MainActivity.this, OrdersActivity.class));

        });

        Button ClientButton = findViewById(R.id.ClientButton);
        ClientButton.setOnClickListener(e -> {
            this.startActivity(new Intent(MainActivity.this, MenuActivity.class));
        });

    }
}
