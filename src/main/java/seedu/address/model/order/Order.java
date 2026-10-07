package seedu.address.model.order;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Represents an order in the address book.
 * Guarantees: immutable; item name and quantity are valid as declared in
 * {@link #isValidItemName(String)} and {@link #isValidQuantity(int)}, and recorded time is present.
 */
public class Order {

    public static final String ITEM_NAME_CONSTRAINTS = "Item name cannot be blank";
    public static final String QUANTITY_CONSTRAINTS = "Quantity must be a positive whole number";
    public static final String RECORDED_AT_CONSTRAINTS = "Timestamp must be a valid ISO-8601 instant";

    public final String itemName;
    public final int quantity;
    public final Instant recordedAt;

    /**
     * Constructs an {@code Order}.
     *
     * @param itemName Name of the item bought.
     * @param quantity Number of units bought; must be positive.
     */
    public Order(String itemName, int quantity) {
        this(itemName, quantity, Instant.now());
    }

    /**
     * Constructs an {@code Order} with the given recorded time.
     * This constructor is also useful when restoring an order from storage.
     *
     * @param itemName Name of the item bought.
     * @param quantity Number of units bought; must be positive.
     * @param recordedAt Time at which the order was recorded.
     */
    public Order(String itemName, int quantity, Instant recordedAt) {
        requireNonNull(itemName);
        requireNonNull(recordedAt, RECORDED_AT_CONSTRAINTS);
        checkArgument(isValidItemName(itemName), ITEM_NAME_CONSTRAINTS);
        checkArgument(isValidQuantity(quantity), QUANTITY_CONSTRAINTS);
        this.itemName = itemName;
        this.quantity = quantity;
        this.recordedAt = recordedAt;
    }

    /**
     * Returns true if a given string is a valid item name.
     */
    public static boolean isValidItemName(String test) {
        return test != null && !test.isBlank();
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
    public static boolean isValidRecordedAt(String recordedAt) {
        if (recordedAt == null) {
            return false;
        }
        try {
            Instant.parse(recordedAt);
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
        return itemName.equals(otherOrder.itemName)
                && quantity == otherOrder.quantity
                && recordedAt.equals(otherOrder.recordedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, quantity, recordedAt);
    }

    /**
     * Formats state as text for viewing.
     */
    @Override
    public String toString() {
        return '[' + itemName + " x " + quantity + " at " + recordedAt + ']';
    }
}
