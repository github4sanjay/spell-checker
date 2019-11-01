package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserResource {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    private UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseBody
    public User create(@RequestBody User user ){
        LOGGER.info("User create call by username {}", user.getUserName());
        return userService.create(user);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<User>> get(){
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{userId}")
    @ResponseBody
    public ResponseEntity<User> get(@PathVariable UUID userId){
        LOGGER.info("User get call by id {}", userId);
        return new ResponseEntity<>(userService.findById(userId), HttpStatus.OK);
    }

}
