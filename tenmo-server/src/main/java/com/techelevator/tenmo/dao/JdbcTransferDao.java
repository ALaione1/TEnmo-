package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component("JdbcTransferDao")
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;
    public JdbcTransferDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    public List<Transfer> getAllTransfers() {
        List<Transfer> list = new ArrayList<>();
        String sqlGetAllTransfers = "SELECT transfer_id, transfer_type_id, " +
                "transfer_status_id, account_from, account_to, amount FROM transfer;";

        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlGetAllTransfers);
        while(srs.next()){
            Transfer transfer = mapTransferFromRowSet(srs);
            list.add(transfer);
        }
        return list;
    }

    @Override
    public List<Transfer> getAllTransfersByAccount(int accountId) {
        List<Transfer> list = new ArrayList<>();
        String sqlGetAllTransfers = "SELECT transfer_id, transfer_type_id, " +
                "transfer_status_id, account_from, account_to, amount " +
                "FROM transfer WHERE account_from = ? OR account_to = ?;";

        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlGetAllTransfers, accountId, accountId);
        while(srs.next()){
            Transfer transfer = mapTransferFromRowSet(srs);
            list.add(transfer);
        }
        return list;
    }

    @Override
    public List<Transfer> getAllTransfersPending() {
        List<Transfer> list = new ArrayList<>();
        int pendingId = 0;  //TODO: make a method to pull up the ID for pending status
        String sqlGetAllTransfers = "SELECT transfer_id, transfer_type_id, " +
                "transfer_status_id, account_from, account_to, amount " +
                "FROM transfer WHERE transfer_status_id = ?;";

        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlGetAllTransfers, pendingId);
        while(srs.next()){
            Transfer transfer = mapTransferFromRowSet(srs);
            list.add(transfer);
        }
        return list;
    }

    @Override
    public List<Transfer> getAllTransfersPendingByAccount(int accountId) {
        List<Transfer> list = new ArrayList<>();
        int pendingId = 0;  //TODO: make a method to pull up the ID for pending status
        String sqlGetAllTransfers = "SELECT transfer_id, transfer_type_id, " +
                "transfer_status_id, account_from, account_to, amount FROM transfer " +
                "WHERE transfer_status_id = ? AND account_from = ? OR account_to = ?;";

        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlGetAllTransfers, pendingId, accountId, accountId);
        while(srs.next()){
            Transfer transfer = mapTransferFromRowSet(srs);
            list.add(transfer);
        }
        return list;
    }

    //Todo: added this so that the getPendingTransfersByTransferId method in transferService will run
    //Todo: copy and pasted method above, so I think the sql statement might need to be changed?
    @Override
    public List<Transfer> getAllTransfersPendingByTransferId(int transferId) {
        List<Transfer> list = new ArrayList<>();
        int pendingId = 0;  //TODO: make a method to pull up the ID for pending status
        String sqlGetAllTransfers = "SELECT transfer_id, transfer_type_id, " +
                "transfer_status_id, account_from, account_to, amount FROM transfer " +
                "WHERE transfer_status_id = ? AND account_from = ? OR account_to = ?;";

        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlGetAllTransfers, pendingId, transferId, transferId);
        while(srs.next()){
            Transfer transfer = mapTransferFromRowSet(srs);
            list.add(transfer);
        }
        return list;
    }


    //needs every value for transfer, this is not an update this is making a new transfer to put in the database
    //Todo: made couple changes here by adding if statement since there was a NPE warning
    @Override
    public int sendTransfer(int typeId, int statusId, int sendingAccount, int receivingAccount, BigDecimal amountToTransfer ) {
        System.out.println("send transfer method called");
        String sqlSendTransfer = "INSERT INTO transfer(transfer_type_id,transfer_status_id,account_from,account_to,amount) "
                + "VALUES (?,?,?,?,?) RETURNING transfer_id;";
        //removed the BigDecimal.class
        int id = jdbcTemplate.queryForObject(sqlSendTransfer, int.class, typeId, statusId, sendingAccount, receivingAccount, amountToTransfer);

        //was not needed
        // if(id == null) {
       //     throw new RuntimeException("failed to retrieve ID of new transfer.");
       // }
        //Transfer newTransfer = new Transfer(id, typeId, statusId, sendingAccount, receivingAccount, amountToTransfer);
        return id;
    }





    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        String sqlGetTransferById = "SELECT transfer_id, transfer_type_id, " +
                                "transfer_status_id, account_from, account_to, amount FROM transfer " +
                                "WHERE transfer_id = ?;";

        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlGetTransferById, transferId);

        while(srs.next()){
            transfer = mapTransferFromRowSet(srs);
        }
        return transfer;
    }

    @Override
    public void requestTransfer(Transfer transfer) {
        String sqlRequestTransfer = "INSERT INTO transfers" +
                "(transfer_type_id,transfer_status_id,account_from,account_to,amount) "
                + "VALUES (?,?,?,?,?);";

        transfer.setTransferStatus(1); //todo make a method for pulling the id for approved instead of hardcoding
        transfer.setTransferType(1); //todo make a method for sending the id for approved instead of hardcoding

        jdbcTemplate.update(
                sqlRequestTransfer,
                transfer.getTransferTypeId(),
                transfer.getTransferStatusId(),
                transfer.getAccountFrom(),
                transfer.getAccountTo(),
                transfer.getAmount());


    }

    @Override
    public void rejectTransfer(int transferId) {
        String sqlRejectTransfer = "UPDATE transfer SET transfer_status_id = 3 " +
                "WHERE transfer_id = ?;"; //todo method to get the id for reject
        jdbcTemplate.update(sqlRejectTransfer, transferId);
    }

    @Override
    public void approveTransfer(int transferId) {
        String sqlRejectTransfer = "UPDATE transfer SET transfer_status_id = 2 " +
                "WHERE transfer_id = ?;"; //todo method to get the id for approve
        jdbcTemplate.update(sqlRejectTransfer, transferId);
    }

    private Transfer mapTransferFromRowSet(SqlRowSet srs){

        Transfer transfer = new Transfer();

        int transferId = srs.getInt("transfer_id");
        transfer.setTransferId(transferId);

        int transferTypeId = srs.getInt("transfer_type_id");
        transfer.setTransferType(transferTypeId);

        int transferStatusId = srs.getInt("transfer_status_id");
        transfer.setTransferStatus(transferStatusId);

        int accountFrom = srs.getInt("account_from");
        transfer.setAccountSending(accountFrom);

        int accountTo = srs.getInt("account_to");
        transfer.setAccountReceiving(accountTo);

        BigDecimal amount = srs.getBigDecimal("amount");
        transfer.setTransferAmount(amount);

        return transfer;

    }

    public void executeTransfer(Transfer transfer) {
        String sqlNewTransfer = "INSERT INTO transfer(transfer_type_id,transfer_status_id,account_from,account_to,amount) "
                + " VALUES (?,?,?,?,?);";

        System.out.println("Inside the executeTransfer method!");

        jdbcTemplate.update(sqlNewTransfer,
                transfer.getTransferTypeId(),
                transfer.getTransferStatusId(),
                transfer.getAccountFrom(),
                transfer.getAccountTo(),
                transfer.getAmount());

        makeTheTransfer(transfer);
    }

    private void makeTheTransfer(Transfer transfer) {
        System.out.println("Inside the makeTheTransfer method" + transfer.toString());
        String subtractionSql = "UPDATE account SET balance = balance - ? WHERE account_id = ?;";
        String additionSql = "UPDATE account SET balance = balance + ? WHERE account_id = ?;";
        jdbcTemplate.update(subtractionSql, transfer.getAmount(), transfer.getAccountFrom());
        jdbcTemplate.update(additionSql, transfer.getAmount(), transfer.getAccountTo());
    }
}
