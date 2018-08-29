package com.amm.orderify.maintenance.editors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.Addon;
import com.amm.orderify.helpers.data.AddonCategory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class EditAddonsActivity extends AppCompatActivity {

    public Spinner addonCategoriesSpinner;

    LinearLayout addonsLinearLayout;

    public EditText nameEditText;
    public EditText priceEditText;

    Button actionButton;
    Button cancelButton;

    int chosenAddonCategoryID = 0;
    int editedAddonID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_edit_addons_activity);


        addonCategoriesSpinner = findViewById(R.id.AddonCategoriesSpinner);
        updateAddonCategoryList(getAddonCategories());
        addonCategoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenAddonCategoryID = Integer.parseInt(String.valueOf(addonCategoriesSpinner.getSelectedItem()).split("\\.")[0]);
                updateAddonsList(getAddons(chosenAddonCategoryID));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        nameEditText = findViewById(R.id.NameEditText);
        priceEditText = findViewById(R.id.PriceEditText);

        addonsLinearLayout = findViewById(R.id.AddonsLinearLayout);

        actionButton = findViewById(R.id.ActionButton);
        cancelButton = findViewById(R.id.CancelButton);


        actionButton.setOnClickListener(e -> {
            if(editedAddonID == 0) {
                try {
                    ExecuteUpdate("INSERT INTO addons (name, price, addonCategoryID)\n" +
                            "VALUES  ('" + nameEditText.getText() + "', " + priceEditText.getText() + ", " + chosenAddonCategoryID + ")");
                    Toast.makeText(this, "Addon added!", Toast.LENGTH_SHORT).show();
                    updateAddonsList(getAddons(chosenAddonCategoryID));
                } catch (SQLException e1) { Log.wtf("SQL Exception", e1.getMessage()); }
            } else {
                try {
                    ExecuteUpdate("UPDATE addons SET name = '" + nameEditText.getText() + "', " + "price = " + priceEditText.getText() +
                            " WHERE ID = " + editedAddonID);
                    Toast.makeText(this, "AddonCategory edited!", Toast.LENGTH_SHORT).show();
                    cancelButton.callOnClick();
                    updateAddonsList(getAddons(chosenAddonCategoryID));
                } catch (SQLException d) { Log.wtf("SQLException", d.getMessage()); }
            }
        });

        cancelButton.setOnClickListener(e -> {
            nameEditText.setText("");
            priceEditText.setText("");
            editedAddonID = 0;
            cancelButton.setVisibility(View.GONE);
            actionButton.setText("Add addon");
        });


    }

    private SparseArray<AddonCategory> getAddonCategories(){
        SparseArray<AddonCategory> addonCategories = new SparseArray<>();
        try {
            ResultSet addonCategoriesRS = ExecuteQuery("SELECT * FROM addonCategories");
            while (addonCategoriesRS.next()) addonCategories.put(addonCategoriesRS.getInt("ID"), new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), null));
        } catch (SQLException e2) { Log.wtf("SQL Exception", e2.getMessage()); }
        return addonCategories;
    }

    private SparseArray<Addon> getAddons(int addonCategoryID){
        SparseArray<Addon> addons = new SparseArray<>();
        try {
            ResultSet addonsRS = ExecuteQuery("SELECT * FROM addons WHERE addonCategoryID = " + addonCategoryID);
            while (addonsRS.next()) addons.put(addonsRS.getInt("ID"), new Addon(addonsRS.getInt("ID"), addonsRS.getString("name"), addonsRS.getFloat("price"), addonCategoryID));
        } catch (SQLException e2) { Log.wtf("SQL Exception", e2.getMessage()); }
        return addons;
    }

    public void updateAddonCategoryList(SparseArray<AddonCategory> addonCategories) {
        //addonCategories.sort(Comparator.comparing(object -> String.valueOf(object.id)));
        List<String> addonCategoriesStrings = new ArrayList<>();
        for (int addonCategoryNumber = 0; addonCategoryNumber < addonCategories.size(); addonCategoryNumber++){
            AddonCategory addonCategory = addonCategories.valueAt(addonCategoryNumber);
            if(addonCategory.description != null) addonCategoriesStrings.add(addonCategory.id + ". " + addonCategory.name + " (" + addonCategory.description + ")");
            else addonCategoriesStrings.add(addonCategory.id + ". " + addonCategory.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, addonCategoriesStrings);
        addonCategoriesSpinner.setAdapter(adapter);
    }


    public void updateAddonsList(SparseArray<Addon> addons) {
        //addons.sort(Comparator.comparing(object -> String.valueOf(object.name))); //sort
        if(addonsLinearLayout != null) addonsLinearLayout.removeAllViews();
        addonsLinearLayout = findViewById(R.id.AddonsLinearLayout);
        for (int addonNumber = 0; addonNumber < addons.size(); addonNumber++){
            View addonCategoryElement = getLayoutInflater().inflate(R.layout.maintenance_element_addon, null);

            TextView idTextView = addonCategoryElement.findViewById(R.id.IdTextView);
            TextView nameTextView = addonCategoryElement.findViewById(R.id.NameTextView);
            TextView priceTextView = addonCategoryElement.findViewById(R.id.PriceTextView);
            ImageButton deleteButton = addonCategoryElement.findViewById(R.id.DeleteButton);
            ImageButton editButton = addonCategoryElement.findViewById(R.id.EditButton);

            Addon addon = addons.valueAt(addonNumber);

            nameTextView.setText(addon.name);
            idTextView.setText(addon.getIdString());
            priceTextView.setText(addon.getPriceString());

            editButton.setOnClickListener(v -> {
                nameEditText.setText(addon.name);
                priceEditText.setText(addon.getPurePriceString());
                editedAddonID = addon.id;
                actionButton.setText("Edit addon");
                cancelButton.setVisibility(View.VISIBLE);
            });

            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM addons WHERE ID = " + addon.id);
                    Toast.makeText(this, "Addon deleted!", Toast.LENGTH_SHORT).show();
                    updateAddonsList(getAddons(addon.addonCategoryID));
                } catch (SQLException e) {
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "Addon " + addon.name + " is assigned to order!", Toast.LENGTH_SHORT).show();
                }
            });
            addonsLinearLayout.addView(addonCategoryElement);
        }
    }
}
