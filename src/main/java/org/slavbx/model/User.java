package org.slavbx.model;

/**
 * Класс, представляющий пользователя.
 * Предоставляет информацию о email, пароле, имени, уровне доступа пользователя
 */
public class User {
    Long id;
    /**
     * Электронная почта
     */
    private String email;
    /**
     * Пароль
     */
    private String password;
    /**
     * Имя пользователя
     */
    private String name;
    /**
     * Уровень доступа пользователя
     */
    private final Level level;

    /**
     * Перечисление, представляющее уровни доступа пользователей
     */
    public enum Level {
        USER, ADMIN
    }

    /**
     * Конструктор класса User
     * @param email электронная почта
     * @param password пароль
     * @param name имя пользователя
     * @param level уровень доступа
     */
    public User(Long id, String email, String password, String name, Level level) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.level = level;
    }

    public User(String email, String password, String name, Level level) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.level = level;
    }
    public User() {
        level = Level.USER;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return email.hashCode() + password.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        User other = (User) obj;
        return other.getEmail().equals(this.getEmail()) && other.getPassword().equals(this.getPassword());
    }

    @Override
    public String toString() {
        return "Email: " +
                email +
                " | имя: " + name +
                " | уровень доступа: " + level;
    }
}
