package org.slavbx.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slavbx.model.User;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DisplayName("Тестирование UserRepository")
class UserRepositoryCoreTest {
    User user;
    UserRepository userRepository;

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("db_test")
            .withUsername("slav")
            .withPassword("slav");

    @BeforeEach
    void init() {
        userRepository = new UserRepositoryJdbc(postgresContainer.getJdbcUrl(), "slav", "slav");
        user = new User("user@mail.com", "psw", "username", User.Level.USER);
        userRepository.save(user);
    }

    @Test
    @DisplayName("Проверка удаления пользователя по email")
    void deleteByEmail() {
        userRepository.deleteByEmail("user@mail.com");
        assertThat(userRepository.findByEmail("user@mail.com")).isEqualTo(Optional.empty());
    }


    @Test
    @DisplayName("Проверка поиска пользователя по email")
    void findByEmail() {
        assertThat(userRepository.findByEmail("user@mail.com")).isEqualTo(Optional.of(user));
    }

    @Test
    @DisplayName("Проверка возвращения всех существующих пользователей")
    void findAllUsers() {
        User slav = new User("slav", "slav", "slav", User.Level.USER);
        User admin = new User("admin", "admin", "admin", User.Level.ADMIN);
        User user1 = new User("user1@mail.com", "psw1", "username1", User.Level.USER);
        User user3 = new User("user", "user", "user", User.Level.USER);
        List<User> users = new ArrayList<>(List.of(admin, slav, user3, user, user1));
        userRepository.save(user1);
        assertThat(userRepository.findAllUsers()).isEqualTo(users);
    }
}