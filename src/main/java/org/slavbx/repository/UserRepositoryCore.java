package org.slavbx.repository;

import org.slavbx.DatabaseProvider;
import org.slavbx.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class UserRepositoryCore implements UserRepository {
    private final Map<String, User> users;

    public UserRepositoryCore() {
        this.users = new HashMap<>();
    }


    public void save(String email, User user) {
        users.put(email, user);
    }

    @Override
    public void save(User user) {
    }

    @Override
    public void deleteByEmail(String email) {
        users.remove(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void saveById(Long id, User user) {

    }
}
