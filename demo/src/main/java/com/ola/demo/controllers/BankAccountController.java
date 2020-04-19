package com.ola.demo.controllers;


import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class BankAccountController {
    @RequestMapping("/bank")

    public ArrayList<Account> getWelcome(){
        System.out.println("making account");
        Accounts acc= new Accounts();
        System.out.println(acc);
        System.out.println("printing accounts . . .");
        return acc.printAccs();
    }

    @GetMapping("/getAccount/{Id}")
    public Account getAccount(@PathVariable String Id){
        Accounts acc = new Accounts();
        return acc.getAccount(Id);
    }

    @PutMapping("/updateAccount/{Id}/{amount}")
    public Account updateBalance(@PathVariable("Id") String Id, @PathVariable("amount") int amount){
        Accounts acc = new Accounts();
        acc.updateBalance(Id, amount);
        return acc.getAccount(Id);
    }

    @PostMapping("/addAccount/{Id}")
    public ArrayList<Account> postAccount(@PathVariable String Id){
        Accounts acc = new Accounts();
        acc.addAccount(Id);
        return acc.printAccs();
    }
    @DeleteMapping("deleteAccount/{Id}")
    public ArrayList<Account> deleteAccount(@PathVariable String Id) {
        Accounts acc = new Accounts();
        acc.deleteAccount(Id);
        return acc.printAccs();
    }

}
