package org.slavbx.repository;

import org.slavbx.model.Habit;
import org.slavbx.model.User;

import java.time.LocalDate;
import java.util.*;

public class HabitRepositoryCore implements HabitRepository{
    private final Map<String, Habit> habits;

    public HabitRepositoryCore() {
        this.habits = new HashMap<>();
    }

    @Override
    public void save(String name, Habit habit) {
        habits.put(name, habit);
    }

    @Override
    public void deleteByName(String name) {
        habits.remove(name);
    }

    @Override
    public Optional<Habit> findByName(String name) {
        return Optional.ofNullable(habits.get(name));
    }

    public List<Habit> findByUser(User user, LocalDate date) {
        if (date == null) {
            return habits.values().stream().filter(h -> h.getUser().equals(user)).toList();
        } else {
            return habits.values().stream()
                    .filter(h -> h.getUser().equals(user))
                    .filter(h -> h.getCreateDate().getYear() == date.getYear())
                    .filter(h -> h.getCreateDate().getMonth() == date.getMonth())
                    .filter(h -> h.getCreateDate().getDayOfMonth() == date.getDayOfMonth()).toList();
        }
    }

}
