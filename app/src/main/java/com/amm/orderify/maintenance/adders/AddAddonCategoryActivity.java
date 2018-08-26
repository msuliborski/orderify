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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.AddonCategory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class AddAddonCategoryActivity extends AppCompatActivity {

    public LinearLayout addonCategoriesLinearLayout;
    static LayoutInflater addonCategoriesListInflater;
    public List<AddonCategory> addonCategories = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_addoncategory_activity);


        try {
            ResultSet addonCategoriesRS = ExecuteQuery("SELECT * FROM addonCategories");
            while (addonCategoriesRS.next()) addonCategories.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), null));
        } catch (SQLException ignored) {}

        addonCategoriesLinearLayout = findViewById(R.id.AddonCategoryLinearLayout);
        addonCategoriesListInflater = getLayoutInflater();


        updateAddonCategoryList();

        EditText addonCategoryNameEditText = findViewById(R.id.AddonCategoryNameEditText);
        Switch multiChoiceToggleButton = findViewById(R.id.MultiChoiceToggleButton);

        Button addAddonCategoryButton = findViewById(R.id.AddAddonCategoryButton);
        addAddonCategoryButton.setOnClickListener(e -> {
            try {
                ExecuteUpdate("INSERT INTO addonCategories (name)\n" +
                        "VALUES ('" + addonCategoryNameEditText.getText().toString() + "')");
                int newAddonCategoryID = 0;
                ResultSet orderIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
                while (orderIDRS.next()) newAddonCategoryID = orderIDRS.getInt(1);
                addonCategories.add(new AddonCategory(newAddonCategoryID, addonCategoryNameEditText.getText().toString(), null, multiChoiceToggleButton.isChecked(), null));
            } catch (SQLException ignored) { }
            Toast.makeText(this, "AddonCategory added!", Toast.LENGTH_SHORT).show();
            //addonCategoryNameEditText.setText("");
            updateAddonCategoryList();
            //this.startActivity(new Intent(this, EditActivity.class));
        });


    }

    @SuppressLint("SetTextI18n")
    public void updateAddonCategoryList() {
        addonCategoriesLinearLayout.removeAllViews();
        for (int addonCategoryNumber = 0; addonCategoryNumber < addonCategories.size(); addonCategoryNumber++){
            View addonCategoryElement = addonCategoriesListInflater.inflate(R.layout.maintenance_addoncategory_element, null);

            TextView idTextView = addonCategoryElement.findViewById(R.id.IdTextView);
            idTextView.setText(addonCategories.get(addonCategoryNumber).id + "");

            TextView nameTextView = addonCategoryElement.findViewById(R.id.NameTextView);
            nameTextView.setText(addonCategories.get(addonCategoryNumber).name);

            TextView multiChoiceTextView = addonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            if (addonCategories.get(addonCategoryNumber).multiChoice) multiChoiceTextView.setText("YES");
            else multiChoiceTextView.setText("NO");

            ImageButton deleteButton = addonCategoryElement.findViewById(R.id.ActionButton);
            final int finaladdonCategoryNumber = addonCategoryNumber;
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM addonCategories WHERE ID = " + addonCategories.get(finaladdonCategoryNumber).id);
                    addonCategories.remove(addonCategories.get(finaladdonCategoryNumber));
                    updateAddonCategoryList();
                    Toast.makeText(this, "AddonCategory deleted!", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "AddonCategory " + addonCategories.get(finaladdonCategoryNumber).name + " has addons assigned!", Toast.LENGTH_SHORT).show();
                }
            });

            addonCategoriesLinearLayout.addView(addonCategoryElement);
        }
    }
}
