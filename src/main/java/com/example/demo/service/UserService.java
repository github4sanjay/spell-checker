package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.CustomExceptions;
import com.example.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User create(User user){
        user.setBlackListed(false);
        return userRepo.save(user);
    }

    public List<User> getAll(){
        return userRepo.findAll();
    }

    public User findById(UUID id){
        Optional<User> userOptional = userRepo.findById(id);
        userOptional.orElseThrow(() -> new CustomExceptions.UserNotFoundException("No user found with id " + id));
        return userOptional.get();
    }


}
