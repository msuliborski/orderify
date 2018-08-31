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
import static com.amm.orderify.helpers.FetchDataFromDatabase.*;

public class SummaryActivity extends AppCompatActivity {

    boolean blMyAsyncTask;
    UpdateOrdersTask task = new UpdateOrdersTask();

    LinearLayout orderListLinearLayout;

    TextView clientPriceNumberTextView;
    TextView tablePriceNumberTextView;
    ConstraintLayout cancelBillScreen;
    ConstraintLayout freezeButtonScreen;

    Table globalTable;
    Client globalClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_summary_activity);

        globalTable = getFullTableData(thisTableID);
        if (globalTable != null) globalClient = globalTable.clients.get(thisClientID);
        generateOrdersView();

        clientPriceNumberTextView = findViewById(R.id.ClientPriceNumberTextView);
        tablePriceNumberTextView = findViewById(R.id.TablePriceNumberTextView);

        Button askForGlobalBillButton = findViewById(R.id.AskForGlobalBillButton);
        askForGlobalBillButton.setOnClickListener(v -> {
            try {
                ExecuteUpdate("UPDATE tables SET state = 3 WHERE ID = " + thisTableID);
                ExecuteUpdate("UPDATE clients SET state = 3 WHERE tableID = " + thisTableID);
                ExecuteUpdate("UPDATE orders JOIN clients ON orders.clientID = clients.ID SET orders.state = 3 WHERE clients.tableID = " + thisTableID + " AND orders.state = 2");
            } catch (SQLException ignored) { }
        });

        Button goToMenuButton = findViewById(R.id.GoToMenuButton);
        goToMenuButton.setOnClickListener(v -> {
            this.startActivity(new Intent(this, MenuActivity.class));
        });

        cancelBillScreen = findViewById(R.id.CancelBillScreen);
//        ImageButton cancelBillButton = cancelBillScreen.findViewById(R.id.CancelBillButton);
//        cancelBillButton.setOnClickListener(e -> {
//            try {
//                ExecuteUpdate("UPDATE clients SET state = 1 WHERE tableID = " + thisTableID);
//                ExecuteUpdate("UPDATE tables SET state = 1 WHERE ID = " + thisTableID);
//                ExecuteUpdate("UPDATE orders JOIN clients ON orders.clientID = clients.ID SET orders.state = 2 WHERE clients.tableID = " + thisTableID + " AND orders.state = 3");
//            } catch (SQLException ignored) { }
//        });



        freezeButtonScreen = findViewById(R.id.FreezeButtonScreen);
    }

    private void generateOrdersView() {
        if(orderListLinearLayout != null) orderListLinearLayout.removeAllViews();
        orderListLinearLayout = findViewById(R.id.WishListLinearLayout);
        for (int orderNumber = 0; orderNumber < globalClient.orders.size(); orderNumber++) {

            Order globalOrder =  globalClient.orders.valueAt(orderNumber);

            globalOrder.orderElement = getLayoutInflater().inflate(R.layout.client_summary_element_order, null);
            TextView orderNumberTextView = globalOrder.orderElement.findViewById(R.id.OrderNumberTextView);
            TextView orderStateTextView = globalOrder.orderElement.findViewById(R.id.OrderStateTextView);
            TextView orderSumNumberTextView = globalOrder.orderElement.findViewById(R.id.OrderSumNumberTextView);


            orderNumberTextView.setText(globalOrder.getOrderNumberString());
            orderStateTextView.setText(globalOrder.getState());
            orderSumNumberTextView.setText(globalOrder.getTotalPriceString());

            LinearLayout wishListLinearLayout = globalOrder.orderElement.findViewById(R.id.WishListLinearLayout);

            for (int wishNumber = 0; wishNumber < globalOrder.wishes.size(); wishNumber++) {
                View wishElement = getLayoutInflater().inflate(R.layout.client_summary_element_wish, null);
                TextView wishNameTextView = wishElement.findViewById(R.id.WishNameTextView);
                TextView wishPriceTextView = wishElement.findViewById(R.id.WishPriceTextView);

                Wish wish = globalOrder.wishes.valueAt(wishNumber);

                wishNameTextView.setText(wish.dish.name);
                wishPriceTextView.setText(wish.getTotalPriceString());
                wishListLinearLayout.addView(wishElement);
            }
            orderListLinearLayout.addView(globalOrder.orderElement);
        }
    }


    private void updateView() {

        Table table = getFullTableData(thisTableID);
        Client client = table.clients.get(thisClientID);

        globalClient.state = client.state;
        globalTable.state = table.state;

        //load everything that is update from getFullTableData but remember to backup View elements
        //this way we update global tablepricing
        //dunno if it is really necessary

        for(int orderNumber = 0; orderNumber <  globalClient.orders.size(); orderNumber++) {

            Order globalOrder =  globalClient.orders.valueAt(orderNumber);

//            if(globalClient.orders.containsKey(globalOrder.id)){
                globalOrder.state = client.orders.get(globalOrder.id).state;
                TextView orderStateTextView = globalOrder.orderElement.findViewById(R.id.OrderStateTextView);
                runOnUiThread(() -> orderStateTextView.setText(globalOrder.getState()));
//            } else {
//                ((LinearLayout)(table.tableElement.findViewById(R.id.WishListLinearLayout))).removeView(globalClient.orders.get(globalOrder.id).orderElement);
//            }
        }

        switch (globalClient.state){
            case 1:
                runOnUiThread(() -> cancelBillScreen.setVisibility(View.GONE));
                runOnUiThread(() -> freezeButtonScreen.setVisibility(View.GONE));
                break;
            case 2:
                runOnUiThread(() -> cancelBillScreen.setVisibility(View.GONE));
                runOnUiThread(() -> freezeButtonScreen.setVisibility(View.VISIBLE));
                break;
            case 3:
                runOnUiThread(() -> cancelBillScreen.setVisibility(View.VISIBLE));
                runOnUiThread(() -> freezeButtonScreen.setVisibility(View.GONE));
                break;
            case 4:
                runOnUiThread(() -> cancelBillScreen.setVisibility(View.GONE));
                runOnUiThread(() -> freezeButtonScreen.setVisibility(View.GONE));
                break;
            default: break;
        }
//        if (globalClient.state == 1 || globalClient.state == 4)
//        else runOnUiThread(() -> cancelBillScreen.setVisibility(View.GONE));
//        if (globalTable.state == 2) runOnUiThread(() -> freezeButtonScreen.setVisibility(View.VISIBLE));
//        else runOnUiThread(() -> freezeButtonScreen.setVisibility(View.GONE));
//        if (globalClient.state == 3) runOnUiThread(() -> cancelBillScreen.setVisibility(View.VISIBLE));
//        else runOnUiThread(() -> cancelBillScreen.setVisibility(View.GONE));

        runOnUiThread(() -> clientPriceNumberTextView.setText(client.getTotalPriceString()));
        runOnUiThread(() -> tablePriceNumberTextView.setText(table.getTotalPriceString()));




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
                    updateView();

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
