package com.amm.orderify.structure;

import com.amm.orderify.helpers.data.Addon;

import java.util.List;

public class AddonCategory {
    public int id;
    public String name;
    public List<Addon> addons;

    public AddonCategory(int id, String name, List<Addon> addons){
        this.id = id;
        this.name = name;
        this.addons = addons;
    }
}