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
import android.widget.TextView;
import android.widget.Toast;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.ExecuteQuery;
import static com.amm.orderify.helpers.JBDCDriver.ExecuteUpdate;

public class EditTableActivity extends AppCompatActivity {

    public LinearLayout tablesLinearLayout;
    static LayoutInflater tableListInflater;

    EditText tableNumberEditText;
    EditText tableDescriptionEditText;

    int editedTableID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_edit_table_activity);

        tablesLinearLayout = findViewById(R.id.TableLinearLayout);
        tableListInflater = getLayoutInflater();
        updateTableList(getTables());

        tableNumberEditText = findViewById(R.id.TableNumberEditText);
        tableDescriptionEditText = findViewById(R.id.TableDescriptionEditText);

        Button saveTableButton = findViewById(R.id.SaveTableButton);
        saveTableButton.setOnClickListener(v -> {
            try {
                ExecuteUpdate("UPDATE tables SET number = " +  tableNumberEditText.getText() + ", description = '" + tableDescriptionEditText.getText() + "' WHERE ID = " + editedTableID);
                tableNumberEditText.setText("");
                tableDescriptionEditText.setText("");
                editedTableID = 0;
                Toast.makeText(this, "Table edited!", Toast.LENGTH_SHORT).show();
                updateTableList(getTables());
            } catch (SQLException d) { Log.wtf("SQLException", d.getMessage()); }
        });


    }

    private List<Table> getTables(){
        List<Table> tables = new ArrayList<>();
        try {
            ResultSet tablesRS = ExecuteQuery("SELECT * FROM tables");
            while (tablesRS.next())
                tables.add(new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), 1, null));
        } catch (SQLException ignored) {}
        return tables;
    }

    public void updateTableList(List<Table> tables) {
        tablesLinearLayout.removeAllViews();
        for (int tableNumber = -1; tableNumber < tables.size(); tableNumber++){
            View tableElement = tableListInflater.inflate(R.layout.maintenance_table_element, null);
            TextView idTextView = tableElement.findViewById(R.id.TablesTextView);
            TextView numberTextView = tableElement.findViewById(R.id.NumberTextView);
            TextView descriptionTextView = tableElement.findViewById(R.id.DescriptionTextView);
            ImageButton selectTableButton = tableElement.findViewById(R.id.ActionButton);

            if(tableNumber == -1) {
                idTextView.setText("ID");
                numberTextView.setText("NUMBER");
                descriptionTextView.setText("DESCRIPTION");
                selectTableButton.setImageAlpha(1);
                tablesLinearLayout.addView(tableElement);
                continue;
            }

            idTextView.setText(tables.get(tableNumber).getIdString());
            numberTextView.setText(tables.get(tableNumber).getPlaneNumberString());
            descriptionTextView.setText(tables.get(tableNumber).description);
            final int finalTableNumber = tableNumber;
            selectTableButton.setImageDrawable(this.getDrawable(R.drawable.bar_accept_request_button));
            selectTableButton.setOnClickListener(v -> {
                tableNumberEditText.setText(tables.get(finalTableNumber).getPlaneNumberString());
                tableDescriptionEditText.setText(tables.get(finalTableNumber).description);
                editedTableID = tables.get(finalTableNumber).id;
            });
            tablesLinearLayout.addView(tableElement);
        }
    }
}
