package seedu.address.model.order;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import seedu.address.model.menu.FoodItem;

/**
 * Represents an order in the address book.
 * Guarantees: immutable, item code, saved name, quantity, and score gained are valid as declared in
 * {@link FoodItem#isValidCode(String)}, {@link FoodItem#isValidName(String)}, and
 * {@link #isValidQuantity(int)} and {@link #isValidScoreGained(int)}; recorded time is present, and its string
 * representation is valid as declared in {@link #isValidRecordedAt(String)}.
 */
public class Order {
    public static final String ITEM_CODE_CONSTRAINTS = FoodItem.MESSAGE_CODE_CONSTRAINTS;
    public static final String ITEM_NAME_CONSTRAINTS = FoodItem.MESSAGE_NAME_CONSTRAINTS;
    public static final String QUANTITY_CONSTRAINTS = "Quantity must be a positive whole number";
    public static final String SCORE_GAINED_CONSTRAINTS = "Score gained must be a non-negative whole number";
    public static final String RECORDED_AT_CONSTRAINTS = "Timestamp must be a valid ISO-8601 instant";
    public static final int CENTS_PER_POINT = 10;

    public final String itemCode;
    public final String itemName;
    public final int quantity;
    public final int scoreGained;
    public final Instant recordedAt;

    /**
     * Constructs an {@code Order}.
     *
     * @param itemCode Code of the menu item bought.
     * @param itemName Name of the menu item when the order was recorded.
     * @param quantity Number of units bought; must be positive.
     */
    public Order(String itemCode, String itemName, int quantity) {
        this(itemCode, itemName, quantity, 0, Instant.now());
    }

    /**
     * Constructs an order with the score gained from this purchase.
     */
    public Order(String itemCode, String itemName, int quantity, int scoreGained) {
        this(itemCode, itemName, quantity, scoreGained, Instant.now());
    }

    /**
     * Constructs an {@code Order} with the given recorded time.
     * This constructor is also useful when restoring an order from storage.
     *
     * @param itemCode Code of the menu item bought.
     * @param itemName Name of the menu item when the order was recorded.
     * @param quantity Number of units bought; must be positive.
     * @param recordedAt Time at which the order was recorded.
     */
    public Order(String itemCode, String itemName, int quantity, Instant recordedAt) {
        this(itemCode, itemName, quantity, 0, recordedAt);
    }

    /**
     * Constructs an order with its recorded time and score gained.
     */
    public Order(String itemCode, String itemName, int quantity, int scoreGained, Instant recordedAt) {
        requireNonNull(itemCode);
        requireNonNull(itemName);
        requireNonNull(recordedAt, RECORDED_AT_CONSTRAINTS);
        checkArgument(FoodItem.isValidCode(itemCode), ITEM_CODE_CONSTRAINTS);
        checkArgument(FoodItem.isValidName(itemName), ITEM_NAME_CONSTRAINTS);
        checkArgument(isValidQuantity(quantity), QUANTITY_CONSTRAINTS);
        checkArgument(isValidScoreGained(scoreGained), SCORE_GAINED_CONSTRAINTS);
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.scoreGained = scoreGained;
        this.recordedAt = recordedAt;
    }

    /**
     * Returns true if a given quantity is positive.
     */
    public static boolean isValidQuantity(int test) {
        return test > 0;
    }

    /**
     * Returns true if the score gained is non-negative.
     */
    public static boolean isValidScoreGained(int test) {
        return test >= 0;
    }

    /**
     * Returns the whole points earned at ten points per dollar, rounding down fractional points.
     */
    public static long calculateScoreGained(int priceInCents, int quantity) {
        checkArgument(FoodItem.isValidPrice(priceInCents), FoodItem.MESSAGE_PRICE_CONSTRAINTS);
        checkArgument(isValidQuantity(quantity), QUANTITY_CONSTRAINTS);
        return (long) priceInCents * quantity / CENTS_PER_POINT;
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

    /**
     * Formats the given instant as a day, full month, and year.
     */
    private static String formatTime(Instant time) {
        return time.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("d MMMM uuuu"));
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
                && itemName.equals(otherOrder.itemName)
                && quantity == otherOrder.quantity
                && scoreGained == otherOrder.scoreGained
                && recordedAt.equals(otherOrder.recordedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemCode, itemName, quantity, scoreGained, recordedAt);
    }

    /**
     * Formats state as text for viewing.
     */
    @Override
    public String toString() {
        return "[ (" + itemCode + ") " + itemName + " x " + quantity
                + ", +" + scoreGained + " score, on " + formatTime(recordedAt) + "]";
    }

}
