package com.amm.orderify.helpers.data;

import java.util.List;

public class DishCategory {
    public int id;
    public String name;
    public List<Dish> dishes;

    public DishCategory(int id, String name, List<Dish> items){
        this.id = id;
        this.name = name;
        this.dishes = items;
    }

    public String getIdString(){
        return id+"";
    }
}