package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transfers")

public class TransferController {
    @Autowired


    private TransferDao dao;


    @RequestMapping(method = RequestMethod.GET)
    public List<Transfer> getTransfers() {

        return dao.getAllTransfers();
    }

    @RequestMapping(path = "/{accountId}", method = RequestMethod.GET)
    public List<Transfer> getTransfersByAccount(@PathVariable int accountId) {
        return dao.getAllTransfersByAccount(accountId);
    }

    @RequestMapping(path = "/pending", method = RequestMethod.GET)
    public List<Transfer> getTransfersPending() {
        return dao.getAllTransfersPending();
    }

    @RequestMapping(path = "/account/{accountId}", method = RequestMethod.GET)
    public List<Transfer> getTransfersPendingByAccount(@PathVariable int accountId) {
        return dao.getAllTransfersByAccount(accountId);
    }

    //Todo: path might be the issue. Created this because the method in TransferService was using transferId instead of accountId
    @RequestMapping(path = "/pending/{transferId}", method = RequestMethod.GET)
    public List<Transfer> getTransfersPendingByTransferId(@PathVariable int transferId) {
        return dao.getAllTransfersPendingByTransferId(transferId);
    }

// i do not recall if this has been here the past few pushes
//    @RequestMapping(path = "/sendmoney", method = RequestMethod.POST)
//    public Transfer sendTransfer(@RequestBody Transfer transfer) {
//        return dao.sendTransfer(transfer);
//    }


    @RequestMapping(path = "/sendmoney", method = RequestMethod.POST)
    public void sendTransfer(@RequestBody Transfer transfer) {
        dao.sendTransfer(transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFrom(),
                transfer.getAccountTo(), transfer.getAmount());
    }

    @RequestMapping(path = "/get/{transferId}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable int transferId) {
        return dao.getTransferById(transferId);
    }

    @RequestMapping(path = "/request", method = RequestMethod.GET)
    public void requestTransfer(@RequestBody Transfer transfer) {
        dao.requestTransfer(transfer);
    }

    @RequestMapping(path = "/reject/{transferId}", method = RequestMethod.GET)
    public void rejectTransfer(@PathVariable int transferId) {
        dao.rejectTransfer(transferId);

    }

   @RequestMapping(path = "/approve/{transferId}", method = RequestMethod.GET)
    public void approveTransfer(@PathVariable int transferId) {
        dao.approveTransfer(transferId);

    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/executeTransfer", method = RequestMethod.POST)
    public void executeTransfer(@RequestBody Transfer transfer) {
        dao.executeTransfer(transfer);
    }
}