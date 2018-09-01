package com.amm.orderify.bar;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;

import java.sql.SQLException;

import static com.amm.orderify.helpers.FetchDataFromDatabase.*;
import static com.amm.orderify.helpers.JBDCDriver.*;

public class TablesActivity extends AppCompatActivity {

    static boolean blMyAsyncTask;
    LinearLayout tablesLinearLayout;
    UpdateTableTask task = new UpdateTableTask(TablesActivity.this);

    ArrayMap<Integer,Table> globalTables = new ArrayMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_tables_activity);


        Button refreshButton = findViewById(R.id.RefreshButton);
        refreshButton.setOnClickListener(v -> {
            generateTablesView();
            try {
                generateTablesView();
                addNewOrdersView(getAllOrders());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        generateTablesView();


    }

    //do sortowania - funkcja która sprawdza czy jest nowy order/wezwanier kelnera/platnosc, usuwa wszystkie ordery z danego stolika i wkleja na nowo, posortowane

    private void generateTablesView() {
        globalTables = getOnlyTables();
        if(tablesLinearLayout != null) tablesLinearLayout.removeAllViews();
        tablesLinearLayout = findViewById(R.id.TablesLinearLayout);
        LayoutTransition layoutTransition = tablesLinearLayout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        for (int tableNumber = 0; tableNumber < globalTables.size(); tableNumber++) {
            Table globalTable = globalTables.valueAt(tableNumber);
            globalTable.tableElement = getLayoutInflater().inflate(R.layout.bar_tables_element_table, null);
            TextView tableNumberTextView = globalTable.tableElement.findViewById(R.id.TableNumberTextView);
            TextView tableStateTextView = globalTable.tableElement.findViewById(R.id.TableStateTextView);
            Button acceptRequestButton = globalTable.tableElement.findViewById(R.id.AcceptRequestButton);
            Button freezeStateButton = globalTable.tableElement.findViewById(R.id.FreezeStateButton);
            Button expandCollapseButton = globalTable.tableElement.findViewById(R.id.ExpandCollapseButton);
            Button ordersPaidButton = globalTable.tableElement.findViewById(R.id.OrdersPaidButton);
            LinearLayout ordersLinearLayout = globalTable.tableElement.findViewById(R.id.OrdersLinearLayout);

            tableNumberTextView.setText(globalTable.getNumberString());
            tableStateTextView.setText(globalTable.getState());

            ordersPaidButton.setOnClickListener(e -> {
                try {
                    globalTable.state = 1;
                    for(int i = 0; i < globalTable.clients.size(); i++) globalTable.clients.valueAt(i).state = 1;
                    ExecuteUpdate("UPDATE tables SET state = 1 WHERE ID = " + globalTable.id);
                    ExecuteUpdate("UPDATE clients SET state = 1 WHERE tableID = " + globalTable.id);
                    ExecuteUpdate("UPDATE orders JOIN clients ON orders.clientID = clients.ID SET orders.state = 4 WHERE clients.tableID = " + globalTable.id + " AND orders.state = 3");
                } catch (SQLException ignored) {}
            });

            acceptRequestButton.setOnClickListener(v -> {
                try {
                    globalTable.state = 1;
                    for(int i = 0; i < globalTable.clients.size(); i++) globalTable.clients.valueAt(i).state = 1;
                    ExecuteUpdate("UPDATE tables SET state = 1 WHERE ID = " + globalTable.id);
                    ExecuteUpdate("UPDATE clients SET state = 1 WHERE tableID = " + globalTable.id);
                    sortTable(globalTable.state, globalTable, 1000);
                } catch (SQLException ignored) {}
            });
            freezeStateButton.setOnClickListener(v -> {
                try {
                    if (globalTable.state == 1) {
                        globalTable.state = 2;
                        freezeStateButton.setText(R.string.bar_tables_table_state_unfreeze_button);
                        tableStateTextView.setText(R.string.lifecycle_table_freezed);
                    } else if(globalTable.state == 2){
                        globalTable.state = 1;
                        freezeStateButton.setText(R.string.bar_tables_table_state_freeze_button);
                        tableStateTextView.setText(R.string.lifecycle_table_ready);
                    }
                    ExecuteUpdate("UPDATE tables SET state = " + globalTable.state + " WHERE ID = " + globalTable.id);
                } catch (SQLException ignored) { }
            });
            expandCollapseButton.setOnClickListener(v -> {
                if (ordersLinearLayout.getVisibility() == View.GONE)
                    ordersLinearLayout.setVisibility(View.VISIBLE);
                else ordersLinearLayout.setVisibility(View.GONE);
            });
            tablesLinearLayout.addView(globalTable.tableElement);

        }
    }

    private void addNewOrdersView(ArrayMap<Integer,Order> orders) {
        for (int orderNumber = 0; orderNumber < orders.size(); orderNumber++) {
            Order order = orders.valueAt(orderNumber);

            this.globalTables.get(order.tableID).clients.get(order.clientID).orders.put(order.id, order);

            Order globalOrder = this.globalTables.get(order.tableID).clients.get(order.clientID).orders.get(order.id);
            Client globalClient = this.globalTables.get(order.tableID).clients.get(order.clientID);
            Table globalTable = this.globalTables.get(order.tableID);

            globalOrder.orderElement = getLayoutInflater().inflate(R.layout.bar_tables_element_order, null);
            TextView orderNumberTextView = globalOrder.orderElement.findViewById(R.id.OrderNumberTextView);
            TextView orderWaitingTimeTextView = globalOrder.orderElement.findViewById(R.id.OrderWaitingTimeTextView);
            TextView orderPriceTextView = globalOrder.orderElement.findViewById(R.id.OrderPriceTextView);
            TextView orderStateTextView = globalOrder.orderElement.findViewById(R.id.OrderStateTextView);
            TextView commentsTextView = globalOrder.orderElement.findViewById(R.id.CommentsTextView);
            ImageButton deleteOrderButton = globalOrder.orderElement.findViewById(R.id.DeleteOrderButton);
            Button changeOrderStateButton = globalOrder.orderElement.findViewById(R.id.ChangeOrderStateButton);
            LinearLayout ordersLinearLayout = globalTable.tableElement.findViewById(R.id.OrdersLinearLayout);

            runOnUiThread(() -> {
                orderNumberTextView.setText(order.getOrderNumberString());
                orderWaitingTimeTextView.setText(order.getWaitingTime());
                orderPriceTextView.setText(order.getTotalPriceString());
                orderStateTextView.setText(order.getState());
                commentsTextView.setText(order.comments);
            });

            deleteOrderButton.setOnClickListener(v -> {
                ordersLinearLayout.removeView(order.orderElement);
                globalClient.orders.remove(order.id);
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
                    if (globalOrder.state == 1) {
                        globalOrder.state = 2;
                        runOnUiThread(() -> {
                            orderStateTextView.setText(R.string.lifecycle_order_delivered);
                            changeOrderStateButton.setVisibility(View.GONE);
                            //changeOrderStateButton.setText(R.string.bar_tables_order_state_paid_button);
                        });
                    }
//                    else if(globalOrder.state == 3){
//                        globalOrder.state = 4;
//                        globalClient.state = 1;
//                        globalTable.state = 1;
//                        runOnUiThread(() -> {
//                            changeOrderStateButton.setVisibility(View.GONE);
//                            orderStateTextView.setText(R.string.lifecycle_order_paid);
//                        });
//                    }

                    ExecuteUpdate("UPDATE tables SET state = " + globalTable.state + " WHERE ID = " + globalOrder.tableID);
                    ExecuteUpdate("UPDATE clients SET state = " + globalClient.state + " WHERE tableID = " + globalOrder.tableID);
                    ExecuteUpdate("UPDATE orders SET state = " + globalOrder.state + " WHERE ID = " + globalOrder.id);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
            sortTable(5, globalTable, 1000);
        }
    }
    private void sortTable(int tableState, Table table, int time)
    {
        View tableElementChange = table.tableElement;

        if (((LinearLayout)table.tableElement.findViewById(R.id.OrdersLinearLayout)).getChildCount() == 0 && tableState != 5) tableState = 0;

        try
        {
            switch (tableState)
            {
                case 0:
                    Log.wtf("Sort started", table.getState() + " nr: " + table.number + " case 0");
                    
                    runOnUiThread(() -> tablesLinearLayout.removeView(table.tableElement));
                    Thread.sleep(time);
                    runOnUiThread(() -> tablesLinearLayout.addView(table.tableElement));


                    break;
                case 1:
                    Log.wtf("Sort started", table.getState() + " nr: " + table.number + " case 1");
                    int position = 0;
                    if (tablesLinearLayout.getChildCount() == 0) runOnUiThread(() -> tablesLinearLayout.addView(table.tableElement));
                    else
                    {
                       while (tablesLinearLayout.getChildAt(position).findViewById(R.id.TableNumberBackgroundUrgent).getVisibility() == View.VISIBLE || tablesLinearLayout.getChildAt(position).findViewById(R.id.TableNumberBackground).getVisibility() == View.VISIBLE)
                            position++;
                       position--;
                        if(tablesLinearLayout.indexOfChild(table.tableElement) != position)
                        {
                            runOnUiThread(() -> tablesLinearLayout.removeView(table.tableElement));
                            Thread.sleep(time);
                            final int finalPosition = position;
                            runOnUiThread(() -> tablesLinearLayout.addView(table.tableElement, finalPosition));
                        }
                    }
                    break;
                case 3:
                case 4:
                    Log.wtf("Sort started", table.getState() + " nr: " + table.number + " case 3,4");
                    if(tablesLinearLayout.indexOfChild(table.tableElement) != 0)
                    {
                        runOnUiThread(() -> tablesLinearLayout.removeView(table.tableElement));
                        Thread.sleep(time);
                        runOnUiThread(() -> tablesLinearLayout.addView(table.tableElement, 0));
                    }
                    break;
                case 5:
                    Log.wtf("Sort started", table.getState() + " nr: " + table.number + " case 5");
                    int position2 = 0;
                    if (tablesLinearLayout.getChildCount() == 0) runOnUiThread(() -> tablesLinearLayout.addView(table.tableElement));
                    else
                    {
                        while (tablesLinearLayout.getChildAt(position2).findViewById(R.id.TableNumberBackgroundUrgent).getVisibility() == View.VISIBLE)
                            position2++;
                        //position2--;
                        if(tablesLinearLayout.indexOfChild(table.tableElement) != position2)
                        {
                            runOnUiThread(() -> tablesLinearLayout.removeView(table.tableElement));
                            Thread.sleep(time);
                            final int finalPosition = position2;
                            Log.wtf("FINAL POSITION", finalPosition + "");
                            runOnUiThread(() -> tablesLinearLayout.addView(table.tableElement, finalPosition));
                        }
                    }
                    break;
            }
        }
        catch (Exception e)
        {
            Log.wtf("!!!!!!!!!!!!!!!!!!!!!!!Ex", e.getMessage());
        }
    }


    private void updateTablesView() {
        ArrayMap<Integer,Table> tables = getFullTablesData();
        for(int tableNumber = 0; tableNumber < tables.size(); tableNumber++) {
            Table table = tables.valueAt(tableNumber);
            Table globalTable = this.globalTables.get(table.id);
            boolean areAllOrdersFinished = true;
            LinearLayout ordersLinearLayout = globalTable.tableElement.findViewById(R.id.OrdersLinearLayout);
            ImageView tableNumberBackground = globalTable.tableElement.findViewById(R.id.TableNumberBackground);
            ImageView tableNumberBackgroundInactive = globalTable.tableElement.findViewById(R.id.TableNumberBackgroundInactive);
            ImageView tableNumberBackgroundUrgent = globalTable.tableElement.findViewById(R.id.TableNumberBackgroundUrgent);
            Button acceptRequestButton = globalTable.tableElement.findViewById(R.id.AcceptRequestButton);
            TextView tableStateTextView = globalTable.tableElement.findViewById(R.id.TableStateTextView);
            TextView overallPriceTextView = globalTable.tableElement.findViewById(R.id.OverallPriceTextView);
            Button ordersPaidButton = globalTable.tableElement.findViewById(R.id.OrdersPaidButton);

            try {
                runOnUiThread(() -> tableStateTextView.setText(table.getState()));
                runOnUiThread(() -> overallPriceTextView.setText(table.getTotalPriceString()));

            } catch (Exception ignored) { }

            if (ordersLinearLayout.getChildCount() < 1)
            {
                runOnUiThread(() -> {
                    acceptRequestButton.setVisibility(View.GONE);
                    ordersPaidButton.setVisibility(View.GONE);
                    tableStateTextView.setVisibility(View.GONE);
                    tableNumberBackgroundInactive.setVisibility(View.VISIBLE);
                    tableNumberBackgroundUrgent.setVisibility(View.GONE);
                    tableNumberBackground.setVisibility(View.INVISIBLE);
                });

                if (table.state != globalTable.state)
                {
                    globalTable.state = table.state;
                    sortTable(0, globalTable, 1000);
                }
            }
            else
            {

                switch(table.state)
                {
                    case 1:
                        runOnUiThread(() -> {
                            ordersPaidButton.setVisibility(View.GONE);
                            acceptRequestButton.setVisibility(View.GONE);
                            tableStateTextView.setVisibility(View.GONE);
                            tableNumberBackground.setVisibility(View.VISIBLE);
                            tableNumberBackgroundInactive.setVisibility(View.GONE);
                            tableNumberBackgroundUrgent.setVisibility(View.GONE);
                        });
                        break;
                    case 2:
                        runOnUiThread(() -> {
                            tableNumberBackground.setVisibility(View.VISIBLE);
                            tableNumberBackgroundInactive.setVisibility(View.GONE);
                            tableNumberBackgroundUrgent.setVisibility(View.GONE);
                        });
                        break;
                    case 3:
                        runOnUiThread(() -> {
                            tableStateTextView.setVisibility(View.VISIBLE);
                            ordersPaidButton.setVisibility(View.VISIBLE);
                            acceptRequestButton.setVisibility(View.GONE);
                            tableNumberBackgroundUrgent.setVisibility(View.VISIBLE);
                            tableNumberBackgroundInactive.setVisibility(View.GONE);
                            tableNumberBackground.setVisibility(View.INVISIBLE);
                        });
                        break;
                    case 4:
                        runOnUiThread(() -> {
                            tableStateTextView.setVisibility(View.VISIBLE);
                            acceptRequestButton.setVisibility(View.VISIBLE);
                            tableNumberBackgroundUrgent.setVisibility(View.VISIBLE);
                            tableNumberBackgroundInactive.setVisibility(View.GONE);
                            tableNumberBackground.setVisibility(View.INVISIBLE);
                        });
                        break;


                }
                if (table.state != globalTable.state)
                {
                    globalTable.state = table.state;
                    sortTable(table.state, globalTable, 1000);
                }

            }


            boolean isTableEmpty = true;
            for (int clientNumber = 0; clientNumber < table.clients.size(); clientNumber++) {
                Client client = table.clients.valueAt(clientNumber);
                for (int orderNumber = 0; orderNumber < client.orders.size(); orderNumber++) {
                    Order order = client.orders.valueAt(orderNumber);
                    Order globalOrder = this.globalTables.get(order.tableID).clients.get(order.clientID).orders.get(order.id);
                    if (client.orders.size() > 0) isTableEmpty = false;
                    try{
                        Thread.sleep(30);
                        Button changeOrderStateButton = globalOrder.orderElement.findViewById(R.id.ChangeOrderStateButton);
                        TextView orderStateTextView = globalOrder.orderElement.findViewById(R.id.OrderStateTextView);
                        TextView orderWaitingTimeTextView = globalOrder.orderElement.findViewById(R.id.OrderWaitingTimeTextView);

                        this.globalTables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).state = order.state;
                        if(order.state == 2) runOnUiThread(() -> {
                            orderStateTextView.setText(order.getState());
                            changeOrderStateButton.setVisibility(View.GONE); });
//                        else if(order.state == 3) runOnUiThread(() -> {
//                            orderStateTextView.setText(order.getState());
//                            changeOrderStateButton.setText(R.string.lifecycle_order_paid);
//                            changeOrderStateButton.setVisibility(View.VISIBLE); });
                        else if(order.state == 4) runOnUiThread(() -> {
                            orderStateTextView.setText(order.getState());
                            changeOrderStateButton.setVisibility(View.GONE); });

                        runOnUiThread(() -> orderWaitingTimeTextView.setText(order.getWaitingTime()));
                        if (order.state != 4) areAllOrdersFinished = false;
                    } catch(Exception e){
                        Log.wtf("ex",  e.getMessage()+"");
                    }
                }
            }

            if(areAllOrdersFinished && !isTableEmpty) {
                for (int clientNumber = 0; clientNumber < globalTable.clients.size(); clientNumber++) {
                    Client globalClient = globalTable.clients.valueAt(clientNumber);
                    for (int orderNumber = 0; orderNumber < globalClient.orders.size(); orderNumber++) {
                        Order order = globalClient.orders.valueAt(orderNumber);
                        globalClient.orders.remove(order.id);
                        deleteOrder(order);
                    }
                }
                Log.wtf("Log before animation", globalTable.number + "");
                Animation quitAnim = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
                //runOnUiThread(() -> ordersLinearLayout.startAnimation(quitAnim));
                Log.wtf("Log after animation", globalTable.number + "");
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.getMessage(); }
                Log.wtf("Log after wait", globalTable.number + "");
                runOnUiThread(() -> ordersLinearLayout.removeAllViews());
                Log.wtf("Log before sorting", globalTable.number + "");
                sortTable(globalTable.state, globalTable, 1000);
                Log.wtf("Log after sorting", globalTable.number + "");
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
            updateTablesView();
            for (int tableNumber = 0; tableNumber < globalTables.size(); tableNumber++)
            {
                try
                {
                    sortTable(globalTables.valueAt(tableNumber).state, globalTables.valueAt(tableNumber), 1000);
                }
                catch (Exception e) {}
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