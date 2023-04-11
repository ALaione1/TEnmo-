package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/account")


public class AccountController {
    @Autowired
    private AccountDao dao;

    //public AccountController() {this.dao = new JdbcAccountDao();}

    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public List<Account> getAccounts(){
        return dao.list();
    }


    @RequestMapping(path = "/{accountId}", method = RequestMethod.GET)
    public Account getById(@PathVariable int accountId) {
        Account account = dao.getAccount(accountId);

        return account;
    }


    @RequestMapping(path = "/{accountId}", method = RequestMethod.PUT)
    public void updateBalance(@PathVariable int accountId, @PathVariable  BigDecimal balance){
        //Account updateAccount = dao.updateBalance(accountId,balance);
        dao.updateBalance(accountId , balance);
    }



    @RequestMapping(path = "/user/{userId}", method = RequestMethod.GET)
    public Account getFromUser(@PathVariable int userId) {
        Account account = dao.getAccountFromUserId(userId);
        if(account == null) {
            System.out.println("whoops");
        }
        else {
            return account;
        }
        return account;
    }


}
