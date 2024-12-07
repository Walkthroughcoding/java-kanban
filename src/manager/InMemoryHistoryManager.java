package manager;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    // Хранение задач по ID и узлов двусвязного списка
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task == null) return;

        // Удаляем задачу из истории, если она уже существует
        remove(task.getId());

        // Добавляем задачу в конец списка
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    // Добавляет задачу в конец двусвязного списка
    private void linkLast(Task task) {
        Node newNode = new Node(task, tail, null);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        historyMap.put(task.getId(), newNode);
    }

    // Удаляет узел из двусвязного списка
    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    // Вложенный класс для узла двусвязного списка
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }
}
