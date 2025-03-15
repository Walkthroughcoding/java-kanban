package manager.exceptions;

/**
 * Исключение для ситуации, когда задача пересекается по времени с другой задачей.
 */
public class TaskTimeConflictException extends Exception {
    public TaskTimeConflictException(String message) {
        super(message);
    }
}
