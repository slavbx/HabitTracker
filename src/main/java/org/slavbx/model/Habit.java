package org.slavbx.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий привычку пользователя.
 * Предоставляет информацию о названии, описании, частоте выполнения,
 * дате создания и днях, когда привычка была выполнена
 */
@Getter
@Setter
@Builder
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
