package com.amm.orderify.structure;

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

}