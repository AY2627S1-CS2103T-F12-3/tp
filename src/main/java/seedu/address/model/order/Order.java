package seedu.address.model.order;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import seedu.address.model.menu.FoodItem;

/**
 * Represents an order in the address book.
 * Guarantees: immutable; item code and quantity are valid as declared in
 * {@link FoodItem#isValidCode(String)} and {@link #isValidQuantity(int)}; recorded time is present, and its string
 * representation is valid as declared in {@link #isValidRecordedAt(String)}.
 */
public class Order {

    public static final String ITEM_CODE_CONSTRAINTS = FoodItem.MESSAGE_CODE_CONSTRAINTS;
    public static final String QUANTITY_CONSTRAINTS = "Quantity must be a positive whole number";
    public static final String RECORDED_AT_CONSTRAINTS = "Timestamp must be a valid ISO-8601 instant";
    public final String itemCode;
    public final int quantity;
    public final Instant recordedAt;

    /**
     * Constructs an {@code Order}.
     *
     * @param itemCode Code of the menu item bought.
     * @param quantity Number of units bought; must be positive.
     */
    public Order(String itemCode, int quantity) {
        this(itemCode, quantity, Instant.now());
    }

    /**
     * Constructs an {@code Order} with the given recorded time.
     * This constructor is also useful when restoring an order from storage.
     *
     * @param itemCode Code of the menu item bought.
     * @param quantity Number of units bought; must be positive.
     * @param recordedAt Time at which the order was recorded.
     */
    public Order(String itemCode, int quantity, Instant recordedAt) {
        requireNonNull(itemCode);
        requireNonNull(recordedAt, RECORDED_AT_CONSTRAINTS);
        checkArgument(FoodItem.isValidCode(itemCode), ITEM_CODE_CONSTRAINTS);
        checkArgument(isValidQuantity(quantity), QUANTITY_CONSTRAINTS);
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.recordedAt = recordedAt;
    }

    /**
     * Returns true if a given quantity is positive.
     */
    public static boolean isValidQuantity(int test) {
        return test > 0;
    }

    /**
     * Returns true if a given recorded time is a valid ISO-8601 instant.
     */
    public static boolean isValidRecordedAt(String test) {
        if (test == null) {
            return false;
        }
        try {
            Instant.parse(test);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Order otherOrder)) {
            return false;
        }
        return itemCode.equals(otherOrder.itemCode)
                && quantity == otherOrder.quantity
                && recordedAt.equals(otherOrder.recordedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemCode, quantity, recordedAt);
    }

    /**
     * Formats state as text for viewing.
     */
    @Override
    public String toString() {
        return '[' + itemCode + " x " + quantity + " at " + recordedAt + ']';
    }
}
