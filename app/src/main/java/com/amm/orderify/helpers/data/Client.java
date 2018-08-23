package com.amm.orderify.helpers.data;

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

}