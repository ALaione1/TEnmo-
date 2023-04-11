package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao dao;

    @RequestMapping(method = RequestMethod.GET)
    public List<User> FindUAllUsers(){
        return dao.findAll();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public User getById(@PathVariable int id) {
        User user = dao.getUserById(id);
        if(user == null) {
            System.out.println("whoops");
        }
        else {
            return user;
        }
        return dao.getUserById(id);
    }

    @RequestMapping(path = "/username", method = RequestMethod.GET)
    public User findByName(@RequestParam(defaultValue = "") String username){
        User user = dao.findByUsername(username);
        if (!username.equals("")) {
            return user;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no input detected or user not found");
        }
    }

    @RequestMapping(path = "/username/id", method = RequestMethod.GET)
    public int findIdByUser(@RequestParam String username){
        return dao.findIdByUsername(username);
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    boolean create(@RequestBody String username, String password){
        return dao.create(username,password);
    }
}
