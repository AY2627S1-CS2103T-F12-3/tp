package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.menu.FoodItem;

/**
 * Jackson-friendly version of {@link FoodItem}.
 */
class JsonAdaptedFoodItem {
    private static final String MESSAGE_INVALID_FIELD_TYPE = "Menu item's %s must be %s.";
    private final String code;
    private final String name;
    private final Integer priceInCents;

    /**
     * Constructs an adapted food item with the given saved fields.
     */
    public JsonAdaptedFoodItem(String code, String name, Integer priceInCents) {
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
     * Reads JSON fields without allowing Jackson to truncate prices or coerce other types into strings.
     *
     * @throws IllegalValueException if a field has the wrong JSON type or the price exceeds the integer range.
     */
    @JsonCreator
    public static JsonAdaptedFoodItem fromJson(@JsonProperty("code") JsonNode code,
            @JsonProperty("name") JsonNode name, @JsonProperty("priceInCents") JsonNode priceInCents)
            throws IllegalValueException {
        if (code != null && !code.isNull() && !code.isTextual()) {
            throw new IllegalValueException(String.format(MESSAGE_INVALID_FIELD_TYPE, "code", "a JSON string"));
        }
        if (name != null && !name.isNull() && !name.isTextual()) {
            throw new IllegalValueException(String.format(MESSAGE_INVALID_FIELD_TYPE, "name", "a JSON string"));
        }
        Integer price = null;
        if (priceInCents != null && !priceInCents.isNull()) {
            if (!priceInCents.isIntegralNumber() || !priceInCents.canConvertToInt()) {
                throw new IllegalValueException(String.format(MESSAGE_INVALID_FIELD_TYPE,
                        "priceInCents", "a JSON integer within the 32-bit integer range"));
            }
            price = priceInCents.intValue();
        }
        return new JsonAdaptedFoodItem(code == null ? null : code.textValue(),
                name == null ? null : name.textValue(), price);
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
