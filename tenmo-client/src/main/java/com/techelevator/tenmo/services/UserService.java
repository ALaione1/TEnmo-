package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserService {
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


    //get all users
    // changed from array to arraylist.
    public List<User> getAllUsers(AuthenticatedUser authenticatedUser) {
        List<User> userList = new ArrayList<>();
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL + "user/", HttpMethod.GET, makeAuthEntity(authenticatedUser), User[].class);
            userList = Arrays.asList(Objects.requireNonNull(response.getBody()));
        } catch (RestClientResponseException rcre) {
            BasicLogger.log(rcre.getRawStatusCode() + " : " + rcre.getStatusText());
        } catch (ResourceAccessException rae) {
            BasicLogger.log(rae.getMessage());
        }
        return userList;
    }

    public User getUserByUserId(AuthenticatedUser authenticatedUser, int userId) {
        User user = null;
        try {
            ResponseEntity<User> response = restTemplate.exchange(API_BASE_URL + "user/" + userId, HttpMethod.GET, makeAuthEntity(authenticatedUser), User.class);
            user = response.getBody();
        } catch (RestClientResponseException rcre) {
            BasicLogger.log(rcre.getRawStatusCode() + " : " + rcre.getStatusText());
        } catch (ResourceAccessException rae) {
            BasicLogger.log(rae.getMessage());
        }
        return user;
    }


}
