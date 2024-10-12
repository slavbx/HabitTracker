package org.slavbx.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slavbx.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryCoreTest {
    User user;
    UserRepository userRepository;

    @BeforeEach
    void init() {
        userRepository = new UserRepositoryCore();
        user = new User("user@mail.com", "psw", "username", User.Level.USER);
        userRepository.save(user);
    }

    @Test
    void deleteByEmail() {
        userRepository.deleteByEmail("user@mail.com");
        assertThat(userRepository.findByEmail("user@mail.com")).isEqualTo(Optional.empty());
    }

    @Test
    void findByEmail() {
        assertThat(userRepository.findByEmail("user@mail.com")).isEqualTo(Optional.of(user));
    }

    @Test
    void findAllUsers() {
        User user1 = new User("user1@mail.com", "psw1", "username1", User.Level.USER);
        userRepository.save(user1);
        assertThat(userRepository.findAllUsers()).isEqualTo(List.of(user1, user));
    }
}