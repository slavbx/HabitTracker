package org.slavbx.repository;

import org.slavbx.model.Habit;
import org.slavbx.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitRepository {
    void save(Habit habit);
    void deleteByName(String name);
    Optional<Habit> findByName(String name);
    List<Habit> findByUser(User user, LocalDate date);
}
