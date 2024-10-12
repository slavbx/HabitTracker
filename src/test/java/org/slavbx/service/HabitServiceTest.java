package org.slavbx.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slavbx.model.Habit;
import org.slavbx.model.User;
import org.slavbx.repository.HabitRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {
    @Mock
    HabitRepository habitRepository;
    @InjectMocks
    HabitService habitService;
    User user;
    Habit habit;

    @BeforeEach
    void init() {
        user = new User("user@mail.com", "psw", "username", User.Level.USER);
        habit = new Habit("name", "desc", Habit.Frequency.DAILY, user);
    }

    @Test
    void save() {
        habitService.save(habit);
        Mockito.verify(habitRepository).save(habit); //Проверяем, что был вызван нужный метод с нужным аргументом
    }

    @Test
    void findHabitByName() {
        Mockito.when(habitRepository.findByName("name")).thenReturn(Optional.of(habit));
        assertThat(habitService.findHabitByName("name")).isEqualTo(Optional.of(habit));
    }

    @Test
    void deleteHabitByName() {
        habitService.deleteHabitByName("name");
        Mockito.verify(habitRepository).deleteByName("name");
    }

    @Test
    void findHabitByUser() {
        List<Habit> habits = new ArrayList<>();
        habits.add(habit);
        Mockito.when(habitRepository.findByUser(user, LocalDate.now())).thenReturn(habits);
        assertThat(habitService.findHabitByUser(user, LocalDate.now())).isEqualTo(habits);
    }

    @Test
    void markAsCompleted() {
        habitService.markAsCompleted(habit);
        assertThat(habit.getCompletionDates()).contains(LocalDate.now());
    }

    @Test
    void getCompletionsInPeriod() {
        habitService.markAsCompleted(habit);
        assertThat(habitService.getCompletionsInPeriod(habit, LocalDate.now().minusDays(2), LocalDate.now())).isEqualTo(1L);

        habit = new Habit("name", "desc", Habit.Frequency.WEEKLY, user);
        habitService.markAsCompleted(habit);
        assertThat(habitService.getCompletionsInPeriod(habit, LocalDate.now(), LocalDate.now().plusDays(6))).isEqualTo(1L);
    }

    @Test
    void getSuccessRate() {
        habitService.markAsCompleted(habit);
        assertThat(habitService.getSuccessRate(habit, LocalDate.now(), LocalDate.now())).isEqualTo(100.0d);
    }

    @Test
    void getStreak() {
        habitService.markAsCompleted(habit);
        assertThat(habitService.getStreak(habit)).isEqualTo(1);
    }
}