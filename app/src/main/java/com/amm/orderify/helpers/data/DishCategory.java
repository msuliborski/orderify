package com.amm.orderify.helpers.data;

import android.util.ArrayMap;

public class DishCategory {
    public int id;
    public String name;
    public ArrayMap<Integer,Dish> dishes;

    public DishCategory(int id, String name, ArrayMap<Integer,Dish> dishes){
        this.id = id;
        this.name = name;
        this.dishes = dishes;
    }

    public String getIdString(){
        return id+"";
    }

    @Override
    public String toString() {
        return name;
    }
}