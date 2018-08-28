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

public class EditDishActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.maintenance_add_table_activity);

    }
}
