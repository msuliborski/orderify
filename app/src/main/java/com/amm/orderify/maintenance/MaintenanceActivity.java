package com.amm.orderify.maintenance;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.amm.orderify.R;
import com.amm.orderify.maintenance.editors.*;

import static com.amm.orderify.helpers.LogFile.clearLog;

public class MaintenanceActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_maintenance_activity);


        Button clearLogButton = findViewById(R.id.ClearLogButton);
        clearLogButton.setOnClickListener(e -> clearLog());

        Button choseTablesButton = findViewById(R.id.ChoseTablesButton);
        choseTablesButton.setOnClickListener(e -> this.startActivity(new Intent(this, EditTablesActivity.class)));

        Button choseDishCategoriesButton = findViewById(R.id.ChoseDishCategoriesButton);
        choseDishCategoriesButton.setOnClickListener(e -> this.startActivity(new Intent(this, EditDishCategoriesActivity.class)));

        Button choseDishesButton = findViewById(R.id.ChoseDishesButton);
        choseDishesButton.setOnClickListener(e -> this.startActivity(new Intent(this, EditDishesActivity.class)));

        Button choseAddonCategoriesButton = findViewById(R.id.ChoseAddonCategoriesButton);
        choseAddonCategoriesButton.setOnClickListener(e -> this.startActivity(new Intent(this, EditAddonCategoriesActivity.class)));

        Button choseAddonsButton = findViewById(R.id.ChoseAddonsButton);
        choseAddonsButton.setOnClickListener(e -> this.startActivity(new Intent(this, EditAddonsActivity.class)));
    }
}
