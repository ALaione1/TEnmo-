package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private HttpEntity<Void> makeAuthEntity(AuthenticatedUser authenticatedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }

    public HttpEntity makeEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<String>(headers);
    }


    public void transferMoney(Transfer transfer, AuthenticatedUser authenticatedUser) {

        try {
            restTemplate.exchange(API_BASE_URL + "transfers/sendmoney",
                    HttpMethod.POST, makeTransferAuthEntity(authenticatedUser, transfer), Transfer.class);

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }



    public Transfer[] getAllTransfers(AuthenticatedUser authenticatedUser, long accountId) {
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "transfers/" + accountId, HttpMethod.GET, makeAuthEntity(authenticatedUser),
                    Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException rcre) {
            BasicLogger.log(rcre.getRawStatusCode() + " : " + rcre.getStatusText());
        } catch (ResourceAccessException rae) {
            BasicLogger.log(rae.getMessage());
        }
        return transfers;
    }

    //Todo: this is the current problem when trying to run the getPendingTransfers method in app.
    //Todo: not sure if it's the path or not. But running pendingTransfers breaks when it calls this method
    //Todo: says that transfer = null
    public Transfer getTransferByTransferId(AuthenticatedUser authenticatedUser, int transferId) {
        Transfer transfer = null;
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfers/get/" + transferId, HttpMethod.GET, makeAuthEntity(authenticatedUser),
                    Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException rcre) {
            BasicLogger.log(rcre.getRawStatusCode() + " : " + rcre.getStatusText());
        } catch (ResourceAccessException rae) {
            BasicLogger.log(rae.getMessage());
        }
        return transfer;
    }

    public Transfer getTransferStatusByTransferStatusId(AuthenticatedUser authenticatedUser, int transferStatusId) {
        Transfer transferStatus = null;
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfers/status/" + transferStatusId, HttpMethod.GET, makeAuthEntity(authenticatedUser),
                    Transfer.class);
            transferStatus = response.getBody();
        } catch (RestClientResponseException rcre) {
            BasicLogger.log(rcre.getRawStatusCode() + " : " + rcre.getStatusText());
        } catch (ResourceAccessException rae) {
            BasicLogger.log(rae.getMessage());
        }
        return transferStatus;
    }

    public Transfer getTransferTypeByTransferTypeId(AuthenticatedUser authenticatedUser, int transferTypeId) {
        Transfer transferType = null;
        try {
            HttpEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfers/type/" + transferTypeId, HttpMethod.GET, makeAuthEntity(authenticatedUser), Transfer.class);
            transferType = response.getBody();
        } catch (RestClientResponseException rcre) {
            BasicLogger.log(rcre.getRawStatusCode() + " : " + rcre.getStatusText());
        } catch (ResourceAccessException rae) {
            BasicLogger.log(rae.getMessage());
        }
        return transferType;
    }

    public void executeTransfer(Transfer transfer, AuthenticatedUser authenticatedUser) {
        try {
            restTemplate.exchange(API_BASE_URL + "transfers/executeTransfer", HttpMethod.POST, makeTransferAuthEntity(authenticatedUser, transfer), Transfer.class);
        } catch (RestClientResponseException e) {
            e.printStackTrace();
        }
    }

    public void approveTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        try {
            restTemplate.exchange(API_BASE_URL + "transfers/approval", HttpMethod.PUT, makeTransferAuthEntity(authenticatedUser, transfer), Transfer.class);
        } catch (RestClientResponseException rcre) {
            BasicLogger.log(rcre.getRawStatusCode() + " : " + rcre.getStatusText());
        } catch (ResourceAccessException rae) {
            BasicLogger.log(rae.getMessage());
        }
    }


    public void rejectTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        try {
            restTemplate.exchange(API_BASE_URL + "transfers/rejection", HttpMethod.PUT, makeTransferAuthEntity(authenticatedUser, transfer), Transfer.class);
        } catch (RestClientResponseException rcre) {
            BasicLogger.log(rcre.getRawStatusCode() + " : " + rcre.getStatusText());
        } catch (ResourceAccessException rae) {
            BasicLogger.log(rae.getMessage());
        }
    }


    private HttpEntity<Transfer> makeTransferAuthEntity(AuthenticatedUser authenticatedUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }


}
