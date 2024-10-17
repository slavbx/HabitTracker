package org.slavbx.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование сущности User")
class UserTest {

    @Test
    @DisplayName("Проверка метода equals()")
    void testEquals() {
        User user1 = new User("user@mail.com", "psw", "username", User.Level.USER);
        User user2 = new User("user@mail.com", "psw", "username", User.Level.USER);
        assertThat(user1).isEqualTo(user2);
        assertThat(user2).isEqualTo(user1);
    }
}