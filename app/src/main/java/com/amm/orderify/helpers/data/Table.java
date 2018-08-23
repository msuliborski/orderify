package com.amm.orderify.helpers.data;

import java.util.List;

public class Table {
    public int id;
    public int number;
    public String description;
    public int state;
    public List<Client> clients;

    public Table(int id, int number, String description, int state, List<Client> clients) {
        this.id = id;
        this.number = number;
        this.description = description;
        this.state = state;
        this.clients = clients;

    }

    public float getTotalPrice(){

        float totalPrice = 0;

        for (int clientNumber = 0; clientNumber < clients.size(); clientNumber++)
                totalPrice += clients.get(clientNumber).getTotalPrice();

        return totalPrice;
    }

}