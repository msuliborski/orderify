package com.amm.orderify.structure;

import java.util.List;

public class Wish {

    public int id;
    public String name;
    public float price;
    public int amount;
    public int dishID;
    public List<Addon> addons;

    public Wish(int id, String name, float price, int amount, int dishID, List<Addon> addons) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.dishID = dishID;
        this.addons = addons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}