package com.amm.orderify.structure;

import java.util.List;

public class DishCategory {
    public int id;
    public String name;
    public List<Dish> dishes;

    public DishCategory(int id, String name, List<Dish> dishes){
        this.id = id;
        this.name = name;
        this.dishes = dishes;
    }
}