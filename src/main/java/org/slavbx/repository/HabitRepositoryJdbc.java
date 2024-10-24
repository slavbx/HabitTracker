package org.slavbx.repository;

import org.slavbx.DatabaseProvider;
import org.slavbx.model.CompletionDate;
import org.slavbx.model.Habit;
import org.slavbx.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class HabitRepositoryJdbc implements HabitRepository{

    public HabitRepositoryJdbc() {
    }

    public HabitRepositoryJdbc(String jdbcUrl, String user, String password) {
    }

    @Override
    public void saveCompData(Habit habit) {
        for (CompletionDate compDate: habit.getCompletionDates()) {
            try (Connection connection = DatabaseProvider.getConnection()) {
                PreparedStatement prepStatement = connection.prepareStatement(SqlQueries.INSERT_COMPLETION_DATE);
                //prepStatement.setLong(1, compDate.getId());
                prepStatement.setString(1, compDate.getDate().toString());
                prepStatement.setLong(2, compDate.getHabit().getId());
                prepStatement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Ошибка. Не удалось сохранить дату " + e.getMessage());
            }
        }
    }

    @Override
    public void save(Habit habit) {
        saveCompData(habit);
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement prepStatement = connection.prepareStatement(SqlQueries.INSERT_HABIT);
            //prepStatement.setLong(1, habit.getId());
            prepStatement.setString(1, habit.getName());
            prepStatement.setString(2, habit.getDesc());
            prepStatement.setString(3, habit.getFreq().name());
            prepStatement.setString(4, habit.getCreateDate().toString());
            prepStatement.setLong(5, habit.getUser().getId());
            prepStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось сохранить привычку " + e.getMessage());
        }
    }

    @Override
    public void deleteByName(String name) {
        Optional<Long> optId = findIdByName(name);
        optId.ifPresent(this::deleteCompletionsByHabitId);
        try (Connection connection = DatabaseProvider.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(SqlQueries.DELETE_HABIT_BY_ID)) {
            prepStatement.setString(1, name);
            prepStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось удалить привычку: " + e.getMessage());
        }
    }

    @Override
    public void deleteCompletionsByHabitId(Long id) {
        try (Connection connection = DatabaseProvider.getConnection();
            PreparedStatement prepStatement = connection.prepareStatement(SqlQueries.DELETE_COMPLETIONS_BY_HABIT_ID)) {
            prepStatement.setLong(1, id);
            prepStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось удалить даты: " + e.getMessage());
        }
    }

    @Override
    public Optional<Habit> findByName(String name, User user) {
        Optional<Long> optId = findIdByName(name);
        if (optId.isPresent()) {
            try (Connection connection = DatabaseProvider.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(SqlQueries.SELECT_HABIT_BY_ID);
                preparedStatement.setLong(1, optId.get());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Habit habit = mapResultSetToHabit(resultSet, user);
                    return Optional.of(habit);
                }

            } catch (SQLException e) {
                System.out.println("Ошибка. Не удалось вернуть пользователя"  + e.getMessage());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> findIdByName(String name) {
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SqlQueries.SELECT_HABIT_ID_BY_NAME);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось вернуть id привычки"  + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Habit> findByUser(User user) {
        List<Habit> habits = new ArrayList<>();
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SqlQueries.SELECT_HABIT_BY_USER_ID);
            preparedStatement.setLong(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Habit habit = mapResultSetToHabit(resultSet, user);
                habit.setCompletionDates(findCompDatesByHabit(habit));
                habits.add(habit);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось вернуть список привычек"  + e.getMessage());
        }
        return habits;
    }

    @Override
    public List<CompletionDate> findCompDatesByHabit(Habit habit) {
        List<CompletionDate> compDates = new ArrayList<>();
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SqlQueries.SELECT_COMPLETIONS_BY_HABIT_ID);
            preparedStatement.setLong(1, habit.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CompletionDate compDate = CompletionDate.builder()
                        .id(resultSet.getLong("id"))
                        .date(LocalDate.parse(resultSet.getString("completion_date")))
                        .habit(habit)
                        .build();
                compDates.add(compDate);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось вернуть список отмеченных дат"  + e.getMessage());
        }
        return compDates;
    }

    private Habit mapResultSetToHabit(ResultSet resultSet, User user) throws SQLException {
        return Habit.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .desc(resultSet.getString("description"))
                .freq(Habit.Frequency.valueOf(resultSet.getString("freq")))
                .createDate(LocalDate.parse(resultSet.getString("create_date")))
                .user(user)
                .completionDates(new ArrayList<>())
                .build();
    }
}
