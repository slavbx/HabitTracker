package org.slavbx.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slavbx.model.Habit;
import org.slavbx.model.User;
import org.slavbx.repository.HabitRepository;
import org.slavbx.repository.HabitRepositoryJdbc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DisplayName("Тестирование HabitService")
class HabitServiceTest {
    HabitService habitService;
    User user;
    Habit habit;

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("db_test")
            .withUsername("slav")
            .withPassword("slav");

    @BeforeEach
    void init() {
        HabitRepository habitRepository = new HabitRepositoryJdbc(postgresContainer.getJdbcUrl(), "slav", "slav");
        habitService = new HabitService(habitRepository);
        user = new User(1L,"user@mail.com", "psw", "username", User.Level.USER);
        habit = new Habit(1L, "name", "desc", Habit.Frequency.DAILY, user);
    }

    @Test
    @DisplayName("Проверка вызова метода сохранения привычки")
    void save() {
        habitService.save(habit);
        assertThat(habitService.findHabitByName("name", user)).isEqualTo(Optional.of(habit));
    }

    @Test
    @DisplayName("Проверка поиска привычки по имени")
    void findHabitByName() {
        assertThat(habitService.findHabitByName("name", user)).isEqualTo(Optional.of(habit));
    }

    @Test
    @DisplayName("Проверка удаления привычки по имени")
    void deleteHabitByName() {
        habitService.deleteHabitByName("name");
        assertThat(habitService.findHabitByName("name", user)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Проверка поиска привычек по пользователю и дате")
    void findHabitByUser() {
        List<Habit> habits = new ArrayList<>();
        habits.add(habit);
          assertThat(habitService.findHabitByUser(user, LocalDate.now())).isEqualTo(habits);
    }

    @Test
    @DisplayName("Проверка отметки выполнения привычки")
    void markAsCompleted() {
        habitService.markAsCompleted(habit);
        assertThat(habit.getCompletionDates()).contains(habit.getCompletionDates().stream().findFirst().get());
    }

    @Test
    @DisplayName("Проверка возвращения отмеченных привычек за определенный период")
    void getCompletionsInPeriod() {
        habitService.markAsCompleted(habit);
        assertThat(habitService.getCompletionsInPeriod(habit, LocalDate.now().minusDays(2), LocalDate.now())).isEqualTo(1L);

        habit = new Habit(2L, "name", "desc", Habit.Frequency.WEEKLY, user);
        habitService.markAsCompleted(habit);
        assertThat(habitService.getCompletionsInPeriod(habit, LocalDate.now(), LocalDate.now().plusDays(6))).isEqualTo(1L);
    }

    @Test
    @DisplayName("Проверка возвращения величиины успешного выполнения привычки")
    void getSuccessRate() {
        habitService.markAsCompleted(habit);
        assertThat(habitService.getSuccessRate(habit, LocalDate.now(), LocalDate.now())).isEqualTo(100.0d);
    }

    @Test
    @DisplayName("Проверка возвращения серии выполнения привычки")
    void getStreak() {
        habitService.markAsCompleted(habit);
        assertThat(habitService.getStreak(habit)).isEqualTo(1);
    }
}