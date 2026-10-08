package seedu.address.storage;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.order.Order;

/**
 * Jackson-friendly version of {@link Order}
 */
public class JsonAdaptedOrder {

    private final String itemName;
    private final Integer quantity;
    private final String recordedAt;

    /**
     * Constructs a {@code JsonAdaptedOrder} with the given {@code itemName}, {@code quantity}, {@code recordedAt}.
     */
    @JsonCreator
    public JsonAdaptedOrder(
            @JsonProperty("itemName") String itemName,
            @JsonProperty("quantity") Integer quantity,
            @JsonProperty("recordedAt") String recordedAt) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.recordedAt = recordedAt;
    }

    /**
     *  Converts a given {@code Order} into this class for Jackson use
     */
    public JsonAdaptedOrder(Order source) {
        itemName = source.itemName;
        quantity = source.quantity;
        recordedAt = source.recordedAt.toString();
    }

    /**
     * Converts this Jackson-friendly adapted order object into the model's {@code Order} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted order.
     */
    public Order toModelType() throws IllegalValueException {
        if (!Order.isValidItemName(itemName)) {
            throw new IllegalValueException(Order.ITEM_NAME_CONSTRAINTS);
        }
        if (quantity == null || !Order.isValidQuantity(quantity)) {
            throw new IllegalValueException(Order.QUANTITY_CONSTRAINTS);
        }
        if (!Order.isValidRecordedAt(recordedAt)) {
            throw new IllegalValueException(Order.RECORDED_AT_CONSTRAINTS);
        }
        return new Order(itemName, quantity, Instant.parse(recordedAt));
    }
}
