package com.amm.orderify.helpers.data;

import android.util.ArrayMap;
import android.util.SparseArray;

import java.text.DecimalFormat;

public class Wish {

    public int id;
    public Dish dish;
    public int amount;
    public ArrayMap<Integer,Addon> addons;

    public Wish(int id, Dish dish, int amount, ArrayMap<Integer,Addon> addons) {
        this.id = id;
        this.dish = dish;
        this.amount = amount;
        this.addons = addons;
    }

    public float getTotalPrice(){
        float totalPrice = 0;

        for (int addonNumber = 0; addonNumber < addons.size(); addonNumber++)
            totalPrice += addons.valueAt(addonNumber).price;

        return (totalPrice + dish.price) * amount;
    }

    public String getTotalPriceString(){
        float totalPrice = 0;

        for (int addonNumber = 0; addonNumber < addons.size(); addonNumber++)
            totalPrice += addons.valueAt(addonNumber).price;

        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format((totalPrice + dish.price) * amount) + " zł";
    }
    public String getAmountString(){
        return amount + "";
    }

}