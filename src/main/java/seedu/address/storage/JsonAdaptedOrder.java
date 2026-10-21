package seedu.address.storage;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.order.Order;

/**
 * Jackson-friendly version of {@link Order}
 */
public class JsonAdaptedOrder {

    private final String itemCode;
    private final String itemName;
    private final Integer quantity;
    private final Integer scoreGained;
    private final String recordedAt;

    /**
     * Constructs a {@code JsonAdaptedOrder} with the given order details.
     */
    @JsonCreator
    public JsonAdaptedOrder(
            @JsonProperty("itemCode") String itemCode,
            @JsonProperty("itemName") String itemName,
            @JsonProperty("quantity") Integer quantity,
            @JsonProperty("scoreGained") Integer scoreGained,
            @JsonProperty("recordedAt") String recordedAt) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.scoreGained = scoreGained;
        this.recordedAt = recordedAt;
    }

    /**
     *  Converts a given {@code Order} into this class for Jackson use
     */
    public JsonAdaptedOrder(Order source) {
        itemCode = source.itemCode;
        itemName = source.itemName;
        quantity = source.quantity;
        scoreGained = source.scoreGained;
        recordedAt = source.recordedAt.toString();
    }

    /**
     * Converts this Jackson-friendly adapted order object into the model's {@code Order} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted order.
     */
    public Order toModelType() throws IllegalValueException {
        if (!FoodItem.isValidCode(itemCode)) {
            throw new IllegalValueException(Order.ITEM_CODE_CONSTRAINTS);
        }
        if (!FoodItem.isValidName(itemName)) {
            throw new IllegalValueException(Order.ITEM_NAME_CONSTRAINTS);
        }
        if (quantity == null || !Order.isValidQuantity(quantity)) {
            throw new IllegalValueException(Order.QUANTITY_CONSTRAINTS);
        }
        if (scoreGained != null && !Order.isValidScoreGained(scoreGained)) {
            throw new IllegalValueException(Order.SCORE_GAINED_CONSTRAINTS);
        }
        if (!Order.isValidRecordedAt(recordedAt)) {
            throw new IllegalValueException(Order.RECORDED_AT_CONSTRAINTS);
        }
        int restoredScoreGained = scoreGained == null ? 0 : scoreGained;
        return new Order(itemCode, itemName, quantity, restoredScoreGained, Instant.parse(recordedAt));
    }
}
