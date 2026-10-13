package seedu.address.model.menu;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents an immutable food item with an identifying code, name, and price in cents.
 * Codes are case-sensitive and alphanumeric; names are not blank; prices are positive.
 */
public final class FoodItem {
    public static final String MESSAGE_CODE_CONSTRAINTS = "Item codes must be alphanumeric.";
    public static final String MESSAGE_NAME_CONSTRAINTS = "Item names must not be blank.";
    public static final String MESSAGE_PRICE_CONSTRAINTS = "Prices must be positive.";
    private static final String CODE_VALIDATION_REGEX = "[A-Za-z0-9]+";

    private final String code;
    private final String name;
    private final int priceInCents;

    /**
     * Constructs a food item with the given code, name, and price.
     *
     * @param code A case-sensitive alphanumeric item code.
     * @param name A nonblank item name.
     * @param priceInCents A positive price in cents.
     * @throws NullPointerException if {@code code} or {@code name} is null.
     * @throws IllegalArgumentException if a field violates its constraints.
     */
    public FoodItem(String code, String name, int priceInCents) {
        requireAllNonNull(code, name);
        checkArgument(isValidCode(code), MESSAGE_CODE_CONSTRAINTS);
        checkArgument(isValidName(name), MESSAGE_NAME_CONSTRAINTS);
        checkArgument(isValidPrice(priceInCents), MESSAGE_PRICE_CONSTRAINTS);
        this.code = code;
        this.name = name;
        this.priceInCents = priceInCents;
    }

    /**
     * Returns true if the given code contains one or more ASCII letters or digits.
     */
    public static boolean isValidCode(String code) {
        return code != null && code.matches(CODE_VALIDATION_REGEX);
    }

    /**
     * Returns true if the given name is neither null nor blank.
     */
    public static boolean isValidName(String name) {
        return name != null && !name.isBlank();
    }

    /**
     * Returns true if the given price in cents is positive.
     */
    public static boolean isValidPrice(int priceInCents) {
        return priceInCents > 0;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getPriceInCents() {
        return priceInCents;
    }

    /**
     * Returns true if both food items have the same case-sensitive code.
     * This compares identity independently of the name and price.
     */
    public boolean isSameFoodItem(FoodItem otherFoodItem) {
        return otherFoodItem != null && code.equals(otherFoodItem.code);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FoodItem otherFoodItem)) {
            return false;
        }
        return code.equals(otherFoodItem.code)
                && name.equals(otherFoodItem.name)
                && priceInCents == otherFoodItem.priceInCents;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, priceInCents);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("code", code)
                .add("name", name)
                .add("priceInCents", priceInCents)
                .toString();
    }
}
