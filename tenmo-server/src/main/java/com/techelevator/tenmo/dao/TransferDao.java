package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    /*
    PK transfer_type *int*
    transfer_type_desc *String*

    PK transfer_status_id *int*
    transfer_status_desc

    PK transfer_id *int*
    FK transfer_type_id *int*
    FK transfer_status_id  *int*
    FK account_from *int*
    FK account_to *int*
    amount *BigDecimal*

    getAllTransfers *List<Transfers>*
    getTransferById *Transfer*
    getAllTransfersByAccount *List<Transfer>*
    getAllPendingTransfers *List<Transfer>*
    getAllPendingTransfersByAccountId *List<Transfer>*
    sendingTransfer*Transfer* --> status Approved, must be positive, cant be to self
    requestingTransfer*Transfer* --> status Pending, must be positive,
                                     cant be to self, no account changes until approved,
                                     must be able to reject transfer, pending transfers should
                                     be included in the transfer list
    void rejectRequest*int* --> no changes happen to account, status becomes rejected
    void approveRequest*int* --> transfer happens, status becomes approved

     */

    List<Transfer> getAllTransfers();
    List<Transfer> getAllTransfersByAccount(int accountId);
    List<Transfer> getAllTransfersPending();
    List<Transfer> getAllTransfersPendingByAccount(int accountId);

    //Todo: added so it can be called by the transferController
    List<Transfer> getAllTransfersPendingByTransferId(int transferId);

    //Todo: created this sendTransfer instead to include parameters for the sendTransfer in controller
    //changed to int instead of transfer
    int sendTransfer(int typeId, int statusId, int sendingAccount, int receivingAccount, BigDecimal amountToTransfer);
    Transfer getTransferById(int transferId);
    void requestTransfer(Transfer transfer);
    void rejectTransfer(int transferId);
    void approveTransfer(int transferId);

    void executeTransfer(Transfer transfer);

}
