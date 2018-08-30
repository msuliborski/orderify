package com.amm.orderify.client;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.ArrayMap;
import android.util.Log;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.amm.orderify.MainActivity.thisClientID;
import static com.amm.orderify.MainActivity.thisTableID;
import static com.amm.orderify.helpers.Comparators.wishesTheSame;
import static com.amm.orderify.helpers.FetchDataFromDatabase.getFullMenuData;
import static com.amm.orderify.helpers.JBDCDriver.*;
import static com.amm.orderify.helpers.TimeAndDate.*;

public class MenuActivity extends AppCompatActivity {

    boolean blMyAsyncTask;
    UpdateMenuTask task = new UpdateMenuTask();

    LinearLayout ordersLinearLayout;
    LinearLayout dishCategoriesLinearLayout;
    ConstraintLayout freezeButtonScreen;

    EditText enterCommentsEditText;
    TextView totalPriceTextView;

    List<Wish> wishes = new ArrayList<>();
    ArrayMap<Integer,DishCategory> dishCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_menu_activity);

        dishCategories = getFullMenuData();
        updateMenu();

        Log.wtf("thisTableID", thisTableID + "");
        Log.wtf("thisClientID", thisClientID + "");

        enterCommentsEditText = findViewById(R.id.EnterCommentsEditText);
        freezeButtonScreen = findViewById(R.id.FreezeButtonScreen);
        totalPriceTextView = findViewById(R.id.TotalPriceTextView);
        updateOrderTotalPrice();

        ImageButton orderButton = findViewById(R.id.OrderButton);
        orderButton.setOnClickListener((View e) -> {
            if(!wishes.equals(new ArrayList<>())) {
                addOrder();
                this.startActivity(new Intent(this, SummaryActivity.class));
            }// else { popup ze jest pusty order }
        });

        ImageButton goToSummaryButton = findViewById(R.id.GoToSummaryButton);
        goToSummaryButton.setOnClickListener(e -> this.startActivity(new Intent(this, SummaryActivity.class)));

        ImageButton cancelOrderButton = findViewById(R.id.CancelOrderButton);
        cancelOrderButton.setOnClickListener(e -> this.startActivity(new Intent(this, MainActivity.class)));

        ImageButton askWaiterButton = findViewById(R.id.AskWaiterButton);
        askWaiterButton.setOnClickListener(e -> {
            try {
                ExecuteUpdate("UPDATE clients SET state = 4 WHERE ID = " + thisClientID);
            } catch (SQLException ignored) {}
        });
    }

    private void updateOrderTotalPrice(){
        float totalPrice = 0;
        for (int wishNumber = 0; wishNumber < wishes.size(); wishNumber++) totalPrice += wishes.get(wishNumber).getTotalPrice();

        DecimalFormat formatter = new DecimalFormat("0.00");
        String totalPriceString = formatter.format(totalPrice) + " zł";
        totalPriceTextView.setText(totalPriceString);
    }


    private void updateMenu() {
        if(dishCategoriesLinearLayout != null) dishCategoriesLinearLayout.removeAllViews();
        //dishCategories.sort(Comparator.comparing(object -> String.valueOf(object.id))); //sort
        dishCategoriesLinearLayout = findViewById(R.id.DishCategoriesLinearLayout);
        for (int dishCategoryNumber = 0; dishCategoryNumber < dishCategories.size(); dishCategoryNumber++){
            //dishCategory.dishes.sort(Comparator.comparing(object -> String.valueOf(object.name))); //sort
            View dishCategoryElement = getLayoutInflater().inflate(R.layout.client_menu_element_dishcategory, null);
            TextView dishCategoryTextView = dishCategoryElement.findViewById(R.id.DishCategoryTextView);

            DishCategory dishCategory = dishCategories.valueAt(dishCategoryNumber);

            dishCategoryTextView.setText(dishCategory.name);

            LinearLayout dishesLinearLayout = dishCategoryElement.findViewById(R.id.DishesLinearLayout);
            for (int dishNumber = 0; dishNumber < dishCategory.dishes.size(); dishNumber++){
                //dish.addonCategories.sort(Comparator.comparing(object -> String.valueOf(object.name))); //sort

                View dishElement = getLayoutInflater().inflate(R.layout.client_menu_element_dish, null);
                TextView nameTextView = dishElement.findViewById(R.id.NameTextView);
                TextView priceTextView = dishElement.findViewById(R.id.PriceTextView);
                TextView shortDescriptionTextView = dishElement.findViewById(R.id.ShortDescriptionTextView);
                TextView longDescriptionTextView = dishElement.findViewById(R.id.LongDescriptionTextView);
                ImageButton menuBackgroundButton = dishElement.findViewById(R.id.MenuBackgroundButton);
                ImageView addToOrderButton = dishElement.findViewById(R.id.AddToOrderButton);

                Dish dish = dishCategory.dishes.valueAt(dishNumber);

                nameTextView.setText(dish.name);
                priceTextView.setText(dish.getPriceString());
                shortDescriptionTextView.setText(dish.descS);
                longDescriptionTextView.setText(dish.descL);

                ConstraintLayout cl = dishElement.findViewById(R.id.cl);
                ConstraintSet constraintSetCopy = new ConstraintSet();
                constraintSetCopy.clone(cl);

                ConstraintLayout menuExpand = dishElement.findViewById(R.id.MenuExpand);

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
                });

                addToOrderButton.setOnClickListener(e -> {
                    Wish newWish = new Wish((int) (new Date()).getTime(), dish, 1, new ArrayMap<>(dish.chosenAddons));
                    for(int wishNumber = 0; wishNumber < wishes.size(); wishNumber++){
                        if (wishesTheSame(wishes.get(wishNumber), newWish)) {
                            wishes.get(wishNumber).amount++; break;}
                        if (wishNumber == wishes.size() - 1) {
                            wishes.add(newWish); break;}
                    }
                    if (wishes.size() == 0) wishes.add(newWish);
                    updateWishes();
                });

                GridLayout addonCategoriesGridLayout = dishElement.findViewById(R.id.AddonCategoriesGridLayout);
                for (int addonCategoriesNumber = 0; addonCategoriesNumber < dish.addonCategories.size(); addonCategoriesNumber++) {
                    AddonCategory addonCategory = dish.addonCategories.valueAt(addonCategoriesNumber);
                    //addonCategory.addons.sort(Comparator.comparing(object -> String.valueOf(object.name))); //sort

                    View addonCategoryElement = getLayoutInflater().inflate(R.layout.client_menu_element_addoncategory, null);

                    TextView CategoryNameTextView = addonCategoryElement.findViewById(R.id.AddonCategoryNameTextView);
                    CategoryNameTextView.setText(addonCategory.name);

                    LinearLayout AddonsLinearLayout = addonCategoryElement.findViewById(R.id.AddonsLinearLayout);
                    for (int addonNumber = 0; addonNumber < addonCategory.addons.size(); addonNumber++) {
                        Addon addon = addonCategory.addons.valueAt(addonNumber);
                        View addonElement = getLayoutInflater().inflate(R.layout.client_menu_element_addon, null);
                        TextView AddonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);
                        String addonNameString = addon.name + "(" + addon.getPriceString() + ")";
                        AddonNameTextView.setText(addonNameString);
                        ImageView CheckboxCheckImage = addonElement.findViewById(R.id.CheckboxCheckImage);
                        if (addonCategory.multiChoice){
                            addonElement.setOnClickListener(e -> {
                                if(CheckboxCheckImage.getVisibility() == View.INVISIBLE) {
                                    CheckboxCheckImage.setVisibility(View.VISIBLE);
                                    dish.chosenAddons.put(addon.id, addon);
                                } else {
                                    CheckboxCheckImage.setVisibility(View.INVISIBLE);
                                    dish.chosenAddons.remove(addon.id);
                                }
                            });
                        } else {
                            if (addonNumber == 0){
                                CheckboxCheckImage.setVisibility(View.VISIBLE);
                                dish.chosenAddons.put(addon.id, addon);
                            }
                            addonElement.setOnClickListener(e -> {
                                for(int i = 0; i < AddonsLinearLayout.getChildCount(); i++) {
                                    AddonsLinearLayout.getChildAt(i).findViewById(R.id.CheckboxCheckImage).setVisibility(View.INVISIBLE);
                                    dish.chosenAddons.remove(addonCategory.addons.valueAt(i).id);
                                }
                                CheckboxCheckImage.setVisibility(View.VISIBLE);
                                dish.chosenAddons.put(addon.id, addon);
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


    private void updateWishes() {
        if(ordersLinearLayout != null) ordersLinearLayout.removeAllViews();
        ordersLinearLayout = findViewById(R.id.OrdersLinearLayout);
        //wishes.sort(Comparator.comparing(object -> String.valueOf(object.dish.dishCategoryID))); //sort
        for (int wishNumber = 0; wishNumber < wishes.size(); wishNumber++) {
            View orderElement = getLayoutInflater().inflate(R.layout.client_menu_element_wish, null);
            TextView orderPriceTextView = orderElement.findViewById(R.id.orderPriceTextView);
            TextView orderNameTextView = orderElement.findViewById(R.id.orderNameTextView);
            TextView QuantityTextView = orderElement.findViewById(R.id.QuantityTextView);

            Wish wish = wishes.get(wishNumber);

            orderPriceTextView.setText(wish.getTotalPriceString());
            orderNameTextView.setText(wish.dish.name);
            QuantityTextView.setText(wish.getAmountString());

            ImageButton QuantityUpButton = orderElement.findViewById(R.id.QuantityUpButton);
            QuantityUpButton.setOnClickListener(e -> {
                wish.amount++;
                updateWishes();
            });
            ImageButton QuantityDownButton = orderElement.findViewById(R.id.QuantityDownButton);
            QuantityDownButton.setOnClickListener(e -> {
                if (wish.amount > 1) {
                    wish.amount--;
                    updateWishes();
                }
            });
            ImageButton OrderCancelButton = orderElement.findViewById(R.id.OrderCancelButton);
            OrderCancelButton.setOnClickListener(v -> {
                wishes.remove(wish);
                updateWishes();
            });
            ordersLinearLayout.addView(orderElement);
        }
        updateOrderTotalPrice();
    }

    private void addOrder(){
        try {
            ExecuteUpdate("INSERT INTO padlock (TTL) VALUES (15); \n");
            int lockID = 0;
            ResultSet lockIDRS = ExecuteQuery("SELECT LAST_INSERT_ID(); \n");
            if(lockIDRS.next()) lockID = lockIDRS.getInt(1);

            ExecuteUpdate("INSERT INTO orders (time, date, comments, state, clientID) \n" +
                    "VALUES  ('" + getCurrentTime() +"', '" + getCurrentDate() +"', '" + enterCommentsEditText.getText() + "', 1, " + thisClientID + "); \n");
            ExecuteUpdate( "INSERT INTO newOrders (time, date, comments, state, clientID) \n" +
                        "VALUES  ('" + getCurrentTime() +"', '" + getCurrentDate() +"', '" + enterCommentsEditText.getText() + "', 1, " + thisClientID + "); \n");

            int newOrderID = 0;
            ResultSet orderIDRS = ExecuteQuery("SELECT LAST_INSERT_ID(); \n");
            if(orderIDRS.next()) newOrderID = orderIDRS.getInt(1);

            for(int wishNumber = 0; wishNumber < wishes.size(); wishNumber++){
                ExecuteUpdate("INSERT INTO wishes (dishID, amount, orderID) \n" +
                        "VALUES  ("+ wishes.get(wishNumber).dish.id + ", " + wishes.get(wishNumber).amount + ", " + newOrderID + "); \n");
                ExecuteUpdate("INSERT INTO newWishes (dishID, amount, orderID) \n" +
                        "VALUES  ("+ wishes.get(wishNumber).dish.id + ", " + wishes.get(wishNumber).amount + ", " + newOrderID + "); \n");

                int newWishID = 0;
                ResultSet wishIDRS = ExecuteQuery("SELECT LAST_INSERT_ID(); \n");
                if(wishIDRS.next()) newWishID = wishIDRS.getInt(1);

                for(int addonNumber = 0; addonNumber < wishes.get(wishNumber).addons.size(); addonNumber++){
                    ExecuteUpdate("INSERT INTO addonsToWishes (wishID, addonID) \n" +
                            "VALUES  (" + newWishID + ", " + wishes.get(wishNumber).addons.valueAt(addonNumber).id + "); \n");
                    ExecuteUpdate("INSERT INTO newAddonsToWishes (wishID, addonID) \n" +
                            "VALUES  (" + newWishID + ", " + wishes.get(wishNumber).addons.valueAt(addonNumber).id + "); \n");
                }
            }
            ExecuteUpdate("DELETE FROM padlock WHERE ID = " + lockID);
        } catch (Exception e) { Log.wtf("SQL Exeption", e.getMessage()+""); }
        wishes = new ArrayList<>();
        updateWishes();
    }

    private void refreshTableState() {
        try {
            int state = 1;
            ResultSet stateRS = ExecuteQuery("SELECT state FROM tables WHERE ID = " + thisTableID);
            if(stateRS.next()) state = stateRS.getInt("state");

            if (state == 2) runOnUiThread(() -> freezeButtonScreen.setVisibility(View.VISIBLE));
            else runOnUiThread(() -> freezeButtonScreen.setVisibility(View.GONE));

        } catch (Exception ignored) {}
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (blMyAsyncTask)   {
            blMyAsyncTask = false;
            task.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        task = new UpdateMenuTask();
        task.execute();
    }

    protected class UpdateMenuTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            blMyAsyncTask = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(true) {
                try {
                    Thread.sleep(100);
                    refreshTableState();

                    if(Thread.interrupted()) break;
                    if (!blMyAsyncTask) break;
                } catch (InterruptedException ignored) {}
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            blMyAsyncTask = false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}

