package com.amm.orderify.helpers.data;

import java.text.DecimalFormat;

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

    public String getPriceString(){
        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(this.price) + " zł";
    }
}