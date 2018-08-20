package com.amm.orderify.helpers.data;

import java.util.List;

public class Table {
    public int id;
    public int number;
    public String description;
    public int state;
    public List<Order> orders;

    public Table(int id, int number, String description, int state, List<Order> orders) {
        this.id = id;
        this.number = number;
        this.description = description;
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