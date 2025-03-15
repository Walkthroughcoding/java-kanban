package manager;

import model.*;
import model.enums.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        task.setId(1);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "Задача должна быть добавлена в историю.");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description", StatusEnum.NEW, Duration.ofMinutes(60), LocalDateTime.now());

        Task task2 = new Task("Task 1", "Description", StatusEnum.NEW, Duration.ofMinutes(60), LocalDateTime.now());

        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления.");
        assertEquals(task2, history.get(0), "Оставшаяся задача должна быть корректной.");
    }

    @Test
    void shouldNotLimitHistorySize() {
        for (int i = 0; i < 15; i++) {
            Task task = new Task("Task " + i, "Description " + i, StatusEnum.NEW,
                    Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(i * 10));
            task.setId(i);
            historyManager.add(task);
        }
        assertEquals(15, historyManager.getHistory().size(), "История должна содержать 15 задач.");
    }
}