package org.slavbx.repository;

import org.slavbx.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для репозитория пользователей. Предоставляет методы для управления пользователями
 */
public interface UserRepository {
    /**
     * Сохраняет пользователя с указанным email
     * @param user объект пользователя для сохранения
     */
    void save(User user);

    /**
     * Удаляет пользователя по указанному email
     * @param email электронная почта для удаления пользователя
     */
    void deleteByEmail(String email);

    /**
     * Находит пользователя по указанному email
     * @param email электронная почта для поиска пользователя
     * @return объект Optional, содержащий найденного пользователя, или пустой объект, если пользователь не найден
     */
    Optional<User> findByEmail(String email);

    /**
     * Находит всех существующих пользователей
     * @return список пользователей
     */
    List<User> findAllUsers();

    Optional<User> findById(Long id);

    void saveById(Long id, User user);
}
