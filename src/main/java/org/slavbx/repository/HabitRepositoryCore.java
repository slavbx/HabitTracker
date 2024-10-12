package org.slavbx.repository;

import org.slavbx.model.Habit;
import org.slavbx.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class HabitRepositoryCore implements HabitRepository{
    private final HashSet<Habit> habits;

    public HabitRepositoryCore() {
        this.habits = new HashSet<>();
    }

    @Override
    public void save(Habit habit) {
        habits.add(habit);
    }

    @Override
    public void deleteByName(String name) {
        Optional<Habit> optHabit = findByName(name);
        optHabit.ifPresent(habit -> habits.remove(habit));
    }

    @Override
    public Optional<Habit> findByName(String name) {
        return habits.stream().filter(h -> h.getName().equals(name)).findFirst();
    }

    public List<Habit> findByUser(User user, LocalDate date) {
        if (date == null) {
            return habits.stream().filter(h -> h.getUser().equals(user)).toList();
        } else {
            return habits.stream()
                    .filter(h -> h.getUser().equals(user))
                    .filter(h -> h.getCreateDate().getYear() == date.getYear())
                    .filter(h -> h.getCreateDate().getMonth() == date.getMonth())
                    .filter(h -> h.getCreateDate().getDayOfMonth() == date.getDayOfMonth()).toList();
        }
    }

}
