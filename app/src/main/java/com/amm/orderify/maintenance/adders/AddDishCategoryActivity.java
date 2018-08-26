package com.amm.orderify.maintenance.adders;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.DishCategory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class AddDishCategoryActivity extends AppCompatActivity {

    public LinearLayout dishCategoriesLinearLayout;
    static LayoutInflater dishCategoriesListInflater;
    public List<DishCategory> dishCategories = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_dishcategory_activity);


        try {
            ResultSet dishCategoriesRS = ExecuteQuery("SELECT * FROM dishCategories");
            while (dishCategoriesRS.next()) dishCategories.add(new DishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), null));
        } catch (SQLException ignored) {}

        dishCategoriesLinearLayout = findViewById(R.id.DishesLinearLayout);
        dishCategoriesListInflater = getLayoutInflater();


        updateDishCategoryList();




        EditText dishCategoryNameEditText = findViewById(R.id.DishCategoryNameEditText);

        Button addDishCategoryButton = findViewById(R.id.AddDishCategoryButton);
        addDishCategoryButton.setOnClickListener(e -> {
            try {
                ExecuteUpdate("INSERT INTO dishCategories (name)\n" +
                        "VALUES ('" + dishCategoryNameEditText.getText().toString() + "')");
                int newDishCategoryID = 0;
                ResultSet orderIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
                if(orderIDRS.next()) newDishCategoryID = orderIDRS.getInt(1);
                dishCategories.add(new DishCategory(newDishCategoryID, dishCategoryNameEditText.getText().toString(), null));
            } catch (SQLException ignored) { }
            Toast.makeText(this, "DishCategory added!", Toast.LENGTH_SHORT).show();
            //dishCategoryNameEditText.setText("");
            updateDishCategoryList();
            //this.startActivity(new Intent(this, EditActivity.class));
        });


    }

    @SuppressLint("SetTextI18n")
    public void updateDishCategoryList() {
        dishCategoriesLinearLayout.removeAllViews();

        for (int dishCategoryNumber = 0; dishCategoryNumber < dishCategories.size(); dishCategoryNumber++){
            View dishCategoryElement = dishCategoriesListInflater.inflate(R.layout.maintenance_dishcategory_element, null);

            TextView idTextView = dishCategoryElement.findViewById(R.id.IdTextView);
            idTextView.setText(dishCategories.get(dishCategoryNumber).id + "");

            TextView nameTextView = dishCategoryElement.findViewById(R.id.NameTextView);
            nameTextView.setText(dishCategories.get(dishCategoryNumber).name);

            ImageButton deleteButton = dishCategoryElement.findViewById(R.id.ActionButton);
            final int finalDishCategoryNumber = dishCategoryNumber;
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM dishCategories WHERE ID = " + dishCategories.get(finalDishCategoryNumber).id);
                    dishCategories.remove(dishCategories.get(finalDishCategoryNumber));
                    updateDishCategoryList();
                    Toast.makeText(this, "DishCategory deleted!", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "DishCategory " + dishCategories.get(finalDishCategoryNumber).name + " has addons assigned!", Toast.LENGTH_SHORT).show();
                }
            });

            dishCategoriesLinearLayout.addView(dishCategoryElement);
        }
    }
}
