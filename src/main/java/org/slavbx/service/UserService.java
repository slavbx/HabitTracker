package org.slavbx.service;


import org.slavbx.model.User;
import org.slavbx.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями.
 * Данный класс предоставляет методы для сохранения, удаления,
 * поиска пользователей и управления авторизацией
 */
public class UserService {
    /**
     * Репозиторий пользователей
     */
    private final UserRepository userRepository;
    /**
     * Авторизованный пользователь
     */
    private User authorizedUser;

    /**
     * Конструктор класса UserService
     * @param userRepository репозиторий пользователей, используемый для операций с данными
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Сохраняет пользователя с указанным email
     * @param email электронная почта для сохранения
     * @param user объект пользователя для сохранения
     */
    public void save(String email, User user) {
        userRepository.save(email, user);
    }

    /**
     * Удаляет пользователя по указанному email
     * @param email электронная почта для удаления пользователя
     */
    public void deleteUserByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    /**
     * Сбрасывает пароль пользователя по его электронному адресу.
     * Устанавливает пароль по умолчанию в "psw".
     * @param email электронный адрес пользователя, для которого нужно сбросить пароль.
     */
    public void resetPassword(String email) {
        Optional<User> optionalUser = this.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword("psw");
            userRepository.save(email, user);
        }
    }

    /**
     * Авторизует пользователя по электронному адресу и паролю.
     * @param email электронный адрес пользователя
     * @param password пароль пользователя
     * @return true, если авторизация успешна, иначе false
     */
    public boolean authorize(String email, String password) {
        Optional<User> optionalUser = this.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.hashCode() == email.hashCode() + password.hashCode()) {
                this.setAuthorizedUser(user);
                return true;
            }
        }
        return false;
    }

    /**
     * Находит пользователя по его электронному адресу.
     * @param email электронный адрес пользователя, которого нужно найти
     * @return объект Optional, содержащий найденного пользователя или пустой объект, если пользователь не найден
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Возвращает список всех зарегистрированных пользователей.
     * @return список пользователей
     */
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    /**
     * Устанавливает авторизованного пользователя для хранения сессии
     * @param user пользователь, которого необходимо авторизовать
     */
    public void setAuthorizedUser(User user) {
        this.authorizedUser = user;
    }

    /**
     * Возвращает авторизованного пользователя текущей сессии
     * @return авторизованный пользователь
     */
    public User getAuthorizedUser() {
        return this.authorizedUser;
    }

    /**
     * Производит завершение сессии авторизованного пользователя
     */
    public void unauthorize() {
        this.setAuthorizedUser(null);
    }

    /**
     * Проверяет, зарегистрирован ли пользователь с указанным электронным адресом.
     * @param email электронный адрес для проверки
     * @return true, если пользователь зарегистрирован, иначе false
     */
    public boolean isUserRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Регистрирует нового пользователя.
     * @param email    электронный адрес нового пользователя
     * @param password пароль нового пользователя
     * @param name     имя нового пользователя
     * @param level    уровень нового пользователя
     */
    public void registerUser(String email, String password, String name, User.Level level) {
        save(email, new User(email, password, name, level));
    }
}
