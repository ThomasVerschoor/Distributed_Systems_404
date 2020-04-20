package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

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
        System.out.println(Id);
        boolean lel=false;
        for (Account acc : accounts) {
            System.out.println(acc.getId());
            if (Id.equals(acc.getId())) {
                lel = true;
            }
        }
        if(!lel) {
            accounts.add(new Account(Id, 0));
        }else{
            System.out.println("account already taken");
        }
    }

    public void deleteAccount(String Id){
        accounts.removeIf(acc -> acc.getId().equals(Id));
    }
}