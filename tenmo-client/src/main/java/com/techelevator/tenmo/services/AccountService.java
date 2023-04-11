package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

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

    public BigDecimal getBalance(AuthenticatedUser authenticatedUser) {
        Account currentAccount = getAccountByUserId(authenticatedUser.getUser().getId(), authenticatedUser);
        return currentAccount.getBalance();
    }


    public Account getAccountByUserId(int userId, AuthenticatedUser authenticatedUser) {
        Account account = null;
        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "account/user/" + userId, HttpMethod.GET, makeAuthEntity(authenticatedUser), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException rae) {
            System.out.println("Error in retrieving account");
        }
        return account;
    }

    public Account getAccountByAccountId(AuthenticatedUser authenticatedUser, int accountId) {
        Account account = null;
        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "account/" + accountId, HttpMethod.GET, makeAuthEntity(authenticatedUser), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException rcre) {
            BasicLogger.log(rcre.getRawStatusCode() + " : " + rcre.getStatusText());
        } catch (ResourceAccessException rae) {
            BasicLogger.log(rae.getMessage());
        }
        return account;
    }

    // needed an account id, rather than entire account object.
    public int getAccountIdByUserId(int id) {
        int accountId = 0;
        try {
            ResponseEntity<Integer> response = restTemplate.exchange(API_BASE_URL + "accounts/account?id=" + id, HttpMethod.GET, makeEntity(), Integer.class);
            accountId = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException | NullPointerException e) {
            BasicLogger.log(e.getMessage());
        }
        return accountId;
    }

}
