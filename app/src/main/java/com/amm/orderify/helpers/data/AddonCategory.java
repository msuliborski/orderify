package com.amm.orderify.helpers.data;

import java.util.List;

public class AddonCategory {
    public int id;
    public String name;
    public Boolean multiChoice;
    public List<Addon> addons;

    public AddonCategory(int id, String name, Boolean multiChoice, List<Addon> addons){
        this.id = id;
        this.name = name;
        this.multiChoice = multiChoice;
        this.addons = addons;
    }
}