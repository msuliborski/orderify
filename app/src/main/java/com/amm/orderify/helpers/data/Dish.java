package com.amm.orderify.helpers.data;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    public int id;
    public String name;
    public float price;
    public String descS;
    public String descL;
    public List<AddonCategory> addonCategories;
    public List<Addon> chosenAddons = new ArrayList<>(); //for menu handling
    public List<Addon> baseChosenAddons = new ArrayList<>(); //for menu handling

    public Dish(int id, String name, float price, String descS, String descL, List<AddonCategory> addonCategories) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.descS = descS;
        this.descL = descL;
        this.addonCategories = addonCategories;
    }

}