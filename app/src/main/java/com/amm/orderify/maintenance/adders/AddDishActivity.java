package com.amm.orderify.maintenance.adders;

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
import com.amm.orderify.helpers.data.Dish;
import com.amm.orderify.helpers.data.DishCategory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class AddDishActivity extends AppCompatActivity {

    public LinearLayout dishesLinearLayout;
    public LinearLayout addonCategoriesLinearLayout;
    public LinearLayout chosenAddonCategoriesLinearLayout;
    public Spinner dishCategoriesSpinner;

    static LayoutInflater dishesInflater;
    static LayoutInflater addonCategoriesInflater;
    static LayoutInflater chosenAddonCategoriesInflater;
    static LayoutInflater dishCategoriesInflater;

    public EditText nameEditText;
    public EditText priceEditText;
    public EditText descSEditText;
    public EditText descLEditText;

    public List<Dish> dishes = new ArrayList<>();
    public List<DishCategory> dishCategories = new ArrayList<>();
    public List<AddonCategory> addonCategories = new ArrayList<>();
    public List<AddonCategory> chosenAddonCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_add_dish_activity);

        try {
            ResultSet dishesRS = ExecuteQuery("SELECT * FROM dishes");
            while (dishesRS.next()) dishes.add(new Dish(dishesRS.getInt("ID"), dishesRS.getString("name"), dishesRS.getFloat("price"), dishesRS.getString("descS"), dishesRS.getString("descL"), dishesRS.getInt("dishCategoryID") , null));

            ResultSet addonCategoriesRS = ExecuteQuery("SELECT * FROM addonCategories");
            while (addonCategoriesRS.next()) addonCategories.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), null));

            ResultSet dishCategoriesRS = ExecuteQuery("SELECT * FROM dishCategories");
            while (dishCategoriesRS.next()) dishCategories.add(new DishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), null));
        } catch (SQLException ignored) {}

        dishesLinearLayout = findViewById(R.id.DishesLinearLayout);
        dishCategoriesSpinner = findViewById(R.id.DishCategoriesSpinner);
        addonCategoriesLinearLayout = findViewById(R.id.AddonCategoriesLinearLayout);
        chosenAddonCategoriesLinearLayout = findViewById(R.id.ChosenAddonCategoriesLinearLayout);

        nameEditText = findViewById(R.id.NameEditText);
        priceEditText = findViewById(R.id.PriceEditText);
        descSEditText = findViewById(R.id.DescSEditText);
        descLEditText = findViewById(R.id.DescLEditText);

        dishesInflater = getLayoutInflater();
        updateDishesList();

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
                updateDishesList();
                Toast.makeText(this, "Dish added!", Toast.LENGTH_SHORT).show();
            } catch (SQLException ignored) { }
        });
    }

    public void updateDishesList() {
        dishes.sort(Comparator.comparing(object -> String.valueOf(object.id))); //sort
        dishes.sort(Comparator.comparing(object -> String.valueOf(object.dishCategoryID))); //sort
        dishesLinearLayout.removeAllViews();
        for (int dishNumber = 0; dishNumber < dishes.size(); dishNumber++){
            Dish dish = dishes.get(dishNumber);
            View dishElement = dishesInflater.inflate(R.layout.maintenance_dish_element, null);

            TextView idTextView = dishElement.findViewById(R.id.TablesTextView);
            idTextView.setText(dish.getIdString());

            TextView nameTextView = dishElement.findViewById(R.id.NameTextView);
            nameTextView.setText(dish.name);

            TextView dishCategoryNameTextView = dishElement.findViewById(R.id.DishCategoryNameTextView);
            dishCategoryNameTextView.setText(dish.dishCategoryID + "");

            TextView descSTextView = dishElement.findViewById(R.id.DescSTextView);
            descSTextView.setText(dish.descS);

            TextView descLTextView = dishElement.findViewById(R.id.DescLTextView);
            descLTextView.setText(dish.descL);

            TextView priceTextView = dishElement.findViewById(R.id.PriceTextView);
            priceTextView.setText(dish.getPriceString());

            int finalDishNumber = dishNumber;
            ImageButton deleteButton = dishElement.findViewById(R.id.ActionButton);
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM addonCategoriesToDishes WHERE dishID = " + dish.id);
                    ExecuteUpdate("DELETE FROM dishes WHERE ID = " + dish.id);
                    dishes.remove(dishes.get(finalDishNumber));
                    updateDishesList();
                } catch (SQLException e) {
                    Log.wtf("adfs", e.getMessage()+" "+e.getErrorCode());
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "Dish " + dishes.get(finalDishNumber).name + " has wishes/orders assigned!", Toast.LENGTH_SHORT).show();
                }
            });
            dishesLinearLayout.addView(dishElement);
        }
    }
    public void updateDishCategoryList() {

        String[] items = new String[dishCategories.size()];
        for (int dishCategoryNumber = 0; dishCategoryNumber < dishCategories.size(); dishCategoryNumber++){
            items[dishCategoryNumber] = dishCategories.get(dishCategoryNumber).name;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dishCategoriesSpinner.setAdapter(adapter);
    }

    public void updateAddonCategoryList() {
        addonCategories.sort(Comparator.comparing(object -> String.valueOf(object.name))); //sort
        addonCategoriesLinearLayout.removeAllViews();

        for (int addonCategoryNumber = 0; addonCategoryNumber < addonCategories.size(); addonCategoryNumber++){
            View addonCategoryElement = addonCategoriesInflater.inflate(R.layout.maintenance_addoncategory_element, null);

            TextView idTextView = addonCategoryElement.findViewById(R.id.TablesTextView);
            idTextView.setText(addonCategories.get(addonCategoryNumber).getIdString());

            TextView nameTextView = addonCategoryElement.findViewById(R.id.NameTextView);
            nameTextView.setText(addonCategories.get(addonCategoryNumber).name);

            TextView descriptionTextView = addonCategoryElement.findViewById(R.id.DescriptionTextView);
            descriptionTextView.setText(addonCategories.get(addonCategoryNumber).description);

            TextView multiChoiceTextView = addonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            String yes = "YES", no = "NO";
            if (addonCategories.get(addonCategoryNumber).multiChoice) multiChoiceTextView.setText(yes);
            else multiChoiceTextView.setText(no);

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

    public void updateChosenAddonCategoryList() {
        chosenAddonCategories.sort(Comparator.comparing(object -> String.valueOf(object.name))); //sort
        chosenAddonCategoriesLinearLayout.removeAllViews();

        for (int chosenAddonCategoryNumber = 0; chosenAddonCategoryNumber < chosenAddonCategories.size(); chosenAddonCategoryNumber++){
            View chosenAddonCategoryElement = chosenAddonCategoriesInflater.inflate(R.layout.maintenance_addoncategory_element, null);

            TextView idTextView = chosenAddonCategoryElement.findViewById(R.id.TablesTextView);
            idTextView.setText(chosenAddonCategories.get(chosenAddonCategoryNumber).getIdString());

            TextView nameTextView = chosenAddonCategoryElement.findViewById(R.id.NameTextView);
            nameTextView.setText(chosenAddonCategories.get(chosenAddonCategoryNumber).name);

            TextView descriptionTextView = chosenAddonCategoryElement.findViewById(R.id.DescriptionTextView);
            descriptionTextView.setText(chosenAddonCategories.get(chosenAddonCategoryNumber).description);

            TextView multiChoiceTextView = chosenAddonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            String yes = "YES", no = "NO";
            if (chosenAddonCategories.get(chosenAddonCategoryNumber).multiChoice) multiChoiceTextView.setText(yes);
            else multiChoiceTextView.setText(no);

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
