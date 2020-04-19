package com.ola.demo.controllers;

import java.util.ArrayList;

public class Accounts {
    private ArrayList<Account> accounts = new ArrayList<>();
    public Accounts(){
        accounts.add(new Account("Caitlin", 100));
        accounts.add(new Account("Guido", 100));
        accounts.add(new Account("Thomas", 500));
        accounts.add(new Account("Jorre", 200));
    }
    public Account getAccount(String Id) {
        System.out.println(Id);
        for (Account acc : accounts) {
            System.out.println(acc.getId());
            if (Id.equals(acc.getId())) {
                return acc;
            }
        }
        return new Account("", 0);
    }
    public ArrayList<Account> printAccs(){
        return accounts;
    }

    public void updateBalance(String Id, int amount){
        for(Account acc : accounts){
            if(Id.equals(acc.getId())){
                acc.setBalance(acc.getBalance()+amount);
            }
        }
    }

    public void addAccount(String Id){
        accounts.add(new Account(Id, 0));
    }
    public void deleteAccount(String Id){
        accounts.removeIf(acc -> acc.getId().equals(Id));
    }
}
