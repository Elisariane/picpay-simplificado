package com.picpay.picpaysimplificado.controllers;

import com.picpay.picpaysimplificado.domain.user.User;
import com.picpay.picpaysimplificado.dtos.UserDTO;
import com.picpay.picpaysimplificado.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        User newUser = userService.createUser(userDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users =  userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
