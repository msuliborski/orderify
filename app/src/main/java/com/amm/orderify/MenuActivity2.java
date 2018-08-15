package com.amm.orderify;

import android.content.Context;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.amm.orderify.structure.*;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MenuActivity2 extends AppCompatActivity {
    public LinearLayout orderListLinearLayout;
    public ListView menuListView;
    public android.support.v7.widget.GridLayout addonCategoriesGridLayout;
    public int marginCopy;
    public int activeMenuElementNumber = -1;


    public int orderID = 5;
    public int wishID = 0;
    List<Wish> wishesORDER = new ArrayList<>();
    private EditText EnterCommentsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);



        //====================ORDER LIST=================================
        orderListLinearLayout = findViewById(R.id.OrderListLinearLayout);


        //=====================MENU LIST====================================
        menuListView = findViewById(R.id.MenuListView);

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
                        addonCategoryMENU.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getBoolean("multiChoice"), addonsMENU));
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

        EnterCommentsEditText = findViewById(R.id.EnterCommentsEditText);

        ImageButton orderButton = findViewById(R.id.OrderButton);
        orderButton.setOnClickListener((View e) -> {
            try {
            ExecuteUpdate("INSERT INTO `orders` (`time`, `date`, `tableID`, `comments`)\n" +
                    "VALUES  ('21:32:22', '2018-07-31', 1, '" + EnterCommentsEditText.getText() + "');");

            ResultSet orderIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
            while (orderIDRS.next()){
                orderID = orderIDRS.getInt(1);
            }

            for(int wishI = 0; wishI < wishesORDER.size(); wishI++){
                    ExecuteUpdate("INSERT INTO `wishes` (`dishID`, `amount`, `orderID`)\n" +
                                        "VALUES  ("+ wishesORDER.get(wishI).dish.id + ", " + wishesORDER.get(wishI).amount + ", " + orderID + ");");

                    ResultSet wishIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
                    while (wishIDRS.next()){
                        wishID = wishIDRS.getInt(1);
                    }

                    for(int addonI = 0; addonI < wishesORDER.get(wishI).addons.size(); addonI++){
                        ExecuteUpdate("INSERT INTO `addonsToWishes` (`wishID`, `addonID`)\n" +
                                            "VALUES  (" + wishID + ", " + wishesORDER.get(wishI).addons.get(addonI).id + ");");
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            wishesORDER = new ArrayList<>();
            refreshOrderList();

        });
    }



    class customMenuAdapter extends BaseAdapter {
        List<Object> menuList;
        List<Addon> clickedAddons = new ArrayList<>();

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

            switch (getItemType(i)) {
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
                                if (activeMenuElementNumber != -1)
                                {   try
                                    {
                                        View vw = menuListView.getChildAt(activeMenuElementNumber - menuListView.getFirstVisiblePosition());
                                        ConstraintLayout cl = vw.findViewById(R.id.MenuExpand);
                                        cl.setVisibility(View.GONE);
                                        notifyDataSetChanged();
                                    } catch (Exception e) {}

                                }

                            MenuExpand.setVisibility(View.VISIBLE);
                            activeMenuElementNumber = i;

                            notifyDataSetChanged();
                        } else if (MenuExpand.getVisibility() == View.VISIBLE) {
                            MenuExpand.setVisibility(View.GONE);
                            activeMenuElementNumber = -1;
                        }

                        clickedAddons = new ArrayList<>();
                    });

                    if(MenuExpand.getVisibility() != View.GONE) {

                        addonCategoriesGridLayout = view.findViewById(R.id.AddonCategoriesGridLayout);
                        LayoutInflater gridInflater = getLayoutInflater();
                        addonCategoriesGridLayout.removeAllViews();


                        Dish dish = (Dish)menuList.get(i);



                        android.support.v7.widget.AppCompatImageView AddOrderButton = view.findViewById(R.id.AddOrderButton);
                        AddOrderButton.setOnClickListener(e -> {
                            Wish newWish = new Wish(dish, 1, clickedAddons);
                            for(int wishI = 0; wishI < wishesORDER.size(); wishI++){
                                Log.wtf("dupa", wishesORDER.get(wishI).addons.size() + "    kurwa    " + newWish.addons.size());
                                if (checkIfTheSame(wishesORDER.get(wishI), newWish)) {wishesORDER.get(wishI).amount++; break;}
                                if (wishI == wishesORDER.size()-1) {wishesORDER.add(newWish); break;}
                            }
                            if (wishesORDER.size() == 0) wishesORDER.add(newWish);
                            refreshOrderList();
                            MenuExpand.setVisibility(View.GONE);
                            activeMenuElementNumber = -1;
                            clickedAddons = new ArrayList<>();
                        });

                        for (int categoryI = 0; categoryI < dish.addonCategories.size(); categoryI++) {
                            AddonCategory addonCategory = dish.addonCategories.get(categoryI);
                            View v = gridInflater.inflate(R.layout.expand_grid_element, null);
                            TextView CategoryNameTextView = v.findViewById(R.id.CategoryNameTextView);
                            LinearLayout AddonsLinearLayout = v.findViewById(R.id.AddonsLinearLayout);
                            CategoryNameTextView.setText(addonCategory.name); //cat name
                            for (int addonI = 0; addonI < addonCategory.addons.size(); addonI++ ) {

                                Addon addon = addonCategory.addons.get(addonI);
                                View x = gridInflater.inflate(R.layout.expand_addon_list_element, null);

                                TextView AddonNameTextView = x.findViewById(R.id.AddonNameTextView);
                                ImageView CheckboxCheckImage = x.findViewById(R.id.CheckboxCheckImage);

                                if (addonCategory.multiChoice){
                                    x.setOnClickListener(e -> {
                                        if(CheckboxCheckImage.getVisibility() == View.INVISIBLE) {
                                            CheckboxCheckImage.setVisibility(View.VISIBLE);
                                            clickedAddons.add(addon);
                                        } else {
                                            CheckboxCheckImage.setVisibility(View.INVISIBLE);
                                            clickedAddons.remove(addon);
                                        }

                                    });
                                } else{
                                    if (addonI == 0){
                                        CheckboxCheckImage.setVisibility(View.VISIBLE);
                                        clickedAddons.add(addon); }

                                    x.setOnClickListener(e -> {

                                        final int childCount = AddonsLinearLayout.getChildCount();
                                        for (int ii = 0; ii < childCount; ii++) {
                                            View vv = AddonsLinearLayout.getChildAt(ii);
                                            ImageView iv = vv.findViewById(R.id.CheckboxCheckImage);
                                            iv.setVisibility(View.INVISIBLE);
                                            clickedAddons.remove(addonCategory.addons.get(ii));
                                        }


                                        CheckboxCheckImage.setVisibility(View.VISIBLE);
                                        clickedAddons.add(addon);
                                    });



                                }
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

    boolean checkIfTheSame(Wish w1, Wish w2){
        try {
            for (int i = 0; i < w1.addons.size(); i++) {
                if (!(w1.addons.get(i).id == w2.addons.get(i).id)) return false;
            }
            for (int i = 0; i < w2.addons.size(); i++) {
                if (!(w1.addons.get(i).id == w2.addons.get(i).id)) return false;
            }
            if (!(w1.dish.id == w2.dish.id)) return false;
            if (!(w1.addons.size() == w2.addons.size())) return false;
        }
        catch(Exception e){
            return false;
        }

        return true;

    }

    void refreshOrderList ()
    {
        orderListLinearLayout.removeAllViews();

        for (int wishNumber = 0; wishNumber < wishesORDER.size(); wishNumber++)
        {
            LayoutInflater orderListInflater = getLayoutInflater();
            View x = orderListInflater.inflate(R.layout.order_list_element, null);
            TextView orderPriceTextView = x.findViewById(R.id.orderPriceTextView);
            TextView orderNameTextView = x.findViewById(R.id.orderNameTextView);
            TextView QuantityTextView = x.findViewById(R.id.QuantityTextView);
            ImageButton OrderCancelButton = x.findViewById(R.id.OrderCancelButton);
            ImageButton QuantityUpButton = x.findViewById(R.id.QuantityUpButton);
            ImageButton QuantityDownButton = x.findViewById(R.id.QuantityDownButton);

            QuantityTextView.setText(wishesORDER.get(wishNumber).amount + "");

            final int finalWishNumber = wishNumber;

            QuantityUpButton.setOnClickListener(e -> {
                wishesORDER.get(finalWishNumber).amount++;
                refreshOrderList();
            });

            QuantityDownButton.setOnClickListener(e -> {
                if (wishesORDER.get(finalWishNumber).amount > 1)
                {
                    wishesORDER.get(finalWishNumber).amount--;
                    refreshOrderList();
                }
            });


            OrderCancelButton.setOnClickListener(v -> {
                wishesORDER.remove(finalWishNumber);
                refreshOrderList();
            }
            );

            orderPriceTextView.setText(wishesORDER.get(wishNumber).dish.price + " zł");
            orderNameTextView.setText(wishesORDER.get(wishNumber).dish.name);
            orderListLinearLayout.addView(x);

        }

    }

}

