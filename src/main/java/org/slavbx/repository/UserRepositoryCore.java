package org.slavbx.repository;

import org.slavbx.model.User;

import java.util.*;

public class UserRepositoryCore implements UserRepository {
    private final Map<String, User> users;

    public UserRepositoryCore() {
        this.users = new HashMap<>();
    }

    @Override
    public void save(String email, User user) {
        users.put(email, user);
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
}
