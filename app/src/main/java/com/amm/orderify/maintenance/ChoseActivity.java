package com.amm.orderify.maintenance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.amm.orderify.R;
import com.amm.orderify.maintenance.adders.*;

public class ChoseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_chose_activity);


        Button AddTableButton = findViewById(R.id.ChoseTableButton);
        AddTableButton.setOnClickListener(e -> {
            this.startActivity(new Intent(this, AddTableActivity.class));
        });

        Button AddDishCategoryButton = findViewById(R.id.ChoseDishCategoryButton);
        AddDishCategoryButton.setOnClickListener(e -> {
            this.startActivity(new Intent(this, AddDishCategoryActivity.class));
        });

        Button AddDishButton = findViewById(R.id.ChoseDishButton);
        AddDishButton.setOnClickListener(e -> {
            this.startActivity(new Intent(this, AddDishActivity.class));
        });
        Button AddAddonCategoriesButton = findViewById(R.id.ChoseAddonCategoriesButton);
        AddAddonCategoriesButton.setOnClickListener(e -> {
            this.startActivity(new Intent(this, AddAddonCategoryActivity.class));
        });
        Button addAddonButton = findViewById(R.id.ChoseAddonButton);
        addAddonButton.setOnClickListener(e -> {
            this.startActivity(new Intent(this, AddAddonActivity.class));
        });


    }
}
