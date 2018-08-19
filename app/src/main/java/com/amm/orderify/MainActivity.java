package com.amm.orderify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.amm.orderify.bar.OrdersActivity;
import com.amm.orderify.client.MenuActivity;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main_activity);

        InitiateConnection();
        ConnectToDatabase();

        Button GetButton = findViewById(R.id.BarButton);
        GetButton.setOnClickListener(e -> {
            Intent openMenuIntent = new Intent(MainActivity.this, OrdersActivity.class);
            this.startActivity(openMenuIntent);

        });

        Button AddButton = findViewById(R.id.ClientButton);
        AddButton.setOnClickListener(e -> {
            Intent openMenuIntent = new Intent(MainActivity.this, MenuActivity.class);
            this.startActivity(openMenuIntent);
        });

    }
}
