package com.amm.orderify;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.amm.orderify.structure.*;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MenuActivity2 extends AppCompatActivity {
    public ListView orderListView;
    public ListView menuListView;
    public android.support.v7.widget.GridLayout addonCategoriesGridLayout;
    public int marginCopy;
    public int activeMenuElementNumber = -1;

    public List<String> names = new ArrayList<>();
    public List<String> prices = new ArrayList<>();
    public List<String> amounts = new ArrayList<>();

    public int orderID = 5;
    List<Addon> addonsORDER = new ArrayList<>();
    List<Wish> wishesORDER = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);



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


        //====================ORDER LIST=================================
        orderListView = findViewById(R.id.OrderListView);
        ViewGroup.LayoutParams lp = orderListView.getLayoutParams();
        //ViewGroup.LayoutParams lpb = lp;
        if (names.size() <= 3) lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        orderListView.setLayoutParams(lp);
        orderListView.setAdapter(new customAdapter(names, prices));

        //=====================MENU LIST====================================
        menuListView = findViewById(R.id.MenuListView);

        //DODANE NR 1 POCZĄTEK !!!!!!!!!!!!!!!!!!!!!!!
//        menuListView.setRecyclerListener(new AbsListView.RecyclerListener() {
//            @Override
//            public void onMovedToScrapHeap(View view) {
//                try
//                {
//                    ConstraintLayout cl = view.findViewById(R.id.MenuExpand);
//                    cl.setVisibility(View.GONE);
//                }
//                catch(Exception e)
//                {
//                    Log.wtf("Error", "Cos nie tak");
//                }
//            }
//        });
        //DODANE NR 1 KONIEC !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        List<Addon> addonsMENU = new ArrayList<>();
        List<AddonCategory> addonCategoryMENU = new ArrayList<>();
        List<Dish> dishesMENU = new ArrayList<>();
        List<DishCategory> dishCategoriesMENU = new ArrayList<>();


        try {
            Statement dishCategoriesS = getConnection().createStatement();
            ResultSet dishCategoriesRS = dishCategoriesS.executeQuery("SELECT * FROM dishesCategories");
            while (dishCategoriesRS.next()) {
                Statement dishesS = getConnection().createStatement();
                ResultSet dishesRS = dishesS.executeQuery("SELECT * FROM dishes \n" +
                        "WHERE categoryID = " + dishCategoriesRS.getInt("ID"));
                while (dishesRS.next()) {
                    Statement addonCategoriesS = getConnection().createStatement();
                    ResultSet addonCategoriesRS = addonCategoriesS.executeQuery("SELECT * FROM addonsCategoriesToDishes\n" +
                            "JOIN addonsCategories ON addonsCategories.ID = addonsCategoriesToDishes.addonCategoryID\n" +
                            "WHERE dishID = " + dishesRS.getInt("ID"));
                    while (addonCategoriesRS.next()) {
                        Statement addonsS = getConnection().createStatement();
                        ResultSet addonsRS = addonsS.executeQuery("SELECT * FROM addons \n" +
                                "WHERE addonCategoryID = " + addonCategoriesRS.getInt("addonCategoryID"));
                        while (addonsRS.next()) {
                            addonsMENU.add(new Addon(addonsRS.getInt("ID"), addonsRS.getString("name"), addonsRS.getFloat("price")));
                        }
                        addonCategoryMENU.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonsMENU));
                        addonsMENU = new ArrayList<>();
                    }
                    dishesMENU.add(new Dish(dishesRS.getInt("ID"), dishesRS.getString("name"),
                            dishesRS.getFloat("price"), dishesRS.getString("descS"),
                            dishesRS.getString("descL"), addonCategoryMENU));
                    addonCategoryMENU = new ArrayList<>();
                }
                dishCategoriesMENU.add(new DishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), dishesMENU));
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



        menuListView.setAdapter(new customMenuAdapter(this, menuList));


    }



    class customMenuAdapter extends BaseAdapter {
        List<Object> menuList;
        List<Addon> checkedDishAddons;

        private static final int MENU_ITEM = 0;
        private static final int HEADER = 1;
        private LayoutInflater menuInflater;

        customMenuAdapter(Context context, List<Object> menuList)
        {
            this.menuList = menuList;
            menuInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getItemViewType(int i)
        {
            return i;

        }

        public int getItemType(int i)
        {
            if (menuList.get(i) instanceof DishCategory) return HEADER;
            else return MENU_ITEM;
        }

        @Override
        public int getViewTypeCount()
        {
            return getCount();
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
            if (view == null) {
                switch (getItemType(i)) {
                    case MENU_ITEM:
                        view = menuInflater.inflate(R.layout.menu_list_element, null);
                        break;

                    case HEADER:
                        view = menuInflater.inflate(R.layout.menu_list_header, null);
                        break;
                }
            }

            switch (getItemType(i))
            {
                case MENU_ITEM:
                    ConstraintLayout MenuExpand = view.findViewById(R.id.MenuExpand);

                    TextView nameTextView = view.findViewById(R.id.NameTextView);
                    nameTextView.setText(((Dish)menuList.get(i)).name);

                    TextView priceTextView = view.findViewById(R.id.PriceTextView);
                    priceTextView.setText(String.valueOf(((Dish)(menuList.get(i))).price));

                    ImageButton MenuBackgroundButton = view.findViewById(R.id.MenuBackgroundButton);
                    MenuBackgroundButton.setOnClickListener(v -> {
                        if(MenuExpand.getVisibility() == View.GONE)
                        {
//                                if (activeMenuElementNumber != -1)
//                                {
//                                    View vw = menuListView.getChildAt(activeMenuElementNumber);
//                                    ConstraintLayout cl = vw.findViewById(R.id.MenuExpand);
//                                    cl.setVisibility(View.GONE);
//                                    notifyDataSetChanged();
//
//                                }
//                            for (int ii = 0; i < menuList.size(); ii++)
//                            {
//                                if (getItemViewType(ii) == MENU_ITEM)
//                                {
//                                    View vw = menuListView.getChildAt(ii);
//                                    ConstraintLayout cl = vw.findViewById(R.id.MenuExpand);
//                                    cl.setVisibility(View.GONE);
//                                }
//
//                            }
                            MenuExpand.setVisibility(View.VISIBLE);
                            //activeMenuElementNumber = i;


                            notifyDataSetChanged();

                        }
                        else if (MenuExpand.getVisibility() == View.VISIBLE)
                        {
                            MenuExpand.setVisibility(View.GONE);
                            //activeMenuElementNumber = -1;
                        }
                        Log.wtf("Active element", activeMenuElementNumber + "");
                    });

                    if(MenuExpand.getVisibility() != View.GONE) {

                        addonCategoriesGridLayout = view.findViewById(R.id.AddonCategoriesGridLayout);
                        LayoutInflater gridInflater = getLayoutInflater();
                        addonCategoriesGridLayout.removeAllViews();


                        Dish dish = (Dish)menuList.get(i);
                        AddonCategory addonCategory;
                        Addon addon;

                        android.support.v7.widget.AppCompatImageView AddOrderButton = view.findViewById(R.id.AddOrderButton);
                        AddOrderButton.setOnClickListener(e -> {
                            Wish newWish = new Wish(dish, 1, checkedDishAddons);
                            for(int wishI = 0; wishI < wishesORDER.size(); wishI++){
                                if (wishesORDER.get(wishI).addons == newWish.addons && wishesORDER.get(wishI).dish == newWish.dish) {wishesORDER.get(wishI).amount++; break;}
                                if (wishI == wishesORDER.size()-1) wishesORDER.add(newWish);
                            }
                            if (wishesORDER.size() == 0) wishesORDER.add(newWish);
                            Log.wtf("dupa", wishesORDER.size() + "");
                        });


                        for (int categoryIterator = 0; categoryIterator < dish.addonCategories.size(); categoryIterator++) {
                            addonCategory = dish.addonCategories.get(categoryIterator);
                            View v = gridInflater.inflate(R.layout.expand_grid_element, null);
                            TextView CategoryNameTextView = v.findViewById(R.id.CategoryNameTextView);
                            LinearLayout AddonsLinearLayout = v.findViewById(R.id.AddonsLinearLayout);
                            CategoryNameTextView.setText(addonCategory.name); //cat name
                            for (int addonIterator = 0; addonIterator < addonCategory.addons.size(); addonIterator++ ) {
                                addon = addonCategory.addons.get(addonIterator);
                                View x = gridInflater.inflate(R.layout.expand_addon_list_element, null);
                                TextView AddonNameTextView = x.findViewById(R.id.AddonNameTextView);
                                AddonNameTextView.setText(addon.name); //addon
                                AddonsLinearLayout.addView(x);
                            }
                            addonCategoriesGridLayout.addView(v);
                        }
                    }
                    break;

                case HEADER:
                    TextView headerTextView = view.findViewById(R.id.HeaderTextView);

                    headerTextView.setText(((DishCategory)menuList.get(i)).name);
                    break;



            }
            return view;
        }
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
            //Button orderCancelButton = convertView.findViewById(R.id.OrderCancelButton);

            nameTextView.setText(names.get(position));
            priceTextView.setText(prices.get(position));
//            orderCancelButton.setOnClickListener(e -> {
//                try {
//                    ExecuteUpdate("DELETE FROM wishes \n" +
//                            "WHERE wishID = " + orderID + "\n" +
//                            "GROUP BY dishes.name;");
//                } catch (SQLException e1) {
//                    e1.printStackTrace();
//                }
//            });
            return convertView;
        }
    }




}

