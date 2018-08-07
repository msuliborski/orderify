package com.amm.orderify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<Integer> prices = new ArrayList<>();
    //public float[] prices;

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
                    "WHERE orderID = 1 " +
                    "GROUP BY dishes.name;");

            //ResultSet resultSet = ExecuteQuery("SELECT * FROM tables;");
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
                prices.add(resultSet.getInt("dishPrice") + resultSet.getInt("addonsPrice"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        listView=(ListView)findViewById(R.id.OrderListView);
        listView.setAdapter(new customAdapter(names, prices));


    }

    class customAdapter extends BaseAdapter {

        public ArrayList<String> Title = new ArrayList<>();
        public ArrayList<Integer> Prices = new ArrayList<>();

        public customAdapter(ArrayList<String>  titles, ArrayList<Integer> prices) {
            Title = titles;
            Prices = prices;
        }

        public int getCount() {
            return Title.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.order_list_element, parent, false);
            TextView title, price;
            title = (TextView) row.findViewById(R.id.orderNameTextView);
            price = (TextView) row.findViewById(R.id.orderPriceTextView);
            title.setText(Title.get(position));
            price.setText((Prices.get(position)));

            return (row);
        }
    }
}
