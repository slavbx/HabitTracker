package org.slavbx.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slavbx.model.User;
import org.slavbx.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование UserService")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("Проверка вызова метода сохранения пользователя")
    void save() {
        User user = new User("user@mail.com", "psw", "username", User.Level.USER);
        userService.save(user.getEmail(), user);
        Mockito.verify(userRepository).save(user.getEmail(), user);
    }

    @Test
    @DisplayName("Проверка удаления пользователя по имени")
    void deleteUserByEmail() {
        userService.deleteUserByEmail("user@mail.com");
        Mockito.verify(userRepository).deleteByEmail("user@mail.com");
    }

    @Test
    @DisplayName("Проверка входа пользователя в личный кабинет")
    void authorize() {
        User user = new User("user@mail.com", "psw", "username", User.Level.USER);
        Mockito.when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        userService.authorize("user@mail.com", "psw");
        assertThat(userService.getAuthorizedUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Проверка поиска пользователя по email")
    void findUserByEmail() {
        User user = new User("user@mail.com", "psw", "username", User.Level.USER);
        Mockito.when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        assertThat(userService.findUserByEmail("user@mail.com")).isEqualTo(Optional.of(user));
    }

    @Test
    @DisplayName("Проверка выхода пользователя из личного кабинета")
    void unauthorize() {
        User user = new User("user@mail.com", "psw", "username", User.Level.USER);
        Mockito.when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        userService.authorize("user@mail.com", "psw");
        userService.unauthorize();
        assertThat(userService.getAuthorizedUser()).isNull();
    }

    @Test
    @DisplayName("Проверка возвращения всех существующих пользователей")
    void findAllUsers() {
        User user1 = new User("user1@mail.com", "psw1", "username1", User.Level.USER);
        User user2 = new User("user2@mail.com", "psw2", "username2", User.Level.USER);
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        Mockito.when(userRepository.findAllUsers()).thenReturn(users);
        assertThat(userService.findAllUsers()).isEqualTo(users);
    }
}