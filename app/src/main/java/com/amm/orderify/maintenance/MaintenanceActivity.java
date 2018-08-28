package com.amm.orderify.maintenance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.amm.orderify.R;
import com.amm.orderify.maintenance.adders.*;
import com.amm.orderify.maintenance.editors.*;

import static com.amm.orderify.helpers.LogFile.clearLog;

public class MaintenanceActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_maintenance_activity);


        Button clearLogButton = findViewById(R.id.ClearLogButton);
        clearLogButton.setOnClickListener(e -> clearLog());

        Button choseAddTableButton = findViewById(R.id.ChoseAddTableButton);
        choseAddTableButton.setOnClickListener(e -> this.startActivity(new Intent(this, AddTableActivity.class)));
        Button choseEditTableButton = findViewById(R.id.ChoseEditTableButton);
        choseEditTableButton.setOnClickListener(e -> this.startActivity(new Intent(this, EditTableActivity.class)));

        Button AddDishCategoryButton = findViewById(R.id.ChoseAddDishCategoryButton);
        AddDishCategoryButton.setOnClickListener(e -> this.startActivity(new Intent(this, AddDishCategoryActivity.class)));
        Button choseEditDishCategoryButton = findViewById(R.id.ChoseEditDishCategoryButton);
        choseEditDishCategoryButton.setOnClickListener(e -> this.startActivity(new Intent(this, EditDishCategoryActivity.class)));

        Button AddDishButton = findViewById(R.id.ChoseAddDishButton);
        AddDishButton.setOnClickListener(e -> this.startActivity(new Intent(this, AddDishActivity.class)));
        Button choseEditDishButton = findViewById(R.id.ChoseEditDishButton);
        choseEditDishButton.setOnClickListener(e -> this.startActivity(new Intent(this, EditDishActivity.class)));

        Button AddAddonCategoriesButton = findViewById(R.id.ChoseAddAddonCategoriesButton);
        AddAddonCategoriesButton.setOnClickListener(e -> this.startActivity(new Intent(this, AddAddonCategoryActivity.class)));
        Button choseEditAddonCategoriesButton = findViewById(R.id.ChoseEditAddonCategoriesButton);
        choseEditAddonCategoriesButton.setOnClickListener(e -> this.startActivity(new Intent(this, EditAddonCategoryActivity.class)));

        Button addAddonButton = findViewById(R.id.ChoseAddAddonButton);
        addAddonButton.setOnClickListener(e -> this.startActivity(new Intent(this, AddAddonActivity.class)));
        Button choseEditAddonButton = findViewById(R.id.ChoseEditAddonButton);
        choseEditAddonButton.setOnClickListener(e -> this.startActivity(new Intent(this, EditAddonActivity.class)));


    }
}
