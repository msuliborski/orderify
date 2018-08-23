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

    public String getState(){
        String state;
        //states: 1-unfreezed, 2-freezed, 3-wantsHelp, 4-wantsABill =============================================================
        switch (this.state){
            case 1: state = "READY!"; break;
            case 2: state = "FREEZED!"; break;
            case 3: state = "NEEDS HELP!"; break;
            case 4: state = "WANTS A BILL!"; break;
            default: state = "BROKEN - contact dev!";
        }

        return state;
    }

}