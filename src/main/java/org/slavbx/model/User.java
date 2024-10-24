package org.slavbx.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий пользователя.
 * Предоставляет информацию о email, пароле, имени, уровне доступа пользователя
 */
@Getter
@Setter
@Builder
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
