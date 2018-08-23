package com.amm.orderify.helpers.data;

import java.util.Date;
import java.util.List;

public class Order {
    public int id;
    public Date time;
    public Date date;
    public String comments;
    public int state;
    public int clientID;
    public List<Wish> wishes;

    public Order(int id, Date time, Date date, String comments, int state, int clientID, List<Wish> wishes) {
        this.id = id;
        this.time = time;
        this.date = date;
        this.comments = comments;
        this.state = state;
        this.clientID = clientID;
        this.wishes = wishes;
    }

    public float getTotalPrice(){
        float totalPrice = 0;

        for (int wishNumber = 0; wishNumber < wishes.size(); wishNumber++)
            totalPrice += wishes.get(wishNumber).getTotalPrice();

        return totalPrice;
    }
}
