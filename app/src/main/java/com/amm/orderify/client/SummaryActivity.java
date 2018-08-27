package com.amm.orderify.client;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import static com.amm.orderify.MainActivity.*;
import static com.amm.orderify.helpers.JBDCDriver.*;

public class SummaryActivity extends AppCompatActivity {

    boolean blMyAsyncTask;
    UpdateOrdersTask task = new UpdateOrdersTask();

    LinearLayout orderListLinearLayout;
    ConstraintLayout cancelBillScreen;

    Table table;
    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_summary_activity);

        table = getFullTableData();
        if (table != null) client = table.clients.get(thisClient.number-1);
        generateOrdersView();

        cancelBillScreen = findViewById(R.id.CancelBillScreen);
        ImageButton cancelBillButton = cancelBillScreen.findViewById(R.id.CancelBillButton);
        cancelBillButton.setOnClickListener(e -> {
            if (thisTable.state == 3)
            {
                for (int clientNumber = 0; clientNumber < thisTable.clients.size(); clientNumber++)
                {
                    thisTable.clients.get(clientNumber).state = 1;
                    try
                    {
                        ExecuteUpdate("UPDATE clients SET state = " + thisTable.clients.get(clientNumber).state + " WHERE ID = " + thisTable.clients.get(clientNumber).id);
                    } catch (SQLException ee)
                    {
                    }
                }
                try
                {
                    thisTable.state = 1;
                    ExecuteUpdate("UPDATE tables SET state = " + thisTable.state + " WHERE ID = " + thisTable.id);
                } catch (SQLException ee)
                {
                }
            }
            else if (thisClient.state == 3)
            {
                try
                {
                    thisClient.state = 1;
                    ExecuteUpdate("UPDATE clients SET state = " + thisClient.state + " WHERE ID = " + thisClient.id);
                } catch (SQLException ee)
                {
                }
            }
        });

        Button askForGlobalBillButton = findViewById(R.id.AskForGlobalBillButton);
        askForGlobalBillButton.setOnClickListener(v -> {

            for (int clientNumber = 0; clientNumber < thisTable.clients.size(); clientNumber++)
            {
                thisTable.clients.get(clientNumber).state = 3;
                try
                {
                    ExecuteUpdate("UPDATE clients SET state = " + thisTable.clients.get(clientNumber).state + " WHERE ID = " + thisTable.clients.get(clientNumber).id);
                } catch (SQLException e)
                {
                }
            }
            try
            {
                thisTable.state = 3;
                ExecuteUpdate("UPDATE tables SET state = " + thisTable.state + " WHERE ID = " + thisTable.id);
            } catch (SQLException e)
            {
            }
        });

        Button goToMenuButton = findViewById(R.id.GoToMenuButton);
        goToMenuButton.setOnClickListener(v -> {
            this.startActivity(new Intent(this, MenuActivity.class));
        });

        TextView clientPriceNumberTextView = findViewById(R.id.ClientPriceNumberTextView);
        clientPriceNumberTextView.setText(client.getTotalPriceString());

        TextView tablePriceNumberTextView = findViewById(R.id.TablePriceNumberTextView);
        tablePriceNumberTextView.setText(table.getTotalPriceString());
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
        task = new UpdateOrdersTask();
        task.execute();
    }

    public void generateOrdersView() {
        if(orderListLinearLayout != null) orderListLinearLayout.removeAllViews();
        orderListLinearLayout = findViewById(R.id.WishListLinearLayout);
        for (int orderNumber = 0; orderNumber < client.orders.size(); orderNumber++) {
            final Order order =  client.orders.get(orderNumber);
            View orderElement = getLayoutInflater().inflate(R.layout.client_summary_order_element, null);

            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            orderNumberTextView.setText(order.getOrderNumberString());

            TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
            orderStateTextView.setText(order.getState());

            TextView orderSumNumberTextView = orderElement.findViewById(R.id.OrderSumNumberTextView);
            orderSumNumberTextView.setText(order.getTotalPriceString());

            LinearLayout wishListLinearLayout = orderElement.findViewById(R.id.WishListLinearLayout);

            for (int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                final Wish wish = order.wishes.get(wishNumber);

                View wishElement = getLayoutInflater().inflate(R.layout.client_summary_wish_element, null);
                TextView wishNameTextView = wishElement.findViewById(R.id.WishNameTextView);
                wishNameTextView.setText(wish.dish.name);

                TextView wishPriceTextView = wishElement.findViewById(R.id.WishPriceTextView);
                wishPriceTextView.setText(wish.getTotalPriceString());

                wishListLinearLayout.addView(wishElement);
            }
            orderListLinearLayout.addView(orderElement);
        }
    }

    View findOrderViewById(int orderID, LinearLayout orderListLinearLayout) {
        for (int orderNumber = 0; orderNumber < orderListLinearLayout.getChildCount(); orderNumber++) {
            View orderElement = orderListLinearLayout.getChildAt(orderNumber);
            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            if (orderNumberTextView.getText().equals(String.valueOf("Order #" + orderID))) {
                return orderElement;
            }
        }
        return null;
    }

    private void updateOrdersView() {
        for(int orderNumber = 0; orderNumber <  client.orders.size(); orderNumber++) {
            Order order =  client.orders.get(orderNumber);
            try {
                View orderElement = findOrderViewById(order.id, orderListLinearLayout);
                TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
                String orderState = order.getState();
                runOnUiThread(() -> orderStateTextView.setText(orderState));
            } catch (Exception ignored) { }
        }
    }


    private Table getFullTableData(){
        Table table = null;
        List<Client> clients = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        List<Wish> wishes = new ArrayList<>();
        List<Addon> addons = new ArrayList<>();
        try {
            Statement clientS = getConnection().createStatement();
            ResultSet clientRS = clientS.executeQuery("SELECT tables.ID AS tableID, tables.number AS tableNumber, tables.description AS tableDescription, tables.state AS tableState, clients.ID AS clientID, clients.number AS clientNumber, clients.state AS clientState FROM tables \n" +
                                                           "JOIN clients ON clients.tableID = tables.ID\n" +
                                                           "WHERE tableID = " + thisTable.id);
            while (clientRS.next()) {
                Statement ordersS = getConnection().createStatement();
                ResultSet ordersRS = ordersS.executeQuery("SELECT * FROM orders \n" +
                        "WHERE clientID = " + clientRS.getInt("clientID"));
                while (ordersRS.next()) {
                    Statement wishesS = getConnection().createStatement();
                    ResultSet wishesRS = wishesS.executeQuery("SELECT wishes.ID, dishID, dishes.dishCategoryID, name, price, amount, orderID FROM wishes\n" +
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
                        Dish dish = new Dish(wishesRS.getInt("dishID"), wishesRS.getString("name"), wishesRS.getFloat("price"), null, null, wishesRS.getInt("dishCategoryID"), null);
                        wishes.add(new Wish(wishesRS.getInt("ID"), dish, wishesRS.getInt("amount"), addons));
                        addons = new ArrayList<>();
                    }
                    orders.add(new Order(ordersRS.getInt("ID"), ordersRS.getTime("time"), ordersRS.getDate("date"), ordersRS.getString("comments"), ordersRS.getInt("state"), clientRS.getInt("clientID"), clientRS.getInt("tableID"), wishes));
                    wishes = new ArrayList<>();
                }
                clients.add(new Client(clientRS.getInt("clientID"), clientRS.getInt("clientNumber"), clientRS.getInt("clientState"), orders));
                orders = new ArrayList<>();
                table = new Table(clientRS.getInt("tableID"), clientRS.getInt("tableNumber"), clientRS.getString("tableDescription"), clientRS.getInt("tableState"), clients);
            }
            return table;
        }catch (SQLException ignored) { }
        return null;
    }

    void refreshTableState() {
        try {
            ResultSet stateRS = ExecuteQuery("SELECT state FROM clients WHERE ID = " + thisClient.state);
            if(stateRS.next()) thisClient.state = stateRS.getInt("state");

            if (thisClient.state == 3) runOnUiThread(() -> cancelBillScreen.setVisibility(View.VISIBLE));
            else runOnUiThread(() -> cancelBillScreen.setVisibility(View.GONE));

        } catch (Exception ignored) {}
    }

    protected class UpdateOrdersTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            blMyAsyncTask = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(true) {
                try {
                    Thread.sleep(100);
                    table = getFullTableData();
                    if (table != null) client = table.clients.get(thisClient.number-1);
                    updateOrdersView();
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
