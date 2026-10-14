package ro.msg.learning.shop.exception;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException() {
        super("No location can fulfill all products in the required quantities.");
    }
}
