package com.amm.orderify.client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.amm.orderify.MainActivity;
import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.MainActivity.client;
import static com.amm.orderify.helpers.JBDCDriver.*;
import static com.amm.orderify.helpers.TimeAndDate.*;

public class MenuActivity extends AppCompatActivity {

    List<DishCategory> dishCategories;

    LinearLayout ordersLinearLayout;
    LinearLayout dishCategoriesLinearLayout;

    EditText enterCommentsEditText;

    TextView totalPriceTextView;

    List<Wish> wishes = new ArrayList<>();
    List<Addon> addons = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_menu_activity);



        dishCategories = getDishCategories();
        updateMenu();

        enterCommentsEditText = findViewById(R.id.EnterCommentsEditText);

        totalPriceTextView = findViewById(R.id.TotalPriceTextView);
        updateTotalPrice();

        ImageButton orderButton = findViewById(R.id.OrderButton);
        orderButton.setOnClickListener((View e) -> {
            addOrder();
        });

        ImageButton goToSummaryButton = findViewById(R.id.GoToSummaryButton);
        goToSummaryButton.setOnClickListener(e -> {
            this.startActivity(new Intent(this, SummaryActivity.class));
        });

        ImageButton cancelOrderButton = findViewById(R.id.CancelOrderButton);
        cancelOrderButton.setOnClickListener(e -> {
            this.startActivity(new Intent(this, MainActivity.class));
        });

        ImageButton askWaiterButton = findViewById(R.id.AskWaiterButton);
        askWaiterButton.setOnClickListener(e -> {
            if(client.state == 1) {
                client.state = 3;
                try {
                    ExecuteUpdate("UPDATE clients SET state = " + client.state +  " WHERE ID = " + client.id);
                } catch (SQLException ignored) {}
            }
        });


    }

    @SuppressLint("SetTextI18n")
    void updateTotalPrice(){
        float totalPrice = 0;
        for (int wishNumber = 0; wishNumber < wishes.size(); wishNumber++) totalPrice += wishes.get(wishNumber).getTotalPrice();
        totalPriceTextView.setText(totalPrice + " zł");
    }


    boolean checkIfTheSame(Wish w1, Wish w2){
        try {
            for (int i = 0; i < w1.addons.size(); i++) if (!(w1.addons.get(i).id == w2.addons.get(i).id)) return false;
            for (int i = 0; i < w2.addons.size(); i++) if (!(w1.addons.get(i).id == w2.addons.get(i).id)) return false;
            if (!(w1.dish.id == w2.dish.id)) return false;
            if (!(w1.addons.size() == w2.addons.size())) return false;
        }
        catch(Exception e){
            return false;
        }

        return true;
    }

    @SuppressLint("SetTextI18n")
    private void updateMenu() {
        if(dishCategoriesLinearLayout != null) dishCategoriesLinearLayout.removeAllViews();
        dishCategoriesLinearLayout = findViewById(R.id.DishCategoriesLinearLayout);
        for (int dishCategoryNumber = 0; dishCategoryNumber < dishCategories.size(); dishCategoryNumber++){

            DishCategory dishCategory = dishCategories.get(dishCategoryNumber);
            View dishCategoryElement = getLayoutInflater().inflate(R.layout.client_menu_dishcategory_element, null);

            TextView dishCategoryTextView = dishCategoryElement.findViewById(R.id.DishCategoryTextView);
            dishCategoryTextView.setText(dishCategory.name);

            LinearLayout dishesLinearLayout = dishCategoryElement.findViewById(R.id.DishesLinearLayout);
            for (int dishNumber = 0; dishNumber < dishCategory.dishes.size(); dishNumber++){
                Dish dish = dishCategory.dishes.get(dishNumber);
                View dishElement = getLayoutInflater().inflate(R.layout.client_menu_dish_element, null);

                TextView nameTextView = dishElement.findViewById(R.id.NameTextView);
                nameTextView.setText(dish.name);

                TextView priceTextView = dishElement.findViewById(R.id.PriceTextView);
                priceTextView.setText(dish.price + "");

                TextView shortDescriptionTextView = dishElement.findViewById(R.id.ShortDescriptionTextView);
                shortDescriptionTextView.setText(dish.descS);

                TextView longDescriptionTextView = dishElement.findViewById(R.id.LongDescriptionTextView);
                longDescriptionTextView.setText(dish.descL);

                ConstraintLayout cl = dishElement.findViewById(R.id.cl);
                ConstraintSet constraintSetCopy = new ConstraintSet();
                constraintSetCopy.clone(cl);


                ConstraintLayout menuExpand = dishElement.findViewById(R.id.MenuExpand);

                ImageButton menuBackgroundButton = dishElement.findViewById(R.id.MenuBackgroundButton);
                menuBackgroundButton.setOnClickListener(v -> {
                    if(menuExpand.getVisibility() == View.GONE) {
                        menuExpand.setVisibility(View.VISIBLE);
                        shortDescriptionTextView.setVisibility(View.GONE);
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(cl);
                        constraintSet.connect(R.id.PriceTextView,ConstraintSet.BOTTOM,R.id.MenuBackgroundButton,ConstraintSet.BOTTOM,0);
                        constraintSet.connect(R.id.PriceTextView,ConstraintSet.TOP,R.id.MenuBackgroundButton,ConstraintSet.TOP,0);
                        constraintSet.applyTo(cl);
                    }
                    else  {
                        menuExpand.setVisibility(View.GONE);
                        constraintSetCopy.applyTo(cl);
                        shortDescriptionTextView.setVisibility(View.VISIBLE);
                    }
                    addons = new ArrayList<>();
                });

                ImageView addToOrderButton = dishElement.findViewById(R.id.AddToOrderButton);
                addToOrderButton.setOnClickListener(e -> {
                    Wish newWish = new Wish(dish, 1, addons);
                    for(int wishI = 0; wishI < wishes.size(); wishI++){
                        if (checkIfTheSame(wishes.get(wishI), newWish)) {
                            wishes.get(wishI).amount++; break;}
                        if (wishI == wishes.size()-1) {
                            wishes.add(newWish); break;}
                    }
                    if (wishes.size() == 0) wishes.add(newWish);
                    updateOrders();
                    menuExpand.setVisibility(View.GONE);
                    addons = new ArrayList<>();
                });

                GridLayout addonCategoriesGridLayout = dishElement.findViewById(R.id.AddonCategoriesGridLayout);
                for (int addonCategoriesNumber = 0; addonCategoriesNumber < dish.addonCategories.size(); addonCategoriesNumber++) {
                    AddonCategory addonCategory = dish.addonCategories.get(addonCategoriesNumber);

                    View addonCategoryElement = getLayoutInflater().inflate(R.layout.client_menu_addoncategory_element, null);

                    TextView CategoryNameTextView = addonCategoryElement.findViewById(R.id.AddonCategoryNameTextView);
                    CategoryNameTextView.setText(addonCategory.name);

                    LinearLayout AddonsLinearLayout = addonCategoryElement.findViewById(R.id.AddonsLinearLayout);
                    for (int addonNumber = 0; addonNumber < addonCategory.addons.size(); addonNumber++) {
                        Addon addon = addonCategory.addons.get(addonNumber);
                        View addonElement = getLayoutInflater().inflate(R.layout.client_menu_addon_element, null);
                        TextView AddonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);
                        AddonNameTextView.setText(addon.name);
                        ImageView CheckboxCheckImage = addonElement.findViewById(R.id.CheckboxCheckImage);
                        if (addonCategory.multiChoice){
                            addonElement.setOnClickListener(e -> {
                                if(CheckboxCheckImage.getVisibility() == View.INVISIBLE) {
                                    CheckboxCheckImage.setVisibility(View.VISIBLE);
                                    addons.add(addon);
                                } else {
                                    CheckboxCheckImage.setVisibility(View.INVISIBLE);
                                    addons.remove(addon);
                                }
                            });
                        } else {
                            if (addonNumber == 0 && addonCategory.addons.size() > 1){ //czy działa?
                                CheckboxCheckImage.setVisibility(View.VISIBLE);
                                addons.add(addon); }
                            addonElement.setOnClickListener(e -> {
                                final int childCount = AddonsLinearLayout.getChildCount();
                                for(int ii = 0; ii < childCount; ii++) {
                                    View vv = AddonsLinearLayout.getChildAt(ii);
                                    ImageView iv = vv.findViewById(R.id.CheckboxCheckImage);
                                    iv.setVisibility(View.INVISIBLE);
                                    addons.remove(addonCategory.addons.get(ii));
                                }
                                CheckboxCheckImage.setVisibility(View.VISIBLE);
                                addons.add(addon);
                            });
                        }
                        AddonsLinearLayout.addView(addonElement);
                    }
                    addonCategoriesGridLayout.addView(addonCategoryElement);
                }
                dishesLinearLayout.addView(dishElement);
            }
            dishCategoriesLinearLayout.addView(dishCategoryElement);
        }
    }



    @SuppressLint("SetTextI18n")
    private void updateOrders() {
        if(ordersLinearLayout != null) ordersLinearLayout.removeAllViews();
        ordersLinearLayout = findViewById(R.id.OrdersLinearLayout);
        for (int wishNumber = 0; wishNumber < wishes.size(); wishNumber++) {
            View orderElement = getLayoutInflater().inflate(R.layout.client_menu_wish_element, null);

            TextView orderPriceTextView = orderElement.findViewById(R.id.orderPriceTextView);
            orderPriceTextView.setText(wishes.get(wishNumber).getTotalPrice() + " zł");

            TextView orderNameTextView = orderElement.findViewById(R.id.orderNameTextView);
            orderNameTextView.setText(wishes.get(wishNumber).dish.name);

            TextView QuantityTextView = orderElement.findViewById(R.id.QuantityTextView);
            QuantityTextView.setText(wishes.get(wishNumber).amount + "");

            final int finalWishNumber = wishNumber;
            ImageButton QuantityUpButton = orderElement.findViewById(R.id.QuantityUpButton);
            QuantityUpButton.setOnClickListener(e -> {
                wishes.get(finalWishNumber).amount++;
                updateOrders();
            });
            ImageButton QuantityDownButton = orderElement.findViewById(R.id.QuantityDownButton);
            QuantityDownButton.setOnClickListener(e -> {
                if (wishes.get(finalWishNumber).amount > 1) {
                    wishes.get(finalWishNumber).amount--;
                    updateOrders();
                }
            });
            ImageButton OrderCancelButton = orderElement.findViewById(R.id.OrderCancelButton);
            OrderCancelButton.setOnClickListener(v -> {
                wishes.remove(finalWishNumber);
                updateOrders();
            });
            ordersLinearLayout.addView(orderElement);
        }
        updateTotalPrice();

    }

    private List<DishCategory> getDishCategories() {

        List<Addon> addons = new ArrayList<>();
        List<AddonCategory> addonCategories = new ArrayList<>();
        List<Dish> dishes = new ArrayList<>();
        List<DishCategory> dishCategories = new ArrayList<>();

        try {
            Statement dishCategoriesS = getConnection().createStatement();
            ResultSet dishCategoriesRS = dishCategoriesS.executeQuery("SELECT * FROM dishCategories");
            while (dishCategoriesRS.next()) {
                Statement dishesS = getConnection().createStatement();
                ResultSet dishesRS = dishesS.executeQuery("SELECT * FROM dishes \n" +
                        "WHERE dishCategoryID = " + dishCategoriesRS.getInt("ID"));
                while (dishesRS.next()) {
                    Statement addonCategoriesS = getConnection().createStatement();
                    ResultSet addonCategoriesRS = addonCategoriesS.executeQuery("SELECT * FROM addonCategoriesToDishes\n" +
                            "JOIN addonCategories ON addonCategories.ID = addonCategoriesToDishes.addonCategoryID\n" +
                            "WHERE dishID = " + dishesRS.getInt("ID"));
                    while (addonCategoriesRS.next()) {
                        Statement addonsS = getConnection().createStatement();
                        ResultSet addonsRS = addonsS.executeQuery("SELECT * FROM addons \n" +
                                "WHERE addonCategoryID = " + addonCategoriesRS.getInt("addonCategoryID"));
                        while (addonsRS.next()) {
                            addons.add(new Addon(addonsRS.getInt("ID"), addonsRS.getString("name"), addonsRS.getFloat("price")));
                        }
                        addonCategories.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), addons));
                        addons = new ArrayList<>();
                    }
                    dishes.add(new Dish(dishesRS.getInt("ID"), dishesRS.getString("name"),
                            dishesRS.getFloat("price"), dishesRS.getString("descS"),
                            dishesRS.getString("descL"), addonCategories));
                    addonCategories = new ArrayList<>();
                }
                dishCategories.add(new DishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), dishes));
                dishes = new ArrayList<>();
            }
        } catch(SQLException ignore) { }
        return dishCategories;
    }

    private void addOrder(){
        try {
            ExecuteUpdate("INSERT INTO orders (time, date, comments, state, clientID)\n" +
                    "VALUES  ('" + getCurrentTime() +"', '" + getCurrentDate() +"', '" + enterCommentsEditText.getText() + "', 1, " + client.id + ");");
            int newOrderID = 0;
            ResultSet orderIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
            if(orderIDRS.next()) newOrderID = orderIDRS.getInt(1);

            for(int wishI = 0; wishI < wishes.size(); wishI++){
                ExecuteUpdate("INSERT INTO wishes (dishID, amount, orderID)\n" +
                        "VALUES  ("+ wishes.get(wishI).dish.id + ", " + wishes.get(wishI).amount + ", " + newOrderID + ");");

                int newWishID = 0;
                ResultSet wishIDRS = ExecuteQuery("SELECT LAST_INSERT_ID();");
                if(wishIDRS.next()) newWishID = wishIDRS.getInt(1);

                for(int addonI = 0; addonI < wishes.get(wishI).addons.size(); addonI++){
                    ExecuteUpdate("INSERT INTO addonsToWishes (wishID, addonID)\n" +
                            "VALUES  (" + newWishID + ", " + wishes.get(wishI).addons.get(addonI).id + ");");
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        wishes = new ArrayList<>();
        updateOrders();
    }

}

