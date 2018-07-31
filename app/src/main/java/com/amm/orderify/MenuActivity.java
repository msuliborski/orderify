package com.amm.orderify;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity
{

    Button GoToSummaryButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        GoToSummaryButton = findViewById(R.id.GoToSummaryButton);
        GoToSummaryButton.setOnClickListener(e -> {
            Intent openSummaryIntent = new Intent(MenuActivity.this, SummaryActivity.class);
            this.startActivity(openSummaryIntent);

        });


    }





}
