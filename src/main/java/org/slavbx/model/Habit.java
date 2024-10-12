package org.slavbx.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Habit {
    private String name;
    private String desc;
    private Frequency freq;
    private final LocalDate createDate;
    private User user;
    private final List<LocalDate> completionDates;


    public enum Frequency {
        DAILY, WEEKLY
    }

    public Habit(String name, String desc, Frequency freq, User user) {
        this.name = name;
        this.desc = desc;
        this.freq = freq;
        this.user = user;
        this.createDate = LocalDate.now();
        this.completionDates = new ArrayList<>();
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

    public List<LocalDate> getCompletionDates() {
        return completionDates;
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
