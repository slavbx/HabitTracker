package org.slavbx.repository;

import org.slavbx.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);
    void deleteByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findAllUsers();
}
