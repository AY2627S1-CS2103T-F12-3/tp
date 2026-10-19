package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.menu.FoodItem;

/**
 * Jackson-friendly version of {@link FoodItem}.
 */
class JsonAdaptedFoodItem {
    private final String code;
    private final String name;
    private final Integer priceInCents;

    /**
     * Constructs an adapted food item with the given saved fields.
     */
    @JsonCreator
    public JsonAdaptedFoodItem(@JsonProperty("code") String code, @JsonProperty("name") String name,
            @JsonProperty("priceInCents") Integer priceInCents) {
        this.code = code;
        this.name = name;
        this.priceInCents = priceInCents;
    }

    /**
     * Converts a food item into this class for Jackson use, preserving its price in cents.
     */
    public JsonAdaptedFoodItem(FoodItem source) {
        requireNonNull(source);
        code = source.getCode();
        name = source.getName();
        priceInCents = source.getPriceInCents();
    }

    /**
     * Converts the saved fields into a food item.
     *
     * @throws IllegalValueException if a field is missing or violates the food item constraints.
     */
    public FoodItem toModelType() throws IllegalValueException {
        if (!FoodItem.isValidCode(code)) {
            throw new IllegalValueException(FoodItem.MESSAGE_CODE_CONSTRAINTS);
        }
        if (!FoodItem.isValidName(name)) {
            throw new IllegalValueException(FoodItem.MESSAGE_NAME_CONSTRAINTS);
        }
        if (priceInCents == null || !FoodItem.isValidPrice(priceInCents)) {
            throw new IllegalValueException(FoodItem.MESSAGE_PRICE_CONSTRAINTS);
        }
        return new FoodItem(code, name, priceInCents);
    }
}
