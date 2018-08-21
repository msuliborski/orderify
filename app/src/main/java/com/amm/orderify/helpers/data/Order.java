package com.amm.orderify.helpers.data;

import java.util.List;

public class Order {
    public int id;
    public String time;
    public String date;
    public int tableID;
    public String comments;
    public int state;
    public List<Wish> wishes;

    public Order(int id, String time, String date, int tableID, String comments, int state, List<Wish> wishes) {
        this.id = id;
        this.time = time;
        this.date = date;
        this.tableID = tableID;
        this.comments = comments;
        this.state = state;
        this.wishes = wishes;
    }

    public float getTotalPrice(){
        float totalPrice = 0;

        for (int wishNumber = 0; wishNumber < wishes.size(); wishNumber++)
            totalPrice += wishes.get(wishNumber).getTotalPrice();

        return totalPrice;
    }
}
