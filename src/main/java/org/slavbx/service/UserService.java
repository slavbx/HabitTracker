package org.slavbx.service;


import org.slavbx.model.User;
import org.slavbx.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    private User authorizedUser;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void deleteUserByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    public void resetPassword(String email) {
        Optional<User> optionalUser = this.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword("psw");
            userRepository.save(user);
        }
    }

    public boolean authorize(String email, String password) {
        Optional<User> optionalUser = this.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.hashCode() == email.hashCode() + password.hashCode()) {
                this.setAuthorizedUser(user);
                return true;
            }
        }
        return false;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    public void setAuthorizedUser(User user) {
        this.authorizedUser = user;
    }

    public User getAuthorizedUser() {
        return this.authorizedUser;
    }

    public void unauthorize() {
        this.setAuthorizedUser(null);
    }
}
