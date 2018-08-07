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

        String[] stadoKoz = {"mama koza", "siostra koza", "brat koza", "świania, ktora podszyla sie pod koze", "nfz"};
        listView=(ListView)findViewById(R.id.OrderListView);
        listView.setAdapter(new customAdapter(stadoKoz, 28));

        //TextView dupa = findViewById(R.id.ErrorsTextView);
        //dupa.setText(names.get(0));
    }

    class customAdapter extends BaseAdapter {

        //public ArrayList<String> Title = new ArrayList<>();
        //public ArrayList<Integer> Prices = new ArrayList<>();
        public String[] chuj;
        public int vagina;

//        public customAdapter(ArrayList<String>  titles, ArrayList<Integer> prices) {
//            Title = titles;
//            Prices = prices;
//        }
        public customAdapter(String[] chujjj, int waginaaa) {
            chuj = chujjj;
            vagina = waginaaa;
        }

        public int getCount() {
            return chuj.length;
            //return chuj.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.order_list_element, null);
            TextView title, price;
            title = (TextView) convertView.findViewById(R.id.orderNameTextView);
            //price = (TextView) convertView.findViewById(R.id.orderPriceTextView);
//            title.setText(Title.get(position));
//            price.setText((Prices.get(position)));
            title.setText(chuj[position]);
            //price.setText(toString);
            return convertView;
        }
    }
}
