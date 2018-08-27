package com.amm.orderify.maintenance.adders;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.AddonCategory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class AddAddonActivity extends AppCompatActivity {

    public Spinner addonCategoriesSpinner;

    static LayoutInflater addonCategoriesInflater;

    public EditText nameEditText;
    public EditText priceEditText;

    public List<AddonCategory> addonCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_addon_activity);

        try {
            ResultSet addonCategoriesRS = ExecuteQuery("SELECT * FROM addonCategories");
            while (addonCategoriesRS.next()) addonCategories.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), null));
        } catch (SQLException e2) { Log.wtf("SQL Exception", e2.getMessage()); }

        addonCategoriesSpinner = findViewById(R.id.AddonCategoriesSpinner);

        nameEditText = findViewById(R.id.NameEditText);
        priceEditText = findViewById(R.id.PriceEditText);

        addonCategoriesInflater = getLayoutInflater();
        updateAddonCategoryList();

        Button addDishButton = findViewById(R.id.AddDishButton);
        addDishButton.setOnClickListener(e -> {
            try {
                ExecuteUpdate("INSERT INTO addons (name, price, addonCategoryID)\n" +
                        "VALUES  ('" + nameEditText.getText() + "', " + priceEditText.getText() + ", " + addonCategories.get((int) addonCategoriesSpinner.getSelectedItemId()).id + ")");
            } catch (SQLException e1) { Log.wtf("SQL Exception", e1.getMessage()); }
            Toast.makeText(this, "Addon added!", Toast.LENGTH_SHORT).show();
        });
    }

    public void updateAddonCategoryList() {
        //addonCategories.sort(Comparator.comparing(object -> String.valueOf(object.id)));
        List<String> addonCategoryString = new ArrayList<>();
        for (int addonCategoryNumber = 0; addonCategoryNumber < addonCategories.size(); addonCategoryNumber++)
            addonCategoryString.add(addonCategories.get(addonCategoryNumber).id + ". " + addonCategories.get(addonCategoryNumber).name + " (" + addonCategories.get(addonCategoryNumber).description + ")");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, addonCategoryString);
        addonCategoriesSpinner.setAdapter(adapter);
    }
}
