package org.slavbx.service;

import org.slavbx.model.Habit;
import org.slavbx.model.User;
import org.slavbx.repository.HabitRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HabitService {
    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public void save(Habit habit) {
        habitRepository.save(habit);
    }

    public Optional<Habit> findHabitByName(String name) {
        return habitRepository.findByName(name);
    }

    public void deleteHabitByName(String name) {
        habitRepository.deleteByName(name);
    }

    public List<Habit> findHabitByUser(User user, LocalDate date) {
        return habitRepository.findByUser(user, date);
    }

    public void markAsCompleted(Habit habit) {
        habit.getCompletionDates().add(LocalDate.now());
    }

    public long getCompletionsInPeriod(Habit habit, LocalDate startDate, LocalDate endDate) {
        return habit.getCompletionDates().stream()
                .filter(date -> !date.isBefore(startDate) && !date.isAfter(endDate))
                .count();
    }

    public long getStreak(Habit habit) {
        long streak = 0;
        List<LocalDate> dates = habit.getCompletionDates();

        for (int i = 0; i < dates.size(); i++) {
            LocalDate date = dates.get(dates.size() - 1 - i);
            if (date.isEqual(LocalDate.now().minusDays(i))) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    public double getSuccessRate(Habit habit, LocalDate start, LocalDate end) {
        long totalDays = start.datesUntil(end.plusDays(1)).count();
        if (totalDays <= 0) {
            return 0;
        }
        long completions = getCompletionsInPeriod(habit, start, end);
        return (double) completions / totalDays * 100;
    }

    public void showHabitStats(Habit habit, LocalDate start, LocalDate end) {
        long completions = getCompletionsInPeriod(habit, start, end);
        double successRate = getSuccessRate(habit, start, end);
        long streak = getStreak(habit);
        System.out.println("\nСтатистика: ");
        System.out.println("Выполнений за период: " + completions);
        System.out.println("Процент успешного выполнения: " + successRate + "%");
        System.out.println("Текущая серия выполнения: " + streak);
    }
}
