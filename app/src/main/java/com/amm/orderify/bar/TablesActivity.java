package com.amm.orderify.bar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
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

import static com.amm.orderify.helpers.JBDCDriver.*;

public class TablesActivity extends AppCompatActivity {

    boolean blMyAsyncTask;
    boolean cancelTask;
    LinearLayout tablesLinearLayout;
    UpdateTableTask task = new UpdateTableTask(TablesActivity.this);


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_tables_activity);

        Button refreshButton = findViewById(R.id.RefreshButton);
        refreshButton.setOnClickListener(v -> {
            generateTablesView();
        });

        generateTablesView(); //wszystko, na czysto wrzucamy. Czyścimy do tego newOrders. Potem ASync

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                updateTableStates();
//
//                handler.postDelayed(this, 1000);
//            }}, 1000);
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
        task = new UpdateTableTask(TablesActivity.this);
        task.execute();
    }

    @SuppressLint("SetTextI18n")
    private void generateTablesView() {

        List<Table> tables = getFullTablesData();

        if(tablesLinearLayout != null) tablesLinearLayout.removeAllViews();
        tablesLinearLayout = findViewById(R.id.TablesLinearLayout);

        for (int tableNumber = 0; tableNumber < tables.size(); tableNumber++) {
            Table table = tables.get(tableNumber);
            View tableElement = getLayoutInflater().inflate(R.layout.bar_table_element, null);

            TextView tableNumberTextView = tableElement.findViewById(R.id.TableNumberTextView);
            tableNumberTextView.setText(table.number + "");

            TextView overallPriceTextView = tableElement.findViewById(R.id.OverallPriceTextView);
            overallPriceTextView.setText(table.getTotalPrice() + " zł");

            TextView tableStateTextView = tableElement.findViewById(R.id.TableStateTextView);
            tableStateTextView.setText(table.getState());

            Button acceptRequestButton = tableElement.findViewById(R.id.AcceptRequestButton);
            acceptRequestButton.setOnClickListener(v -> {
                if (table.state == 3) {
                    table.state = 1;
                    try { ExecuteUpdate("UPDATE tables SET state = " + table.state + " WHERE ID = " + table.id);
                    } catch (SQLException ignored) {}
                }
            });

            Button freezeStateButton = tableElement.findViewById(R.id.FreezeStateButton);
            freezeStateButton.setOnClickListener(v -> {
                if (table.state == 1) {
                    table.state = 2;
                    freezeStateButton.setText(R.string.bar_unfreeze_table_button_string);
                } else {
                    table.state = 1;
                    freezeStateButton.setText(R.string.bar_freeze_table_button_string);
                }
                try { ExecuteUpdate("UPDATE tables SET state = " + table.state + " WHERE ID = " + table.id);
                } catch (SQLException ignored) { }
            });
            if (table.state == 1) {
                table.state = 2;
                freezeStateButton.setText(R.string.bar_unfreeze_table_button_string);
                tableStateTextView.setText("FREEZED!");
            } else {
                table.state = 1;
                freezeStateButton.setText(R.string.bar_freeze_table_button_string);
                tableStateTextView.setText("READY!");
            }

            if (table.state == 1) freezeStateButton.setText(R.string.bar_unfreeze_table_button_string);
            else freezeStateButton.setText(R.string.bar_freeze_table_button_string);

            LinearLayout ordersLinearLayout = tableElement.findViewById(R.id.OrdersLinearLayout);
            for (int clientNumber = 0; clientNumber < table.clients.size(); clientNumber++) {
                Client client = table.clients.get(clientNumber);
                for (int orderNumber = 0; orderNumber < client.orders.size(); orderNumber++) {
                    Order order = client.orders.get(orderNumber);
                    View orderElement = getLayoutInflater().inflate(R.layout.bar_order_element, null);

                    ImageButton deleteOrderButton = orderElement.findViewById(R.id.DeleteOrderButton);
                    deleteOrderButton.setOnClickListener(v -> {
                        try {
                            ExecuteUpdate("DELETE FROM orders WHERE ID = " + order.id);
                        } catch (SQLException ignored) { }
                        client.orders.remove(order);
                        //ordersLinearLayout.removeView(orderElement);
                        //updateTablesView();
                    });

                    TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
                    orderNumberTextView.setText(order.id + "");

                    TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
                    orderWaitingTimeTextView.setText(order.getWaitingTime());

                    TextView orderPriceTextView = orderElement.findViewById(R.id.OrderPriceTextView);
                    orderPriceTextView.setText(order.getTotalPrice() + " zł");

                    TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
                    orderStateTextView.setText(order.getState());

                    Button changeOrderStateButton = orderElement.findViewById(R.id.ChangeOrderStateButton);
                    if (order.state == 1) changeOrderStateButton.setVisibility(View.VISIBLE);
                    else changeOrderStateButton.setVisibility(View.GONE);
                    changeOrderStateButton.setOnClickListener(v -> {
                        if (order.state == 1) {
                            order.state = 2;
                            changeOrderStateButton.setVisibility(View.GONE);
                        }
                        try {
                            ExecuteUpdate("UPDATE orders SET state = " + order.state + " WHERE ID = " + order.id);
                        } catch (SQLException ignored) {
                        }
                    });

                    TextView commentsTextView = orderElement.findViewById(R.id.CommentsTextView);
                    commentsTextView.setText(order.comments);

                    LinearLayout wishesLinearLayout = orderElement.findViewById(R.id.WishesLinearLayout);
                    for (int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                        Wish wish = order.wishes.get(wishNumber);
                        View wishElement = getLayoutInflater().inflate(R.layout.bar_wish_element, null);

                        TextView dishNameTextView = wishElement.findViewById(R.id.DishNameTextView);
                        dishNameTextView.setText(wish.dish.name + " x" + wish.amount);

                        LinearLayout addonsLinearLayout = wishElement.findViewById(R.id.AddonsLinearLayout);
                        for (int addonNumber = 0; addonNumber < wish.addons.size(); addonNumber++) {
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
            }
            tablesLinearLayout.addView(tableElement);


            Button expandCollapseButton = tableElement.findViewById(R.id.ExpandCollapseButton);
            expandCollapseButton.setOnClickListener(v -> {
                if (ordersLinearLayout.getVisibility() == View.GONE)
                    ordersLinearLayout.setVisibility(View.VISIBLE);
                else ordersLinearLayout.setVisibility(View.GONE);
            });
        }
    }

    private void updateTablesView() {
        List<Table> tables = getTablesData();
        for(int tableNumber = 0; tableNumber < tables.size(); tableNumber++) {
            Table table = tables.get(tableNumber);
            try {
                View tableElement = findTable(table.id);
                TextView tableStateTextView = tableElement.findViewById(R.id.TableStateTextView);
                String tableState = table.getState();
                Thread.sleep(10);
                runOnUiThread(() -> {
                    tableStateTextView.setText(tableState);
                });
            } catch (Exception ignored) { }

            for (int clientNumber = 0; clientNumber < table.clients.size(); clientNumber++) {
                Client client = table.clients.get(clientNumber);
                for (int orderNumber = 0; orderNumber < client.orders.size(); orderNumber++) {
                    Order order = client.orders.get(orderNumber);
                    try{
                        View orderElement = findOrder(order.id).orderElement;
                        TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
                        String waitingTime = order.getWaitingTime();

                        TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
                        String orderState = order.getState();

                        Thread.sleep(10);
                        runOnUiThread(() -> {
                            orderWaitingTimeTextView.setText(waitingTime);
                            orderStateTextView.setText(orderState);
                        });
                    } catch(Exception ignore){}
                }
            }
        }
    }
    private void addNewOrdersView() throws Exception{
        List<Order> newOrders = getNewOrders();
        for (int orderNumber = 0; orderNumber < newOrders.size(); orderNumber++) {
            Order order = newOrders.get(orderNumber);

            View tableElement = findTable(order.tableID);
            LinearLayout ordersLinearLayout = tableElement.findViewById(R.id.OrdersLinearLayout);

            View orderElement = getLayoutInflater().inflate(R.layout.bar_order_element, null);

            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
            TextView orderPriceTextView = orderElement.findViewById(R.id.OrderPriceTextView);
            TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
            TextView commentsTextView = orderElement.findViewById(R.id.CommentsTextView);
            runOnUiThread(() -> {
                orderNumberTextView.setText(order.id + "");
                orderWaitingTimeTextView.setText(order.getWaitingTime());
                orderPriceTextView.setText(order.getTotalPrice() + " zł");
                orderStateTextView.setText(order.getState());
                commentsTextView.setText(order.comments);
            });

            ImageButton deleteOrderButton = orderElement.findViewById(R.id.DeleteOrderButton);
            deleteOrderButton.setOnClickListener(v -> {
                Log.wtf("delete", "delete");
                try {
                    for(int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                        for (int addonNumber = 0; addonNumber < order.wishes.get(wishNumber).addons.size(); addonNumber++){
                            ExecuteUpdate("DELETE FROM addonsToWishes WHERE addonID = " + order.wishes.get(wishNumber).addons.get(addonNumber).id);
                        }
                        ExecuteUpdate("DELETE FROM wishes WHERE ID = " + order.wishes.get(wishNumber).id);
                    }
                    ExecuteUpdate("DELETE FROM orders WHERE ID = " + order.id);
                } catch (SQLException e) {
                    Log.wtf("wada", e.getMessage()+"");
                }
                ordersLinearLayout.removeView(orderElement);
            });



            Button changeOrderStateButton = orderElement.findViewById(R.id.ChangeOrderStateButton);
            if (order.state == 1) {
                runOnUiThread(() -> {
                    changeOrderStateButton.setVisibility(View.VISIBLE);
                });
            } else {
                runOnUiThread(() -> {
                    changeOrderStateButton.setVisibility(View.GONE);
                });
            }
            changeOrderStateButton.setOnClickListener(v -> {
                if (order.state == 1) {
                    order.state = 2;
                    runOnUiThread(() -> {
                        changeOrderStateButton.setVisibility(View.GONE);
                    });
                }
                try {
                    ExecuteUpdate("UPDATE orders SET state = " + order.state + " WHERE ID = " + order.id);
                } catch (SQLException ignored) { }
            });


            LinearLayout wishesLinearLayout = orderElement.findViewById(R.id.WishesLinearLayout);
            for (int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                Wish wish = order.wishes.get(wishNumber);
                View wishElement = getLayoutInflater().inflate(R.layout.bar_wish_element, null);

                TextView dishNameTextView = wishElement.findViewById(R.id.DishNameTextView);
                runOnUiThread(() -> {
                    dishNameTextView.setText(wish.dish.name + " x" + wish.amount);
                });

                LinearLayout addonsLinearLayout = wishElement.findViewById(R.id.AddonsLinearLayout);
                for (int addonNumber = 0; addonNumber < wish.addons.size(); addonNumber++) {
                    Addon addon = wish.addons.get(addonNumber);

                    View addonElement = getLayoutInflater().inflate(R.layout.bar_addon_element, null);

                    TextView addonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);
                    runOnUiThread(() -> {
                        addonNameTextView.setText(addon.name);
                        addonsLinearLayout.addView(addonElement);
                    });

                }
                runOnUiThread(() -> {
                    wishesLinearLayout.addView(wishElement);
                });

            }
            runOnUiThread(() -> {
                ordersLinearLayout.addView(orderElement);
            });
        }
    }


    private List<Table> getFullTablesData(){
        List<Table> tables = new ArrayList<>();
        List<Client> clients = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        List<Wish> wishes = new ArrayList<>();
        List<Addon> addons = new ArrayList<>();
        try {
            Statement tablesS = getConnection().createStatement();
            ResultSet tablesRS = tablesS.executeQuery("SELECT * FROM tables");
            while (tablesRS.next()) {
                Statement clientS = getConnection().createStatement();
                ResultSet clientRS = clientS.executeQuery("SELECT * FROM clients \n" +
                        "WHERE tableID = " + tablesRS.getInt("ID"));
                while (clientRS.next()) {
                    Statement ordersS = getConnection().createStatement();
                    ResultSet ordersRS = ordersS.executeQuery("SELECT * FROM orders \n" +
                            "WHERE clientID = " + clientRS.getInt("ID"));
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
                            wishes.add(new Wish(wishesRS.getInt("ID"), dish, wishesRS.getInt("amount"), addons));
                            addons = new ArrayList<>();
                        }
                    orders.add(new Order(ordersRS.getInt("ID"), ordersRS.getTime("time"), ordersRS.getDate("date"), ordersRS.getString("comments"), ordersRS.getInt("state"), clientRS.getInt("ID"), tablesRS.getInt("ID"), wishes));
                    wishes = new ArrayList<>();
                    }
                    clients.add(new Client(clientRS.getInt("ID"), clientRS.getInt("number"), clientRS.getInt("state"), orders));
                    orders = new ArrayList<>();
                }
                tables.add(new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), tablesRS.getInt("state"), clients));
                clients = new ArrayList<>();

                ExecuteUpdate("DELETE FROM newAddonsToWishes");
                ExecuteUpdate("DELETE FROM newWishes");
                ExecuteUpdate("DELETE FROM newOrders");
            }
        } catch (SQLException ignored) {}
        return tables;
    }

    private List<Table> getTablesData(){
        List<Table> tables = new ArrayList<>();
        List<Client> clients = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        try {
            Statement tablesS = getConnection().createStatement();
            ResultSet tablesRS = tablesS.executeQuery("SELECT * FROM tables");
            while (tablesRS.next()) {
                Statement clientS = getConnection().createStatement();
                ResultSet clientRS = clientS.executeQuery("SELECT * FROM clients \n" +
                        "WHERE tableID = " + tablesRS.getInt("ID"));
                while (clientRS.next()) {
                    Statement ordersS = getConnection().createStatement();
                    ResultSet ordersRS = ordersS.executeQuery("SELECT * FROM orders \n" +
                            "WHERE clientID = " + clientRS.getInt("ID"));
                    while (ordersRS.next()) {
                        orders.add(new Order(ordersRS.getInt("ID"), ordersRS.getTime("time"), ordersRS.getDate("date"), ordersRS.getString("comments"), ordersRS.getInt("state"), clientRS.getInt("ID"), tablesRS.getInt("ID"), null));
                    }
                    clients.add(new Client(clientRS.getInt("ID"), clientRS.getInt("number"), clientRS.getInt("state"), orders));
                    orders = new ArrayList<>();
                }
                tables.add(new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), tablesRS.getInt("state"), clients));
                clients = new ArrayList<>();
            }
        } catch (SQLException ignored) {}
        return tables;
    }

    private List<Order> getNewOrders() {
        boolean locked = false;
        try {
            ResultSet lockedRS = ExecuteQuery("SELECT * FROM padlock; \n");
            if (lockedRS.next()) locked = true;
        } catch (SQLException ignored) {}
        if (!locked) {
            List<Order> newOrders = new ArrayList<>();
            List<Wish> newWishes = new ArrayList<>();
            List<Addon> newAddons = new ArrayList<>();
            try {
                Statement newOrdersS = getConnection().createStatement();
                ResultSet newOrdersRS = newOrdersS.executeQuery("SELECT newOrders.*, " +
                        "(SELECT ID FROM tables WHERE ID = clients.tableID) AS tableID FROM newOrders\n" +
                        "JOIN clients ON clients.ID = newOrders.clientID\n" +
                        "GROUP BY newOrders.ID");
                while (newOrdersRS.next()) {
                    Statement newWishesS = getConnection().createStatement();
                    ResultSet newWishesRS = newWishesS.executeQuery("SELECT newWishes.ID, dishID, name, price, amount, orderID FROM newWishes\n" +
                            "JOIN dishes ON dishes.ID = newWishes.dishID\n" +
                            "WHERE orderID = " + newOrdersRS.getInt("ID"));
                    while (newWishesRS.next()) {
                        Statement addonsS = getConnection().createStatement();
                        ResultSet addonsRS = addonsS.executeQuery("SELECT addonID, name, price FROM newAddonsToWishes\n" +
                                "JOIN addons ON addons.ID = newAddonsToWishes.addonID\n" +
                                "WHERE wishID = " + newWishesRS.getInt("ID"));
                        while (addonsRS.next()) {
                            newAddons.add(new Addon(addonsRS.getInt("addonID"), addonsRS.getString("name"), addonsRS.getFloat("price")));
                        }
                        Dish dish = new Dish(newWishesRS.getInt("dishID"), newWishesRS.getString("name"), newWishesRS.getFloat("price"), null, null, null);
                        newWishes.add(new Wish(newWishesRS.getInt("ID"), dish, newWishesRS.getInt("amount"), newAddons));
                        newAddons = new ArrayList<>();
                    }
                    newOrders.add(new Order(newOrdersRS.getInt("ID"), newOrdersRS.getTime("time"), newOrdersRS.getDate("date"), newOrdersRS.getString("comments"), newOrdersRS.getInt("state"), newOrdersRS.getInt("clientID"), newOrdersRS.getInt("tableID"), newWishes));
                    newWishes = new ArrayList<>();
                }
                ExecuteUpdate("DELETE FROM newAddonsToWishes");
                ExecuteUpdate("DELETE FROM newWishes");
                ExecuteUpdate("DELETE FROM newOrders");
            } catch (SQLException e) {
                Log.wtf("!!!!!!!!!!!!!!EXCEPTION", e.getMessage());
            }
            return newOrders;
        }
        return new ArrayList<>();
    }

    @SuppressLint("StaticFieldLeak")
    protected class UpdateTableTask extends AsyncTask<Void, Void, Void> {
        Context context;

        UpdateTableTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            blMyAsyncTask = true;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected Void doInBackground(Void... params) {

            while(true) {
                try {
                    Thread.sleep(100);
                    updateTablesView();
                    Thread.sleep(100);
                    addNewOrdersView();

                    } catch (Exception e) {
                    e.printStackTrace();
                }
                if(Thread.interrupted()) break;
                if (!blMyAsyncTask) break;
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

    View findTable (int tableID) throws Exception {
        for (int tableNumber = 0; tableNumber < tablesLinearLayout.getChildCount(); tableNumber++) {
            View tableElement = tablesLinearLayout.getChildAt(tableNumber);
            TextView tableNumberTextView = tableElement.findViewById(R.id.TableNumberTextView);
            if (tableNumberTextView.getText().equals(String.valueOf(tableID))) {
                return tableElement;
            }
        }
        return null;
    }

    OrderAndTableView findOrder (int orderID) throws Exception {
        for (int tableI = 0; tableI < tablesLinearLayout.getChildCount(); tableI++) {
            View tableElement = tablesLinearLayout.getChildAt(tableI);
                LinearLayout ordersLinearLayout = tableElement.findViewById(R.id.OrdersLinearLayout);
                for (int orderNumber = 0; orderNumber < ordersLinearLayout.getChildCount(); orderNumber++) {
                    View orderElement = ordersLinearLayout.getChildAt(orderNumber);
                    TextView orderNr = orderElement.findViewById(R.id.OrderNumberTextView);

                    if (orderNr.getText().equals(String.valueOf(orderID)) )
                        return new OrderAndTableView(tableElement, orderElement);
                }
        }
        return null;
    }
    class OrderAndTableView {
        View tableElement;
        View orderElement;

        OrderAndTableView(View tableElement,  View orderElement) {
            this.orderElement = orderElement;
            this.tableElement = tableElement;
        }
    }
}
