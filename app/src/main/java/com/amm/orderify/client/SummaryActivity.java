package com.amm.orderify.client;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;

import java.sql.SQLException;

import static com.amm.orderify.MainActivity.*;
import static com.amm.orderify.helpers.JBDCDriver.*;
import static com.amm.orderify.helpers.DataManagement.*;

public class SummaryActivity extends AppCompatActivity {

    boolean blMyAsyncTask;
    UpdateOrdersTask task = new UpdateOrdersTask();

    LinearLayout orderListLinearLayout;

    TextView clientPriceNumberTextView;
    TextView tablePriceNumberTextView;
    ConstraintLayout cancelBillScreen;
    ConstraintLayout freezeButtonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_summary_activity);

        thisTable = getFullTableData(thisTable.id);
        if (thisTable != null) thisClient = thisTable.clients.get(thisClient.id);
        generateOrdersView();

        clientPriceNumberTextView = findViewById(R.id.ClientPriceNumberTextView);
        tablePriceNumberTextView = findViewById(R.id.TablePriceNumberTextView);

        Button askForGlobalBillButton = findViewById(R.id.AskForGlobalBillButton);
        askForGlobalBillButton.setOnClickListener(v -> {
            try {
                ExecuteUpdate("UPDATE tables SET state = 3 WHERE ID = " + thisTable.id);
                ExecuteUpdate("UPDATE clients SET state = 3 WHERE tableID = " + thisTable.id);
                ExecuteUpdate("UPDATE orders JOIN clients ON orders.clientID = clients.ID SET orders.state = 3 WHERE clients.tableID = " + thisTable.id + " AND orders.state = 2");
            } catch (SQLException ignored) { }
        });

        Button goToMenuButton = findViewById(R.id.GoToMenuButton);
        goToMenuButton.setOnClickListener(v -> {
            this.startActivity(new Intent(this, MenuActivity.class));
        });

        cancelBillScreen = findViewById(R.id.CancelBillScreen);
        ImageButton cancelBillButton = cancelBillScreen.findViewById(R.id.CancelBillButton);
        cancelBillButton.setOnClickListener(e -> {
            try {
                ExecuteUpdate("UPDATE clients SET state = 1 WHERE tableID = " + thisTable.id);
                ExecuteUpdate("UPDATE tables SET state = 1 WHERE ID = " + thisTable.id);
                ExecuteUpdate("UPDATE orders JOIN clients ON orders.clientID = clients.ID SET orders.state = 2 WHERE clients.tableID = " + thisTable.id + " AND orders.state = 3");
            } catch (SQLException ignored) { }
        });



        freezeButtonScreen = findViewById(R.id.FreezeButtonScreen);
    }

    private void generateOrdersView() {
        if(orderListLinearLayout != null) orderListLinearLayout.removeAllViews();
        orderListLinearLayout = findViewById(R.id.WishListLinearLayout);
        for (int orderNumber = 0; orderNumber < thisClient.orders.size(); orderNumber++) {
            View orderElement = getLayoutInflater().inflate(R.layout.client_summary_element_order, null);
            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
            TextView orderSumNumberTextView = orderElement.findViewById(R.id.OrderSumNumberTextView);

            Order order =  thisClient.orders.valueAt(orderNumber);

            orderNumberTextView.setText(order.getOrderNumberString());
            orderStateTextView.setText(order.getState());
            orderSumNumberTextView.setText(order.getTotalPriceString());

            LinearLayout wishListLinearLayout = orderElement.findViewById(R.id.WishListLinearLayout);

            for (int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                View wishElement = getLayoutInflater().inflate(R.layout.client_summary_element_wish, null);
                TextView wishNameTextView = wishElement.findViewById(R.id.WishNameTextView);
                TextView wishPriceTextView = wishElement.findViewById(R.id.WishPriceTextView);

                Wish wish = order.wishes.valueAt(wishNumber);

                wishNameTextView.setText(wish.dish.name);
                wishPriceTextView.setText(wish.getTotalPriceString());
                wishListLinearLayout.addView(wishElement);
            }
            orderListLinearLayout.addView(orderElement);
        }
    }


    private void updateOrdersView() {
        for(int orderNumber = 0; orderNumber <  thisClient.orders.size(); orderNumber++) {
            try {
                Order order =  thisClient.orders.valueAt(orderNumber);
                View orderElement = findOrderViewById(order.id, orderListLinearLayout);
                TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
                String orderState = order.getState();
                runOnUiThread(() -> orderStateTextView.setText(orderState));
            } catch (Exception ignored) { }
        }
    }

    private void refreshTableState() {
        if (thisClient.state == 3) runOnUiThread(() -> cancelBillScreen.setVisibility(View.VISIBLE));
        else runOnUiThread(() -> cancelBillScreen.setVisibility(View.GONE));

        if (thisTable.state == 2) runOnUiThread(() -> freezeButtonScreen.setVisibility(View.VISIBLE));
        else runOnUiThread(() -> freezeButtonScreen.setVisibility(View.GONE));
    }

    private void refreshPriceView() {
        runOnUiThread(() -> clientPriceNumberTextView.setText(thisClient.getTotalPriceString()));
        runOnUiThread(() -> tablePriceNumberTextView.setText(thisTable.getTotalPriceString()));
    }

    private View findOrderViewById(int orderID, LinearLayout orderListLinearLayout) {
        for (int orderNumber = 0; orderNumber < orderListLinearLayout.getChildCount(); orderNumber++) {
            View orderElement = orderListLinearLayout.getChildAt(orderNumber);
            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            if (orderNumberTextView.getText().equals(String.valueOf("Order #" + orderID))) {
                return orderElement;
            }
        }
        return null;
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

    protected class UpdateOrdersTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            blMyAsyncTask = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(true) {
                try {
                    Thread.sleep(1000);
                    thisTable = getFullTableData(thisTable.id);
                    if (thisTable != null) thisClient = thisTable.clients.get(thisClient.id);
                    updateOrdersView();
                    refreshTableState();
                    refreshPriceView();

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
