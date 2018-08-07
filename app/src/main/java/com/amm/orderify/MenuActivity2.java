package com.amm.orderify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MenuActivity2 extends AppCompatActivity
{
    public ListView listView;

    public List<String> names = new ArrayList<>();
    public List<String> prices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);


        try {
            ResultSet resultSet = ExecuteQuery("SELECT dishes.name AS name, sum(dishes.price) AS dishPrice, sum(addons.price) AS addonsPrice " +
                    "FROM addonsToWishes  " +
                    "    JOIN addons ON addons.ID = addonsToWishes.addonID " +
                    "    JOIN wishes ON wishes.ID = addonsToWishes.wishID " +
                    "    JOIN dishes ON dishes.ID = wishes.dishID " +
                    "WHERE orderID = 2 " +
                    "GROUP BY dishes.name;");

            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
                prices.add(String.valueOf(resultSet.getFloat("dishPrice") + resultSet.getFloat("addonsPrice")) + " zł");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        listView = findViewById(R.id.OrderListView);
        listView.setAdapter(new customAdapter(names, prices));

    }

    class customAdapter extends BaseAdapter {

        List<String> names = null;
        List<String> prices = null;

        customAdapter(List<String> val1, List<String> val2) {
            names = val1;
            prices = val2;
        }

        public int getCount() {
            return names.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.order_list_element, null);
            TextView nameTextView = convertView.findViewById(R.id.orderNameTextView);
            TextView priceTextView = convertView.findViewById(R.id.orderPriceTextView);
            nameTextView.setText(names.get(position));
            priceTextView.setText(prices.get(position));
            return convertView;
        }
    }
}
