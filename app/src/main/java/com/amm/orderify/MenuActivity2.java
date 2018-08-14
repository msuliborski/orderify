package com.amm.orderify;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MenuActivity2 extends AppCompatActivity
{
    public ListView orderListView;
    public ListView menuListView;
    public int marginCopy;

    public List<String> names = new ArrayList<>();
    public List<String> prices = new ArrayList<>();
    public List<String> amounts = new ArrayList<>();

    public int orderID = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);

        List<Addon> addonsOLIST = new ArrayList<>();
        List<Wish> wishesOLIST = new ArrayList<>();

        try {
            ResultSet resultSet = ExecuteQuery("SELECT dishes.name AS name, max(wishes.amount) AS amount, sum(dishes.price) AS dishPrice, sum(addons.price) AS addonsPrice\n" +
                    "FROM addonsToWishes\n" +
                    "JOIN addons ON addons.ID = addonsToWishes.addonID\n" +
                    "RIGHT JOIN wishes ON wishes.ID = addonsToWishes.wishID\n" +
                    "JOIN dishes ON dishes.ID = wishes.dishID\n" +
                    "WHERE orderID = " + orderID + "\n" +
                    "GROUP BY dishes.name;");

            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
                prices.add(String.valueOf(String.format(Locale.FRANCE, "%.2f", resultSet.getFloat("dishPrice") + resultSet.getFloat("addonsPrice"))) + " zł");
                amounts.add(resultSet.getString("amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        addonsOLIST = new ArrayList<>();

        //====================ORDER LIST=================================
        orderListView = findViewById(R.id.OrderListView);
        ViewGroup.LayoutParams lp = orderListView.getLayoutParams();
        //ViewGroup.LayoutParams lpb = lp;
        if (names.size() <= 3) lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        orderListView.setLayoutParams(lp);
        orderListView.setAdapter(new customAdapter(names, prices));

        //=====================MENU LIST====================================
        menuListView = findViewById(R.id.MenuListView);

        List<Addon> addonsMENU = new ArrayList<>();
        List<Dish> dishesMENU = new ArrayList<>();
        List<dishCategory> dishCategoriesMENU = new ArrayList<>();

        try {
            Statement dishCategoriesS = getConnection().createStatement();
            ResultSet dishCategoriesRS = dishCategoriesS.executeQuery("SELECT * FROM dishesCategories");
            while (dishCategoriesRS.next()) {

                Statement dishesS = getConnection().createStatement();
                ResultSet dishesRS = dishesS.executeQuery("SELECT * FROM dishes \n" +
                        "WHERE categoryID = " + dishCategoriesRS.getInt("ID"));

                while (dishesRS.next()) {
                    Statement addonsS = getConnection().createStatement();
                    ResultSet addonsRS = addonsS.executeQuery("SELECT addons.* FROM addonsCategoriesToDishes\n" +
                            "JOIN addons ON addons.addonCategoryID = addonsCategoriesToDishes.addonCategoryID\n" +
                            "RIGHT JOIN dishes ON dishes.ID = addonsCategoriesToDishes.dishID\n" +
                            "WHERE dishes.ID = " + dishesRS.getInt("ID"));
                    while (addonsRS.next()) {
                        addonsMENU.add(new Addon( addonsRS.getInt("ID"), addonsRS.getString("name"), addonsRS.getFloat("price")));
                    }
                    dishesMENU.add(new Dish(dishesRS.getInt("ID"), dishesRS.getString("name"),
                            dishesRS.getFloat("price"), dishesRS.getString("descS"),
                            dishesRS.getString("descL"), null));//@@@@
                    addonsMENU = new ArrayList<>();
                }
                dishCategoriesMENU.add(new dishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), dishesMENU));

                dishesMENU = new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



        List<Object> menuList = new ArrayList<>();
        for(int i = 0; i < dishCategoriesMENU.size(); i++){
            menuList.add(dishCategoriesMENU.get(i));//send just category
            menuList.addAll(dishCategoriesMENU.get(i).dishes); //send item on category one by one
        }



        menuListView.setOnItemClickListener((parent, view, position, id) -> {
            ConstraintLayout menuExpand = view.findViewById(R.id.MenuExpand);
            ConstraintLayout.MarginLayoutParams params = (ConstraintLayout.MarginLayoutParams) menuExpand.getLayoutParams();
            if (params.bottomMargin == 0) {
                params.bottomMargin = marginCopy;
            } else {
                marginCopy = params.bottomMargin;
                params.bottomMargin = 0;
            }
            menuExpand.setLayoutParams(params);
        });

        menuListView.setAdapter(new customMenuAdapter(this, menuList));

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
            Button orderCancelButton = convertView.findViewById(R.id.OrderCancelButton);

            nameTextView.setText(names.get(position));
            priceTextView.setText(prices.get(position));
            orderCancelButton.setOnClickListener(e -> {
                try {
                    ExecuteUpdate("DELETE FROM wishes \n" +
                            "WHERE wishID = " + orderID + "\n" +
                            "GROUP BY dishes.name;");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });
            return convertView;
        }
    }


    class customMenuAdapter extends BaseAdapter
    {
        List<Object> menuList = null;

        private static final int MENU_ITEM = 0;
        private static final int HEADER = 1;
        private LayoutInflater inflater;


        customMenuAdapter(Context context, List<Object> menuList)
        {
            this.menuList = menuList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getItemViewType(int i)
        {
            if (menuList.get(i) instanceof dishCategory) return HEADER;
            else return MENU_ITEM;

        }

        @Override
        public int getViewTypeCount()
        {
            return 2;
        }

        @Override
        public int getCount()
        {
            return menuList.size();
        }

        @Override
        public Object getItem(int i)
        {
            return menuList.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return 1;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {

            if (view == null)
            {
                switch (getItemViewType(i))
                {
                    case MENU_ITEM:
                        view = inflater.inflate(R.layout.menu_list_element, null);
                        break;

                    case HEADER:
                        view = inflater.inflate(R.layout.menu_list_header, null);
                        break;

                }
            }

            switch (getItemViewType(i))
            {
                case MENU_ITEM:
                    TextView nameTextView = view.findViewById(R.id.NameTextView);
                    TextView priceTextView = view.findViewById(R.id.PriceTextView);



                    priceTextView.setText(String.valueOf(((Dish)(menuList.get(i))).price));
                    nameTextView.setText(((Dish)menuList.get(i)).name);
                    break;

                case HEADER:
                    TextView headerTextView = view.findViewById(R.id.HeaderTextView);

                    headerTextView.setText(((dishCategory)menuList.get(i)).name);
                    break;



            }
            return view;
        }
    }


    public class Addon {
        public int id;
        public String name;
        public float price;

        public Addon(int id, String name, float price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }
    public class Dish {

        public int id;
        public String name;
        public float price;
        public String descS;
        public String descL;
        //pulic int categoryID;

        public List<Addon> addons;

        public Dish(int id, String name, float price, String descS, String descL, List<Addon> addons) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.descS = descS;
            this.descL = descL;
            this.addons = addons;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class dishCategory {
        public int id;
        public String name;
        public List<Dish> dishes;

        dishCategory(int id, String name, List<Dish> items){
            this.id = id;
            this.name = name;
            dishes = items;
        }
    }

    public class Wish {

        public int id;
        public String name;
        public float price;
        public int amount;
        public int dishID;
        public List<Addon> addons;

        public Wish(int id, String name, float price, int amount, int dishID, List<Addon> addons) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.amount = amount;
            this.dishID = dishID;
            this.addons = addons;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }




}
