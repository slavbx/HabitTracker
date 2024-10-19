package org.slavbx.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование сущности Habit")
class HabitTest {

    @Test
    @DisplayName("Проверка метода equals()")
    void testEquals() {
        User user = new User("user@mail.com", "psw", "username", User.Level.USER);
        Habit habit1 = new Habit("name", "desc", Habit.Frequency.DAILY, user);
        Habit habit2 = new Habit("name", "desc", Habit.Frequency.DAILY, user);
        assertThat(habit1).isEqualTo(habit2);
        assertThat(habit2).isEqualTo(habit1);
    }
}