package com.amm.orderify.helpers.data;

import com.amm.orderify.R;

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

    public String getState(){
        String state;
        switch (this.state){
            case 1: state = String.valueOf(R.string.lifecycle_table_ready); break;
            case 2: state = String.valueOf(R.string.lifecycle_table_freezed); break;
            case 3: state = String.valueOf(R.string.lifecycle_table_payment); break;
            case 4: state = String.valueOf(R.string.lifecycle_table_help); break;
            default: state = "HEART BROKEN - contact dev!";
        }
        return state;
    }

}