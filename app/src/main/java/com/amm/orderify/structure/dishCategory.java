package com.amm.orderify.structure;

import java.util.List;

public class dishCategory {
    public int id;
    public String name;
    public List<Dish> dishes;

    public dishCategory(int id, String name, List<Dish> items){
        this.id = id;
        this.name = name;
        dishes = items;
    }
}