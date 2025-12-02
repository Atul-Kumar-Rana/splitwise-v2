package com.example.splitwise.service;

import com.example.splitwise.model.User;
import com.example.splitwise.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @Transactional
    public User createUser(User u){
        return userRepo.save(u);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(Long id){
        Optional<User> opt = userRepo.findById(id);
        // initialize lazy collections while we are still inside the transaction
        opt.ifPresent(user -> {
            // touch collections to load them
            user.getDebitors().size();
            user.getEvents().size();
        });
        return opt;
    }



    @Transactional(readOnly = true)
    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    @Transactional
    public User updateUser(User u){
        return userRepo.save(u);
    }
    @Transactional(readOnly = true)
    public boolean existsById(Long id){
        return userRepo.existsById(id);
    }


    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    @Transactional
    public void deleteUser(Long id){
        if (!userRepo.existsById(id)) throw new IllegalArgumentException("User not found");
        userRepo.deleteById(id);
    }
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        if (username == null) return false;
        return userRepo.existsByUsername(username);
    }

    @Transactional
    public User setUsernameForEmail(String email, String username) {
        if (email == null || username == null) throw new IllegalArgumentException("email/username required");

        // basic validation (you already had same checks in controller)
        username = username.trim();
        if (username.length() < 3) throw new IllegalArgumentException("username too short");
        if (!username.matches("^[A-Za-z0-9._]+$")) throw new IllegalArgumentException("invalid username");

        if (userRepo.existsByUsername(username)) {
            throw new IllegalStateException("username_taken");
        }

        User u = userRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("user_not_found"));
        u.setUsername(username);
        return userRepo.save(u);
    }

    // in UserService.java
    public Optional<User> findByEmailOptional(String email) {
        return userRepo.findByEmail(email); // implement repo method to return Optional
    }
    public Optional<User> findByUsernameOptional(String username) {
        return userRepo.findByUsername(username);
    }



}
