package org.slavbx.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slavbx.model.Habit;
import org.slavbx.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование HabitRepository")
class HabitRepositoryCoreTest {
    Habit habit;
    User user;
    HabitRepository habitRepository;

    @BeforeEach
    void init() {
        habitRepository = new HabitRepositoryJdbc();
        user = new User("user@mail.com", "psw", "username", User.Level.USER);
        habit = new Habit("name", "desc", Habit.Frequency.DAILY, user);
        habitRepository.save(habit);
    }

    @Test
    @DisplayName("Проверка удаления привычки по её имени")
    void deleteByName() {
        habitRepository.deleteByName("name");
        assertThat(habitRepository.findByName("name", new User())).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Проверка возвращения привычки по её имени")
    void findByName() {
        assertThat(habitRepository.findByName("name", new User())).isEqualTo(Optional.of(habit));
    }

    @Test
    @DisplayName("Проверка возвращаемого списка привычек по имени пользователя")
    void findByUser() {
        assertThat(habitRepository.findByUser(user)).isEqualTo(List.of(habit));
    }
}