package ro.msg.learning.shop.exception;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message) {
        super(message + " already exists.");
    }
}
