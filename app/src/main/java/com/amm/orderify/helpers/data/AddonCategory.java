package com.amm.orderify.helpers.data;

import java.util.List;

public class AddonCategory {
    public int id;
    public String name;
    public String description;
    public Boolean multiChoice;
    public List<Addon> addons;

    public AddonCategory(int id, String name, String description, Boolean multiChoice, List<Addon> addons){
        this.id = id;
        this.name = name;
        this.description = description;
        this.multiChoice = multiChoice;
        this.addons = addons;
    }

    public String getIdString() {
        return id+"";
    }
}

//
//        if(description == null) this.description = "";
//        else this.description = description;