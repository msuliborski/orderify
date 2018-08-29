package com.amm.orderify.bar;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
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

    ArrayMap<Integer,Table> tables = new ArrayMap<>();


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
            Table table = tables.valueAt(tableNumber);
            table.tableElement = getLayoutInflater().inflate(R.layout.bar_tables_element_table, null);
            TextView tableNumberTextView = table.tableElement.findViewById(R.id.TableNumberTextView);
            TextView tableStateTextView = table.tableElement.findViewById(R.id.TableStateTextView);
            Button acceptRequestButton = table.tableElement.findViewById(R.id.AcceptRequestButton);
            Button freezeStateButton = table.tableElement.findViewById(R.id.FreezeStateButton);


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
            tablesLinearLayout.addView(table.tableElement);
        }
    }

    private void addNewOrdersView(ArrayMap<Integer,Order> orders) {
        if(!orders.equals(new ArrayMap<>())) {
            for(int i = 0; i < orders.size(); i++){
                this.tables.get(orders.valueAt(i).tableID).clients.get(orders.valueAt(i).clientID).orders.put(orders.valueAt(i).id, orders.valueAt(i));
            }
        }
        for (int orderNumber = 0; orderNumber < orders.size(); orderNumber++) {
            Order order = orders.valueAt(orderNumber);
            Table table = tables.get(order.tableID);

            Order globalOrder = this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id);

            Button expandCollapseButton = table.tableElement.findViewById(R.id.ExpandCollapseButton);
            LinearLayout ordersLinearLayout = table.tableElement.findViewById(R.id.OrdersLinearLayout);

            globalOrder.orderElement = getLayoutInflater().inflate(R.layout.bar_tables_element_order, null);
            Log.wtf("tables", this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).orderElement+"");

            TextView orderNumberTextView = globalOrder.orderElement.findViewById(R.id.OrderNumberTextView);
            TextView orderWaitingTimeTextView = globalOrder.orderElement.findViewById(R.id.OrderWaitingTimeTextView);
            TextView orderPriceTextView = globalOrder.orderElement.findViewById(R.id.OrderPriceTextView);
            TextView orderStateTextView = globalOrder.orderElement.findViewById(R.id.OrderStateTextView);
            TextView commentsTextView = globalOrder.orderElement.findViewById(R.id.CommentsTextView);
            ImageButton deleteOrderButton = globalOrder.orderElement.findViewById(R.id.DeleteOrderButton);
            Button changeOrderStateButton = globalOrder.orderElement.findViewById(R.id.ChangeOrderStateButton);

            runOnUiThread(() -> {
                orderNumberTextView.setText(order.getOrderNumberString());
                orderWaitingTimeTextView.setText(order.getWaitingTime());
                orderPriceTextView.setText(order.getTotalPriceString());
                orderStateTextView.setText(order.getState());
                commentsTextView.setText(order.comments);
            });

            deleteOrderButton.setOnClickListener(v -> {
                ordersLinearLayout.removeView(order.orderElement);
                this.tables.get(order.tableID).clients.get(order.clientID).orders.remove(order.id);
                deleteOrder(order);
            });
            if (order.state == 1) {
                runOnUiThread(() -> {
                    orderStateTextView.setText(R.string.lifecycle_order_preparation);
                    changeOrderStateButton.setText(R.string.bar_tables_order_state_prepared_button);
                    changeOrderStateButton.setVisibility(View.VISIBLE);
                });
            } else if (order.state == 2) {
                runOnUiThread(() -> {
                    orderStateTextView.setText(R.string.lifecycle_order_delivered);
                    changeOrderStateButton.setText(R.string.bar_tables_order_state_paid_button);
                    changeOrderStateButton.setVisibility(View.GONE);
                });
            } else if (order.state == 3) {
                runOnUiThread(() -> {
                    orderStateTextView.setText(R.string.lifecycle_order_payment);
                    changeOrderStateButton.setVisibility(View.VISIBLE);
                });
            } else {
                runOnUiThread(() -> {
                    orderStateTextView.setText(R.string.lifecycle_order_paid);
                    changeOrderStateButton.setVisibility(View.GONE);
                });
            }
            changeOrderStateButton.setOnClickListener(v -> {
                try {
                    if (this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).state == 1) {
                        this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).state = 2;
                        runOnUiThread(() -> {
                            orderStateTextView.setText(R.string.lifecycle_order_delivered);
                            changeOrderStateButton.setVisibility(View.GONE);
                            changeOrderStateButton.setText(R.string.bar_tables_order_state_paid_button);
                        });
                    } else if(this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).state == 3){
                        this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).state = 4;
                        runOnUiThread(() -> {
                            changeOrderStateButton.setVisibility(View.GONE);
                            orderStateTextView.setText(R.string.lifecycle_order_paid);
                        });
                    }
                    ExecuteUpdate("UPDATE orders SET state = " + this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).state + " WHERE ID = " + order.id);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Log.wtf("chuja nie wiem", this.tables.get(1).clients.get(1).orders.get(1).orderElement+"");
            });

            LinearLayout wishesLinearLayout = order.orderElement.findViewById(R.id.WishesLinearLayout);
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
            runOnUiThread(() -> ordersLinearLayout.addView(globalOrder.orderElement));

            expandCollapseButton.setOnClickListener(v -> {
                if (ordersLinearLayout.getVisibility() == View.GONE)
                    ordersLinearLayout.setVisibility(View.VISIBLE);
                else ordersLinearLayout.setVisibility(View.GONE);
            });
        }
        Log.wtf("chuja nie wiem", this.tables.get(1).clients.get(1).orders.get(1).orderElement+"");
    }


    private void updateTablesView() {
        ArrayMap<Integer,Table> tables = getFullTablesData();


        for(int tableNumber = 0; tableNumber < tables.size(); tableNumber++) {
            Table table = tables.valueAt(tableNumber);
            try {

                TextView tableStateTextView = this.tables.get(table.id).tableElement.findViewById(R.id.TableStateTextView);
                TextView overallPriceTextView = this.tables.get(table.id).tableElement.findViewById(R.id.OverallPriceTextView);

                runOnUiThread(() -> tableStateTextView.setText(table.getState()));
                runOnUiThread(() -> overallPriceTextView.setText(table.getTotalPriceString()));

            } catch (Exception ignored) { }

            for (int clientNumber = 0; clientNumber < table.clients.size(); clientNumber++) {
                Client client = table.clients.valueAt(clientNumber);
                for (int orderNumber = 0; orderNumber < client.orders.size(); orderNumber++) {
                    Order order = client.orders.valueAt(orderNumber);

                    //Log.wtf("size",  client.orders.size()+"");
                    try{
                        Thread.sleep(10);
                        //View orderElement = findOrderViewById(order.id, tablesLinearLayout).orderElement;
                       Log.wtf("tables", this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).orderElement+"");
                       // Log.wtf("clients", this.tables.get(order.tableID).clients.size()+"");
                        //Log.wtf("orders", this.tables.get(order.tableID).clients.get(order.clientID).orders.size()+"");
                        Button changeOrderStateButton = this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).orderElement.findViewById(R.id.ChangeOrderStateButton);
                        TextView orderStateTextView = this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).orderElement.findViewById(R.id.OrderStateTextView);
                        Log.wtf("aesfeas", order.state+"");
                        this.tables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).state = order.state;
                        if(order.state == 2) runOnUiThread(() -> {
                            orderStateTextView.setText(order.getState());
                            changeOrderStateButton.setVisibility(View.GONE);
                        });
                        if(order.state == 3) runOnUiThread(() -> {
                            orderStateTextView.setText(order.getState());
                            changeOrderStateButton.setText(R.string.lifecycle_order_paid);
                            changeOrderStateButton.setVisibility(View.VISIBLE);
                        });
                        if(order.state == 4) runOnUiThread(() -> {
                            orderStateTextView.setText(order.getState());
                            changeOrderStateButton.setVisibility(View.GONE);
                        });

                        TextView orderWaitingTimeTextView = order.orderElement.findViewById(R.id.OrderWaitingTimeTextView);
                        runOnUiThread(() -> orderWaitingTimeTextView.setText(order.getWaitingTime()));

                    } catch(Exception e){
                        Log.wtf("ex",  e.getMessage()+"");
                    }
                }
            }
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