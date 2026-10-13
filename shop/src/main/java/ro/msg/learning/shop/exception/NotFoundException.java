package ro.msg.learning.shop.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String resourceName) {
        super(resourceName + " not found");
    }
}
