package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.order.Order;

public class JsonAdaptedOrderTest {
    private static final String VALID_ITEM_NAME = "pancake";
    private static final Integer VALID_QUANTITY = 2;
    private static final String VALID_RECORDED_AT = "2026-01-01T00:00:00Z";

    @Test
    public void toModelType_validValues_returnsOrder() throws Exception {
        JsonAdaptedOrder adaptedOrder = new JsonAdaptedOrder(
                VALID_ITEM_NAME, VALID_QUANTITY, VALID_RECORDED_AT);

        assertEquals(
                new Order(VALID_ITEM_NAME, VALID_QUANTITY, Instant.parse(VALID_RECORDED_AT)),
                adaptedOrder.toModelType());
    }

    @Test
    public void constructor_fromOrder_roundTripsValues() throws Exception {
        Order source = new Order(VALID_ITEM_NAME, VALID_QUANTITY, Instant.parse(VALID_RECORDED_AT));

        assertEquals(source, new JsonAdaptedOrder(source).toModelType());
    }

    @Test
    public void toModelType_nullItemName_throwsIllegalValueException() {
        assertInvalidOrder(null, VALID_QUANTITY, VALID_RECORDED_AT, Order.ITEM_NAME_CONSTRAINTS);
    }

    @Test
    public void toModelType_blankItemName_throwsIllegalValueException() {
        assertInvalidOrder("  ", VALID_QUANTITY, VALID_RECORDED_AT, Order.ITEM_NAME_CONSTRAINTS);
    }

    @Test
    public void toModelType_nullQuantity_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_NAME, null, VALID_RECORDED_AT, Order.QUANTITY_CONSTRAINTS);
    }

    @Test
    public void toModelType_zeroQuantity_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_NAME, 0, VALID_RECORDED_AT, Order.QUANTITY_CONSTRAINTS);
    }

    @Test
    public void toModelType_negativeQuantity_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_NAME, -1, VALID_RECORDED_AT, Order.QUANTITY_CONSTRAINTS);
    }

    @Test
    public void toModelType_nullRecordedAt_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_NAME, VALID_QUANTITY, null, Order.RECORDED_AT_CONSTRAINTS);
    }

    @Test
    public void toModelType_malformedRecordedAt_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_NAME, VALID_QUANTITY, "not-a-timestamp", Order.RECORDED_AT_CONSTRAINTS);
    }

    @Test
    public void toModelType_multipleInvalidValues_reportsItemNameConstraintFirst() {
        assertInvalidOrder(null, null, null, Order.ITEM_NAME_CONSTRAINTS);
    }

    private static void assertInvalidOrder(
            String itemName, Integer quantity, String recordedAt, String expectedMessage) {
        JsonAdaptedOrder adaptedOrder = new JsonAdaptedOrder(itemName, quantity, recordedAt);

        assertThrows(IllegalValueException.class, expectedMessage, adaptedOrder::toModelType);
    }
}
