package org.slavbx.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slavbx.model.Habit;
import org.slavbx.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HabitRepositoryCoreTest {
    Habit habit;
    User user;
    HabitRepository habitRepository;

    @BeforeEach
    void init() {
        habitRepository = new HabitRepositoryCore();
        user = new User("user@mail.com", "psw", "username", User.Level.USER);
        habit = new Habit("name", "desc", Habit.Frequency.DAILY, user);
        habitRepository.save(habit);
    }

    @Test
    void deleteByName() {
        habitRepository.deleteByName("name");
        assertThat(habitRepository.findByName("name")).isEqualTo(Optional.empty());
    }

    @Test
    void findByName() {
        assertThat(habitRepository.findByName("name")).isEqualTo(Optional.of(habit));
    }

    @Test
    void findByUser() {
        assertThat(habitRepository.findByUser(user, LocalDate.now())).isEqualTo(List.of(habit));
    }
}