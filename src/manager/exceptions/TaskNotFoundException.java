package manager.exceptions;

/**
 * Исключение, если задача или подзадача не найдена.
 */
public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
