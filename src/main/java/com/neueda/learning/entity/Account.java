package com.neueda.learning.entity;

import java.math.BigDecimal;

public class Account {

    private int id;
    private String name;
    private BigDecimal balance;

    private Transaction transaction;
    public Account(){

    }

    public Account(int id, String name, BigDecimal balance , Transaction transaction) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.transaction=transaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
