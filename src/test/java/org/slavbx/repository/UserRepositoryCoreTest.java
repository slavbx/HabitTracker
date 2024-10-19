package org.slavbx.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slavbx.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование UserRepository")
class UserRepositoryCoreTest {
    User user;
    UserRepository userRepository;

    @BeforeEach
    void init() {
        userRepository = new UserRepositoryJdbc();
        user = new User("user@mail.com", "psw", "username", User.Level.USER);
        userRepository.save(user);
    }

    @Test
    @DisplayName("Проверка удаления пользователя по email")
    void deleteByEmail() {
        userRepository.deleteByEmail("user@mail.com");
        assertThat(userRepository.findByEmail("user@mail.com")).isEqualTo(Optional.empty());
    }

    @DisplayName("Проверка поиска пользователя по email")
    @Test
    void findByEmail() {
        assertThat(userRepository.findByEmail("user@mail.com")).isEqualTo(Optional.of(user));
    }

    @Test
    @DisplayName("Проверка возвращения всех существующих пользователей")
    void findAllUsers() {
        User user1 = new User("user1@mail.com", "psw1", "username1", User.Level.USER);
        userRepository.save(user1);
        assertThat(userRepository.findAllUsers()).isEqualTo(List.of(user, user1));
    }
}