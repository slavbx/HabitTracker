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

    @Override
    public void save(String email, User user) {
        users.put(email, user);
        String sql = "INSERT INTO habittracker.users (email, password, name, level) VALUES (?, ?, ?, ?);";
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setString(4, user.getLevel().name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось сохранить пользователя"  + e.getMessage());
        }

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
