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

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MenuActivity2 extends AppCompatActivity
{
    public ListView listView;

    public String[] names;
    public float[] prices;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);


        try {
            ResultSet resultSet = ExecuteQuery("SELECT dishes.name AS name, sum(dishes.price) AS dishPrice, sum(addons.price) AS addonsPrice\n" +
                    "FROM addonsToWishes \n" +
                    "    JOIN addons ON addons.ID = addonsToWishes.addonID\n" +
                    "    JOIN wishes ON wishes.ID = addonsToWishes.wishID\n" +
                    "    JOIN dishes ON dishes.ID = wishes.dishID\n" +
                    "WHERE orderID = 1\n" +
                    "GROUP BY dishes.name;");

            int i = 1;
            while (resultSet.next()) {
                names[i] = resultSet.getString("name");
                prices[i] = resultSet.getFloat("dishPrice") + resultSet.getFloat("addonsPrice");
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        listView=(ListView)findViewById(R.id.OrderListView);
        listView.setAdapter(new customAdapter(names, prices));


    }

    class customAdapter extends BaseAdapter {
        String[] Title;
        float[] Prices;

        public customAdapter(String[] titles, float[] prices) {
            Title = titles;
            Prices = prices;
        }

        public int getCount() {
            return Title.length;
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
            title.setText(Title[position]);
            price.setText((int) Prices[position]);

            return (row);
        }
    }
}
