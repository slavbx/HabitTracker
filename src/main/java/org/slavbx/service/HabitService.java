package org.slavbx.service;

import org.slavbx.model.CompletionDate;
import org.slavbx.model.Habit;
import org.slavbx.model.User;
import org.slavbx.repository.HabitRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления привычками.
 * Предоставляет методы для сохранения, поиска,
 * удаления и получения статистики по привычкам пользователя
 */
public class HabitService {
    private static final int DAYS_IN_WEEK = 7;
    private static final int MAX_PERCENTAGE = 100;
    /**
     * Репозиторий привычек
     */
    private final HabitRepository habitRepository;

    /**
     * Конструктор класса HabitService
     * @param habitRepository репозиторий привычек, используемый для операций с данными
     */
    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    /**
     * Сохраняет привычку с указанным именем
     * @param name название привычки для сохранения
     * @param habit объект привычки для сохранения
     */
    public void save(Habit habit) {
        habitRepository.save(habit);
    }

    /**
     * Находит привычку по заданному названию
     * @param name название привычки для поиска
     * @return объект Optional, содержащий найденную привычку, или пустой объект, если привычка не найдена
     */
    public Optional<Habit> findHabitByName(String name, User user) {
        return habitRepository.findByName(name, user);
    }

    /**
     * Удаляет привычку по указанному названию
     * @param name название привычки для удаления
     */
    public void deleteHabitByName(String name) {
        habitRepository.deleteByName(name);
    }

    /**
     * Находит все привычки, связанные с указанным пользователем и датой
     * @param user объект пользователя для поиска
     * @param date объект даты для поиска
     * @return список привычек, соответствующих указанному пользователю и дате
     */
    public List<Habit> findHabitByUser(User user, LocalDate date) {
        if (date == null) {
            return habitRepository.findByUser(user);
        } else {
            return habitRepository.findByUser(user).stream().filter(h -> h.getCreateDate().isEqual(date)).toList();
        }
    }

    /**
     * Отмечает привычку как выполненную.
     * Если привычка ежедневная, сохраняется текущая дата,
     * если недельная, сохраняются даты на неделю вперед
     * @param habit привычка, которую необходимо отметить как выполненную
     */
    public void markAsCompleted(Habit habit) {
        if (habit.getFreq() == Habit.Frequency.DAILY) {
            habit.getCompletionDates().add(CompletionDate.builder()
                            .date(LocalDate.now())
                            .habit(habit)
                            .build());
        } else { //WEEKLY
            List <CompletionDate> dates = new ArrayList<>();
            for (int i = 0; i < DAYS_IN_WEEK; i++) { //Заполняем сразу на неделю вперёд
                dates.add(CompletionDate.builder()
                                .date(LocalDate.now().plusDays(i))
                                .habit(habit)
                                .build());
            }
            habit.getCompletionDates().addAll(dates);
        }
        save(habit);

    }

    /**
     * Возвращает количество выполнений привычки за указанный период.
     * @param habit привычка, для которой нужно получить количество выполнений
     * @param start начало периода
     * @param end конец периода
     * @return количество выполнений привычки за указанный период
     */
    public long getCompletionsInPeriod(Habit habit, LocalDate start, LocalDate end) {
        if (habit.getFreq() == Habit.Frequency.DAILY) {
            return getCompletionDaysInPeriod(habit, start, end);
        } else { //WEEKLY
            long completionsDays = getCompletionDaysInPeriod(habit, start, end);
            long completions = completionsDays / DAYS_IN_WEEK;
            if (completionsDays % DAYS_IN_WEEK > 0) completions++; //Часть отмеченной недели, вошедшая в период
            return completions;
        }
    }

    /**
     * Возвращает количество дней, когда привычка была отмечена за указанный период.
     * @param habit привычка, для которой необходимо получить количество выполненных дней
     * @param start начало периода
     * @param end конец периода
     * @return количество дней выполнения привычки за указанный период
     */
    public long getCompletionDaysInPeriod(Habit habit, LocalDate start, LocalDate end) {
        return habit.getCompletionDates().stream().map(CompletionDate::getDate)
                    .filter(date -> !date.isBefore(start) && !date.isAfter(end))
                    .count();
    }

    /**
     * Возвращает текущую серию выполнения привычки.
     * @param habit привычка, для которой необходимо вернуть серию
     * @return количество дней выполнения привычки в текущей серии
     */
    public long getStreak(Habit habit) {
        long streak = 0;
        List<CompletionDate> dates = habit.getCompletionDates();

        for (int i = 0; i < dates.size(); i++) {
            LocalDate date = dates.get(dates.size() - 1 - i).getDate();
            if (date.isEqual(LocalDate.now().minusDays(i))) {
                streak++;
            } else {
                break;
            }
        }
        if (habit.getFreq() == Habit.Frequency.WEEKLY) {
            streak = streak / DAYS_IN_WEEK;
        }
        return streak;
    }

    /**
     * Вычисляет процент успешного выполнения привычки за указанный период.
     * @param habit привычка, для которой необходимо вычислить процент успешного выполнения
     * @param start начало периода
     * @param end конец периода
     * @return процент успешного выполнения привычки за указанный период
     */
    public double getSuccessRate(Habit habit, LocalDate start, LocalDate end) {
        long totalDays = start.datesUntil(end.plusDays(1)).count();
        if (totalDays <= 0) {
            return 0;
        }
        return (double) getCompletionDaysInPeriod(habit, start, end) / totalDays * MAX_PERCENTAGE;
    }

    /**
     * Выводит статистику выполнения привычки за указанный период на консоль
     * @param habit привычка, для которой необходимо вывести статистику
     * @param start начало периода
     * @param end конец периода
     */
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

    /**
     * Создает новую привычку и сохраняет её.
     * @param name  название привычки
     * @param desc  описание привычки
     * @param freq  частота выполнения привычки
     * @param user  пользователь, которому принадлежит привычка
     */
    public void createHabit(String name, String desc, Habit.Frequency freq, User user) {
        save(Habit.builder().name(name).desc(desc).freq(freq).user(user).build());
    }

    /**
     * Проверяет, существует ли привычка с заданным именем.
     * @param name имя привычки, которую нужно проверить на существование
     * @return true, если привычка существует, иначе false
     */
    public boolean isHabitExists(String name) {
        return findHabitByName(name, User.builder().build()).isPresent();
    }
}
