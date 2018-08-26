package com.amm.orderify.helpers;

import com.amm.orderify.helpers.data.Client;
import com.amm.orderify.helpers.data.Order;
import com.amm.orderify.helpers.data.Table;
import com.amm.orderify.helpers.data.Wish;

import java.util.List;

public class Comparators {

    public static List<Table> getDifferenceFromTableLists(List<Table> oldTables, List<Table> newTables){

        for(int ot = 0; ot < oldTables.size(); ot++){
            for(int nt = 0; nt < newTables.size(); nt++){
                if(tablesTheSame(oldTables.get(ot), newTables.get(nt)))
                    newTables.remove(newTables.get(nt));
            }
        }
        return newTables;
    }

    public static List<Order> getDifferenceFromOrderLists(List<Order> oldOrders, List<Order> newOrders){

        for(int ot = 0; ot < oldOrders.size(); ot++){
            for(int nt = 0; nt < newOrders.size(); nt++){
                if(ordersTheSame(oldOrders.get(ot), newOrders.get(nt)))
                    oldOrders.remove(oldOrders.get(ot));

            }
        }
        return newOrders;
    }

    public static boolean tablesTheSame(Table t1, Table t2){
        try {
            if (!(t1.id == t2.id)) return false;
            if (!(t1.number == t2.number)) return false;
            if (!(t1.description.equals(t2.description))) return false;
            if (!(t1.state == t2.state)) return false;

            for (int i = 0; i < t1.clients.size(); i++)
                for (int j = 0; j < t2.clients.size(); j++)
                    if (!clientsTheSame(t1.clients.get(i), t2.clients.get(j))) return false;
        } catch(Exception ignored){}

        return true;
    }

    public static boolean clientsTheSame(Client c1, Client c2){
        if (!(c1.id == c2.id)) return false;
        if (!(c1.number == c2.number)) return false;
        if (!(c1.state == c2.state)) return false;

        for (int i = 0; i < c1.orders.size(); i++)
            for (int j = 0; j < c2.orders.size(); j++)
                if (!ordersTheSame(c1.orders.get(i), c2.orders.get(j))) return false;

        return true;
    }

    public static boolean ordersTheSame(Order o1, Order o2){
        if (!(o1.id == o2.id)) return false;
        if (!(o1.time == o2.time)) return false;
        if (!(o1.date == o2.date)) return false;
        if (!(o1.comments.equals(o2.comments))) return false;
        if (!(o1.state == o2.state)) return false;

        for (int i = 0; i < o1.wishes.size(); i++)
            for (int j = 0; j < o2.wishes.size(); j++)
                if (!wishesTheSame(o1.wishes.get(i), o2.wishes.get(j))) return false;

        return true;
    }

    public static boolean wishesTheSame(Wish w1, Wish w2){
        try {
            if (!(w1.dish.id == w2.dish.id)) return false;
            if (!(w1.addons.size() == w2.addons.size())) return false;

            for (int i = 0; i < w1.addons.size(); i++){
                if (!(w1.addons.contains(w2.addons.get(i)))) return false;
                if (!(w2.addons.contains(w1.addons.get(i)))) return false;
            }
        }
        catch(Exception ignored){}

        return true;
    }
}
