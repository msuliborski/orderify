package com.amm.orderify.helpers.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Dish {

    public int id;
    public String name;
    public float price;
    public String descS;
    public String descL;
    public int dishCategory;
    public List<AddonCategory> addonCategories;
    public List<Addon> chosenAddons = new ArrayList<>(); //for menu handling

    public Dish(int id, String name, float price, String descS, String descL, int dishCategory, List<AddonCategory> addonCategories) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.descS = descS;
        this.descL = descL;
        this.dishCategory = dishCategory;
        this.addonCategories = addonCategories;
    }

    public String getPriceString(){
        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(this.price) + " zł";
    }

}