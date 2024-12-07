package app;

import manager.Managers;
import model.enums.StatusEnum;
import model.*;
import manager.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Создаём задачи
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

        // Печатаем все задачи
        System.out.println("=== Список всех задач ===");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        // Печатаем подзадачи эпика
        System.out.println("\n=== Подзадачи эпика ===");
        for (Subtask subtask : taskManager.getSubtasksOfEpic(epic1.getId())) {
            System.out.println(subtask);
        }
    }
}
