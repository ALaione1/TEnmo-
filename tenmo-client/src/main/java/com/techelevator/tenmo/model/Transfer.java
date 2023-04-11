package com.techelevator.tenmo.model;


import java.math.BigDecimal;


public class Transfer {
    private int transferId, transferTypeId, transferStatusId, accountFrom, accountTo;
    private BigDecimal amount;

    //constructor empty constructor provided
    public Transfer(int transferId, int transferTypeId, int transferStatusId, int accountFrom, int accountTo, BigDecimal amount){
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public Transfer(){
    }

    //getters

    public int getTransferId() {
        return transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }


    //setters may or may not need set transfer id

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public String getTypeById(){
        String type = "";
        switch(this.transferTypeId){
            case 1:
                type = "Request";
                break;
            case 2:
                type = "Send";
                break;
        }
        return type;
    }

    public String getStatusById(){
        String status = "";
        switch(this.transferStatusId){
            case 1:
                status = "Pending";
                break;
            case 2:
                status = "Approved";
                break;
            case 3:
                status = "Rejected";
                break;
        }
        return status;
    }



}
