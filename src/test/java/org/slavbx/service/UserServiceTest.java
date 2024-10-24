package org.slavbx.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slavbx.model.User;
import org.slavbx.repository.UserRepository;
import org.slavbx.repository.UserRepositoryJdbc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DisplayName("Тестирование UserService")
class UserServiceTest {
    UserService userService;

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("db_test")
            .withUsername("slav")
            .withPassword("slav");

    @BeforeEach
    void init() {
        UserRepository userRepository = new UserRepositoryJdbc(postgresContainer.getJdbcUrl(), "slav", "slav");
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Проверка вызова метода сохранения пользователя")
    void save() {
        User user = new User("user@mail.com", "psw", "username", User.Level.USER);
        userService.save(user);
        assertThat(userService.findUserByEmail("user@mail.com")).isEqualTo(Optional.of(user));
    }

    @Test
    @DisplayName("Проверка удаления пользователя по имени")
    void deleteUserByEmail() {
        userService.deleteUserByEmail("user@mail.com");
        assertThat(userService.findUserByEmail("user@mail.com")).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Проверка входа пользователя в личный кабинет")
    void authorize() {
        User user = new User("user@mail.com", "psw", "username", User.Level.USER);
        userService.authorize("user@mail.com", "psw");
        assertThat(userService.getAuthorizedUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Проверка поиска пользователя по email")
    void findUserByEmail() {
        User user = new User("user@mail.com", "psw", "username", User.Level.USER);
        userService.save(user);
        assertThat(userService.findUserByEmail("user@mail.com")).isEqualTo(Optional.of(user));
    }

    @Test
    @DisplayName("Проверка выхода пользователя из личного кабинета")
    void unauthorize() {
        User user = new User("user@mail.com", "psw", "username", User.Level.USER);
        userService.authorize("user@mail.com", "psw");
        userService.unauthorize();
        assertThat(userService.getAuthorizedUser()).isNull();
    }
}