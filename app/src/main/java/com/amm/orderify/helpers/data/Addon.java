package com.amm.orderify.helpers.data;

public class Addon {
    public int id;
    public String name;
    public float price;
    public int addonCategoryID;

    public Addon(int id, String name, float price, int addonCategoryID) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.addonCategoryID = addonCategoryID;
    }
}