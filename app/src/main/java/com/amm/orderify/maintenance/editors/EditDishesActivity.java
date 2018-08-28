package com.amm.orderify.maintenance.editors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
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
//
//    public List<Dish> dishes = new ArrayList<>();
//    public List<DishCategory> dishCategories = new ArrayList<>();
    List<Dish> dishes = new ArrayList<>();
    List<AddonCategory> addonCategories = new ArrayList<>();
    List<AddonCategory> chosenAddonCategories = new ArrayList<>();


    int editedDishID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_edit_dishes_activity);


        dishesLinearLayout = findViewById(R.id.DishesLinearLayout);
        dishCategoriesSpinner = findViewById(R.id.DishCategoriesSpinner);
        addonCategoriesLinearLayout = findViewById(R.id.AddonCategoriesLinearLayout);
        chosenAddonCategoriesLinearLayout = findViewById(R.id.ChosenAddonCategoriesLinearLayout);

        addonCategories = getAddonCategoris();
        nameEditText = findViewById(R.id.NameEditText);
        priceEditText = findViewById(R.id.PriceEditText);
        descSEditText = findViewById(R.id.DescSEditText);
        descLEditText = findViewById(R.id.DescLEditText);

        updateDishesList();
        updateDishCategoryList(getDishCategories());
        updateAddonCategoryList();
        updateChosenAddonCategoryList();

        actionButton = findViewById(R.id.ActionButton);
        actionButton.setOnClickListener(e -> {


            if(editedDishID == 0) {
                try {
                    ExecuteUpdate("INSERT INTO dishes (name, price, descS, descL, dishCategoryID)\n" +
                            "VALUES  ('"+ nameEditText.getText() + "', "+ priceEditText.getText() + ", '" + descSEditText.getText() + "', '"+ descLEditText.getText() + "', "+ /*dishCategories.get((int) dishCategoriesSpinner.getSelectedItemId()).id*/1 + ")");

                    int newDishID = 0;
                    ResultSet orderIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
                    if(orderIDRS.next()) newDishID = orderIDRS.getInt(1);

                    for(int addonCategoryNumber = 0; addonCategoryNumber < chosenAddonCategories.size(); addonCategoryNumber++) {
                        ExecuteUpdate("INSERT INTO addonCategoriesToDishes (dishID, addonCategoryID) \n" +
                                "VALUES  (" + newDishID + ", " + chosenAddonCategories.get(addonCategoryNumber).id + ")");
                    }
                    updateDishesList();
                    Toast.makeText(this, "Dish added!", Toast.LENGTH_SHORT).show();
                } catch (SQLException d) { Log.wtf("SQL Exception", d.getMessage()); }
            } else {
                try {
                    ExecuteUpdate("UPDATE dishes SET " + "name = '" + nameEditText.getText() + "', " + "price = " + priceEditText.getText() + ", " +
                            "descS = '" + descSEditText.getText() + "', " + "descL = '" + descLEditText.getText() + "' WHERE ID = " + editedDishID);

                    ExecuteUpdate("DELETE FROM addonCategoriesToDishes WHERE dishID = " + editedDishID);

                    for(int addonCategoryNumber = 0; addonCategoryNumber < chosenAddonCategories.size(); addonCategoryNumber++) {
                        ExecuteUpdate("INSERT INTO addonCategoriesToDishes (dishID, addonCategoryID) \n" +
                                "VALUES  (" + editedDishID + ", " + chosenAddonCategories.get(addonCategoryNumber).id + ")");
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

            addonCategories = getAddonCategoris();
            chosenAddonCategories = new ArrayList<>();

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
        dishes = new ArrayList<>();
        List<AddonCategory> addonCategories = new ArrayList<>();
        try {
            Statement dishesS = getConnection().createStatement();
            ResultSet dishesRS = dishesS.executeQuery("SELECT dishes.ID, dishes.number, dishes.name, dishes.price, dishes.descS, dishes.descL, dishes.dishCategoryID, dishCategories.name AS dishCategoryName FROM dishes \n" +
                    "JOIN dishCategories ON dishCategories.ID = dishes.dishCategoryID");
            while (dishesRS.next()) {
                Statement addonCategoriesS = getConnection().createStatement();
                ResultSet addonCategoriesRS = addonCategoriesS.executeQuery("SELECT * FROM addonCategoriesToDishes\n" +
                        "JOIN addonCategories ON addonCategories.ID = addonCategoriesToDishes.addonCategoryID\n" +
                        "WHERE dishID = " + dishesRS.getInt("ID"));
                while (addonCategoriesRS.next())
                    addonCategories.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), null));
                dishes.add(new Dish(dishesRS.getInt("ID"), dishesRS.getInt("number"), dishesRS.getString("name"), dishesRS.getFloat("price"), dishesRS.getString("descS"), dishesRS.getString("descL"), dishesRS.getInt("dishCategoryID") , addonCategories));
                dishes.get(dishes.size()-1).dishCategoryName = dishesRS.getString("dishCategoryName");
                addonCategories = new ArrayList<>();
            }
        } catch (SQLException e) { Log.wtf("SQLException", e.getMessage() + "");}
    }

    private List<AddonCategory> getAddonCategoris(){
        List<AddonCategory> addonCategories = new ArrayList<>();
        try {
            ResultSet addonCategoriesRS = ExecuteQuery("SELECT * FROM addonCategories");
            while (addonCategoriesRS.next()) addonCategories.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), null));
        } catch (SQLException e) { Log.wtf("SQLException", e.getMessage() + "");}
        return addonCategories;
    }

    private List<DishCategory> getDishCategories(){
        List<DishCategory> dishCategories = new ArrayList<>();
        try {
            ResultSet dishCategoriesRS = ExecuteQuery("SELECT * FROM dishCategories");
            while (dishCategoriesRS.next()) dishCategories.add(new DishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), null));
        } catch (SQLException e) { Log.wtf("SQLException", e.getMessage() + "");}
        return dishCategories;
    }

    public void updateDishesList() {
        getDishes();
        dishes.sort(Comparator.comparing(object -> String.valueOf(object.id))); //sort
        dishes.sort(Comparator.comparing(object -> String.valueOf(object.dishCategoryID))); //sort
        if(dishesLinearLayout != null) dishesLinearLayout.removeAllViews();
        dishesLinearLayout = findViewById(R.id.DishesLinearLayout);
        for (int dishNumber = 0; dishNumber < dishes.size(); dishNumber++){
            Dish dish = dishes.get(dishNumber);
            View dishElement = getLayoutInflater().inflate(R.layout.maintenance_element_dish, null);

            TextView idTextView = dishElement.findViewById(R.id.IdTextView);
            idTextView.setText(dish.getIdString());

            TextView nameTextView = dishElement.findViewById(R.id.NameTextView);
            nameTextView.setText(dish.name);

            TextView dishCategoryTextView = dishElement.findViewById(R.id.DishCategoryTextView);
            dishCategoryTextView.setText(dish.dishCategoryName);

            TextView descSTextView = dishElement.findViewById(R.id.DescSTextView);
            descSTextView.setText(dish.descS);

            TextView descLTextView = dishElement.findViewById(R.id.DescLTextView);
            descLTextView.setText(dish.descL);

            TextView priceTextView = dishElement.findViewById(R.id.PriceTextView);
            priceTextView.setText(dish.getPriceString());

            int finalDishNumber = dishNumber;
            ImageButton editButton = dishElement.findViewById(R.id.EditButton);
            editButton.setOnClickListener(v -> {
                //Dish dish1 = (Dish) dishes.stream().filter(item -> item.id == finalDishNumber);
                chosenAddonCategories = new ArrayList<>(); //add sort here
                chosenAddonCategories.addAll(dish.addonCategories);
                addonCategories.removeAll(dish.addonCategories);

                updateAddonCategoryList();
                updateChosenAddonCategoryList();
                nameEditText.setText(dishes.get(finalDishNumber).name);
                priceEditText.setText(dishes.get(finalDishNumber).getPurePriceString());
                descSEditText.setText(dishes.get(finalDishNumber).descS);
                descLEditText.setText(dishes.get(finalDishNumber).descL);
                editedDishID = dishes.get(finalDishNumber).id;
                actionButton.setText("Edit dish");
                cancelButton.setVisibility(View.VISIBLE);
            });
            ImageButton deleteButton = dishElement.findViewById(R.id.DeleteButton);
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM addonCategoriesToDishes WHERE dishID = " + dishes.get(finalDishNumber).id);
                    ExecuteUpdate("DELETE FROM dishes WHERE ID = " + dishes.get(finalDishNumber).id);
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
    public void updateDishCategoryList(List<DishCategory> dishCategories) {
        String[] dishCategoriesStrings = new String[dishCategories.size()];
        for (int dishCategoryNumber = 0; dishCategoryNumber < dishCategories.size(); dishCategoryNumber++){
            dishCategoriesStrings[dishCategoryNumber] = dishCategories.get(dishCategoryNumber).name;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dishCategoriesStrings);
        dishCategoriesSpinner.setAdapter(adapter);
    }

    public void updateAddonCategoryList() {
        //addonCategories.sort(Comparator.comparing(object -> String.valueOf(object.name))); //sort
        addonCategoriesLinearLayout.removeAllViews();

        for (int addonCategoryNumber = 0; addonCategoryNumber < addonCategories.size(); addonCategoryNumber++){
            View addonCategoryElement = getLayoutInflater().inflate(R.layout.maintenance_element_addoncategory, null);

            TextView idTextView = addonCategoryElement.findViewById(R.id.IdTextView);
            idTextView.setText(addonCategories.get(addonCategoryNumber).getIdString());

            TextView nameTextView = addonCategoryElement.findViewById(R.id.NameTextView);
            nameTextView.setText(addonCategories.get(addonCategoryNumber).name);

            TextView descriptionTextView = addonCategoryElement.findViewById(R.id.DescriptionTextView);
            descriptionTextView.setText(addonCategories.get(addonCategoryNumber).description);

            TextView multiChoiceTextView = addonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            String yes = "YES", no = "NO";
            if (addonCategories.get(addonCategoryNumber).multiChoice) multiChoiceTextView.setText(yes);
            else multiChoiceTextView.setText(no);

            ImageButton removeButton = addonCategoryElement.findViewById(R.id.DeleteButton);
            removeButton.setVisibility(View.GONE);
            ImageButton addButton = addonCategoryElement.findViewById(R.id.EditButton);
            int finalAddonCategoryNumber = addonCategoryNumber;
            addButton.setOnClickListener(v -> {
                chosenAddonCategories.add(addonCategories.get(finalAddonCategoryNumber));
                addonCategories.remove(addonCategories.get(finalAddonCategoryNumber));
                updateAddonCategoryList();
                updateChosenAddonCategoryList();
            });
            addonCategoriesLinearLayout.addView(addonCategoryElement);
        }
    }

    public void updateChosenAddonCategoryList() {
        //carefull with that
        //chosenAddonCategories.sort(Comparator.comparing(object -> String.valueOf(object.name))); //sort
        chosenAddonCategoriesLinearLayout.removeAllViews();

        for (int chosenAddonCategoryNumber = 0; chosenAddonCategoryNumber < chosenAddonCategories.size(); chosenAddonCategoryNumber++){
            View chosenAddonCategoryElement = getLayoutInflater().inflate(R.layout.maintenance_element_addoncategory, null);

            TextView idTextView = chosenAddonCategoryElement.findViewById(R.id.IdTextView);
            idTextView.setText(chosenAddonCategories.get(chosenAddonCategoryNumber).getIdString());

            TextView nameTextView = chosenAddonCategoryElement.findViewById(R.id.NameTextView);
            nameTextView.setText(chosenAddonCategories.get(chosenAddonCategoryNumber).name);

            TextView descriptionTextView = chosenAddonCategoryElement.findViewById(R.id.DescriptionTextView);
            descriptionTextView.setText(chosenAddonCategories.get(chosenAddonCategoryNumber).description);

            TextView multiChoiceTextView = chosenAddonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            String yes = "YES", no = "NO";
            if (chosenAddonCategories.get(chosenAddonCategoryNumber).multiChoice) multiChoiceTextView.setText(yes);
            else multiChoiceTextView.setText(no);

            ImageButton addButton = chosenAddonCategoryElement.findViewById(R.id.EditButton);
            addButton.setVisibility(View.GONE);
            ImageButton removeButton = chosenAddonCategoryElement.findViewById(R.id.DeleteButton);
            int finalChosenAddonCategoryNumber = chosenAddonCategoryNumber;
            removeButton.setOnClickListener(v -> {
                addonCategories.add(chosenAddonCategories.get(finalChosenAddonCategoryNumber));
                chosenAddonCategories.remove(chosenAddonCategories.get(finalChosenAddonCategoryNumber));
                updateAddonCategoryList();
                updateChosenAddonCategoryList();
            });
            chosenAddonCategoriesLinearLayout.addView(chosenAddonCategoryElement);
        }
    }
}
