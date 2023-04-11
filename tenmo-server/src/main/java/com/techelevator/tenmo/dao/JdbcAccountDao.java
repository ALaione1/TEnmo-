package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component("JdbcAccountDao")
public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate jdbcTemplate;
    public JdbcAccountDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Account> list() {
        List<Account> getAllAccounts = new ArrayList<>();

        String sqlGetAllAccounts = "SELECT account_id, user_id, balance FROM account;";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlGetAllAccounts);
        while(srs.next()){
            Account account = mapAccountFromRowSet(srs);
            getAllAccounts.add(account);
        }
        return getAllAccounts;
    }

    private Account mapAccountFromRowSet(SqlRowSet srs) {
        Account newAccount = new Account();

        int accountId = srs.getInt("account_id");
        newAccount.setAccountId(accountId);

        int userId = srs.getInt("user_id");
        newAccount.setUserId(userId);

        BigDecimal balance = srs.getBigDecimal("balance");
        newAccount.setBalance(balance);

        return newAccount;
    }

    @Override
    public Account getAccount(int accountId) {
        Account account = new Account();

        String sqlGetAccountById = "SELECT account_id, user_id, balance " +
                "FROM account WHERE account_id = ?;";

        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlGetAccountById, accountId);
        if (srs.next()){
            account = mapAccountFromRowSet(srs);
        }
        return account;
    }

    @Override
    public void updateBalance(int accountId, BigDecimal balance) {
        String sqlUpdateBalance = "UPDATE account SET balance = ? WHERE account_id = ?;";
        jdbcTemplate.update(sqlUpdateBalance, balance, accountId);
    }

    @Override
    public Account getAccountFromUserId(int userId) {
        Account account = new Account();
        String sqlGetAccountByUserId = "SELECT account_id, user_id, balance FROM " +
                "account WHERE user_id = ?;";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlGetAccountByUserId, userId);
        while(srs.next()){
            account = mapAccountFromRowSet(srs);
        }
        return account;
    }
}
