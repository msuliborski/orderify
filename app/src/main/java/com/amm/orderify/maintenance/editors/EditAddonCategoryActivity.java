package com.amm.orderify.maintenance.editors;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.util.Comparator;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.ExecuteQuery;
import static com.amm.orderify.helpers.JBDCDriver.ExecuteUpdate;

public class EditAddonCategoryActivity extends AppCompatActivity {

    public LinearLayout addonCategoriesLinearLayout;
    public LayoutInflater addonCategoriesListInflater;


    EditText addonCategoryNameEditText;
    Switch multiChoiceToggleButton;

    int editedAddonCategoryID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_edit_addoncategory_activity);

        addonCategoriesLinearLayout = findViewById(R.id.AddonCategoryLinearLayout);
        addonCategoriesListInflater = getLayoutInflater();

        updateAddonCategoryList(getAddonCategories());

        addonCategoryNameEditText = findViewById(R.id.AddonCategoryNameEditText);
        multiChoiceToggleButton = findViewById(R.id.MultiChoiceToggleButton);

        Button editAddonCategoryButton = findViewById(R.id.EditAddonCategoryButton);
        editAddonCategoryButton.setOnClickListener(v -> {
            try {
                ExecuteUpdate("UPDATE addonCategories SET name = '" + addonCategoryNameEditText.getText().toString() + "', multiChoice = " + multiChoiceToggleButton.isChecked() + " WHERE ID = " + editedAddonCategoryID);
                Toast.makeText(this, "AddonCategory edited!", Toast.LENGTH_SHORT).show();
                addonCategoryNameEditText.setText("");
                multiChoiceToggleButton.setChecked(false);
                editedAddonCategoryID = 0;
                updateAddonCategoryList(getAddonCategories());
            } catch (SQLException ignored) {}
        });
    }

    private List<AddonCategory> getAddonCategories(){
        List<AddonCategory> addonCategories = new ArrayList<>();
        try {
            ResultSet addonCategoriesRS = ExecuteQuery("SELECT * FROM addonCategories");
            while (addonCategoriesRS.next())
                addonCategories.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), null));
        } catch (SQLException ignored) {}
        return addonCategories;
    }

    public void updateAddonCategoryList(List<AddonCategory>  addonCategories) {
        addonCategories.sort(Comparator.comparing(object -> String.valueOf(object.name)));
        addonCategoriesLinearLayout.removeAllViews();
        for (int addonCategoryNumber = -1; addonCategoryNumber < addonCategories.size(); addonCategoryNumber++){

            View addonCategoryElement = addonCategoriesListInflater.inflate(R.layout.maintenance_addoncategory_element, null);
            TextView idTextView = addonCategoryElement.findViewById(R.id.TablesTextView);
            TextView nameTextView = addonCategoryElement.findViewById(R.id.NameTextView);
            TextView descriptionTextView = addonCategoryElement.findViewById(R.id.DescriptionTextView);
            TextView multiChoiceTextView = addonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            ImageButton selectAddonCategoryButton = addonCategoryElement.findViewById(R.id.ActionButton);

            if(addonCategoryNumber == -1) {
                idTextView.setText("ID");
                nameTextView.setText("NAME");
                descriptionTextView.setText("DESCRIPTION");
                multiChoiceTextView.setText("MULTI");
                selectAddonCategoryButton.setImageAlpha(1);
                addonCategoriesLinearLayout.addView(addonCategoryElement);
                continue;
            }

            idTextView.setText(addonCategories.get(addonCategoryNumber).getIdString());
            nameTextView.setText(addonCategories.get(addonCategoryNumber).name);
            descriptionTextView.setText(addonCategories.get(addonCategoryNumber).description);
            String yes = "YES", no = "NO";
            if (addonCategories.get(addonCategoryNumber).multiChoice) multiChoiceTextView.setText(yes);
            else multiChoiceTextView.setText(no);

            final int finaladdonCategoryNumber = addonCategoryNumber;
            selectAddonCategoryButton.setImageDrawable(this.getDrawable(R.drawable.bar_accept_request_button));
            selectAddonCategoryButton.setOnClickListener(v -> {
                addonCategoryNameEditText.setText(addonCategories.get(finaladdonCategoryNumber).name);
                multiChoiceToggleButton.setChecked(addonCategories.get(finaladdonCategoryNumber).multiChoice);
                editedAddonCategoryID = addonCategories.get(finaladdonCategoryNumber).id;
            });

            addonCategoriesLinearLayout.addView(addonCategoryElement);
        }
    }
}
