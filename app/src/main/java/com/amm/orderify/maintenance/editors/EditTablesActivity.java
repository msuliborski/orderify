package com.amm.orderify.maintenance.editors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import static com.amm.orderify.helpers.JBDCDriver.*;

public class EditTablesActivity extends AppCompatActivity {

    public LinearLayout tablesLinearLayout;

    EditText numberEditText;
    EditText descriptionEditText;

    Button actionButton;
    Button cancelButton;

    int editedTableID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_edit_tables_activity);

        tablesLinearLayout = findViewById(R.id.TableLinearLayout);

        numberEditText = findViewById(R.id.NumberEditText);
        descriptionEditText = findViewById(R.id.DescriptionEditText);

        updateTableList(getTables());

        actionButton = findViewById(R.id.ActionButton);
        actionButton.setOnClickListener(v -> {
            if(editedTableID == 0) {
                try {
                    ExecuteUpdate("INSERT INTO tables (number, description, state)\n" +
                                         "VALUES  (" + numberEditText.getText() + ", '" + descriptionEditText.getText() + "', 1);");
                    numberEditText.setText("");
                    descriptionEditText.setText("");
                    Toast.makeText(this, "Table added!", Toast.LENGTH_SHORT).show();
                    updateTableList(getTables());
                } catch (SQLException d) { Log.wtf("SQL Exception", d.getMessage()); }
            } else {
                try {
                    ExecuteUpdate("UPDATE tables SET number = " +  numberEditText.getText() + ", description = '" + descriptionEditText.getText() + "' " +
                                         "WHERE ID = " + editedTableID);
                    cancelButton.callOnClick();
                    Toast.makeText(this, "Table edited!", Toast.LENGTH_SHORT).show();
                    updateTableList(getTables());
                } catch (SQLException d) { Log.wtf("SQLException", d.getMessage()); }
            }
        });

        cancelButton = findViewById(R.id.CancelButton);
        cancelButton.setVisibility(View.GONE);
        cancelButton.setOnClickListener(e -> {
            numberEditText.setText("");
            descriptionEditText.setText("");
            editedTableID = 0;
            cancelButton.setVisibility(View.GONE);
            actionButton.setText("Add table");
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
            View tableElement = getLayoutInflater().inflate(R.layout.maintenance_element_table, null);
            TextView idTextView = tableElement.findViewById(R.id.IdTextView);
            TextView numberTextView = tableElement.findViewById(R.id.NumberTextView);
            TextView descriptionTextView = tableElement.findViewById(R.id.DescriptionTextView);
            ImageButton editButton = tableElement.findViewById(R.id.EditButton);
            ImageButton deleteButton = tableElement.findViewById(R.id.DeleteButton);

            if(tableNumber == -1) {
                idTextView.setText("ID");
                numberTextView.setText("NUMBER");
                descriptionTextView.setText("DESCRIPTION");
                editButton.setImageAlpha(1);
                deleteButton.setImageAlpha(1);
                tablesLinearLayout.addView(tableElement);
                continue;
            }

            idTextView.setText(tables.get(tableNumber).getIdString());
            numberTextView.setText(tables.get(tableNumber).getPureNumberString());
            descriptionTextView.setText(tables.get(tableNumber).description);
            final int finalTableNumber = tableNumber;
            editButton.setOnClickListener(v -> {
                numberEditText.setText(tables.get(finalTableNumber).getPureNumberString());
                descriptionEditText.setText(tables.get(finalTableNumber).description);
                editedTableID = tables.get(finalTableNumber).id;
                actionButton.setText("Edit table");
                cancelButton.setVisibility(View.VISIBLE);
            });
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM tables WHERE ID = " + tables.get(finalTableNumber).id);
                    tables.remove(tables.get(finalTableNumber));
                    Toast.makeText(this, "Table deleted!", Toast.LENGTH_SHORT).show();
                    updateTableList(getTables());
                } catch (SQLException e) {
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "Table " + tables.get(finalTableNumber).number + " has order assigned!", Toast.LENGTH_SHORT).show();
                    else { Log.wtf("SQLException", e.getMessage()); }
                }
            });
            tablesLinearLayout.addView(tableElement);
        }
    }
}
