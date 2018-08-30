package com.amm.orderify.helpers.data;

import android.util.ArrayMap;
import android.view.View;

import com.amm.orderify.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.amm.orderify.MainActivity.context;

public class Order {
    public int id;
    public Date time;
    public Date date;
    public String comments;
    public int state;
    public int clientID;
    public int tableID;
    public ArrayMap<Integer,Wish> wishes;
    public View orderElement;

    public Order(int id, Date time, Date date, String comments, int state, int clientID, int tableID, ArrayMap<Integer,Wish> wishes) {
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
            if(state <= 3)totalPrice += wishes.valueAt(wishNumber).getTotalPrice();

        return totalPrice;
    }

    public String getTotalPriceString(){
        float totalPrice = 0;

        for (int wishNumber = 0; wishNumber < wishes.size(); wishNumber++)
            totalPrice += wishes.valueAt(wishNumber).getTotalPrice();

        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(totalPrice) + " zł";
    }

    public int getOrderNumber(){
        return this.id % 100;
    }

    public String getOrderNumberString(){
        return "Order #" + this.id % 100;
    }

    public String getState(){
        String state;
        switch (this.state){
            case 1: state = context.getString(R.string.lifecycle_order_preparation); break;
            case 2: state = context.getString(R.string.lifecycle_order_delivered); break;
            case 3: state = context.getString(R.string.lifecycle_order_payment); break;
            case 4: state = context.getString(R.string.lifecycle_order_paid); break;
            default: state = "HEART BROKEN - contact dev!";
        }
        return state;
    }

    public String getWaitingTime(){
        Date curr = Calendar.getInstance(TimeZone.getTimeZone("GMT+02:00")).getTime();
        Date orderTime = new Date(this.date.getTime() + this.time.getTime());
        Date diff = new Date(curr.getTime() - orderTime.getTime() - 3600000);
        //String seconds = String.format("%02d", (int) (diff.getTime() / 1000) % 60);String minutes = String.format("%02d", (int) ((diff.getTime() / (1000 * 60)) % 60));String hours = String.format("%02d", (int) ((diff.getTime() / (1000 * 60 * 60)) % 24));String days = String.valueOf((int) ((diff.getTime() / (1000 * 60 * 60 * 24))));return days + " days, " + hours + ":" + minutes + ":" + seconds;
        return String.valueOf((int)(diff.getTime() / (1000 * 60))+1) + " min";
    }
}