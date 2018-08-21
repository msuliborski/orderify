package com.amm.orderify.helpers.data;

import java.util.List;

public class Wish {

    public Dish dish;
    public int amount;
    public List<Addon> addons;

    public Wish(Dish dish, int amount, List<Addon> addons) {
        this.dish = dish;
        this.amount = amount;
        this.addons = addons;
    }

    public float getTotalPrice(){
        float totalPrice = 0;

        for (int addonNumber = 0; addonNumber < addons.size(); addonNumber++)
            totalPrice += addons.get(addonNumber).price;

        return (totalPrice + dish.price) * amount;
    }

}