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
    public int tableID;
    public List<Wish> wishes;

    public Order(int id, Date time, Date date, String comments, int state, int clientID, int tableID, List<Wish> wishes) {
        this.id = id;
        this.time = time;
        this.date = date;
        this.comments = comments;
        this.state = state;
        this.clientID = clientID;
        this.tableID = tableID;
        this.wishes = wishes;
    }

    public float getTotalPrice(){
        float totalPrice = 0;

        for (int wishNumber = 0; wishNumber < wishes.size(); wishNumber++)
            totalPrice += wishes.get(wishNumber).getTotalPrice();

        return totalPrice;
    }

    public String getWaitingTime(){
        Date curr = new Date();
        Date orderTime = new Date(this.date.getTime() + this.time.getTime());
        Date diff = new Date(curr.getTime() - orderTime.getTime());
        String seconds = String.format("%02d", (int) (diff.getTime() / 1000) % 60);
        String minutes = String.format("%02d", (int) ((diff.getTime() / (1000 * 60)) % 60));
        String hours = String.format("%02d", (int) ((diff.getTime() / (1000 * 60 * 60)) % 24));
        String days = String.valueOf((int) ((diff.getTime() / (1000 * 60 * 60 * 24))));
        return days + " days, " + hours + ":" + minutes + ":" + seconds;
    }
}
