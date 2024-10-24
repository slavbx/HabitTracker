package org.slavbx.repository;

public class SqlQueries {
    //Запросы HabitRepository
    public static final String INSERT_COMPLETION_DATE = "INSERT INTO completion_dates (completion_date, habit_id) VALUES (?, ?) " +
            "ON CONFLICT (completion_date, habit_id) DO UPDATE SET " +
            "completion_date = excluded.completion_date;";
    public static final String INSERT_HABIT = "INSERT INTO habits (name, description, freq, create_date, user_id) VALUES (?, ?, ?, ?, ?) " +
            "ON CONFLICT (name) DO UPDATE SET "+
            "description = excluded.description, freq = excluded.freq, create_date = excluded.create_date, user_id = excluded.user_id;";
    public static final String DELETE_HABIT_BY_ID = "DELETE FROM habits WHERE name = ?;";
    public static final String SELECT_HABIT_BY_ID = "SELECT * FROM habits WHERE id = ?;";
    public static final String DELETE_COMPLETIONS_BY_HABIT_ID = "DELETE FROM completion_dates WHERE habit_id = ?;";
    public static final String SELECT_HABIT_ID_BY_NAME = "SELECT id FROM habits WHERE name = ?;";
    public static final String SELECT_HABIT_BY_USER_ID = "SELECT id, name, description, freq, create_date FROM habits WHERE user_id = ?;";
    public static final String SELECT_COMPLETIONS_BY_HABIT_ID = "SELECT id, completion_date, habit_id FROM completion_dates WHERE habit_id = ?;";

    //Запросы UserRepository
    public static final String SELECT_USERS = "SELECT * FROM users;";
    public static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?;";
    public static final String SELECT_USER_BY_EMAIL = "SELECT id FROM users WHERE email = ?;";
    public static final String INSERT_USER = "INSERT INTO users (password, name, level, email) VALUES (?, ?, ?, ?) " +
            "ON CONFLICT (email) DO UPDATE SET password = excluded.password, " +
            "name = excluded.name, level = EXCLUDED.level, email = excluded.email;";
    public static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?;";

}
