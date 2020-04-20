package com.example.demo.controllers;


import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

@RestController
public class BankAccountController {
    Accounts acc= new Accounts();
    private Semaphore addAcc = new Semaphore(1);
    private Semaphore deleteAcc = new Semaphore(1);
    private Semaphore updateBall = new Semaphore(1);
    private Semaphore getACC = new Semaphore(1);
    @RequestMapping("/bank")

    public ArrayList<Account> getWelcome(){
        System.out.println("making account");
        System.out.println(acc);
        System.out.println("printing accounts . . .");
        return acc.printAccs();
    }

    @GetMapping("/getAccount/{Id}")
    public Account getAccount(@PathVariable String Id) throws InterruptedException {
        getACC.acquire();
        System.out.println("gatACC semaphore acquired");
        Account temp = acc.getAccount(Id);
        getACC.release();
        System.out.println("gatACC semaphore released");
        return temp;
    }

    @PutMapping("/updateAccount/{Id}/{amount}")
    public Account updateBalance(@PathVariable("Id") String Id, @PathVariable("amount") int amount) throws InterruptedException {
        updateBall.acquire();
        System.out.println("updateBall semaphore acquired");
        acc.updateBalance(Id, amount);
        Account temp = acc.getAccount(Id);
        updateBall.release();
        System.out.println("updateBall semaphore released");
        return temp;
    }

    @PostMapping("/addAccount/{Id}")
    public ArrayList<Account> postAccount(@PathVariable String Id) throws InterruptedException {
        addAcc.acquire();
        System.out.println("addAcc semaphore acquired");
        acc.addAccount(Id);
        ArrayList<Account> temp = acc.printAccs();
        addAcc.release();
        System.out.println("addAcc semaphore released");
        return temp;
    }
    @DeleteMapping("deleteAccount/{Id}")
    public ArrayList<Account> deleteAccount(@PathVariable String Id) throws InterruptedException {
        deleteAcc.acquire();
        System.out.println("deleteAcc semaphore acquired");
        acc.deleteAccount(Id);
        ArrayList<Account> temp = acc.printAccs();
        deleteAcc.release();
        System.out.println("deleteAcc semaphore released");
        return temp;
    }
}