package org.slavbx.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование сущности Habit")
class HabitTest {

    @Test
    @DisplayName("Проверка метода equals()")
    void testEquals() {
        User user = User.builder().email("user@mail.com").password("psw").name("username").level(User.Level.USER).build();
        Habit habit1 = Habit.builder().name("name").desc("desc").freq(Habit.Frequency.DAILY).user(user).build();
        Habit habit2 = Habit.builder().name("name").desc("desc").freq(Habit.Frequency.DAILY).user(user).build();
        assertThat(habit1).isEqualTo(habit2);
        assertThat(habit2).isEqualTo(habit1);
    }
}