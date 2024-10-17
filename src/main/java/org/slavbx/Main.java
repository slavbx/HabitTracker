package org.slavbx;

/**
 * Главный класс приложения
 */
public class Main {
    /**
     * Метод-точка входа в приложение.
     * @param args не используется
     */
    public static void main(String[] args) {
        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.initAndStart();
    }
}