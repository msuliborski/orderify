package com.amm.orderify.client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.amm.orderify.R;

public class WelcomeActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_welcome_activity);

        Button goToMenuButton = findViewById(R.id.GoToMenuButton);
        goToMenuButton.setOnClickListener(e -> {
            this.startActivity(new Intent(this, MenuActivity.class));
        });
    }
}
