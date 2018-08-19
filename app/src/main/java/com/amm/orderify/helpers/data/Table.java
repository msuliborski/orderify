package com.amm.orderify.helpers.data;

import java.util.List;

public class Table {
    public int id;
    public String name;
    public List<Order> orders;

    public Table(int id, String name, List<Order> orders) {
        this.id = id;
        this.name = name;
        this.orders = orders;
    }
}