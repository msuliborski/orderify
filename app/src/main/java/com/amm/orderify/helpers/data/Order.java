package com.amm.orderify.helpers.data;

import java.util.List;

public class Order {
    int id;
    String time;
    String date;
    int tableID;
    String comments;
    public List<Wish> wishes;

    public Order(int id, String time, String date, int tableID, String comments, List<Wish> wishes) {
        this.id = id;
        this.time = time;
        this.date = date;
        this.tableID = tableID;
        this.comments = comments;
        this.wishes = wishes;
    }
}
