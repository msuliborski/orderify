package com.amm.orderify.structure;

import java.util.List;

public class Dish {

    public int id;
    public String name;
    public float price;
    public String descS;
    public String descL;
    //pulic int categoryID;

    public List<Addon> addons;

    public Dish(int id, String name, float price, String descS, String descL, List<Addon> addons) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.descS = descS;
        this.descL = descL;
        this.addons = addons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}