package com.example.demo.controllers;

public class Account {
    public Account(String Id, double balance){
        this.Id=Id;
        this.balance=balance;
    }
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    private String Id;
    private double balance;

}
