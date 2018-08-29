package com.amm.orderify.bar;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import static com.amm.orderify.helpers.DataManagement.*;
import static com.amm.orderify.helpers.JBDCDriver.*;

public class TablesActivity extends AppCompatActivity {

    static boolean blMyAsyncTask;
    LinearLayout tablesLinearLayout;
    UpdateTableTask task = new UpdateTableTask(TablesActivity.this);

    ArrayMap<Integer, Table> tables;

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

    //do sortowania - funkcja która sprawdza czy jest nowy order/wezwanier kelnera/platnosc, usuwa wszystkie ordery z danego stolika i wkleja na nowo, posortowane

    private void generateTablesView() {
        tables = getOnlyTables();
        if(tablesLinearLayout != null) tablesLinearLayout.removeAllViews();
        tablesLinearLayout = findViewById(R.id.TablesLinearLayout);

        for (int tableNumber = 0; tableNumber < tables.size(); tableNumber++) {
            View tableElement = getLayoutInflater().inflate(R.layout.bar_tables_element_table, null);
            TextView tableNumberTextView = tableElement.findViewById(R.id.TableNumberTextView);
            TextView tableStateTextView = tableElement.findViewById(R.id.TableStateTextView);
            Button acceptRequestButton = tableElement.findViewById(R.id.AcceptRequestButton);
            Button freezeStateButton = tableElement.findViewById(R.id.FreezeStateButton);

            Table table = tables.valueAt(tableNumber);

            tableNumberTextView.setText(table.getNumberString());
            tableStateTextView.setText(table.getState());
            acceptRequestButton.setOnClickListener(v -> {
                try {
                    table.state = 1;
                    for(int i = 0; i < table.clients.size(); i++) table.clients.valueAt(i).state = 1;
                    ExecuteUpdate("UPDATE tables SET state = 1 WHERE ID = " + table.id);
                    ExecuteUpdate("UPDATE clients SET state = 1 WHERE tableID = " + table.id);
                } catch (SQLException ignored) {}
            });
            freezeStateButton.setOnClickListener(v -> {
                try {
                    if (table.state == 1) {
                        table.state = 2;
                        freezeStateButton.setText(R.string.bar_tables_table_state_unfreeze_button);
                        tableStateTextView.setText(R.string.lifecycle_table_freezed);
                    } else if(table.state == 2){
                        table.state = 1;
                        freezeStateButton.setText(R.string.bar_tables_table_state_freeze_button);
                        tableStateTextView.setText(R.string.lifecycle_table_ready);
                    }
                    ExecuteUpdate("UPDATE tables SET state = " + table.state + " WHERE ID = " + table.id);
                } catch (SQLException ignored) { }
            });
            tablesLinearLayout.addView(tableElement);
        }
    }

