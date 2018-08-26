package com.amm.orderify.bar;

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
import java.util.Comparator;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class TablesActivity extends AppCompatActivity {

    static boolean blMyAsyncTask;
    LinearLayout tablesLinearLayout;
    UpdateTableTask task = new UpdateTableTask(TablesActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_tables_activity);

        Button refreshButton = findViewById(R.id.RefreshButton);
        refreshButton.setOnClickListener(v -> {
            generateTablesView();
            try {
                addNewOrdersView(getAllOrders());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        generateTablesView();

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

    //do sortowania - funkcja która sprawdza czy jest nowy order/wezwanier kelnera/platnosc, usuwa wszystkie ordery z danego stolika i wkleja na nowo, posortowane

    private void generateTablesView() {

        List<Table> tables = getOnlyTables();

        if(tablesLinearLayout != null) tablesLinearLayout.removeAllViews();
        tablesLinearLayout = findViewById(R.id.TablesLinearLayout);

        for (int tableNumber = 0; tableNumber < tables.size(); tableNumber++) {
            Table table = tables.get(tableNumber);
            View tableElement = getLayoutInflater().inflate(R.layout.bar_table_element, null);

            TextView tableNumberTextView = tableElement.findViewById(R.id.TableNumberTextView);
            tableNumberTextView.setText(table.getNumberString());

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
                tableStateTextView.setText(this.getString(R.string.lifecycle_table_freezed));
            } else {
                table.state = 1;
                freezeStateButton.setText(R.string.bar_freeze_table_button_string);
                tableStateTextView.setText(this.getString(R.string.lifecycle_table_ready));
            }

            if (table.state == 1) freezeStateButton.setText(R.string.bar_unfreeze_table_button_string);
            else freezeStateButton.setText(R.string.bar_freeze_table_button_string);

            tablesLinearLayout.addView(tableElement);
        }
    }

    private void updateTablesView(List<Table> tables) {
        for(int tableNumber = 0; tableNumber < tables.size(); tableNumber++) {
            Table table = tables.get(tableNumber);
            try {
                View tableElement = findTableViewById(table.id, tablesLinearLayout);
                TextView tableStateTextView = tableElement.findViewById(R.id.TableStateTextView);
                String tableState = table.getState();
                runOnUiThread(() -> tableStateTextView.setText(tableState));

                TextView overallPriceTextView = tableElement.findViewById(R.id.OverallPriceTextView);
                runOnUiThread(() -> overallPriceTextView.setText(table.getTotalPriceString()));

            } catch (Exception ignored) { }

            for (int clientNumber = 0; clientNumber < table.clients.size(); clientNumber++) {
                Client client = table.clients.get(clientNumber);
                for (int orderNumber = 0; orderNumber < client.orders.size(); orderNumber++) {
                    Order order = client.orders.get(orderNumber);
                    try{
                        Thread.sleep(10);
                        View orderElement = findOrderViewById(order.id, tablesLinearLayout).orderElement;
                        TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
                        TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
                        String waitingTime = order.getWaitingTime();
                        String orderState = order.getState();
                        runOnUiThread(() -> {
                            orderWaitingTimeTextView.setText(waitingTime);
                            orderStateTextView.setText(orderState);
                        });
                    } catch(Exception ignore){}
                }
            }
        }
    }

    private void addNewOrdersView(List<Order> newOrders) {
        for (int orderNumber = 0; orderNumber < newOrders.size(); orderNumber++) {
            Order order = newOrders.get(orderNumber);

            View tableElement = findTableViewById(order.tableID, tablesLinearLayout);
            LinearLayout ordersLinearLayout = tableElement.findViewById(R.id.OrdersLinearLayout);

            View orderElement = getLayoutInflater().inflate(R.layout.bar_order_element, null);

            ImageButton deleteOrderButton = orderElement.findViewById(R.id.DeleteOrderButton);
            deleteOrderButton.setOnClickListener(v -> {
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

            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
            TextView orderPriceTextView = orderElement.findViewById(R.id.OrderPriceTextView);
            TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
            TextView commentsTextView = orderElement.findViewById(R.id.CommentsTextView);
            runOnUiThread(() -> {
                orderNumberTextView.setText(order.getOrderNumberString());
                orderWaitingTimeTextView.setText(order.getWaitingTime());
                orderPriceTextView.setText(order.getTotalPriceString());
                orderStateTextView.setText(order.getState());
                commentsTextView.setText(order.comments);
            });


            Button changeOrderStateButton = orderElement.findViewById(R.id.ChangeOrderStateButton);
            if (order.state == 1) runOnUiThread(() -> changeOrderStateButton.setVisibility(View.VISIBLE));
            else runOnUiThread(() -> changeOrderStateButton.setVisibility(View.GONE));
            changeOrderStateButton.setOnClickListener(v -> {
                if (order.state == 1) {
                    order.state = 2;
                    runOnUiThread(() -> changeOrderStateButton.setVisibility(View.GONE));
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
                runOnUiThread(() -> dishNameTextView.setText(wish.dish.name + " x" + wish.amount));

                LinearLayout addonsLinearLayout = wishElement.findViewById(R.id.AddonsLinearLayout);

                //sort
                wish.addons.sort(Comparator.comparing(object -> String.valueOf(object.addonCategoryID)));

                for (int addonNumber = 0; addonNumber < wish.addons.size(); addonNumber++) {
                    Addon addon = wish.addons.get(addonNumber);

                    View addonElement = getLayoutInflater().inflate(R.layout.bar_addon_element, null);

                    TextView addonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);
                    runOnUiThread(() -> {
                        addonNameTextView.setText(addon.name);
                        addonsLinearLayout.addView(addonElement);
                    });
                }
                runOnUiThread(() -> wishesLinearLayout.addView(wishElement));
            }
            runOnUiThread(() -> ordersLinearLayout.addView(orderElement));

            Button expandCollapseButton = tableElement.findViewById(R.id.ExpandCollapseButton);
            expandCollapseButton.setOnClickListener(v -> {
                if (ordersLinearLayout.getVisibility() == View.GONE)
                    ordersLinearLayout.setVisibility(View.VISIBLE);
                else ordersLinearLayout.setVisibility(View.GONE);
            });
        }
    }

    private List<Table> getOnlyTables(){
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

    private boolean notLocked(){
        boolean locked = false;
        try {
            ResultSet padlockRS = ExecuteQuery("SELECT * FROM padlock; \n");
            if (padlockRS.next()) {
                locked = true;
                if(padlockRS.getInt("TTL") == 0) ExecuteUpdate("DELETE FROM padlock WHERE ID = " + padlockRS.getInt("ID"));
                else ExecuteUpdate("UPDATE padlock SET TTL = " + (padlockRS.getInt("TTL") - 1) + " WHERE ID = " + padlockRS.getInt("ID"));
            }
        } catch (SQLException ignored) {}
        return !locked;
    }

    private List<Order> getNewOrders() {

        if (notLocked()) {
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
                    ResultSet newWishesRS = newWishesS.executeQuery("SELECT newWishes.ID, dishID, dishes.dishCategoryID, name, price, amount, orderID FROM newWishes\n" +
                            "JOIN dishes ON dishes.ID = newWishes.dishID\n" +
                            "WHERE orderID = " + newOrdersRS.getInt("ID"));
                    while (newWishesRS.next()) {
                        Statement addonsS = getConnection().createStatement();
                        ResultSet addonsRS = addonsS.executeQuery("SELECT addonID, name, price, addonCategoryID FROM newAddonsToWishes\n" +
                                "JOIN addons ON addons.ID = newAddonsToWishes.addonID\n" +
                                "WHERE wishID = " + newWishesRS.getInt("ID"));
                        while (addonsRS.next()) {
                            newAddons.add(new Addon(addonsRS.getInt("addonID"), addonsRS.getString("name"), addonsRS.getFloat("price"), addonsRS.getInt("addonCategoryID")));
                        }
                        Dish dish = new Dish(newWishesRS.getInt("dishID"), newWishesRS.getString("name"), newWishesRS.getFloat("price"), null, null, newWishesRS.getInt("dishCategoryID"), null);
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

    private List<Order> getAllOrders() {

        while (true){
            if(notLocked()) break;
        }

        List<Order> orders = new ArrayList<>();
        List<Wish> wishes = new ArrayList<>();
        List<Addon> addons = new ArrayList<>();
        try {
            Statement ordersS = getConnection().createStatement();
            ResultSet ordersRS = ordersS.executeQuery("SELECT orders.*, " +
                    "(SELECT ID FROM tables WHERE ID = clients.tableID) AS tableID FROM orders\n" +
                    "JOIN clients ON clients.ID = orders.clientID\n" +
                    "GROUP BY orders.ID");
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
                orders.add(new Order(ordersRS.getInt("ID"), ordersRS.getTime("time"), ordersRS.getDate("date"), ordersRS.getString("comments"), ordersRS.getInt("state"), ordersRS.getInt("clientID"), ordersRS.getInt("tableID"), wishes));
                wishes = new ArrayList<>();
            }
            ExecuteUpdate("DELETE FROM newAddonsToWishes");
            ExecuteUpdate("DELETE FROM newWishes");
            ExecuteUpdate("DELETE FROM newOrders");
        } catch (SQLException e) {
            Log.wtf("!!!!!!!!!!!!!!EXCEPTION", e.getMessage());
        }
        return orders;
    }

    protected class UpdateTableTask extends AsyncTask<Void, Void, Void> {
        Context context;

        UpdateTableTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            blMyAsyncTask = true;
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                addNewOrdersView(getAllOrders());
            } catch (Exception e) {
                e.printStackTrace();
            }
            while(true) {
                try {
                    updateTablesView(getFullTablesData());
                    Thread.sleep(1000);
                    addNewOrdersView(getNewOrders());
                    Thread.sleep(1000);

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

    View findTableViewById(int tableID, LinearLayout tablesLinearLayout) {
        for (int tableNumber = 0; tableNumber < tablesLinearLayout.getChildCount(); tableNumber++) {
            View tableElement = tablesLinearLayout.getChildAt(tableNumber);
            TextView tableNumberTextView = tableElement.findViewById(R.id.TableNumberTextView);
            if (tableNumberTextView.getText().equals("Table #" + String.valueOf(tableID))) {
                return tableElement;
            }
        }
        return null;
    }

    OrderAndTableView findOrderViewById(int orderID, LinearLayout tablesLinearLayout) {
        for (int tableNumber = 0; tableNumber < tablesLinearLayout.getChildCount(); tableNumber++) {
            View tableElement = tablesLinearLayout.getChildAt(tableNumber);
            LinearLayout ordersLinearLayout = tableElement.findViewById(R.id.OrdersLinearLayout);
            for (int orderNumber = 0; orderNumber < ordersLinearLayout.getChildCount(); orderNumber++) {
                View orderElement = ordersLinearLayout.getChildAt(orderNumber);
                TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
                if (orderNumberTextView.getText().equals(String.valueOf("Order #"+orderID)) )
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

    // - arek! zostaw to!
    private List<Table> getFullTablesData(){
        if (notLocked()) {
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
                            orders.add(new Order(ordersRS.getInt("ID"), ordersRS.getTime("time"), ordersRS.getDate("date"), ordersRS.getString("comments"), ordersRS.getInt("state"), clientRS.getInt("ID"), tablesRS.getInt("ID"), wishes));
                            wishes = new ArrayList<>();
                        }
                        clients.add(new Client(clientRS.getInt("ID"), clientRS.getInt("number"), clientRS.getInt("state"), orders));
                        orders = new ArrayList<>();
                    }
                    tables.add(new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), tablesRS.getInt("state"), clients));
                    clients = new ArrayList<>();
                }
                return tables;
            } catch (SQLException ignored) { }
        }
        return new ArrayList<>();
    }
}