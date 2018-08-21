package com.amm.orderify.bar;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class TablesActivity extends AppCompatActivity {
    List<Table> tables = new ArrayList<>();

    LinearLayout tablesLinearLayout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_tables_activity);

        Button refreshButton = findViewById(R.id.RefreshButton);
        refreshButton.setOnClickListener(v -> {
            updateMenu();
        });

        tables = getTables();
        updateMenu();

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                updateAdapter();
//                handler.postDelayed(this, 1000);
//            }}, 1000);
    }

    @SuppressLint("SetTextI18n")
    private void updateMenu() {
        if(tablesLinearLayout != null) tablesLinearLayout.removeAllViews();
        tablesLinearLayout = findViewById(R.id.TablesLinearLayout);

        for (int tableNumber = 0; tableNumber < tables.size(); tableNumber++){
            Table table = tables.get(tableNumber);
            View tableElement = getLayoutInflater().inflate(R.layout.bar_table_element, null);

            TextView tableNumberTextView = tableElement.findViewById(R.id.TableNumberTextView);
            TextView overallPriceTextView = tableElement.findViewById(R.id.OverallPriceTextView);
            TextView tableStateTextView = tableElement.findViewById(R.id.TableStateTextView);

            Button acceptRequestButton = tableElement.findViewById(R.id.AcceptRequestButton);
            Button expandCollapseButton = tableElement.findViewById(R.id.ExpandCollapseButton);
            Button freezeStateButton = tableElement.findViewById(R.id.FreezeStateButton);

            LinearLayout ordersLinearLayout = tableElement.findViewById(R.id.OrdersLinearLayout);
            tableNumberTextView.setText(table.number + "");
            overallPriceTextView.setText(table.getTotalPrice() + " zł");
            tableStateTextView.setText(table.state + " - tableState");

            if(table.state == 1) freezeStateButton.setText("freeze table");
            else freezeStateButton.setText("unfreeze table");

            //states: 1-unfreezed, 2-freezed, 3-wantsHelp =============================================================
            acceptRequestButton.setOnClickListener(v -> {
                if(table.state == 3) {
                    table.state = 1;
                    try {
                        ExecuteUpdate("UPDATE tables SET state = " + table.state +  " WHERE ID = " + table.id);
                    } catch (SQLException ignored) {}
                }
            });

            expandCollapseButton.setOnClickListener(v -> {
                if(ordersLinearLayout.getVisibility() == View.GONE) ordersLinearLayout.setVisibility(View.VISIBLE);
                else ordersLinearLayout.setVisibility(View.GONE);
            });

            freezeStateButton.setOnClickListener(v -> {
                if(table.state == 1) {
                    table.state = 2;
                    freezeStateButton.setText("unfreeze table");
                } else {
                    table.state = 1;
                    freezeStateButton.setText("freeze table");
                }
                try {
                    ExecuteUpdate("UPDATE tables SET state = " + table.state +  " WHERE ID = " + table.id);
                } catch (SQLException ignored) {}
            });

            for(int orderNumber = 0; orderNumber < table.orders.size(); orderNumber++) {
                Order order = table.orders.get(orderNumber);
                View orderElement = getLayoutInflater().inflate(R.layout.bar_order_element, null);

                TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
                orderNumberTextView.setText(order.id + "");

                TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
                orderWaitingTimeTextView.setText("04:21");

                TextView orderPriceTextView = orderElement.findViewById(R.id.OrderPriceTextView);
                orderPriceTextView.setText(order.getTotalPrice() + " zł");

                TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
                orderStateTextView.setText(order.state + " - orderState");

                //states: 1-inPreparation, 2-doneAndDelivered =========================================================================
                Button changeOrderStateButton = orderElement.findViewById(R.id.ChangeOrderStateButton);
                if (order.state == 1) changeOrderStateButton.setVisibility(View.VISIBLE);
                else changeOrderStateButton.setVisibility(View.GONE);
                changeOrderStateButton.setOnClickListener(v -> {
                    if(order.state == 1) {
                        order.state = 2;
                        changeOrderStateButton.setVisibility(View.GONE);
                        orderStateTextView.setText(order.state + " - orderState");
                    }
                    try {
                        ExecuteUpdate("UPDATE orders SET state = " + order.state +  " WHERE ID = " + order.id);
                    } catch (SQLException ignored) {}
                });

                ImageButton deleteOrderButton = orderElement.findViewById(R.id.DeleteOrderButton);
                deleteOrderButton.setOnClickListener(v -> {
                    try {
                        ExecuteUpdate("DELETE FROM orders WHERE ID = " + order.id);
                    } catch (SQLException ignored) {}
                });

                TextView commentsTextView = orderElement.findViewById(R.id.CommentsTextView);
                commentsTextView.setText(order.comments);

                LinearLayout wishesLinearLayout = orderElement.findViewById(R.id.WishesLinearLayout);
                for (int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                    Wish wish = order.wishes.get(wishNumber);
                    View wishElement = getLayoutInflater().inflate(R.layout.bar_wish_element, null);

                    TextView dishNameTextView = wishElement.findViewById(R.id.DishNameTextView);
                    dishNameTextView.setText(wish.dish.name);

                    LinearLayout addonsLinearLayout = wishElement.findViewById(R.id.AddonsLinearLayout);
                    for(int addonNumber = 0; addonNumber < wish.addons.size(); addonNumber++) {
                        Addon addon = wish.addons.get(addonNumber);

                        View addonElement = getLayoutInflater().inflate(R.layout.bar_addon_element, null);

                        TextView addonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);
                        addonNameTextView.setText(addon.name);

                        addonsLinearLayout.addView(addonElement);
                    }
                    wishesLinearLayout.addView(wishElement);
                }
                ordersLinearLayout.addView(orderElement);
            }
            tablesLinearLayout.addView(tableElement);
        }
    }
    private List<Table> getTables(){
        List<Table> tables = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        List<Wish> wishes = new ArrayList<>();
        List<Addon> addons = new ArrayList<>();
        try {
            Statement tablesS = getConnection().createStatement();
            ResultSet tablesRS = tablesS.executeQuery("SELECT * FROM tables");
            while (tablesRS.next()) {
                Statement ordersS = getConnection().createStatement();
                ResultSet ordersRS = ordersS.executeQuery("SELECT * FROM orders " +
                        "WHERE tableID = " + tablesRS.getInt("ID"));
                while (ordersRS.next()) {
                    Statement wishesS = getConnection().createStatement();
                    ResultSet wishesRS = wishesS.executeQuery("SELECT wishes.ID, dishID, name, price, amount, orderID FROM wishes\n" +
                            "JOIN dishes ON dishes.ID = wishes.dishID\n" +
                            "WHERE orderID = " + ordersRS.getInt("ID"));
                    while (wishesRS.next()) {
                        Statement addonsS = getConnection().createStatement();
                        ResultSet addonsRS = addonsS.executeQuery("SELECT addonID, name, price FROM addonsToWishes\n" +
                                "JOIN addons ON addons.ID = addonsToWishes.addonID\n" +
                                "WHERE wishID = " + wishesRS.getInt("ID"));
                        while (addonsRS.next()) {
                            addons.add(new Addon(addonsRS.getInt("addonID"), addonsRS.getString("name"), addonsRS.getFloat("price")));
                        }
                        Dish dish = new Dish(wishesRS.getInt("dishID"), wishesRS.getString("name"), wishesRS.getFloat("price"), null, null, null);
                        wishes.add(new Wish(dish, wishesRS.getInt("amount"), addons));
                        addons = new ArrayList<>();
                    }
                    orders.add(new Order(ordersRS.getInt("ID"), null, null, ordersRS.getInt("tableID"), ordersRS.getString("comments"), ordersRS.getInt("state"), wishes));
                    wishes = new ArrayList<>();
                }
                tables.add(new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), tablesRS.getInt("state"), orders));
                orders = new ArrayList<>();
            }
        } catch (SQLException ignored) {}
        return tables;
    }
}
