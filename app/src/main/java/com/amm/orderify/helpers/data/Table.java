package com.amm.orderify.helpers.data;

import android.util.ArrayMap;
import com.amm.orderify.R;
import java.text.DecimalFormat;
import static com.amm.orderify.MainActivity.context;

public class Table {
    public int id;
    public int number;
    public String description;
    public int state;
    public ArrayMap<Integer,Client> clients;

    public Table(int id, int number, String description, int state, ArrayMap<Integer, Client> clients) {
        this.id = id;
        this.number = number;
        this.description = description;
        this.state = state;
        this.clients = clients;
    }

    public String getNumberString() {
        return "Table #" + this.number + "";
    }
    public String getPureNumberString() {
        return this.number + "";
    }

    public String getIdString() {
        return this.id + "";
    }

    public float getTotalPrice(){
        float totalPrice = 0;

        for (int clientNumber = 0; clientNumber < clients.size(); clientNumber++)
                totalPrice += clients.valueAt(clientNumber).getTotalPrice();

        return totalPrice;
    }

    public String getTotalPriceString(){
        float totalPrice = 0;

        for (int clientNumber = 0; clientNumber < clients.size(); clientNumber++)
                totalPrice += clients.valueAt(clientNumber).getTotalPrice();

        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(totalPrice) + " zł";
    }

    public String getState(){
        String state;
        switch (this.state){
            case 1: state = context.getString(R.string.lifecycle_table_ready); break;
            case 2: state = context.getString(R.string.lifecycle_table_freezed); break;
            case 3: state = context.getString(R.string.lifecycle_table_payment); break;
            case 4: state = context.getString(R.string.lifecycle_table_help); break;
            default: state = "HEART BROKEN - contact dev!";
        }
        return state;
    }

}