package org.slavbx.service;

import org.slavbx.repository.HabitRepositoryCore;
import org.slavbx.repository.UserRepositoryCore;
import org.slavbx.repository.UserRepositoryJdbc;

/**
 * Фабрика для создания сервисов.
 * Предоставляет статические методы для получения экземпляров
 * сервисов HabitService и UserService, с соответствующими репозиториями
 */
public class ServiceFactory {
    /**
     * Создает экземпляр HabitService с использованием HabitRepositoryCore
     * @return новый экземпляр HabitService
     */
    public static HabitService getHabitService() {
        return new HabitService(new HabitRepositoryCore());
    }

    /**
     * Создает экземпляр UserService с использованием UserRepositoryCore
     * @return новый экземпляр UserService
     */
    public static UserService getUserService() {
        return new UserService(new UserRepositoryJdbc());
    }
}
