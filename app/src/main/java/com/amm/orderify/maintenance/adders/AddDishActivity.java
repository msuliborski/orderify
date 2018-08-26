package com.amm.orderify.maintenance.adders;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.AddonCategory;
import com.amm.orderify.helpers.data.DishCategory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class AddDishActivity extends AppCompatActivity {

    public LinearLayout addonCategoriesLinearLayout;
    public LinearLayout chosenAddonCategoriesLinearLayout;
    public Spinner dishCategoriesSpinner;

    static LayoutInflater addonCategoriesInflater;
    static LayoutInflater chosenAddonCategoriesInflater;
    static LayoutInflater dishCategoriesInflater;

    public EditText nameEditText;
    public EditText priceEditText;
    public EditText descSEditText;
    public EditText descLEditText;

    public List<DishCategory> dishCategories = new ArrayList<>();
    public List<AddonCategory> addonCategories = new ArrayList<>();
    public List<AddonCategory> chosenAddonCategories = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_dish_activity);


        try {
            ResultSet addonCategoriesRS = ExecuteQuery("SELECT * FROM addonCategories");
            while (addonCategoriesRS.next()) addonCategories.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("name"), addonCategoriesRS.getBoolean("multiChoice"), null));

            ResultSet dishCategoriesRS = ExecuteQuery("SELECT * FROM dishCategories");
            while (dishCategoriesRS.next()) dishCategories.add(new DishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), null));
        } catch (SQLException ignored) {}


        dishCategoriesSpinner = findViewById(R.id.DishCategoriesSpinner);
        addonCategoriesLinearLayout = findViewById(R.id.AddonCategoriesLinearLayout);
        chosenAddonCategoriesLinearLayout = findViewById(R.id.ChosenAddonCategoriesLinearLayout);

        nameEditText = findViewById(R.id.NameEditText);
        priceEditText = findViewById(R.id.PriceEditText);
        descSEditText = findViewById(R.id.DescSEditText);
        descLEditText = findViewById(R.id.DescLEditText);


        dishCategoriesInflater = getLayoutInflater();
        updateDishCategoryList();

        addonCategoriesInflater = getLayoutInflater();
        updateAddonCategoryList();

        chosenAddonCategoriesInflater = getLayoutInflater();
        updateChosenAddonCategoryList();


        Button addDishButton = findViewById(R.id.AddDishButton);
        addDishButton.setOnClickListener(e -> {
            try {
                ExecuteUpdate("INSERT INTO dishes (name, price, descS, descL, dishCategoryID)\n" +
                        "VALUES  ('"+ nameEditText.getText() + "', "+ priceEditText.getText() + ", '" + descSEditText.getText() + "', '"+ descLEditText.getText() + "', "+ dishCategories.get((int) dishCategoriesSpinner.getSelectedItemId()).id + ")");

                int newDishID = 0;
                ResultSet orderIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
                if(orderIDRS.next()) newDishID = orderIDRS.getInt(1);

                for(int addonCategoryNumber = 0; addonCategoryNumber < chosenAddonCategories.size(); addonCategoryNumber++) {
                    ExecuteUpdate("INSERT INTO addonCategoriesToDishes (dishID, addonCategoryID) \n" +
                            "VALUES  (" + newDishID + ", " + chosenAddonCategories.get(addonCategoryNumber).id + ")");
                }




            } catch (SQLException ignored) { }
            Toast.makeText(this, "Dish added!", Toast.LENGTH_SHORT).show();
            //dishCategoryNameEditText.setText("");
            //updateDishCategoryList();
            //this.startActivity(new Intent(this, EditActivity.class));
        });


    }

    @SuppressLint("SetTextI18n")
    public void updateDishCategoryList() {

        String[] items = new String[dishCategories.size()];
        for (int dishCategoryNumber = 0; dishCategoryNumber < dishCategories.size(); dishCategoryNumber++){
            items[dishCategoryNumber] = dishCategories.get(dishCategoryNumber).name;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dishCategoriesSpinner.setAdapter(adapter);
    }
    @SuppressLint("SetTextI18n")
    public void updateAddonCategoryList() {
        addonCategoriesLinearLayout.removeAllViews();

        for (int addonCategoryNumber = 0; addonCategoryNumber < addonCategories.size(); addonCategoryNumber++){
            View addonCategoryElement = addonCategoriesInflater.inflate(R.layout.maintenance_addoncategory_element, null);

            TextView idTextView = addonCategoryElement.findViewById(R.id.IdTextView);
            idTextView.setText(addonCategories.get(addonCategoryNumber).id + "");

            TextView nameTextView = addonCategoryElement.findViewById(R.id.NameTextView);
            nameTextView.setText(addonCategories.get(addonCategoryNumber).name);
            Log.wtf("eff", addonCategories.get(addonCategoryNumber).name);

            TextView multiChoiceTextView = addonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            if (addonCategories.get(addonCategoryNumber).multiChoice) multiChoiceTextView.setText("YES");
            else multiChoiceTextView.setText("NO");

            ImageButton actionButton = addonCategoryElement.findViewById(R.id.ActionButton);
            int finalAddonCategoryNumber = addonCategoryNumber;
            actionButton.setOnClickListener(v -> {
                chosenAddonCategories.add(addonCategories.get(finalAddonCategoryNumber));
                addonCategories.remove(addonCategories.get(finalAddonCategoryNumber));
                updateAddonCategoryList();
                updateChosenAddonCategoryList();
            });

            addonCategoriesLinearLayout.addView(addonCategoryElement);
        }
    }
    @SuppressLint("SetTextI18n")
    public void updateChosenAddonCategoryList() {
        chosenAddonCategoriesLinearLayout.removeAllViews();

        for (int chosenAddonCategoryNumber = 0; chosenAddonCategoryNumber < chosenAddonCategories.size(); chosenAddonCategoryNumber++){
            View chosenAddonCategoryElement = chosenAddonCategoriesInflater.inflate(R.layout.maintenance_addoncategory_element, null);

            TextView idTextView = chosenAddonCategoryElement.findViewById(R.id.IdTextView);
            idTextView.setText(chosenAddonCategories.get(chosenAddonCategoryNumber).id + "");

            TextView nameTextView = chosenAddonCategoryElement.findViewById(R.id.NameTextView);
            nameTextView.setText(chosenAddonCategories.get(chosenAddonCategoryNumber).name);

            TextView multiChoiceTextView = chosenAddonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            if (chosenAddonCategories.get(chosenAddonCategoryNumber).multiChoice) multiChoiceTextView.setText("YES");
            else multiChoiceTextView.setText("NO");

            ImageButton actionButton = chosenAddonCategoryElement.findViewById(R.id.ActionButton);
            int finalChosenAddonCategoryNumber = chosenAddonCategoryNumber;
            actionButton.setOnClickListener(v -> {
                addonCategories.add(chosenAddonCategories.get(finalChosenAddonCategoryNumber));
                chosenAddonCategories.remove(chosenAddonCategories.get(finalChosenAddonCategoryNumber));
                updateAddonCategoryList();
                updateChosenAddonCategoryList();
            });

            chosenAddonCategoriesLinearLayout.addView(chosenAddonCategoryElement);
        }
    }
}
