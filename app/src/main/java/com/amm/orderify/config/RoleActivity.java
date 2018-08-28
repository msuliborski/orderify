package com.amm.orderify.config;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.amm.orderify.maintenance.MaintenanceActivity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static android.widget.AdapterView.*;
import static com.amm.orderify.MainActivity.*;
import static com.amm.orderify.helpers.JBDCDriver.ExecuteQuery;
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

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);

        sharedPreferences = getSharedPreferences("Orderify", MODE_PRIVATE);
        editor = getSharedPreferences("Orderify", MODE_PRIVATE).edit();

        switch(sharedPreferences.getInt("role", -1)){
            case -1: break;
            case 0: //BAR
                this.startActivity(new Intent(this, TablesActivity.class)); break;
            case 1: //CLIENT
                try {
                    ResultSet tablesRS = ExecuteQuery("SELECT * FROM tables WHERE ID = " + sharedPreferences.getInt("thisTableID", 1));
                    if (tablesRS.next())
                        thisTable = new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), tablesRS.getInt("state"), null);
                    ResultSet clientRS = ExecuteQuery("SELECT * FROM clients WHERE tableID = " + thisTable.id);
                    if (clientRS.next())
                        thisClient = new Client(clientRS.getInt("ID"), clientRS.getInt("number"), clientRS.getInt("state"), null);
                } catch (SQLException ignored) {}
                this.startActivity(new Intent(this, MenuActivity.class));
                break;
            case 2: //MAINTENANCE
                this.startActivity(new Intent(this, MaintenanceActivity.class));
                break;
            default: break;
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
                tables.add(new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), tablesRS.getInt("state"), clients));
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
                        tableSpinner.setVisibility(View.GONE);
                        choseClientTextView.setVisibility(View.GONE);
                        clientSpinner.setVisibility(View.GONE);
                        break;
                    case 1: //CLIENT
                        choseTableTextView.setVisibility(View.VISIBLE);
                        tableSpinner.setVisibility(View.VISIBLE);

                        choseClientTextView.setVisibility(View.VISIBLE);
                        clientSpinner.setVisibility(View.VISIBLE);

                        List<String> tableStrings = new ArrayList<>();
                        for (int tableNumber = 0; tableNumber < tables.size(); tableNumber++)
                            tableStrings.add("Table #" + tables.get(tableNumber).number + " - " + tables.get(tableNumber).description);
                        ArrayAdapter<String> tablesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, tableStrings);
                        tableSpinner.setAdapter(tablesAdapter);
                        tableSpinner.setSelection(0);
                        tableSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                Table table = tables.get(position);
                                List<String> clientStrings = new ArrayList<>();
                                for (int clientNumber = 0; clientNumber < table.clients.size(); clientNumber++)
                                    clientStrings.add("Client #" + table.clients.get(clientNumber).number);
                                ArrayAdapter<String>clientAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, clientStrings);
                                clientSpinner.setAdapter(clientAdapter);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) { }
                        });
                        break;
                    case 2: //MAINTENANCE
                        choseTableTextView.setVisibility(View.GONE);
                        tableSpinner.setVisibility(View.GONE);
                        choseClientTextView.setVisibility(View.GONE);
                        clientSpinner.setVisibility(View.GONE);
                        break;
                    default: break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        rememberCheckBox = findViewById(R.id.RememberCheckBox);
        rememberCheckBox.setOnClickListener(v -> {
            remember = !remember;
        });

        selectButton = findViewById(R.id.SelectRoleButton);
        selectButton.setOnClickListener(v -> {
            if(remember){
                editor.putInt("role", roleSpinner.getSelectedItemPosition());editor.apply();
            } else {
                editor.clear(); editor.apply();
            }
            switch(roleSpinner.getSelectedItemPosition()){
                case 0: //BAR
                    this.startActivity(new Intent(this, TablesActivity.class));
                    break;
                case 1: //CLIENT
                    thisTable = tables.get(tableSpinner.getSelectedItemPosition());
                    thisClient = thisTable.clients.get(clientSpinner.getSelectedItemPosition());

                    Log.wtf("table", thisTable.id + "");
                    Log.wtf("client", thisClient.id + "");
                    if(remember){
                        editor.putInt("thisTableID", thisTable.id);
                        editor.putInt("thisClientID", thisClient.id);
                        editor.apply();
                    }
                    this.startActivity(new Intent(this, MenuActivity.class));
                    break;
                case 2: //MAINTENANCE
                    this.startActivity(new Intent(this, MaintenanceActivity.class));
                    break;
                default:
                    break;
            }
        });
    }
}