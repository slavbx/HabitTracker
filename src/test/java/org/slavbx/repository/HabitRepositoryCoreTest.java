package org.slavbx.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slavbx.model.Habit;
import org.slavbx.model.User;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DisplayName("Тестирование HabitRepository")
class HabitRepositoryCoreTest {
    Habit habit;
    User user;
    HabitRepository habitRepository;

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("db_test")
            .withUsername("slav")
            .withPassword("slav");

    @BeforeEach
    void init() {
        habitRepository = new HabitRepositoryJdbc(postgresContainer.getJdbcUrl(), "slav", "slav");
        user = User.builder().id(1L).email("user@mail.com").password("psw").name("username").level(User.Level.USER).build();
        habit = Habit.builder().name("name").desc("desc").freq(Habit.Frequency.DAILY).user(user).build();
        habitRepository.save(habit);
    }

    @Test
    @DisplayName("Проверка удаления привычки по её имени")
    void deleteByName() {
        habitRepository.deleteByName("name");
        assertThat(habitRepository.findByName("name", user)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Проверка возвращения привычки по её имени")
    void findByName() {
        assertThat(habitRepository.findByName("name", user)).isEqualTo(Optional.of(habit));
    }

    @Test
    @DisplayName("Проверка возвращаемого списка привычек по имени пользователя")
    void findByUser() {
        assertThat(habitRepository.findByUser(user)).isEqualTo(List.of(habit));
    }
}