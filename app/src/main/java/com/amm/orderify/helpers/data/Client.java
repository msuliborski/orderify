package com.amm.orderify.helpers.data;

import com.amm.orderify.R;

import java.util.List;

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

    public String getState(){
        String state;
        switch (this.state){
            case 1: state = String.valueOf(R.string.lifecycle_client_ready); break;
            case 2: state = String.valueOf(R.string.lifecycle_client_freezed); break;
            case 3: state = String.valueOf(R.string.lifecycle_client_payment); break;
            case 4: state = String.valueOf(R.string.lifecycle_client_help); break;
            default: state = "HEART BROKEN - contact dev!";
        }
        return state;
    }

}