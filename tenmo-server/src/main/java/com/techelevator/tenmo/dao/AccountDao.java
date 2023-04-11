package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    List<Account> list();

    Account getAccount(int accountId);

    void updateBalance(int accountId, BigDecimal balance);

    Account getAccountFromUserId(int userId);


}
