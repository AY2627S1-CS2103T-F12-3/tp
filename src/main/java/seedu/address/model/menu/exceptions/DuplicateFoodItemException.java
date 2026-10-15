package seedu.address.model.menu.exceptions;

/**
 * Signals that an operation would result in food items with duplicate codes.
 */
public class DuplicateFoodItemException extends RuntimeException {
    public DuplicateFoodItemException() {
        super("Operation would result in duplicate food item codes");
    }
}
