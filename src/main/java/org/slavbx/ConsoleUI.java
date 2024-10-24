package org.slavbx;

import org.slavbx.model.Habit;
import org.slavbx.model.User;
import org.slavbx.service.HabitService;
import org.slavbx.service.ServiceFactory;
import org.slavbx.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Класс пользовательского интерфейса консольного приложения.
 * Предоставляет интерфейс для взаимодействия с пользователем,
 * включая вход в систему и регистрацию
 */
public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final HabitService habitService = ServiceFactory.getHabitService();
    private final UserService userService = ServiceFactory.getUserService();

    /**
     * Инициализирует и запускает консольный интерфейс.
     * Создает тестовых пользователей и привычки
     */
    public void initAndStart() {
        DatabaseProvider.initDatabase();
        start();
    }

    /**
     * Запускает главное меню для взаимодействия с пользователем
     */
    private void start() {
        System.out.println("""
                --Начальное меню
                Введите для продолжения:\s
                1 - Вход
                2 - Регистрация
                0 - Завершение работы""");
        switch (scanner.next()) {
            case "1" -> signIn();
            case "2" -> signUp();
            case "0" -> {}
            default -> {
                System.out.println("Неверная команда\n");
                start();
            }
        }
    }

    private void signIn() {
        System.out.print("--Вход\n" + "Введите email: ");
        String email = scanner.next();
        if (!userService.isUserRegistered(email)) {
            System.out.println("Ошибка. Пользователь с таким email не зарегистрирован\n");
            start();
        } else {
            System.out.print("Введите пароль: ");
            String password = scanner.next();

            if (userService.authorize(email, password)) {
                System.out.println("Вход выполнен");
                if (userService.getAuthorizedUser().getLevel() == User.Level.ADMIN) {
                    actionsAdmin();
                } else {
                    actionsUser();
                }
            } else {
                System.out.println("Неверный пароль\n");
                start();
            }
        }
    }

    private void signUp() {
        System.out.print("--Регистрация\n" + "Введите email: ");
        String email = scanner.next();
        if (!userService.isUserRegistered(email)) {
            System.out.print("Введите пароль: ");
            String password = scanner.next();
            System.out.print("Введите имя: ");
            String name = scanner.next();
            userService.registerUser(email, password, name, User.Level.USER);
            System.out.println("Пользователь зарегистрирован\n");
        } else {
            System.out.println("Ошибка. Пользователь с таким email уже существует\n");
        }
        start();
    }


    private void actionsUser() {
        System.out.println("\n--Личный кабинет " + userService.getAuthorizedUser().getName());
        System.out.println("""
                Введите команду:
                1 - Управление профилем
                2 - Управление привычками
                0 - Выход""");
        switch (scanner.next()) {
            case "1" -> editProfile(userService.getAuthorizedUser());
            case "2" -> manageHabit(null);
            case "0" -> {
                userService.unauthorize();
                start();
            }
            default -> {
                System.out.println("Неверная команда\n");
                actionsUser();
            }
        }
    }

    private void editProfile(User user) {
        System.out.println("\n--Редактирование профиля " + user.getName());
        System.out.println("Введите команду:\n" +
                "1 - Редактировать имя: " + user.getName() + "\n" +
                "2 - Редактировать email: " + user.getEmail() + "\n" +
                "3 - Редактировать пароль: " + user.getPassword() + "\n" +
                "0 - Назад");
        switch (scanner.next()) {
            case "1" -> {
                editUserField(user, h -> h.setName(scanner.next()), "имя");
                editProfile(user);
            }
            case "2" -> {
                editUserField(user, h -> h.setEmail(scanner.next()), "email");
                userService.authorize(user.getEmail(), user.getPassword());
                editProfile(user);
            }
            case "3" -> {
                editUserField(user, h -> h.setPassword(scanner.next()), "пароль");
                userService.authorize(user.getEmail(), user.getPassword());
                editProfile(user);
            }
            case "0" -> actionsUser();
            default -> {
                System.out.println("Неверная команда\n");
                editProfile(user);
            }
        }

    }

    private void editUserField(User user, Consumer<User> consumer, String field) {
        System.out.print("--Новое " + field + ": ");
        consumer.accept(user);
        userService.save(user);
        System.out.println(field + " отредактировано");
    }

    private void manageHabit(LocalDate date) {
        System.out.println("\n--Управление привычками " + userService.getAuthorizedUser().getName());
        viewAllHabitsByUser(date, userService.getAuthorizedUser());
        System.out.println("""

                Введите команду:
                1 - Создание привычки
                2 - Редактирование привычки
                3 - Удаление привычки
                4 - Фильтровать список по дате
                5 - Отметить выполнение привычки
                6 - Отчёт статистики по привычке
                0 - Назад""");
        switch (scanner.next()) {
            case "1" -> createHabit(date);
            case "2" -> {
                System.out.print("Введите название: ");
                editHabit(scanner.next(), date);
            }
            case "3" -> deleteHabit(date);
            case "4" -> {
                int year = inputInt("Введите год: ");
                int month = inputInt("Введите месяц: ");
                int day = inputInt("Введите день: ");
                date = LocalDate.of(year, month, day);
                manageHabit(date);
            }
            case "5" -> markCompleteHabit(date);
            case "6" -> {
                System.out.print("Введите название привычки: ");
                statisticMenu(scanner.next(), date);
            }
            case "0" -> actionsUser();
            default -> {
                System.out.println("Неверная команда\n");
                manageHabit(date);
            }
        }
    }

    private void createHabit(LocalDate date) {
        System.out.print("--Создание привычки\n");
        System.out.print("Введите название: ");
        String name = scanner.next();
        System.out.print("Введите описание: ");
        String desc = scanner.next();
        System.out.print("Введите weekly, если нужна еженедельная привычка, или любое другое для ежедневной: ");
        boolean weekly = scanner.next().equals("weekly");
        if (weekly) {
            habitService.createHabit(name, desc, Habit.Frequency.WEEKLY, userService.getAuthorizedUser());
        } else {
            habitService.createHabit(name, desc, Habit.Frequency.DAILY, userService.getAuthorizedUser());
        }
        System.out.println("Привычка создана");
        manageHabit(date);
    }

    private void editHabit(String name, LocalDate date) {
        System.out.println("\n--Редактирование привычки " + name);
        if (habitService.isHabitExists(name)) {
            Habit habit = habitService.findHabitByName(name, userService.getAuthorizedUser()).get();
            System.out.println("Введите команду:\n" +
                    "1 - Редактировать название: " + habit.getName() + "\n" +
                    "2 - Редактировать описание: " + habit.getDesc() + "\n" +
                    "3 - Сменить частоту: " + habit.getFreq().name() + "\n" +
                    "0 - Назад");
            switch (scanner.next()) {
                case "1" -> editHabitField(habit, h -> h.setName(scanner.next()), "название");
                case "2" -> editHabitField(habit, h -> h.setDesc(scanner.next()), "описание");
                case "3" -> {
                    Habit.Frequency freq = habit.getFreq() == Habit.Frequency.WEEKLY ? Habit.Frequency.DAILY : Habit.Frequency.WEEKLY;
                    editHabitField(habit, h -> h.setFreq(freq), "частота");
                }
                case "0" -> manageHabit(date);
                default -> System.out.println("Неверная команда\n");
            }
            editHabit(habit.getName(), date);
        } else {
            System.out.println("Привычка не найдена ");
            manageHabit(date);
        }
    }

    private void editHabitField(Habit habit, Consumer<Habit> consumer, String field) {
        System.out.print("--Новое " + field + ": ");
        consumer.accept(habit);
        habitService.save(habit);
        System.out.println(field + " отредактировано");
    }

    private void deleteHabit(LocalDate date) {
        System.out.print("--Удаление привычки\n" + "Введите название: ");
        String name = scanner.next();
        if (habitService.isHabitExists(name)) {
            habitService.deleteHabitByName(name);
            System.out.println("Привычка удалена");
        } else {
            System.out.println("Ошибка. Привычка с таким именем не существует");
        }
        manageHabit(date);
    }

    private void viewAllHabitsByUser(LocalDate date, User user) {
        if (date == null) {
            System.out.println("Список привычек: ");
        } else {
            System.out.println("Список привычек за " + date.getYear() + "." + date.getMonthValue() + "." + date.getDayOfMonth());
        }
        List<Habit> list = habitService.findHabitByUser(user, date);
        if (!list.isEmpty()) {
            list.forEach(System.out::println);
        } else {
            System.out.println("Привычек не найдено");
        }
    }

    private void markCompleteHabit(LocalDate date) {
        System.out.print("--Отметить выполнение привычки\n" + "Введите название: ");
        String name = scanner.next();
        Optional<Habit> optHabit = habitService.findHabitByName(name, userService.getAuthorizedUser());
        if (optHabit.isPresent()) {
            habitService.markAsCompleted(optHabit.get());
            System.out.println("Выполнение отмечено");
        } else {
            System.out.println("Ошибка. Привычка с таким именем не существует");
        }
        manageHabit(date);
    }

    private void statisticMenu(String name, LocalDate date) {
        System.out.println("\n--Статистика по привычке " + name);
        if (habitService.isHabitExists(name)) {
            Habit habit = habitService.findHabitByName(name, userService.getAuthorizedUser()).get();
            int year = inputInt("Введите год: ");
            int month = inputInt("Введите месяц: ");
            int day = inputInt("Введите день: ");
            int days = inputInt("Введите количество дней: ");
            LocalDate start = LocalDate.of(year, month, day);
            LocalDate end = LocalDate.of(year, month, day).plusDays(days - 1);
            habitService.showHabitStats(habit, start, end);
            System.out.println("\nВведите любую команду для возврата:");
            scanner.next();
            manageHabit(date);
        } else {
            System.out.println("Привычка не найдена");
            actionsUser();
        }

    }

    int inputInt(String label) {
        System.out.print(label);
        String input = scanner.next();
        if (input.matches("\\d+")) {
            return Integer.parseInt(input);
        } else {
            System.out.println("Ошибка ввода. Введите целое число\n");
            return inputInt(label);
        }
    }

    private void actionsAdmin() {
        System.out.println("\n--Личный кабинет " + userService.getAuthorizedUser().getName());
        System.out.println("""
                Введите команду:
                1 - Удаление аккаунта
                2 - Сброс пароля аккаунта
                3 - Просмотр списка пользователей
                4 - Просмотр привычек пользователя
                0 - Выход""");
        switch (scanner.next()) {
            case "1" -> deleteUser();
            case "2" -> resetPassword();
            case "3" -> viewAllUsers();
            case "4" -> viewHabitsByUser();
            case "0" -> {
                userService.unauthorize();
                start();
            }
            default -> {
                System.out.println("Неверная команда\n");
                actionsAdmin();
            }
        }
    }

    private void deleteUser() {
        System.out.print("--Удаление аккаунта\n" + "Введите email: ");
        String email = scanner.next();
        if (userService.isUserRegistered(email)) {
            User user = userService.findUserByEmail(email).get();
            for(Habit habit: habitService.findHabitByUser(user, null)) {
                habitService.deleteHabitByName(habit.getName());
            }
            userService.deleteUserByEmail(email);
            System.out.println("Пользователь удалён\n");
        } else {
            System.out.println("Ошибка. Пользователь с таким email не существует\n");
        }
        actionsAdmin();
    }

    private void resetPassword() {
        System.out.print("--Сброс пароля\n" + "Введите email: ");
        String email = scanner.next();
        if (userService.isUserRegistered(email)) {
            userService.resetPassword(email);
            System.out.println("Пароль сброшен на psw\n");
        } else {
            System.out.println("Ошибка. Пользователь с таким email не существует\n");
        }
        actionsAdmin();
    }

    private void viewAllUsers() {
        System.out.println("--Список пользователей: ");
        List<User> list = userService.findAllUsers();
        if (!list.isEmpty()) {
            list.forEach(System.out::println);
        } else {
            System.out.println("Пользователей не найдено");
        }
        System.out.println("\nВведите любую команду для возврата:");
        scanner.next();
        actionsAdmin();
    }

    private void viewHabitsByUser() {
        System.out.print("Введите email: ");
        String email = scanner.next();
        if (userService.isUserRegistered(email)) {
            System.out.print("--Список привычек пользователя\n");
            viewAllHabitsByUser(null, userService.findUserByEmail(email).get());
        } else {
            System.out.println("Ошибка. Пользователь с таким email не существует\n");
        }
        System.out.println("\nВведите любую команду для возврата:");
        scanner.next();
        actionsAdmin();
    }
}
