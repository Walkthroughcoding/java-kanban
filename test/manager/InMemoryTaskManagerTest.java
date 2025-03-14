package manager;

import model.Task;
import model.EpicTask;
import model.Subtask;
import model.enums.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(); // Создаём новый менеджер перед каждым тестом
    }

    @Test
    void addNewTask() {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW,
                     Duration.ofMinutes(60), LocalDateTime.now());
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getAnyTask(task.getId());

        assertNotNull(retrievedTask, "Задача не должна быть null.");
        assertEquals(task, retrievedTask, "Добавленная и полученная задачи должны совпадать.");
    }

    @Test
    void addNewEpicWithSubtasks() {
        EpicTask epic = new EpicTask("Test Epic", "Test Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Subtask Description", StatusEnum.NEW,
                Duration.ofMinutes(45), LocalDateTime.now().plusMinutes(30), epic.getId());

        taskManager.addSubtask(subtask); // ✅ Добавляем подзадачу в менеджер

        EpicTask retrievedEpic = (EpicTask) taskManager.getAnyTask(epic.getId());
        List<Subtask> subtasks = taskManager.getSubtasksOfEpic(epic.getId());

        assertNotNull(retrievedEpic, "Эпик не должен быть null.");
        assertEquals(1, subtasks.size(), "Эпик должен содержать одну подзадачу.");
        assertEquals(subtask, subtasks.get(0), "Подзадача должна совпадать с добавленной.");
    }

    @Test
    void cannotAddSubtaskToNonExistingEpic() {
        Subtask subtask = new Subtask("Orphan Subtask", "Should not be added", StatusEnum.NEW,
                Duration.ofMinutes(45), LocalDateTime.now().plusMinutes(10), 999); // ✅ Добавили Duration и LocalDateTime

        taskManager.addSubtask(subtask);
        assertNull(taskManager.getAnyTask(subtask.getId()), "Подзадача не должна быть добавлена к несуществующему эпику.");
    }

    @Test
    void taskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1", StatusEnum.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        Task task2 = new Task("Task 1", "Description 1", StatusEnum.NEW, Duration.ofMinutes(60), LocalDateTime.now());

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны.");
    }

    @Test
    void epicStatusUpdatesWithSubtasks() {
        EpicTask epic = new EpicTask("Test Epic", "Test Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", StatusEnum.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(10), epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", StatusEnum.DONE,
                Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(40), epic.getId());

        // 🔹 Нужно добавить подзадачи в taskManager, иначе статус эпика не обновится!
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        EpicTask retrievedEpic = (EpicTask) taskManager.getAnyTask(epic.getId());
        assertEquals(StatusEnum.IN_PROGRESS, retrievedEpic.getStatus(),
                "Эпик должен быть IN_PROGRESS, если его подзадачи имеют разные статусы.");
    }

    @Test
    void deleteTaskRemovesTask() {
        Task task = new Task("Test Task", "Description", StatusEnum.NEW,
                Duration.ofMinutes(60), LocalDateTime.now()); // ✅ Добавили Duration и LocalDateTime

        taskManager.addTask(task);
        taskManager.deleteTask(task.getId());

        assertNull(taskManager.getAnyTask(task.getId()), "Удалённая задача не должна существовать.");
    }

    @Test
    void deleteSubtaskUpdatesEpic() {
        EpicTask epic = new EpicTask("Test Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", StatusEnum.DONE, 
                              Duration.ofMinutes(20), LocalDateTime.now().plusMinutes(15), epic.getId());        taskManager.addSubtask(subtask);

        taskManager.deleteSubtask(subtask.getId());
        EpicTask retrievedEpic = (EpicTask) taskManager.getAnyTask(epic.getId());
        assertEquals(StatusEnum.NEW, retrievedEpic.getStatus(), "Эпик должен быть NEW после удаления всех подзадач.");
    }

    @Test
    void shouldUpdateTaskFields() {
        Task task = new Task("Old Name", "Old Description", StatusEnum.NEW, Duration.ofMinutes(90), LocalDateTime.now());
        taskManager.addTask(task);

        task.setTitle("New Name");
        task.setDescription("New Description");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getAnyTask(task.getId());
        assertEquals("New Name", updatedTask.getTitle(), "Название задачи должно обновляться."); // Проверяем title
        assertEquals("New Description", updatedTask.getDescription(), "Описание задачи должно обновляться."); // Проверяем description
    }
}