package manager;

import manager.exceptions.TaskNotFoundException;
import manager.exceptions.TaskTimeConflictException;
import model.Task;
import model.enums.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Duration;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws Exception {
        tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
        manager = FileBackedTaskManager.loadFromFile(tempFile);
    }

    @Test
    void save() throws TaskTimeConflictException {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        manager.addTask(task);
        manager.save();

        assertTrue(tempFile.length() > 0, "Файл должен быть не пустым после сохранения.");
    }

    @Test
    void loadFromFile() throws TaskTimeConflictException {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        manager.addTask(task);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size(), "Должна быть загружена одна задача.");
        assertEquals("Task 1", loadedManager.getAllTasks().get(0).getTitle(), "Название загруженной задачи должно совпадать.");
    }

    @Test
    void addTask() throws TaskTimeConflictException {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        manager.addTask(task);

        assertEquals(1, manager.getAllTasks().size(), "Должна быть добавлена одна задача.");
        assertEquals("Task 1", manager.getAllTasks().get(0).getTitle(), "Название добавленной задачи должно совпадать.");
    }

    @Test
    void updateTask() throws TaskTimeConflictException {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        manager.addTask(task);

        task.setTitle("Updated Task 1");
        manager.updateTask(task);

        assertEquals("Updated Task 1", manager.getAllTasks().get(0).getTitle(), "Название задачи должно обновиться.");
    }

    @Test
    void deleteTask() throws TaskTimeConflictException, TaskNotFoundException {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        manager.addTask(task);

        assertDoesNotThrow(() -> manager.deleteTask(task.getId()), "Удаление существующей задачи не должно выбрасывать исключение.");
        assertTrue(manager.getAllTasks().isEmpty(), "Все задачи должны быть удалены.");
    }

    @Test
    void deleteNonExistingTaskThrowsException() {
        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> {
            manager.deleteTask(999);
        });

        assertEquals("Ошибка: Задача с id 999 не найдена.", thrown.getMessage());
    }
}