package org.slavbx.repository;

import org.slavbx.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class UserRepositoryCore implements UserRepository {
    private final HashSet<User> users;

    public UserRepositoryCore() {
        this.users = new HashSet<>();
    }

    @Override
    public void save(User user) {
        users.add(user);
    }

    @Override
    public void deleteByEmail(String email) {
        Optional<User> optUser = findByEmail(email);
        optUser.ifPresent(user -> users.remove(user));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    @Override
    public List<User> findAllUsers() {
        return users.stream().toList();
    }
}
