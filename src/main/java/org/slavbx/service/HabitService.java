package org.slavbx.service;

import org.slavbx.model.Habit;
import org.slavbx.model.User;
import org.slavbx.repository.HabitRepository;

import java.time.LocalDate;
import java.util.ArrayList;
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
        if (habit.getFreq() == Habit.Frequency.DAILY) {
            habit.getCompletionDates().add(LocalDate.now());
        } else { //WEEKLY
            List <LocalDate> dates = new ArrayList<>();
            for (int i = 0; i < 7; i++) { //Заполняем сразу на неделю вперёд
                dates.add(LocalDate.now().plusDays(i));
            }
            habit.getCompletionDates().addAll(dates);
        }

    }

    public long getCompletionsInPeriod(Habit habit, LocalDate start, LocalDate end) {
        if (habit.getFreq() == Habit.Frequency.DAILY) {
            return getCompletionDaysInPeriod(habit, start, end);
        } else { //WEEKLY
            long completionsDays = getCompletionDaysInPeriod(habit, start, end);
            long completions = completionsDays / 7;
            if (completionsDays % 7 > 0) completions++; //Часть отмеченной недели, вошедшая в период
            return completions;
        }
    }

    public long getCompletionDaysInPeriod(Habit habit, LocalDate start, LocalDate end) {
        return habit.getCompletionDates().stream()
                    .filter(date -> !date.isBefore(start) && !date.isAfter(end))
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
        if (habit.getFreq() == Habit.Frequency.WEEKLY) {
            streak = streak / 7;
        }
        return streak;
    }

    public double getSuccessRate(Habit habit, LocalDate start, LocalDate end) {
        long totalDays = start.datesUntil(end.plusDays(1)).count();
        if (totalDays <= 0) {
            return 0;
        }
        return (double) getCompletionDaysInPeriod(habit, start, end) / totalDays * 100;
    }

    public void showHabitStats(Habit habit, LocalDate start, LocalDate end) {
        long completions = getCompletionsInPeriod(habit, start, end);
        long completionDays = getCompletionDaysInPeriod(habit, start, end);
        double successRate = getSuccessRate(habit, start, end);
        long streak = getStreak(habit);
        System.out.println("\nСтатистика: ");
        System.out.println("Выполнений за период: " + completions);
        System.out.println("Дней под выполнением за период: " + completionDays);
        System.out.println("Процент успешного выполнения: " + successRate + "%");
        System.out.println("Текущая серия выполнения: " + streak);
    }
}
