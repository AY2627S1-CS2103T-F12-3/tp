package seedu.address.model.order;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.Instant;

/**
 * Represents a record of one item and its quantity bought by a member.
 * Guarantees: immutable; item name is non-blank, quantity is positive, and recorded time is present.
 */
public class Order {

    public static final String ITEM_NAME_CONSTRAINTS = "Item name cannot be blank";
    public static final String QUANTITY_CONSTRAINTS = "Quantity must be a positive whole number";
    public static final String RECORDED_AT_CONSTRAINTS = "timestamp must be non null";

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
        requireNonNull(recordedAt);
        checkArgument(isValidItemName(itemName), ITEM_NAME_CONSTRAINTS);
        checkArgument(isValidQuantity(quantity), QUANTITY_CONSTRAINTS);
        this.itemName = itemName;
        this.quantity = quantity;
        this.recordedAt = recordedAt;
    }

    /** Returns true if the given item name is non-blank. */
    public static boolean isValidItemName(String test) {
        return test != null && !test.isBlank();
    }

    /** Returns true if the given quantity is positive. */
    public static boolean isValidQuantity(int test) {
        return test > 0;
    }

    /** Returns true if the given recordedAt is not null. */
    public static boolean isValidRecordedAt(String test) {
        return test != null;
    }

    /** Formats state as text for viewing. */
    @Override
    public String toString() {
        return '[' + itemName + " x " + quantity + " at " + recordedAt + ']';
    }
}
