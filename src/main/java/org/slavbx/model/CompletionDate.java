package org.slavbx.model;

import java.time.LocalDate;

public class CompletionDate {
    private Long id;
    private LocalDate date;
    private Habit habit;

    public CompletionDate(Long id, LocalDate date, Habit habit) {
        this.id = id;
        this.date = date;
        this.habit = habit;
    }

    public CompletionDate(LocalDate date, Habit habit) {
        this.date = date;
        this.habit = habit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
