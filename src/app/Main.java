package app;

import model.enums.StatusEnum;
import model.*;
import manager.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        //  Создаём две задачи
        Task task1 = new Task("Досмотреть сериал", "Досмотреть 6-й сезон Сопрано", StatusEnum.NEW);
        Task task2 = new Task("Сделать домашнее задание", "написать проект по Java", StatusEnum.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Создаём эпик с двумя подзадачами
        EpicTask epic1 = new EpicTask("Переезд", "Организовать переезд в новую квартиру");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Упаковать вещи", "Собрать и упаковать все вещи", StatusEnum.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Заказать транспорт", "Найти и заказать грузовик", StatusEnum.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        // Создаём эпик с одной подзадачей
        EpicTask epic2 = new EpicTask("Подготовка к экзамену", "Изучить все темы к экзамену");
        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Прочитать учебник", "Прочитать главы 1-5", StatusEnum.NEW, epic2.getId());
        taskManager.addSubtask(subtask3);

        //  Распечатываем списки эпиков, задач и подзадач
        System.out.println("=== Список всех задач ==>");
        for (Task task : taskManager.getTaskData().values()) {
            System.out.println(task);
        }

        System.out.println("\n=== Список всех эпиков ==>");
        for (EpicTask epic : taskManager.getEpicData().values()) {
            System.out.println(epic);
        }

        System.out.println("\n=== Список всех подзадач ==>");
        for (Subtask subtask : taskManager.getSubtaskData().values()) {
            System.out.println(subtask);
        }

        // Изменяем статусы созданных объектов
        task1.setStatus(StatusEnum.IN_PROGRESS);
        taskManager.updateTask(task1);

        subtask1.setStatus(StatusEnum.DONE);
        taskManager.updateSubtask(subtask1);

        subtask2.setStatus(StatusEnum.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        subtask3.setStatus(StatusEnum.DONE);
        taskManager.updateSubtask(subtask3);

        // Распечатываем обновлённые объекты и проверяем статусы
        System.out.println("\n=== Обновлённые задачи ===");
        for (Task task : taskManager.getTaskData().values()) {
            System.out.println(task);
        }

        System.out.println("\n=== Обновлённые подзадачи ==>");
        for (Subtask subtask : taskManager.getSubtaskData().values()) {
            System.out.println(subtask);
        }

        System.out.println("\n=== Обновлённые эпики ==>");
        for (EpicTask epic : taskManager.getEpicData().values()) {
            System.out.println(epic);
        }

        // Проверяем статусы
        System.out.println("\nСтатус задачи 1: " + task1.getStatus()); // Должен быть IN_PROGRESS
        System.out.println("Статус подзадачи 1: " + subtask1.getStatus()); // Должен быть DONE
        System.out.println("Статус эпика 1: " + epic1.getStatus()); // Должен быть IN_PROGRESS
        System.out.println("Статус эпика 2: " + epic2.getStatus()); // Должен быть DONE

        System.out.println("\n Удаляем одну из задач и один из эпиков");
        taskManager.deleteTask(task2.getId());
        taskManager.deleteEpic(epic2.getId());

        // Распечатываем списки после удаления
        System.out.println("\n=== Список задач после удаления ==>");
        for (Task task : taskManager.getTaskData().values()) {
            System.out.println(task);
        }

        System.out.println("\n=== Список эпиков после удаления ==>");
        for (EpicTask epic : taskManager.getEpicData().values()) {
            System.out.println(epic);
        }

        System.out.println("\n=== Список подзадач после удаления ==>");
        for (Subtask subtask : taskManager.getSubtaskData().values()) {
            System.out.println(subtask);
        }
    }
}
