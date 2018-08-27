package com.amm.orderify.helpers.data;

import com.amm.orderify.R;

import java.text.DecimalFormat;
import java.util.List;

import static com.amm.orderify.MainActivity.context;

public class Client {
    public int id;
    public int number;
    public int state;
    public List<Order> orders;

    public Client(int id, int number, int state, List<Order> orders) {
        this.id = id;
        this.number = number;
        this.state = state;
        this.orders = orders;
    }

    public float getTotalPrice(){
        float totalPrice = 0;

        for (int orderNumber = 0; orderNumber < orders.size(); orderNumber++)
            totalPrice += orders.get(orderNumber).getTotalPrice();

        return totalPrice;
    }

    public String getTotalPriceString(){
        float totalPrice = 0;

        for (int orderNumber = 0; orderNumber < orders.size(); orderNumber++)
            totalPrice += orders.get(orderNumber).getTotalPrice();

        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(totalPrice) + " zł";
    }

    public String getState(){
        String state;
        switch (this.state){
            case 1: state = context.getString(R.string.lifecycle_client_ready); break;
            case 2: state = context.getString(R.string.lifecycle_client_freezed); break;
            case 3: state = context.getString(R.string.lifecycle_client_payment); break;
            case 4: state = context.getString(R.string.lifecycle_client_help); break;
            default: state = "HEART BROKEN - contact dev!";
        }
        return state;
    }

}