    private void addNewOrdersView(ArrayMap<Integer,Order> orders) {
        for (int orderNumber = 0; orderNumber < orders.size(); orderNumber++) {
            Order order = orders.valueAt(orderNumber);
            View tableElement = findTableViewById(order.tableID, tablesLinearLayout);
            LinearLayout ordersLinearLayout = tableElement.findViewById(R.id.OrdersLinearLayout);
            View orderElement = getLayoutInflater().inflate(R.layout.bar_tables_element_order, null);
            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
            TextView orderPriceTextView = orderElement.findViewById(R.id.OrderPriceTextView);
            TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
            TextView commentsTextView = orderElement.findViewById(R.id.CommentsTextView);
            ImageButton deleteOrderButton = orderElement.findViewById(R.id.DeleteOrderButton);
            Button changeOrderStateButton = orderElement.findViewById(R.id.ChangeOrderStateButton);
            Button expandCollapseButton = tableElement.findViewById(R.id.ExpandCollapseButton);

            runOnUiThread(() -> {
                orderNumberTextView.setText(order.getOrderNumberString());
                orderWaitingTimeTextView.setText(order.getWaitingTime());
                orderPriceTextView.setText(order.getTotalPriceString());
                orderStateTextView.setText(order.getState());
                commentsTextView.setText(order.comments);
            });

            deleteOrderButton.setOnClickListener(v -> {
                deleteOrder(order);
                ordersLinearLayout.removeView(orderElement);
            });
            if (order.state == 1) {
                runOnUiThread(() -> orderStateTextView.setText(R.string.lifecycle_order_preparation));
                runOnUiThread(() -> changeOrderStateButton.setText(R.string.bar_tables_order_state_prepared_button));
                runOnUiThread(() -> changeOrderStateButton.setVisibility(View.VISIBLE));
            } else if (order.state == 2) {
                runOnUiThread(() -> orderStateTextView.setText(R.string.lifecycle_order_delivered));
                runOnUiThread(() -> changeOrderStateButton.setText(R.string.bar_tables_order_state_paid_button));
                runOnUiThread(() -> changeOrderStateButton.setVisibility(View.VISIBLE));
            } else if (order.state == 3) {
                runOnUiThread(() -> orderStateTextView.setText(R.string.lifecycle_order_payment));
                runOnUiThread(() -> changeOrderStateButton.setVisibility(View.VISIBLE));
            } else {
                runOnUiThread(() -> orderStateTextView.setText(R.string.lifecycle_order_paid));
                runOnUiThread(() -> changeOrderStateButton.setVisibility(View.GONE)); }

            changeOrderStateButton.setOnClickListener(v -> {
                try {
                    if (order.state == 1) {
                        order.state = 2;
                        runOnUiThread(() -> orderStateTextView.setText(R.string.lifecycle_order_delivered));
                        runOnUiThread(() -> changeOrderStateButton.setText(R.string.bar_tables_order_state_paid_button));
                        runOnUiThread(() -> changeOrderStateButton.setVisibility(View.VISIBLE));
                    } else if(order.state == 2 || order.state ==3){
                        order.state = 4;
                        runOnUiThread(() -> orderStateTextView.setText(R.string.lifecycle_order_paid));
                        runOnUiThread(() -> changeOrderStateButton.setVisibility(View.GONE));
                    }
                    ExecuteUpdate("UPDATE orders SET state = " + order.state + " WHERE ID = " + order.id);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            LinearLayout wishesLinearLayout = orderElement.findViewById(R.id.WishesLinearLayout);
            for (int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                View wishElement = getLayoutInflater().inflate(R.layout.bar_tables_element_wish, null);
                TextView dishNameTextView = wishElement.findViewById(R.id.DishNameTextView);
                LinearLayout addonsLinearLayout = wishElement.findViewById(R.id.AddonsLinearLayout);

                Wish wish = order.wishes.valueAt(wishNumber);

                runOnUiThread(() -> dishNameTextView.setText(wish.dish.name + " x" + wish.amount));

                //wish.addons.sort(Comparator.comparing(object -> String.valueOf(object.addonCategoryID))); //sort

                for (int addonNumber = 0; addonNumber < wish.addons.size(); addonNumber++) {
                    Addon addon = wish.addons.valueAt(addonNumber);
                    View addonElement = getLayoutInflater().inflate(R.layout.bar_tables_element_addon, null);
                    TextView addonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);

                    runOnUiThread(() -> {
                        addonNameTextView.setText(addon.name);
                        addonsLinearLayout.addView(addonElement);
                    });
                }
                runOnUiThread(() -> wishesLinearLayout.addView(wishElement));
            }
            runOnUiThread(() -> ordersLinearLayout.addView(orderElement));

            expandCollapseButton.setOnClickListener(v -> {
                if (ordersLinearLayout.getVisibility() == View.GONE)
                    ordersLinearLayout.setVisibility(View.VISIBLE);
                else ordersLinearLayout.setVisibility(View.GONE);
            });
        }
    }


    private void updateTablesView() {
        tables = getFullTablesData();
        for(int tableNumber = 0; tableNumber < tables.size(); tableNumber++) {
            Table table = tables.valueAt(tableNumber);
            try {
                View tableElement = findTableViewById(table.id, tablesLinearLayout);

                TextView tableStateTextView = tableElement.findViewById(R.id.TableStateTextView);
                if(table.state == 3 || table.state == 1) runOnUiThread(() -> tableStateTextView.setText(table.getState()));

                TextView overallPriceTextView = tableElement.findViewById(R.id.OverallPriceTextView);
                runOnUiThread(() -> overallPriceTextView.setText(table.getTotalPriceString()));

            } catch (Exception ignored) { }

            for (int clientNumber = 0; clientNumber < table.clients.size(); clientNumber++) {
                Client client = table.clients.valueAt(clientNumber);
                for (int orderNumber = 0; orderNumber < client.orders.size(); orderNumber++) {
                    Order order = client.orders.valueAt(orderNumber);
                    try{
                        Thread.sleep(10);//do we really need it?
                        View orderElement = findOrderViewById(order.id, tablesLinearLayout).orderElement;

                        TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
                        if(order.state == 3) runOnUiThread(() -> orderStateTextView.setText(order.getState()));

                        TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
                        runOnUiThread(() -> orderWaitingTimeTextView.setText(order.getWaitingTime()));

                    } catch(Exception ignore){}
                }
            }
        }
    }

    private View findTableViewById(int tableID, LinearLayout tablesLinearLayout) {
        for (int tableNumber = 0; tableNumber < tablesLinearLayout.getChildCount(); tableNumber++) {
            View tableElement = tablesLinearLayout.getChildAt(tableNumber);
            TextView tableNumberTextView = tableElement.findViewById(R.id.TableNumberTextView);
            if (tableNumberTextView.getText().equals("Table #" + String.valueOf(tableID))) {
                return tableElement;
            }
        }
        return null;
    }

    private OrderAndTableView findOrderViewById(int orderID, LinearLayout tablesLinearLayout) {
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

    private class OrderAndTableView {
        View tableElement;
        View orderElement;
        OrderAndTableView(View tableElement,  View orderElement) {
            this.orderElement = orderElement;
            this.tableElement = tableElement;
        }
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
                    Thread.sleep(1000);
                    updateTablesView();
                    Thread.sleep(1000);
                    addNewOrdersView(getNewOrders());

                    } catch (Exception ignored) {}
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



}