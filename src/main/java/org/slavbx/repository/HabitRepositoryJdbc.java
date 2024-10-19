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

    @Override
    public void saveCompData(Habit habit) {
        for (CompletionDate compDate: habit.getCompletionDates()) {
            String sql = "INSERT INTO completion_dates (completion_date, habit_id) VALUES (?, ?) " +
                    "ON CONFLICT (completion_date, habit_id) DO UPDATE SET " +
                    "completion_date = excluded.completion_date;";
            try (Connection connection = DatabaseProvider.getConnection()) {
                PreparedStatement prepStatement = connection.prepareStatement(sql);
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
        String sql = "INSERT INTO habits (name, description, freq, create_date, user_id) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (name) DO UPDATE SET "+
                "description = excluded.description, freq = excluded.freq, create_date = excluded.create_date, user_id = excluded.user_id;";
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement prepStatement = connection.prepareStatement(sql);
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

        String sql = "DELETE FROM habits WHERE name = ?;";
        try (Connection connection = DatabaseProvider.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, name);
            prepStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось удалить привычку: " + e.getMessage());
        }
    }

    @Override
    public void deleteCompletionsByHabitId(Long id) {
        String sql = "DELETE FROM completion_dates WHERE habit_id = ?;";
        try (Connection connection = DatabaseProvider.getConnection();
            PreparedStatement prepStatement = connection.prepareStatement(sql)) {
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
            String sql = "SELECT * FROM habits WHERE id = ?;";
            try (Connection connection = DatabaseProvider.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, optId.get());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Habit habit = new Habit(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            Habit.Frequency.valueOf(resultSet.getString("freq")),
                            LocalDate.parse(resultSet.getString("create_date")),
                            user,
                            new ArrayList<>());
                    habit.setCompletionDates(findCompDatesByHabit(habit));
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
        String sql = "SELECT id FROM habits WHERE name = ?;";
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
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
        String sql = "SELECT id, name, description, freq, create_date FROM habits WHERE user_id = ?;";
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Habit habit = new Habit(resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        Habit.Frequency.valueOf(resultSet.getString("freq")),
                        LocalDate.parse(resultSet.getString("create_date")), //Возможно дата не спарсится корректно
                        user,
                        new ArrayList<>()); //Добавить completion_Dates
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
        String sql = "SELECT id, completion_date, habit_id FROM completion_dates WHERE habit_id = ?;";
        try (Connection connection = DatabaseProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, habit.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CompletionDate compDate = new CompletionDate(
                        resultSet.getLong("id"),
                        LocalDate.parse(resultSet.getString("completion_date")),
                        habit);
                compDates.add(compDate);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка. Не удалось вернуть список отмеченных дат"  + e.getMessage());
        }
        return compDates;
    }
}
