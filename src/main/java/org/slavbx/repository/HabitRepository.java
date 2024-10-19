package org.slavbx.repository;

import org.slavbx.model.CompletionDate;
import org.slavbx.model.Habit;
import org.slavbx.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для репозитория привычек. Предоставляет методы для управления привычками пользователей
 */
public interface HabitRepository {

    void saveCompData(Habit habit);

    /**
     * Сохраняет привычку с указанным именем
     * @param name название привычки для сохранения
     * @param habit объект привычки для сохранения
     */
    void save(Habit habit);

    /**
     * Удаляет привычку по указанному названию
     * @param name название привычки для удаления
     */
    void deleteByName(String name);

    void deleteCompletionsByHabitId(Long id);

    /**
     * Находит привычку по заданному названию
     * @param name название привычки для поиска
     * @return объект Optional, содержащий найденную привычку, или пустой объект, если привычка не найдена
     */
    Optional<Habit> findByName(String name, User user);

    Optional<Long> findIdByName(String name);

    /**
     * Находит все привычки, связанные с указанным пользователем и датой
     * @param user объект пользователя для поиска
     * @param date объект даты для поиска
     * @return список привычек, соответствующих указанному пользователю и дате
     */
    List<Habit> findByUser(User user);

    List<CompletionDate> findCompDatesByHabit(Habit habit);
}
