package app;

import manager.FileBackedTaskManager;
import model.enums.StatusEnum;
import model.*;
import manager.TaskManager;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");

        // Создаём FileBackedTaskManager
        TaskManager taskManager = new FileBackedTaskManager(file);

        // Добавляем задачи
        Task task1 = new Task("Досмотреть сериал", "Досмотреть 6-й сезон Сопрано", StatusEnum.NEW);
        Task task2 = new Task("Сделать домашнее задание", "Написать проект по Java", StatusEnum.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Создаём эпик и подзадачи
        EpicTask epic1 = new EpicTask("Переезд", "Организовать переезд в новую квартиру");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Упаковать вещи", "Собрать и упаковать все вещи", StatusEnum.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Заказать транспорт", "Найти и заказать грузовик", StatusEnum.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        // Печатаем все задачи перед перезапуском
        System.out.println("=== Список всех задач перед перезапуском ===");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        // Загружаем задачи из файла
        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(file);

        // Проверяем загруженные задачи
        System.out.println("\n=== Список всех задач после перезапуска ===");
        for (Task task : loadedTaskManager.getAllTasks()) {
            System.out.println(task);
        }

        // Проверяем эпики и подзадачи через instanceof
        System.out.println("\n=== Эпики ===");
        for (Task task : loadedTaskManager.getAllTasks()) {
            if (task instanceof EpicTask) {
                System.out.println(task);
            }
        }

        System.out.println("\n=== Подзадачи ===");
        for (Task task : loadedTaskManager.getAllTasks()) {
            if (task instanceof Subtask) {
                System.out.println(task);
            }
        }
    }
}
