package org.slavbx;


import org.slavbx.model.Habit;
import org.slavbx.model.User;
import org.slavbx.repository.HabitRepository;
import org.slavbx.repository.HabitRepositoryCore;
import org.slavbx.repository.UserRepository;
import org.slavbx.repository.UserRepositoryCore;
import org.slavbx.service.HabitService;
import org.slavbx.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static UserRepository userRepository = new UserRepositoryCore();
    static UserService userService = new UserService(userRepository);
    static HabitRepository habitRepository = new HabitRepositoryCore();
    static HabitService habitService = new HabitService(habitRepository);

    public static void main(String[] args) {
        init();
        start();
    }

    static void init() { //Начальное меню
        User user = new User("slav", "slav", "slav", User.Level.USER);
        userService.save(user);
        habitService.save(new Habit("name", "desc", Habit.Frequency.DAILY, user));
    }

    static void start() { //Начальное меню
        System.out.println("""
                --Начальное меню
                Введите для продолжения:\s
                1 - Вход
                2 - Регистрация
                0 - Выход""");
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

    static void signIn() { //Авторизация
        System.out.print("--Вход\n" + "Введите email: ");
        String email = scanner.next();
        if (userService.findUserByEmail(email).isEmpty()) {
            System.out.println("Ошибка. Пользователь с таким email не зарегистрирован\n");
            start();
        } else {
            System.out.print("Введите пароль: ");
            String password = scanner.next();

            if (userService.authorize(email, password)) {
                System.out.println("Вход выполнен\n");
                //Попадаем в нужное меню в зависимости от привилегий
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

    static void signUp() { //Регистрация пользователя
        System.out.print("--Регистрация\n" + "Введите email: ");
        String email = scanner.next();
        if (userService.findUserByEmail(email).isEmpty()) {
            System.out.print("Введите пароль: ");
            String password = scanner.next();
            System.out.print("Введите имя: ");
            String name = scanner.next();
            User user = new User(email, password, name, User.Level.USER);
            userService.save(user);
            System.out.println("Пользователь зарегистрирован\n");
        } else {
            System.out.println("Ошибка. Пользователь с таким email уже существует\n");
        }
        start();
    }

    static void actionsUser() { //Меню действий пользователя
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

    static void editProfile(User user) {
        System.out.println("\n--Редактирование профиля " + user.getName());
        System.out.println("Введите команду:\n" +
                "1 - Редактировать имя: " + user.getName() + "\n" +
                "2 - Редактировать email: " + user.getEmail() + "\n" +
                "3 - Редактировать пароль: " + user.getPassword() + "\n" +
                "0 - Назад");
        switch (scanner.next()) {
            case "1" -> editUserField(user, h -> h.setName(scanner.next()), "имя");
            case "2" -> editUserField(user, h -> h.setEmail(scanner.next()), "email");
            case "3" -> editUserField(user, h -> h.setPassword(scanner.next()), "пароль");
            case "0" -> actionsUser();
            default -> System.out.println("Неверная команда\n");
        }
        editProfile(user);
    }

    public static void editUserField(User user, Consumer<User> consumer, String field) {
        System.out.print("--Новое " + field + ": ");
        consumer.accept(user);
        userService.save(user);
        System.out.println(field + " отредактировано");
    }

    static void manageHabit(LocalDate date) {
        //date прокидывается в методы чтобы держать фильтр списка по дате
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

    static void createHabit(LocalDate date) {
        System.out.print("--Создание привычки\n");
        System.out.print("Введите название: ");
        String name = scanner.next();
        System.out.print("Введите описание: ");
        String desc = scanner.next();
        System.out.print("Введите weekly, если нужна еженедельная привычка, или любое другое для ежедневной: ");
        boolean weekly = scanner.next().equals("weekly");
        if (weekly) {
            habitService.save(new Habit(name, desc, Habit.Frequency.WEEKLY, userService.getAuthorizedUser()));
        } else {
            habitService.save(new Habit(name, desc, Habit.Frequency.DAILY, userService.getAuthorizedUser()));
        }
        System.out.println("Привычка создана");
        manageHabit(date);
    }

    static void editHabit(String name, LocalDate date) {
        System.out.println("\n--Редактирование привычки " + name);
        Optional<Habit> optHabit = habitService.findHabitByName(name);
        if (optHabit.isPresent()) {
            Habit habit = optHabit.get();
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
            System.out.println("Привычка не найдена");
            manageHabit(date);
        }
    }

    public static void editHabitField(Habit habit, Consumer<Habit> consumer, String field) {
        System.out.print("--Новое " + field + ": ");
        consumer.accept(habit);
        habitService.save(habit);
        System.out.println(field + " отредактировано");
    }

    static void deleteHabit(LocalDate date) {
        System.out.print("--Удаление привычки\n" + "Введите название: ");
        String name = scanner.next();
        if (habitService.findHabitByName(name).isPresent()) {
            habitService.deleteHabitByName(name);
            System.out.println("Привычка удалена");
        } else {
            System.out.println("Ошибка. Привычка с таким именем не существует");
        }
        manageHabit(date);
    }

    static void viewAllHabitsByUser(LocalDate date, User user) {
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

    static void markCompleteHabit(LocalDate date) {
        System.out.print("--Отметить выполнение привычки\n" + "Введите название: ");
        String name = scanner.next();
        Optional<Habit> optHabit = habitService.findHabitByName(name);
        if (optHabit.isPresent()) {
            habitService.markAsCompleted(optHabit.get());
            System.out.println("Выполнение отмечено");
        } else {
            System.out.println("Ошибка. Привычка с таким именем не существует");
        }
        manageHabit(date);
    }

    static void statisticMenu(String name, LocalDate date) {
        System.out.println("\n--Статистика по привычке " + name);
        Optional<Habit> optHabit = habitService.findHabitByName(name);
        if (optHabit.isPresent()) {
            Habit habit = optHabit.get();
            int year = inputInt("Введите год: ");
            int month = inputInt("Введите месяц: ");
            int day = inputInt("Введите день: ");
            int days = inputInt("Введите количество дней: ");
            LocalDate start = LocalDate.of(year, month, day);
            LocalDate end = LocalDate.of(year, month, day).plusDays(days);
            habitService.showHabitStats(habit, start, end);
            System.out.println("\nВведите любую команду для возврата:");
            scanner.next();
            manageHabit(date);
        } else {
            System.out.println("Привычка не найдена");
            actionsUser();
        }

    }

    static int inputInt(String label) {
        System.out.print(label);
        String input = scanner.next();
        if (input.matches("\\d+")) {
            return Integer.parseInt(input);
        } else {
            System.out.println("Ошибка ввода. Введите целое число\n");
            return inputInt(label);
        }
    }

    static void actionsAdmin() { //Меню действий администратора
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

    static void deleteUser() { //Удаление аккаунта
        System.out.print("--Удаление аккаунта\n" + "Введите email: ");
        String email = scanner.next();
        if (userService.findUserByEmail(email).isPresent()) {
            userService.deleteUserByEmail(email);
            System.out.println("Пользователь удалён\n");
        } else {
            System.out.println("Ошибка. Пользователь с таким email не существует\n");
        }
        actionsAdmin();
    }

    static void resetPassword() { //Сброс пароля
        System.out.print("--Сброс пароля\n" + "Введите email: ");
        String email = scanner.next();
        if (userService.findUserByEmail(email).isPresent()) {
            userService.resetPassword(email);
            System.out.println("Пароль сброшен на psw\n");
        } else {
            System.out.println("Ошибка. Пользователь с таким email не существует\n");
        }
        actionsAdmin();
    }

    static void viewAllUsers() {
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

    static void viewHabitsByUser() {
        System.out.print("Введите email: ");
        String email = scanner.next();
        Optional<User> optUser = userService.findUserByEmail(email);
        if (optUser.isPresent()) {
            System.out.print("--Список привычек пользователя\n");
            viewAllHabitsByUser(null, optUser.get());
        } else {
            System.out.println("Ошибка. Пользователь с таким email не существует\n");
        }
        System.out.println("\nВведите любую команду для возврата:");
        scanner.next();
        actionsAdmin();
    }
}