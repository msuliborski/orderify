package com.amm.orderify.maintenance.editors;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
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

                    int newTableID = 0;
                    ResultSet orderIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
                    if (orderIDRS.next()) newTableID = orderIDRS.getInt(1);

                    ExecuteUpdate("INSERT INTO clients (number, state, tableID)\n" +
                                         "VALUES  (1, 1, " + newTableID + "), (2, 1, " + newTableID + "), (3, 1, " + newTableID + "), (4, 1, " + newTableID + ");");
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

    private ArrayMap<Integer, Table> getTables(){
        ArrayMap<Integer, Table> tables = new ArrayMap<>();
        try {
            ResultSet tablesRS = ExecuteQuery("SELECT * FROM tables");
            while (tablesRS.next())
                tables.put(tablesRS.getInt("ID"), new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), 1, null));
        } catch (SQLException ignored) {}
        return tables;
    }

    public void updateTableList(ArrayMap<Integer, Table> tables) {
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

            Table table = tables.valueAt(tableNumber);

            idTextView.setText(table.getIdString());
            numberTextView.setText(table.getPureNumberString());
            descriptionTextView.setText(table.description);
            editButton.setOnClickListener(v -> {
                numberEditText.setText(table.getPureNumberString());
                descriptionEditText.setText(table.description);
                editedTableID = table.id;
                actionButton.setText("Edit table");
                cancelButton.setVisibility(View.VISIBLE);
            });
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM clients WHERE tableID = " + table.id);
                    ExecuteUpdate("DELETE FROM tables WHERE ID = " + table.id);
                    tables.remove(table.id);
                    Toast.makeText(this, "Table deleted!", Toast.LENGTH_SHORT).show();
                    updateTableList(getTables());
                } catch (SQLException e) {
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "Table #" + table.number + " has order assigned!", Toast.LENGTH_SHORT).show();
                    else { Log.wtf("SQLException", e.getMessage()); }
                }
            });
            tablesLinearLayout.addView(tableElement);
        }
    }
}
