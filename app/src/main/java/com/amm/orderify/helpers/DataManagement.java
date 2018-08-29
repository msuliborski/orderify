package com.amm.orderify.helpers;

import android.util.ArrayMap;
import android.util.Log;

import com.amm.orderify.helpers.data.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class DataManagement {


    public static ArrayMap<Integer, DishCategory> getFullMenuData() {

        ArrayMap<Integer, Addon> addons = new ArrayMap<>();
        ArrayMap<Integer, AddonCategory> addonCategories = new ArrayMap<>();
        ArrayMap<Integer, Dish> dishes = new ArrayMap<>();
        ArrayMap<Integer, DishCategory> dishCategories = new ArrayMap<>();

        try {
            Statement dishCategoriesS = getConnection().createStatement();
            ResultSet dishCategoriesRS = dishCategoriesS.executeQuery("SELECT * FROM dishCategories");
            while (dishCategoriesRS.next()) {
                Statement dishesS = getConnection().createStatement();
                ResultSet dishesRS = dishesS.executeQuery("SELECT * FROM dishes \n" +
                        "WHERE dishCategoryID = " + dishCategoriesRS.getInt("ID"));
                while (dishesRS.next()) {
                    Statement addonCategoriesS = getConnection().createStatement();
                    ResultSet addonCategoriesRS = addonCategoriesS.executeQuery("SELECT * FROM addonCategoriesToDishes\n" +
                            "JOIN addonCategories ON addonCategories.ID = addonCategoriesToDishes.addonCategoryID\n" +
                            "WHERE dishID = " + dishesRS.getInt("ID"));
                    while (addonCategoriesRS.next()) {
                        Statement addonsS = getConnection().createStatement();
                        ResultSet addonsRS = addonsS.executeQuery("SELECT * FROM addons \n" +
                                "WHERE addonCategoryID = " + addonCategoriesRS.getInt("addonCategoryID"));
                        while (addonsRS.next()) {
                            addons.put(addonsRS.getInt("ID"), new Addon(addonsRS.getInt("ID"), addonsRS.getString("name"), addonsRS.getFloat("price"), addonsRS.getInt("addonCategoryID")));
                        }
                        addonCategories.put(addonCategoriesRS.getInt("ID"), new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), addons));
                        addons = new ArrayMap<>();
                    }
                    dishes.put(dishesRS.getInt("ID"), new Dish(dishesRS.getInt("ID"), dishesRS.getInt("number"), dishesRS.getString("name"), dishesRS.getFloat("price"), dishesRS.getString("descS"), dishesRS.getString("descL"), dishesRS.getInt("dishCategoryID") , addonCategories));
                    addonCategories = new ArrayMap<>();
                }
                dishCategories.put(dishCategoriesRS.getInt("ID"), new DishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), dishes));
                dishes = new ArrayMap<>();
            }
            return dishCategories;
        } catch(SQLException e) { Log.wtf("SQLException", "Code: " + e.getErrorCode() + ", \nMessage:" + e.getMessage()); }
        return new ArrayMap<>();
    }


    public static ArrayMap<Integer, Table> getFullTablesData(){
        if (notLocked()) {
            ArrayMap<Integer, Table> tables = new ArrayMap<>();
            ArrayMap<Integer, Client> clients = new ArrayMap<>();
            ArrayMap<Integer, Order> orders = new ArrayMap<>();
            ArrayMap<Integer, Wish> wishes = new ArrayMap<>();
            ArrayMap<Integer, Addon> addons = new ArrayMap<>();
            try {
                Statement tablesS = getConnection().createStatement();
                ResultSet tablesRS = tablesS.executeQuery("SELECT * FROM tables");
                while (tablesRS.next()) {
                    Statement clientS = getConnection().createStatement();
                    ResultSet clientRS = clientS.executeQuery("SELECT * FROM clients \n" +
                            "WHERE tableID = " + tablesRS.getInt("ID"));
                    while (clientRS.next()) {
                        Statement ordersS = getConnection().createStatement();
                        ResultSet ordersRS = ordersS.executeQuery("SELECT * FROM orders \n" +
                                "WHERE clientID = " + clientRS.getInt("ID"));
                        while (ordersRS.next()) {
                            Statement wishesS = getConnection().createStatement();
                            ResultSet wishesRS = wishesS.executeQuery("SELECT wishes.ID, dishes.number, dishID, dishes.dishCategoryID, name, price, amount, orderID FROM wishes\n" +
                                    "JOIN dishes ON dishes.ID = wishes.dishID\n" +
                                    "WHERE orderID = " + ordersRS.getInt("ID"));
                            while (wishesRS.next()) {
                                Statement addonsS = getConnection().createStatement();
                                ResultSet addonsRS = addonsS.executeQuery("SELECT addonID, name, price, addonCategoryID FROM addonsToWishes\n" +
                                        "JOIN addons ON addons.ID = addonsToWishes.addonID\n" +
                                        "WHERE wishID = " + wishesRS.getInt("ID"));
                                while (addonsRS.next()) {
                                    addons.put(addonsRS.getInt("addonID"), new Addon(addonsRS.getInt("addonID"), addonsRS.getString("name"), addonsRS.getFloat("price"), addonsRS.getInt("addonCategoryID")));
                                }
                                Dish dish = new Dish(wishesRS.getInt("dishID"), wishesRS.getInt("number"), wishesRS.getString("name"), wishesRS.getFloat("price"), null, null, wishesRS.getInt("dishCategoryID"), null);
                                wishes.put(wishesRS.getInt("ID"), new Wish(wishesRS.getInt("ID"), dish, wishesRS.getInt("amount"), addons));
                                addons = new ArrayMap<>();
                            }
                            orders.put(ordersRS.getInt("ID"), new Order(ordersRS.getInt("ID"), ordersRS.getTime("time"), ordersRS.getDate("date"), ordersRS.getString("comments"), ordersRS.getInt("state"), clientRS.getInt("ID"), tablesRS.getInt("ID"), wishes));
                            wishes = new ArrayMap<>();
                        }
                        clients.put(clientRS.getInt("ID"), new Client(clientRS.getInt("ID"), clientRS.getInt("number"), clientRS.getInt("state"), clientRS.getInt("tableID"), orders));
                        orders = new ArrayMap<>();
                    }
                    tables.put(tablesRS.getInt("ID"), new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), tablesRS.getInt("state"), clients));
                    clients = new ArrayMap<>();
                }
                return tables;
            } catch(SQLException e) { Log.wtf("SQLException", "Code: " + e.getErrorCode() + ", \nMessage:" + e.getMessage()); }
        }
        return new ArrayMap<>();
    }

    public static Table getFullTableData(int tableID){
        Table table = null;
        ArrayMap<Integer, Client> clients = new ArrayMap<>();
        ArrayMap<Integer, Order> orders = new ArrayMap<>();
        ArrayMap<Integer, Wish> wishes = new ArrayMap<>();
        ArrayMap<Integer, Addon> addons = new ArrayMap<>();
        try {
            Statement clientS = getConnection().createStatement();
            ResultSet clientRS = clientS.executeQuery("SELECT tables.ID AS tableID, tables.number AS tableNumber, tables.description AS tableDescription, tables.state AS tableState, clients.ID AS clientID, clients.number AS clientNumber, clients.state AS clientState FROM tables \n" +
                    "JOIN clients ON clients.tableID = tables.ID\n" +
                    "WHERE tableID = " + tableID);
            while (clientRS.next()) {
                Statement ordersS = getConnection().createStatement();
                ResultSet ordersRS = ordersS.executeQuery("SELECT * FROM orders \n" +
                        "WHERE clientID = " + clientRS.getInt("clientID"));
                while (ordersRS.next()) {
                    Statement wishesS = getConnection().createStatement();
                    ResultSet wishesRS = wishesS.executeQuery("SELECT wishes.ID, dishes.number, dishID, dishes.dishCategoryID, name, price, amount, orderID FROM wishes\n" +
                            "JOIN dishes ON dishes.ID = wishes.dishID\n" +
                            "WHERE orderID = " + ordersRS.getInt("ID"));
                    while (wishesRS.next()) {
                        Statement addonsS = getConnection().createStatement();
                        ResultSet addonsRS = addonsS.executeQuery("SELECT addonID, name, price, addonCategoryID FROM addonsToWishes\n" +
                                "JOIN addons ON addons.ID = addonsToWishes.addonID\n" +
                                "WHERE wishID = " + wishesRS.getInt("ID"));
                        while (addonsRS.next()) {
                            addons.put(addonsRS.getInt("addonID"), new Addon(addonsRS.getInt("addonID"), addonsRS.getString("name"), addonsRS.getFloat("price"), addonsRS.getInt("addonCategoryID")));
                        }
                        Dish dish = new Dish(wishesRS.getInt("dishID"), wishesRS.getInt("number"), wishesRS.getString("name"), wishesRS.getFloat("price"), null, null, wishesRS.getInt("dishCategoryID"), new ArrayMap<>());
                        wishes.put(wishesRS.getInt("ID"), new Wish(wishesRS.getInt("ID"), dish, wishesRS.getInt("amount"), addons));
                        addons = new ArrayMap<>();
                    }
                    orders.put(ordersRS.getInt("ID"), new Order(ordersRS.getInt("ID"), ordersRS.getTime("time"), ordersRS.getDate("date"), ordersRS.getString("comments"), ordersRS.getInt("state"), clientRS.getInt("clientID"), clientRS.getInt("tableID"), wishes));
                    wishes = new ArrayMap<>();
                }
                clients.put(clientRS.getInt("clientID"), new Client(clientRS.getInt("clientID"), clientRS.getInt("clientNumber"), clientRS.getInt("clientState"), clientRS.getInt("tableID"), orders));
                orders = new ArrayMap<>();
                table = new Table(clientRS.getInt("tableID"), clientRS.getInt("tableNumber"), clientRS.getString("tableDescription"), clientRS.getInt("tableState"), clients);
            }
            return table;
        } catch(SQLException e) { Log.wtf("SQLException", "Code: " + e.getErrorCode() + ", \nMessage:" + e.getMessage()); }
        return null;
    }


    public static ArrayMap<Integer, Order> getNewOrders() {
        if (notLocked()) {
            ArrayMap<Integer, Order> newOrders = new ArrayMap<>();
            ArrayMap<Integer, Wish> newWishes = new ArrayMap<>();
            ArrayMap<Integer, Addon> newAddons = new ArrayMap<>();
            try {
                Statement newOrdersS = getConnection().createStatement();
                ResultSet newOrdersRS = newOrdersS.executeQuery("SELECT newOrders.*, " +
                        "(SELECT ID FROM tables WHERE ID = clients.tableID) AS tableID FROM newOrders\n" +
                        "JOIN clients ON clients.ID = newOrders.clientID\n" +
                        "GROUP BY newOrders.ID");
                while (newOrdersRS.next()) {
                    Statement newWishesS = getConnection().createStatement();
                    ResultSet newWishesRS = newWishesS.executeQuery("SELECT newWishes.ID, dishes.number, dishID, dishes.dishCategoryID, name, price, amount, orderID FROM newWishes\n" +
                            "JOIN dishes ON dishes.ID = newWishes.dishID\n" +
                            "WHERE orderID = " + newOrdersRS.getInt("ID"));
                    while (newWishesRS.next()) {
                        Statement addonsS = getConnection().createStatement();
                        ResultSet addonsRS = addonsS.executeQuery("SELECT addonID, name, price, addonCategoryID FROM newAddonsToWishes\n" +
                                "JOIN addons ON addons.ID = newAddonsToWishes.addonID\n" +
                                "WHERE wishID = " + newWishesRS.getInt("ID"));
                        while (addonsRS.next()) {
                            newAddons.put(addonsRS.getInt("addonID"), new Addon(addonsRS.getInt("addonID"), addonsRS.getString("name"), addonsRS.getFloat("price"), addonsRS.getInt("addonCategoryID")));
                        }
                        Dish dish = new Dish(newWishesRS.getInt("dishID"), newWishesRS.getInt("number"), newWishesRS.getString("name"), newWishesRS.getFloat("price"), null, null, newWishesRS.getInt("dishCategoryID"), new ArrayMap<>());
                        newWishes.put(newWishesRS.getInt("ID"), new Wish(newWishesRS.getInt("ID"), dish, newWishesRS.getInt("amount"), newAddons));
                        newAddons = new ArrayMap<>();
                    }
                    newOrders.put(newOrdersRS.getInt("ID"), new Order(newOrdersRS.getInt("ID"), newOrdersRS.getTime("time"), newOrdersRS.getDate("date"), newOrdersRS.getString("comments"), newOrdersRS.getInt("state"), newOrdersRS.getInt("clientID"), newOrdersRS.getInt("tableID"), newWishes));
                    newWishes = new ArrayMap<>();
                }
                ExecuteUpdate("DELETE FROM newAddonsToWishes");
                ExecuteUpdate("DELETE FROM newWishes");
                ExecuteUpdate("DELETE FROM newOrders");
            } catch(SQLException e) { Log.wtf("SQLException", "Code: " + e.getErrorCode() + ", \nMessage:" + e.getMessage()); }
            return newOrders;
        }
        return new ArrayMap<>();
    }

    public static ArrayMap<Integer, Order> getAllOrders() {
        while (true){
            if(notLocked()) break;
        }
        ArrayMap<Integer,Order> orders = new ArrayMap<>();
        ArrayMap<Integer,Wish> wishes = new ArrayMap<>();
        ArrayMap<Integer,Addon> addons = new ArrayMap<>();
        try {
            Statement ordersS = getConnection().createStatement();
            ResultSet ordersRS = ordersS.executeQuery("SELECT orders.*, " +
                    "(SELECT ID FROM tables WHERE ID = clients.tableID) AS tableID FROM orders\n" +
                    "JOIN clients ON clients.ID = orders.clientID\n" +
                    "GROUP BY orders.ID");
            while (ordersRS.next()) {
                Statement wishesS = getConnection().createStatement();
                ResultSet wishesRS = wishesS.executeQuery("SELECT wishes.ID, dishID, dishes.number, dishes.dishCategoryID, name, price, amount, orderID FROM wishes\n" +
                        "JOIN dishes ON dishes.ID = wishes.dishID\n" +
                        "WHERE orderID = " + ordersRS.getInt("ID"));
                while (wishesRS.next()) {
                    Statement addonsS = getConnection().createStatement();
                    ResultSet addonsRS = addonsS.executeQuery("SELECT addonID, name, price, addonCategoryID FROM addonsToWishes\n" +
                            "JOIN addons ON addons.ID = addonsToWishes.addonID\n" +
                            "WHERE wishID = " + wishesRS.getInt("ID"));
                    while (addonsRS.next()) {
                        addons.put(addonsRS.getInt("addonID"), new Addon(addonsRS.getInt("addonID"), addonsRS.getString("name"), addonsRS.getFloat("price"), addonsRS.getInt("addonCategoryID")));
                    }
                    Dish dish = new Dish(wishesRS.getInt("dishID"), wishesRS.getInt("number"), wishesRS.getString("name"), wishesRS.getFloat("price"), null, null, wishesRS.getInt("dishCategoryID"), new ArrayMap<>());
                    wishes.put(wishesRS.getInt("ID"), new Wish(wishesRS.getInt("ID"), dish, wishesRS.getInt("amount"), addons));
                    addons = new ArrayMap<>();
                }
                orders.put(ordersRS.getInt("ID"), new Order(ordersRS.getInt("ID"), ordersRS.getTime("time"), ordersRS.getDate("date"), ordersRS.getString("comments"), ordersRS.getInt("state"), ordersRS.getInt("clientID"), ordersRS.getInt("tableID"), wishes));
                wishes = new ArrayMap<>();
            }
            ExecuteUpdate("DELETE FROM newAddonsToWishes");
            ExecuteUpdate("DELETE FROM newWishes");
            ExecuteUpdate("DELETE FROM newOrders");
        } catch(SQLException e) { Log.wtf("SQLException", "Code: " + e.getErrorCode() + ", \nMessage:" + e.getMessage()); }
        return orders;
    }

    public static ArrayMap<Integer, Table> getOnlyTables(){
        ArrayMap<Integer, Table> tables = new ArrayMap<>();
        ArrayMap<Integer, Client> clients = new ArrayMap<>();
        ArrayMap<Integer, Order> orders = new ArrayMap<>();
        try {
            Statement tablesS = getConnection().createStatement();
            ResultSet tablesRS = tablesS.executeQuery("SELECT * FROM tables");
            while (tablesRS.next()) {
                Statement clientS = getConnection().createStatement();
                ResultSet clientRS = clientS.executeQuery("SELECT * FROM clients \n" +
                        "WHERE tableID = " + tablesRS.getInt("ID"));
                while (clientRS.next()) {
                    Statement ordersS = getConnection().createStatement();
                    ResultSet ordersRS = ordersS.executeQuery("SELECT * FROM orders \n" +
                            "WHERE clientID = " + clientRS.getInt("ID"));
                    while (ordersRS.next()) {
                        orders.put(ordersRS.getInt("ID"), new Order(ordersRS.getInt("ID"), ordersRS.getTime("time"), ordersRS.getDate("date"), ordersRS.getString("comments"), ordersRS.getInt("state"), clientRS.getInt("ID"), tablesRS.getInt("ID"), new ArrayMap<>()));
                    }
                    clients.put(clientRS.getInt("ID"), new Client(clientRS.getInt("ID"), clientRS.getInt("number"), clientRS.getInt("state"), clientRS.getInt("tableID"), orders));
                    orders = new ArrayMap<>();
                }
                tables.put(tablesRS.getInt("ID"), new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), tablesRS.getInt("state"), clients));
                clients = new ArrayMap<>();
            }
        } catch(SQLException e) { Log.wtf("SQLException", "Code: " + e.getErrorCode() + ", \nMessage:" + e.getMessage()); }
        return tables;
    }

    private static boolean notLocked(){
        boolean locked = false;
        try {
            ResultSet padlockRS = ExecuteQuery("SELECT * FROM padlock; \n");
            if (padlockRS.next()) {
                locked = true;
                if(padlockRS.getInt("TTL") == 0) ExecuteUpdate("DELETE FROM padlock WHERE ID = " + padlockRS.getInt("ID"));
                else ExecuteUpdate("UPDATE padlock SET TTL = " + (padlockRS.getInt("TTL") - 1) + " WHERE ID = " + padlockRS.getInt("ID"));
            }
        } catch(SQLException e) { Log.wtf("SQLException", "Code: " + e.getErrorCode() + ", \nMessage:" + e.getMessage()); }
        return !locked;
    }

    public static void deleteOrder(Order order){
        try {
            for(int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                for (int addonNumber = 0; addonNumber < order.wishes.valueAt(wishNumber).addons.size(); addonNumber++){
                    ExecuteUpdate("DELETE FROM addonsToWishes WHERE addonID = " + order.wishes.valueAt(wishNumber).addons.valueAt(addonNumber).id);
                }
                ExecuteUpdate("DELETE FROM wishes WHERE ID = " + order.wishes.valueAt(wishNumber).id);
            }
            ExecuteUpdate("DELETE FROM orders WHERE ID = " + order.id);
        } catch(SQLException e) { Log.wtf("SQLException", "Code: " + e.getErrorCode() + ", \nMessage:" + e.getMessage()); }
    }
}
