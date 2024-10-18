package org.slavbx.repository;

import org.slavbx.DatabaseProvider;
import org.slavbx.model.User;

import java.sql.*;
import java.util.*;

public class UserRepositoryJdbc implements UserRepository {

    public UserRepositoryJdbc() {
    }

    @Override
    public void save(User user) {
        String sql;
        try (Connection connection = DatabaseProvider.getConnection()) {
            if (findByEmail(user.getEmail()).isEmpty()) {
                sql = "INSERT INTO users (password, name, level, email) VALUES (?, ?, ?, ?);";
                PreparedStatement prepStatement = connection.prepareStatement(sql);
                prepStatement.setString(1, user.getEmail());
                prepStatement.setString(2, user.getPassword());
                prepStatement.setString(3, user.getName());
                prepStatement.setString(4, user.getLevel().name());
                prepStatement.executeUpdate();
            } else {
                sql = "UPDATE users SET password = ?, name = ?, level = ? WHERE email = ?";
                PreparedStatement prepStatement = connection.prepareStatement(sql);
                prepStatement.setString(2, user.getPassword());
                prepStatement.setString(3, user.getName());
                prepStatement.setString(4, user.getLevel().name());
                prepStatement.setString(1, user.getEmail());
                prepStatement.executeUpdate();
            }
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
                int rowsDeleted = preparedStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Пользователь удален.");
                } else {
                    System.out.println("Пользователь не найден.");
                }
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
    public void saveById(Long id, User user) {
        String sql;
        try (Connection connection = DatabaseProvider.getConnection()) {
            if (findById(id).isEmpty()) {
                sql = "INSERT INTO users (password, name, level, email) VALUES (?, ?, ?, ?);";
                PreparedStatement prepStatement = connection.prepareStatement(sql);
                prepStatement.setString(1, user.getEmail());
                prepStatement.setString(2, user.getPassword());
                prepStatement.setString(3, user.getName());
                prepStatement.setString(4, user.getLevel().name());
                prepStatement.executeUpdate();
            } else {
                sql = "UPDATE users SET email = ?, password = ?, name = ?, level = ? WHERE id = ?;";
                PreparedStatement prepStatement = connection.prepareStatement(sql);
                prepStatement.setString(1, user.getEmail());
                prepStatement.setString(2, user.getPassword());
                prepStatement.setString(3, user.getName());
                prepStatement.setString(4, user.getLevel().name());
                prepStatement.setLong(5, user.getId());
                prepStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось сохранить пользователя " + e.getMessage());
        }
    }

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
