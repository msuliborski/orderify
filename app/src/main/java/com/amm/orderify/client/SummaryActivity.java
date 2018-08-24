package com.amm.orderify.client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import static com.amm.orderify.MainActivity.thisClientID;
import static com.amm.orderify.helpers.JBDCDriver.ExecuteUpdate;
import static com.amm.orderify.helpers.JBDCDriver.getConnection;

public class SummaryActivity extends AppCompatActivity
{
    LinearLayout orderListLinearLayout;
    List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_summary_activity);

        orders = getOrders();

        updateOrdersView();

        Button askForBillAButton = findViewById(R.id.AskForBillAButton);
        askForBillAButton.setOnClickListener(v -> {
            if(client.state == 1) {
                client.state = 4;
                try {
                    ExecuteUpdate("UPDATE clients SET state = " + client.state +  " WHERE ID = " + client.id);
                } catch (SQLException ignored) {}
            }
        });

        Button goToMenuButton = findViewById(R.id.GoToMenuButton);
        goToMenuButton.setOnClickListener(v -> {
            this.startActivity(new Intent(this, MenuActivity.class));
        });



        //====================================================================

        //do przerobienia!!!!!!!!!!!

        //====================================================================
        //update z 22/08/16:00
        //nie gwarantuje ze dane sa poprawne, przejrzę w wolnej chwili

        //====================================================================


    }

    public void updateOrdersView() {
        if(orderListLinearLayout != null) orderListLinearLayout.removeAllViews();
        orderListLinearLayout = findViewById(R.id.WishListLinearLayout);

        for (int orderNumber = 0; orderNumber < orders.size(); orderNumber++) {
            final Order order = orders.get(orderNumber);
            View orderElement = getLayoutInflater().inflate(R.layout.client_summary_order_element, null);

            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            orderNumberTextView.setText("Order " + order.id + ".");

            TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
            orderStateTextView.setText(order.getState());

            TextView orderSumNumberTextView = orderElement.findViewById(R.id.OrderSumNumberTextView);
            orderSumNumberTextView.setText(order.getTotalPrice() + "");

            LinearLayout wishListLinearLayout = orderElement.findViewById(R.id.WishListLinearLayout);

            for (int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                final Wish wish = order.wishes.get(wishNumber);

                View wishElement = getLayoutInflater().inflate(R.layout.client_summary_wish_element, null);
                TextView wishNameTextView = wishElement.findViewById(R.id.WishNameTextView);
                TextView wishPriceTextView = wishElement.findViewById(R.id.WishPriceTextView);

                wishNameTextView.setText(wish.dish.name);
                wishPriceTextView.setText(wish.getTotalPrice() + "");

                wishListLinearLayout.addView(wishElement);
            }
            orderListLinearLayout.addView(orderElement);
        }
    }

    private List<Order> getOrders(){
        List<Order> orders = new ArrayList<>();
        List<Wish> wishes = new ArrayList<>();
        List<Addon> addons = new ArrayList<>();
        try {
            Statement ordersS = getConnection().createStatement();
            ResultSet ordersRS = ordersS.executeQuery("SELECT * FROM orders \n" +
                    "WHERE clientID = " + thisClientID);
            while (ordersRS.next()) {
                Statement wishesS = getConnection().createStatement();
                ResultSet wishesRS = wishesS.executeQuery("SELECT wishes.ID, dishID, name, price, amount, orderID FROM wishes\n" +
                        "JOIN dishes ON dishes.ID = wishes.dishID\n" +
                        "WHERE orderID = " + ordersRS.getInt("ID"));
                while (wishesRS.next()) {
                    Statement addonsS = getConnection().createStatement();
                    ResultSet addonsRS = addonsS.executeQuery("SELECT addonID, name, price, addonCategoryID FROM addonsToWishes\n" +
                            "JOIN addons ON addons.ID = addonsToWishes.addonID\n" +
                            "WHERE wishID = " + wishesRS.getInt("ID"));
                    while (addonsRS.next()) {
                        addons.add(new Addon(addonsRS.getInt("addonID"), addonsRS.getString("name"), addonsRS.getFloat("price"), addonsRS.getInt("addonCategoryID")));
                    }
                    Dish dish = new Dish(wishesRS.getInt("dishID"), wishesRS.getString("name"), wishesRS.getFloat("price"), null, null, null);
                    wishes.add(new Wish(0, dish, wishesRS.getInt("amount"), addons));
                    addons = new ArrayList<>();
                }
                orders.add(new Order(ordersRS.getInt("ID"), ordersRS.getTime("time"), ordersRS.getDate("date"), ordersRS.getString("comments"), ordersRS.getInt("state"), 0,0, wishes));
                wishes = new ArrayList<>();
            }
        } catch (SQLException ignored) {}
        return orders;
    }
}
