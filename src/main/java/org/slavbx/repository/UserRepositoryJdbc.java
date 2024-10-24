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
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement prepStatement = connection.prepareStatement(SqlQueries.INSERT_USER);
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
            try (Connection connection = DatabaseProvider.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(SqlQueries.DELETE_USER_BY_ID);
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
            try (Connection connection = DatabaseProvider.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(SqlQueries.SELECT_USER_BY_ID);
                preparedStatement.setLong(1, id.get());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToUser(resultSet));
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
        try (Connection connection = DatabaseProvider.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SqlQueries.SELECT_USERS);
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось найти пользователей"  + e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SqlQueries.SELECT_USER_BY_ID);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось вернуть пользователя"  + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> findIdByEmail(String email) {
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SqlQueries.SELECT_USER_BY_EMAIL);
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

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .name(resultSet.getString("name"))
                .level(User.Level.valueOf(resultSet.getString("level")))
                .build();
    }
}
