package com.amm.orderify.maintenance.editors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
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
import com.amm.orderify.helpers.data.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class EditDishesActivity extends AppCompatActivity {

    public LinearLayout dishesLinearLayout;
    public LinearLayout addonCategoriesLinearLayout;
    public LinearLayout chosenAddonCategoriesLinearLayout;
    public Spinner dishCategoriesSpinner;

    public EditText nameEditText;
    public EditText priceEditText;
    public EditText descSEditText;
    public EditText descLEditText;

    Button actionButton;
    Button cancelButton;

    ArrayMap<Integer,Dish> dishes = new ArrayMap<>();
    ArrayMap<Integer,DishCategory> dishCategories = new ArrayMap<>();
    ArrayMap<Integer,AddonCategory> addonCategories = new ArrayMap<>();
    ArrayMap<Integer,AddonCategory> chosenAddonCategories = new ArrayMap<>();

    int editedDishID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_edit_dishes_activity);


        dishesLinearLayout = findViewById(R.id.DishesLinearLayout);
        dishCategoriesSpinner = findViewById(R.id.DishCategoriesSpinner);
        addonCategoriesLinearLayout = findViewById(R.id.AddonCategoriesLinearLayout);
        chosenAddonCategoriesLinearLayout = findViewById(R.id.ChosenAddonCategoriesLinearLayout);

        addonCategories = getAddonCategories();
        nameEditText = findViewById(R.id.NameEditText);
        priceEditText = findViewById(R.id.PriceEditText);
        descSEditText = findViewById(R.id.DescSEditText);
        descLEditText = findViewById(R.id.DescLEditText);

        updateDishesList();
        updateDishCategoryList();
        updateAddonCategoryList();
        updateChosenAddonCategoryList();

        actionButton = findViewById(R.id.ActionButton);
        actionButton.setOnClickListener(e -> {
            if(editedDishID == 0) {
                try {
                    ExecuteUpdate("INSERT INTO dishes (name, price, descS, descL, dishCategoryID)\n" +
                            "VALUES  ('"+ nameEditText.getText() + "', "+ priceEditText.getText() + ", '" + descSEditText.getText() + "', '"+ descLEditText.getText() + "', "+ ((DishCategory)(dishCategoriesSpinner.getSelectedItem())).id + ")");

                    int newDishID = 0;
                    ResultSet orderIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
                    if(orderIDRS.next()) newDishID = orderIDRS.getInt(1);

                    for(int addonCategoryNumber = 0; addonCategoryNumber < chosenAddonCategories.size(); addonCategoryNumber++) {
                        ExecuteUpdate("INSERT INTO addonCategoriesToDishes (dishID, addonCategoryID) \n" +
                                "VALUES  (" + newDishID + ", " + chosenAddonCategories.valueAt(addonCategoryNumber).id + ")");
                    }
                    updateDishesList();
                    Toast.makeText(this, "Dish added!", Toast.LENGTH_SHORT).show();
                } catch (SQLException d) { Log.wtf("SQL Exception", d.getMessage()); }
            } else {
                try {
                    ExecuteUpdate("UPDATE dishes SET " + "name = '" + nameEditText.getText() + "', " + "price = " + priceEditText.getText() + ", " + "descS = '" + descSEditText.getText() + "', " +
                            "descL = '" + descLEditText.getText() + "', dishCategoryID = " + ((DishCategory)(dishCategoriesSpinner.getSelectedItem())).id + " WHERE ID = " + editedDishID);

                    ExecuteUpdate("DELETE FROM addonCategoriesToDishes WHERE dishID = " + editedDishID);

                    for(int addonCategoryNumber = 0; addonCategoryNumber < chosenAddonCategories.size(); addonCategoryNumber++) {
                        ExecuteUpdate("INSERT INTO addonCategoriesToDishes (dishID, addonCategoryID) \n" +
                                "VALUES  (" + editedDishID + ", " + chosenAddonCategories.valueAt(addonCategoryNumber).id + ")");
                    }
                    Toast.makeText(this, "Dish edited!", Toast.LENGTH_SHORT).show();
                    cancelButton.callOnClick();
                    updateDishesList();
                } catch (SQLException d) { Log.wtf("SQLException", d.getMessage()); }
            }
        });

        cancelButton = findViewById(R.id.CancelButton);
        cancelButton.setVisibility(View.GONE);
        cancelButton.setOnClickListener(e -> {
            dishCategoriesSpinner.setSelection(0);
            addonCategories = getAddonCategories();
            chosenAddonCategories = new ArrayMap<>();
            updateAddonCategoryList();
            updateChosenAddonCategoryList();
            nameEditText.setText("");
            priceEditText.setText("");
            descSEditText.setText("");
            descLEditText.setText("");
            editedDishID = 0;
            cancelButton.setVisibility(View.GONE);
            actionButton.setText("Add dish");
        });
    }


    private void getDishes(){
        dishes = new ArrayMap<>();
        ArrayMap<Integer,AddonCategory> addonCategories = new ArrayMap<>();
        try {
            Statement dishesS = getConnection().createStatement();
            ResultSet dishesRS = dishesS.executeQuery("SELECT dishes.ID, dishes.number, dishes.name, dishes.price, dishes.descS, dishes.descL, dishes.dishCategoryID, dishCategories.name AS dishCategoryName FROM dishes \n" +
                    "JOIN dishCategories ON dishCategories.ID = dishes.dishCategoryID");
            while (dishesRS.next()) {
                Statement addonCategoriesS = getConnection().createStatement();
                ResultSet addonCategoriesRS = addonCategoriesS.executeQuery("SELECT addonCategories.* FROM addonCategoriesToDishes\n" +
                        "JOIN addonCategories ON addonCategories.ID = addonCategoriesToDishes.addonCategoryID\n" +
                        "WHERE dishID = " + dishesRS.getInt("ID"));
                while (addonCategoriesRS.next()) {
                    addonCategories.put(addonCategoriesRS.getInt("ID"), new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), new ArrayMap<>()));
                    Log.wtf("dupa", addonCategoriesRS.getInt("ID")+"");
                }
                dishes.put(dishesRS.getInt("ID"), new Dish(dishesRS.getInt("ID"), dishesRS.getInt("number"), dishesRS.getString("name"), dishesRS.getFloat("price"), dishesRS.getString("descS"), dishesRS.getString("descL"), dishesRS.getInt("dishCategoryID"), addonCategories));
                dishes.get(dishesRS.getInt("ID")).dishCategoryName = dishesRS.getString("dishCategoryName");

                addonCategories = new ArrayMap<>();
            }
        } catch (SQLException e) { Log.wtf("SQLException", e.getMessage() + "");}
    }

    private ArrayMap<Integer,AddonCategory> getAddonCategories(){
        ArrayMap<Integer,AddonCategory> addonCategories = new ArrayMap<>();
        try {
            ResultSet addonCategoriesRS = ExecuteQuery("SELECT * FROM addonCategories");
            while (addonCategoriesRS.next()) addonCategories.put(addonCategoriesRS.getInt("ID"), new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), new ArrayMap<>()));
        } catch (SQLException e) { Log.wtf("SQLException", e.getMessage() + "");}
        return addonCategories;
    }

    private ArrayMap<Integer,DishCategory> getDishCategories(){
        ArrayMap<Integer, DishCategory> dishCategories = new ArrayMap<>();
        try {
            ResultSet dishCategoriesRS = ExecuteQuery("SELECT * FROM dishCategories");
            while (dishCategoriesRS.next()) dishCategories.put(dishCategoriesRS.getInt("ID"), new DishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), new ArrayMap<>()));
        } catch (SQLException e) { Log.wtf("SQLException", e.getMessage() + "");}
        return dishCategories;
    }

    public void updateDishesList() {
        getDishes();
        //dishes.sort(Comparator.comparing(object -> String.valueOf(object.id))); //sort
        //dishes.sort(Comparator.comparing(object -> String.valueOf(object.dishCategoryID))); //sort
        if(dishesLinearLayout != null) dishesLinearLayout.removeAllViews();
        dishesLinearLayout = findViewById(R.id.DishesLinearLayout);
        for (int dishNumber = 0; dishNumber < dishes.size(); dishNumber++){
            View dishElement = getLayoutInflater().inflate(R.layout.maintenance_element_dish, null);
            TextView idTextView = dishElement.findViewById(R.id.IdTextView);
            TextView nameTextView = dishElement.findViewById(R.id.NameTextView);
            TextView dishCategoryTextView = dishElement.findViewById(R.id.DishCategoryTextView);
            TextView descSTextView = dishElement.findViewById(R.id.DescSTextView);
            TextView descLTextView = dishElement.findViewById(R.id.DescLTextView);
            TextView priceTextView = dishElement.findViewById(R.id.PriceTextView);

            Dish dish = dishes.valueAt(dishNumber);

            priceTextView.setText(dish.getPriceString());
            descLTextView.setText(dish.descL);
            descSTextView.setText(dish.descS);
            dishCategoryTextView.setText(dish.dishCategoryName);
            nameTextView.setText(dish.name);
            idTextView.setText(dish.getIdString());

            ImageButton editButton = dishElement.findViewById(R.id.EditButton);
            editButton.setOnClickListener(v -> {

                chosenAddonCategories = dish.addonCategories;
                addonCategories = getAddonCategories();
                ArrayMap<Integer,AddonCategory> tempAddonCategories = new ArrayMap<>(addonCategories);

                for (int i = 0; i < addonCategories.size(); i++)
                    if (chosenAddonCategories.containsKey(addonCategories.valueAt(i).id))
                        tempAddonCategories.remove(addonCategories.valueAt(i).id);
                addonCategories = tempAddonCategories;
                updateAddonCategoryList();
                updateChosenAddonCategoryList();
                for(int i = 0; i < dishCategories.size(); i++)
                    if(dishCategoriesSpinner.getItemAtPosition(i).equals(dishCategories.get(dish.dishCategoryID))){
                        dishCategoriesSpinner.setSelection(i); break;}

                nameEditText.setText(dish.name);
                priceEditText.setText(dish.getPurePriceString());
                descSEditText.setText(dish.descS);
                descLEditText.setText(dish.descL);
                editedDishID = dish.id;
                actionButton.setText("Edit dish");
                cancelButton.setVisibility(View.VISIBLE);
            });
            ImageButton deleteButton = dishElement.findViewById(R.id.DeleteButton);
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM addonCategoriesToDishes WHERE dishID = " + dish.id);
                    ExecuteUpdate("DELETE FROM dishes WHERE ID = " + dish.id);
                    dishes.remove(dish.id);
                    updateDishesList();
                } catch (SQLException e) {
                    Log.wtf("SQLException", e.getMessage() + "" + e.getErrorCode());
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "Dish " + dish.name + " has wishes/orders assigned!", Toast.LENGTH_SHORT).show();
                }
            });
            dishesLinearLayout.addView(dishElement);
        }
    }
    public void updateDishCategoryList() {
        dishCategories = getDishCategories();
        List<DishCategory> spinnerDishCategories = new ArrayList<>();
        for (int dishCategoryNumber = 0; dishCategoryNumber < dishCategories.size(); dishCategoryNumber++){
            spinnerDishCategories.add(dishCategories.valueAt(dishCategoryNumber));
        }
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerDishCategories);
        dishCategoriesSpinner.setAdapter(adapter);
    }

    public void updateAddonCategoryList() {
        //addonCategories.sort(Comparator.comparing(object -> String.valueOf(object.name))); //sort
        addonCategoriesLinearLayout.removeAllViews();
        for (int addonCategoryNumber = 0; addonCategoryNumber < addonCategories.size(); addonCategoryNumber++){
            View addonCategoryElement = getLayoutInflater().inflate(R.layout.maintenance_element_addoncategory, null);
            TextView idTextView = addonCategoryElement.findViewById(R.id.IdTextView);
            TextView nameTextView = addonCategoryElement.findViewById(R.id.NameTextView);
            TextView descriptionTextView = addonCategoryElement.findViewById(R.id.DescriptionTextView);

            AddonCategory addonCategory = addonCategories.valueAt(addonCategoryNumber);

            idTextView.setText(addonCategory.getIdString());
            descriptionTextView.setText(addonCategory.description);
            nameTextView.setText(addonCategory.name);

            TextView multiChoiceTextView = addonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            String yes = "YES", no = "NO";
            if (addonCategory.multiChoice) multiChoiceTextView.setText(yes);
            else multiChoiceTextView.setText(no);

            ImageButton removeButton = addonCategoryElement.findViewById(R.id.DeleteButton);
            removeButton.setVisibility(View.GONE);
            ImageButton addButton = addonCategoryElement.findViewById(R.id.EditButton);
            addButton.setOnClickListener(v -> {
                chosenAddonCategories.put(addonCategory.id, addonCategory);
                addonCategories.remove(addonCategory.id);
                updateAddonCategoryList();
                updateChosenAddonCategoryList();
            });
            addonCategoriesLinearLayout.addView(addonCategoryElement);
        }
    }

    public void updateChosenAddonCategoryList() {
        //careful with that
        //chosenAddonCategories.sort(Comparator.comparing(object -> String.valueOf(object.name))); //sort
        chosenAddonCategoriesLinearLayout.removeAllViews();

        for (int chosenAddonCategoryNumber = 0; chosenAddonCategoryNumber < chosenAddonCategories.size(); chosenAddonCategoryNumber++){
            View chosenAddonCategoryElement = getLayoutInflater().inflate(R.layout.maintenance_element_addoncategory, null);
            TextView idTextView = chosenAddonCategoryElement.findViewById(R.id.IdTextView);
            TextView nameTextView = chosenAddonCategoryElement.findViewById(R.id.NameTextView);
            TextView descriptionTextView = chosenAddonCategoryElement.findViewById(R.id.DescriptionTextView);

            AddonCategory chosenAddonCategory = chosenAddonCategories.valueAt(chosenAddonCategoryNumber);

            idTextView.setText(chosenAddonCategory.getIdString());
            descriptionTextView.setText(chosenAddonCategory.description);
            nameTextView.setText(chosenAddonCategory.name);

            TextView multiChoiceTextView = chosenAddonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            String yes = "YES", no = "NO";
            if (chosenAddonCategory.multiChoice) multiChoiceTextView.setText(yes);
            else multiChoiceTextView.setText(no);

            ImageButton addButton = chosenAddonCategoryElement.findViewById(R.id.EditButton);
            addButton.setVisibility(View.GONE);
            ImageButton removeButton = chosenAddonCategoryElement.findViewById(R.id.DeleteButton);
            removeButton.setOnClickListener(v -> {
                addonCategories.put(chosenAddonCategory.id, chosenAddonCategory);
                chosenAddonCategories.remove(chosenAddonCategory.id);
                updateAddonCategoryList();
                updateChosenAddonCategoryList();
            });
            chosenAddonCategoriesLinearLayout.addView(chosenAddonCategoryElement);
        }
    }
}
