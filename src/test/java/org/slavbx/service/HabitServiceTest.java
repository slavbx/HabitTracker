package org.slavbx.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slavbx.model.CompletionDate;
import org.slavbx.model.Habit;
import org.slavbx.model.User;
import org.slavbx.repository.HabitRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование HabitService")
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
    @DisplayName("Проверка вызова метода сохранения привычки")
    void save() {
        habitService.save(habit);
        Mockito.verify(habitRepository).save(habit);
    }

    @Test
    @DisplayName("Проверка поиска привычки по имени")
    void findHabitByName() {
        Mockito.when(habitRepository.findByName("name",new User())).thenReturn(Optional.of(habit));
        assertThat(habitService.findHabitByName("name", new User())).isEqualTo(Optional.of(habit));
    }

    @Test
    @DisplayName("Проверка удаления привычки по имени")
    void deleteHabitByName() {
        habitService.deleteHabitByName("name");
        Mockito.verify(habitRepository).deleteByName("name");
    }

    @Test
    @DisplayName("Проверка поиска привычек по пользователю и дате")
    void findHabitByUser() {
        List<Habit> habits = new ArrayList<>();
        habits.add(habit);
        Mockito.when(habitRepository.findByUser(user)).thenReturn(habits);
        assertThat(habitService.findHabitByUser(user, LocalDate.now())).isEqualTo(habits);
    }

//    @Test
//    @DisplayName("Проверка отметки выполнения привычки")
//    void markAsCompleted() {
//        habitService.markAsCompleted(habit);
//        assertThat(habit.getCompletionDates()).contains(habit.getCreateDate().LocalDate.now());
//    }

    @Test
    @DisplayName("Проверка возвращения отмеченных привычек за определенный период")
    void getCompletionsInPeriod() {
        habitService.markAsCompleted(habit);
        assertThat(habitService.getCompletionsInPeriod(habit, LocalDate.now().minusDays(2), LocalDate.now())).isEqualTo(1L);

        habit = new Habit("name", "desc", Habit.Frequency.WEEKLY, user);
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