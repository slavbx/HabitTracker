package org.slavbx.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий привычку пользователя.
 * Предоставляет информацию о названии, описании, частоте выполнения,
 * дате создания и днях, когда привычка была выполнена
 */
public class Habit {

    Long id;
    /**
     * Название
     */
    private String name;
    /**
     * Описание
     */
    private String desc;
    /**
     * Частота выолнения
     */
    private Frequency freq;
    /**
     * Дата создания
     */
    private final LocalDate createDate;
    /**
     * Пользователь-владелец привычки
     */
    private User user;
    /**
     * Список дат, в которые привычка была выполнена
     */
    private List<CompletionDate> completionDates;

    /**
     * Перечисление, представляющее частоту выполнения привычек
     */
    public enum Frequency {
        DAILY, WEEKLY
    }

    /**
     * Конструктор класса Habit
     * @param name название привычки
     * @param desc описание привычки
     * @param freq частота привычки
     * @param user объект пользователя-владельца привычки
     */
    public Habit(String name, String desc, Frequency freq, User user) {
        this.name = name;
        this.desc = desc;
        this.freq = freq;
        this.user = user;
        this.createDate = LocalDate.now();
        this.completionDates = new ArrayList<>();
    }

    public Habit(Long id, String name, String desc, Frequency freq, LocalDate createDate, User user, List<CompletionDate> completionDates) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.freq = freq;
        this.user = user;
        this.createDate = createDate;
        this.completionDates = completionDates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Frequency getFreq() {
        return freq;
    }

    public void setFreq(Frequency freq) {
        this.freq = freq;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public List<CompletionDate> getCompletionDates() {
        return completionDates;
    }

    public void setCompletionDates(List<CompletionDate> completionDates) {
        this.completionDates = completionDates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Habit other = (Habit) obj;
        return other.getName().equals(this.getName());
    }

    @Override
    public String toString() {
        return "Дата: " + getCreateDate()
                + " | Название: " + getName()
                + " | Описание: " + getDesc()
                + " | Частота: " + getFreq().name();
    }
}
