package com.techelevator.tenmo.model;


import java.math.BigDecimal;


public class Account {
    //attributes
    private int accountId;
    private int userId;
    User user;
    private BigDecimal balance;

    //constructor
    public Account(int accountId, int userId, BigDecimal balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;


    }
    public Account () {


    }

    public BigDecimal getBalance() {
        return balance;
    }


    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    //getters and setters
    public int getAccountId() {
        return accountId;
    }


    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }


    public int getUserId() {
        return userId;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return  "accountId: " + accountId +
                ", user: " + userId +
                ", balance: " + balance +
                '}';
    }
}
