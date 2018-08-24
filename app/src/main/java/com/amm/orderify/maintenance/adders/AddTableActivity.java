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
import android.widget.TextView;
import android.widget.Toast;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class AddTableActivity extends AppCompatActivity {

    public LinearLayout tablesLinearLayout;
    static LayoutInflater tableListInflater;
    public List<Table> tables = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_table_activity);


        try {
            ResultSet tablesRS = ExecuteQuery("SELECT * FROM tables");
            while (tablesRS.next()) tables.add(new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), 1, null));
        } catch (SQLException ignored) {}

        tablesLinearLayout = findViewById(R.id.TableLinearLayout);
        tableListInflater = getLayoutInflater();


        updateTableList();




        EditText tableNumberEditText = findViewById(R.id.TableNumberEditText);
        EditText tableDescriptionEditText = findViewById(R.id.TableDescriptionEditText);

        Button addTableButton = findViewById(R.id.AddTableButton);
        addTableButton.setOnClickListener(e -> {
            try {
                ExecuteUpdate("INSERT INTO tables (number, description, state)\n" +
                        "VALUES  (" + tableNumberEditText.getText() + ", '" + tableDescriptionEditText.getText() + "', 1);");

                int newTableID = 0;
                ResultSet orderIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
                while (orderIDRS.next()) newTableID = orderIDRS.getInt(1);
                tables.add(new Table(newTableID, Integer.parseInt(tableNumberEditText.getText().toString()), tableDescriptionEditText.getText().toString(), 1, null));
            } catch (SQLException ignored) {
            }
            tableNumberEditText.setText("");
            tableDescriptionEditText.setText("");
            Toast.makeText(this, "Table added!", Toast.LENGTH_SHORT).show();
            updateTableList();
            //this.startActivity(new Intent(this, ChoseActivity.class));
        });


    }

    @SuppressLint("SetTextI18n")
    public void updateTableList() {
        tablesLinearLayout.removeAllViews();

        for (int tableNumber = 0; tableNumber < tables.size(); tableNumber++){
            View tableElement = tableListInflater.inflate(R.layout.maintenance_table_table_element, null);
            TextView idTextView = tableElement.findViewById(R.id.IdTextView);
            idTextView.setText(tables.get(tableNumber).id + "");

            TextView numberTextView = tableElement.findViewById(R.id.NumberTextView);
            numberTextView.setText(tables.get(tableNumber).number + "");

            TextView descriptionTextView = tableElement.findViewById(R.id.DescriptionTextView);
            descriptionTextView.setText(tables.get(tableNumber).description);

            ImageButton deleteButton = tableElement.findViewById(R.id.ActionButton);
            final int finalTableNumber = tableNumber;
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM tables WHERE ID = " + tables.get(finalTableNumber).id);
                    tables.remove(tables.get(finalTableNumber));
                    Toast.makeText(this, "Table deleted!", Toast.LENGTH_SHORT).show();
                    updateTableList();
                } catch (SQLException e) {
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "Table " + tables.get(finalTableNumber).number + " has order assigned!", Toast.LENGTH_SHORT).show();
                }
            });

            tablesLinearLayout.addView(tableElement);
        }
    }
}
