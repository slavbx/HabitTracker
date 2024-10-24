package org.slavbx.repository;

import org.slavbx.DatabaseProvider;
import org.slavbx.model.User;

import java.sql.*;
import java.util.*;

public class UserRepositoryJdbc implements UserRepository {

    public UserRepositoryJdbc() {
    }

    public UserRepositoryJdbc(String jdbcUrl, String user, String password) {
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (password, name, level, email) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (email) DO UPDATE SET password = excluded.password, " +
                "name = excluded.name, level = EXCLUDED.level, email = excluded.email;";

        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement prepStatement = connection.prepareStatement(sql);
            prepStatement.setString(1, user.getPassword());
            prepStatement.setString(2, user.getName());
            prepStatement.setString(3, user.getLevel().name());
            prepStatement.setString(4, user.getEmail());
            prepStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось сохранить пользователя " + e.getMessage());
        }
    }

    @Override
    public void deleteByEmail(String email) {
        Optional<Long> id = findIdByEmail(email);
        if (id.isPresent()) {
            String sql = "DELETE FROM users WHERE id = ?;";
            try (Connection connection = DatabaseProvider.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, id.get());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Ошибка. Не удалось удалить пользователя"  + e.getMessage());
            }
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<Long> id = findIdByEmail(email);
        if (id.isPresent()) {
            String sql = "SELECT * FROM users WHERE id = ?;";
            try (Connection connection = DatabaseProvider.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, id.get());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(new User(
                            resultSet.getLong("id"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getString("name"),
                            User.Level.valueOf(resultSet.getString("level"))));
                }
            } catch (SQLException e) {
                System.out.println("Ошибка. Не удалось вернуть пользователя"  + e.getMessage());
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users;";
        try (Connection connection = DatabaseProvider.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                User user = new User(resultSet.getLong("id"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("name"),
                        User.Level.valueOf(resultSet.getString("level")));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось найти пользователей"  + e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?;";
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new User(resultSet.getLong("id"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("name"),
                        User.Level.valueOf(resultSet.getString("level"))));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось вернуть пользователя"  + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> findIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?;";
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось вернуть id пользователя"  + e.getMessage());
        }
        return Optional.empty();
    }
}