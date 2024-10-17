package org.slavbx.repository;

import org.slavbx.model.Habit;
import org.slavbx.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для репозитория привычек. Предоставляет методы для управления привычками пользователей
 */
public interface HabitRepository {
    /**
     * Сохраняет привычку с указанным именем
     * @param name название привычки для сохранения
     * @param habit объект привычки для сохранения
     */
    void save(String name, Habit habit);

    /**
     * Удаляет привычку по указанному названию
     * @param name название привычки для удаления
     */
    void deleteByName(String name);

    /**
     * Находит привычку по заданному названию
     * @param name название привычки для поиска
     * @return объект Optional, содержащий найденную привычку, или пустой объект, если привычка не найдена
     */
    Optional<Habit> findByName(String name);

    /**
     * Находит все привычки, связанные с указанным пользователем и датой
     * @param user объект пользователя для поиска
     * @param date объект даты для поиска
     * @return список привычек, соответствующих указанному пользователю и дате
     */
    List<Habit> findByUser(User user, LocalDate date);
}
