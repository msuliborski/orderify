package com.amm.orderify.config;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.bar.TablesActivity;
import com.amm.orderify.client.MenuActivity;
import com.amm.orderify.helpers.data.*;
import com.amm.orderify.maintenance.EditActivity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static android.widget.AdapterView.*;
import static com.amm.orderify.MainActivity.*;
import static com.amm.orderify.helpers.JBDCDriver.getConnection;

public class RoleActivity extends AppCompatActivity {

    Spinner roleSpinner;

    TextView choseTableTextView;
    Spinner tableSpinner;

    TextView choseClientTextView;
    Spinner clientSpinner;

    CheckBox rememberCheckBox;
    Button selectButton;


    List<Table> tables = new ArrayList<>();
    List<Client> clients = new ArrayList<>();
    boolean remember = false;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_role_activity);

        sharedPreferences = getSharedPreferences("Orderify", MODE_PRIVATE);
        editor = getSharedPreferences("Orderify", MODE_PRIVATE).edit();

        int ROLE = sharedPreferences.getInt("ROLE", -1);

        if (ROLE != -1) {
            switch(ROLE){
                case 0: //BAR
                    this.startActivity(new Intent(this, TablesActivity.class));
                    break;
                case 1: //CLIENT   //GET data from DB!!!!!!!!!!!!!!!!!1
                    thisTable = new Table(sharedPreferences.getInt("thisTableID", 1), sharedPreferences.getInt("thisTableID", 1), null, 1, null);
                    thisClient = new Client(sharedPreferences.getInt("thisClientID", 1), sharedPreferences.getInt("thisClientID", 1), 1, null);
                    this.startActivity(new Intent(this, MenuActivity.class));
                    break;
                case 2: //MAINTENANCE
                    this.startActivity(new Intent(this, EditActivity.class));
                    break;
                default:
                    break;
            }
        }

        choseTableTextView = findViewById(R.id.ChoseTableTextView);
        tableSpinner = findViewById(R.id.TableSpinner);

        choseClientTextView = findViewById(R.id.ChoseClientTextView);
        clientSpinner = findViewById(R.id.ClientSpinner);

        try {
            Statement tableS = getConnection().createStatement();
            ResultSet tablesRS = tableS.executeQuery("SELECT * FROM tables");
            while (tablesRS.next()) {
                Statement clientS = getConnection().createStatement();
                ResultSet clientRS = clientS.executeQuery("SELECT * FROM clients \n" +
                        "WHERE tableID = " + tablesRS.getInt("ID"));
                while (clientRS.next()){
                    clients.add(new Client(clientRS.getInt("ID"), clientRS.getInt("number"), clientRS.getInt("state"), null));
                }
                tables.add(new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), 1, clients));
                clients = new ArrayList<>();
            }
        } catch (SQLException ignored) {}


        roleSpinner = findViewById(R.id.RoleSpinner);
        String[] roleStrings = new String[3];
        roleStrings[0] = "BAR";
        roleStrings[1] = "CLIENT";
        roleStrings[2] = "MAINTENANCE";
        ArrayAdapter<String> rolesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roleStrings);
        roleSpinner.setAdapter(rolesAdapter);
        roleSpinner.setSelection(0);
        roleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch(position){
                    case 0: //BAR
                        choseTableTextView.setVisibility(View.GONE);
                        choseClientTextView.setVisibility(View.GONE);
                        tableSpinner.setVisibility(View.GONE);
                        clientSpinner.setVisibility(View.GONE);
                        break;
                    case 1: //CLIENT
                        choseTableTextView.setVisibility(View.VISIBLE);
                        choseClientTextView.setVisibility(View.VISIBLE);

                        tableSpinner.setVisibility(View.VISIBLE);
                        clientSpinner.setVisibility(View.VISIBLE);

                        String[] tableStrings = new String[tables.size()];
                        for (int tableNumber = 0; tableNumber < tables.size(); tableNumber++){
                            tableStrings[tableNumber] = tables.get(tableNumber).number + ". " + tables.get(tableNumber).description;
                        }
                        ArrayAdapter<String> tablesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, tableStrings);
                        tableSpinner.setAdapter(tablesAdapter);
                        tableSpinner.setSelection(0);
                        tableSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                Table table = tables.get(position);
                                List<String> clientStrings = new ArrayList<>();
                                for (int clientNumber = 0; clientNumber < table.clients.size(); clientNumber++){
                                    clientStrings.add(table.clients.get(clientNumber).number + "");
                                }
                                ArrayAdapter<String>clientAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, clientStrings);
                                clientSpinner.setAdapter(clientAdapter);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                            }

                        });
                        break;
                    case 2: //MAINTENANCE
                        choseTableTextView.setVisibility(View.GONE);
                        choseClientTextView.setVisibility(View.GONE);
                        tableSpinner.setVisibility(View.GONE);
                        clientSpinner.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        rememberCheckBox = findViewById(R.id.RememberCheckBox);
        rememberCheckBox.setOnClickListener(v -> {
            if(remember) {rememberCheckBox.setSelected(false); remember = false;}
            else {rememberCheckBox.setSelected(true); remember = true;}
        });

        selectButton = findViewById(R.id.SelectRoleButton);
        selectButton.setOnClickListener(v -> {
            if(remember){
                editor.putInt("ROLE", roleSpinner.getSelectedItemPosition());
                editor.apply();
            }
            switch(roleSpinner.getSelectedItemPosition()){
                case 0: //BAR
                    this.startActivity(new Intent(this, TablesActivity.class));
                    break;
                case 1: //CLIENT
                    thisTable = tables.get(tableSpinner.getSelectedItemPosition());
                    thisClient = tables.get(tableSpinner.getSelectedItemPosition()).clients.get(clientSpinner.getSelectedItemPosition());
                    if(remember){
                        editor.putInt("thisTableID", thisTable.id);
                        editor.putInt("thisClientID", thisClient.id);
                        editor.apply();
                    }
                    this.startActivity(new Intent(this, MenuActivity.class));
                    break;
                case 2: //MAINTENANCE
                    this.startActivity(new Intent(this, EditActivity.class));
                    break;
                default:
                    break;
            }
        });
    }
}