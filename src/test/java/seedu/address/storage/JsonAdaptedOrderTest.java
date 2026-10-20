package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.order.Order;

public class JsonAdaptedOrderTest {
    private static final String VALID_ITEM_CODE = "PC01";
    private static final String VALID_ITEM_NAME = "Pancake";
    private static final Integer VALID_QUANTITY = 2;
    private static final Integer VALID_SCORE_GAINED = 3;
    private static final String VALID_RECORDED_AT = "2026-01-01T00:00:00Z";

    @Test
    public void toModelType_validValues_returnsOrder() throws Exception {
        JsonAdaptedOrder adaptedOrder = new JsonAdaptedOrder(
                VALID_ITEM_CODE, VALID_ITEM_NAME, VALID_QUANTITY, VALID_SCORE_GAINED, VALID_RECORDED_AT);

        assertEquals(
                new Order(VALID_ITEM_CODE, VALID_ITEM_NAME, VALID_QUANTITY,
                        VALID_SCORE_GAINED, Instant.parse(VALID_RECORDED_AT)),
                adaptedOrder.toModelType());
    }

    @Test
    public void constructor_fromOrder_roundTripsValues() throws Exception {
        Order source = new Order(VALID_ITEM_CODE, VALID_ITEM_NAME, VALID_QUANTITY,
                VALID_SCORE_GAINED, Instant.parse(VALID_RECORDED_AT));

        assertEquals(source, new JsonAdaptedOrder(source).toModelType());
    }

    @Test
    public void constructor_fromOrder_serializesItemCodeAndName() throws Exception {
        Order source = new Order(VALID_ITEM_CODE, VALID_ITEM_NAME, VALID_QUANTITY,
                VALID_SCORE_GAINED, Instant.parse(VALID_RECORDED_AT));

        String json = JsonUtil.toJsonString(new JsonAdaptedOrder(source));

        assertTrue(json.contains("\"itemCode\" : \"PC01\""));
        assertTrue(json.contains("\"itemName\" : \"Pancake\""));
        assertTrue(json.contains("\"scoreGained\" : 3"));
    }

    @Test
    public void toModelType_nullItemCode_throwsIllegalValueException() {
        assertInvalidOrder(null, VALID_ITEM_NAME, VALID_QUANTITY, VALID_SCORE_GAINED,
                VALID_RECORDED_AT, Order.ITEM_CODE_CONSTRAINTS);
    }

    @Test
    public void toModelType_blankItemCode_throwsIllegalValueException() {
        assertInvalidOrder("  ", VALID_ITEM_NAME, VALID_QUANTITY, VALID_SCORE_GAINED,
                VALID_RECORDED_AT, Order.ITEM_CODE_CONSTRAINTS);
    }

    @Test
    public void toModelType_itemCodeWithSpecialCharacter_throwsIllegalValueException() {
        assertInvalidOrder("chicken a/asdad", VALID_ITEM_NAME, VALID_QUANTITY, VALID_SCORE_GAINED,
                VALID_RECORDED_AT, Order.ITEM_CODE_CONSTRAINTS);
    }

    @Test
    public void toModelType_missingItemName_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_CODE, null, VALID_QUANTITY, VALID_SCORE_GAINED,
                VALID_RECORDED_AT, Order.ITEM_NAME_CONSTRAINTS);
    }

    @Test
    public void toModelType_nullQuantity_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_CODE, VALID_ITEM_NAME, null, VALID_SCORE_GAINED,
                VALID_RECORDED_AT, Order.QUANTITY_CONSTRAINTS);
    }

    @Test
    public void toModelType_zeroQuantity_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_CODE, VALID_ITEM_NAME, 0, VALID_SCORE_GAINED,
                VALID_RECORDED_AT, Order.QUANTITY_CONSTRAINTS);
    }

    @Test
    public void toModelType_negativeQuantity_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_CODE, VALID_ITEM_NAME, -1, VALID_SCORE_GAINED,
                VALID_RECORDED_AT, Order.QUANTITY_CONSTRAINTS);
    }

    @Test
    public void toModelType_negativeScoreGained_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_CODE, VALID_ITEM_NAME, VALID_QUANTITY, -1,
                VALID_RECORDED_AT, Order.SCORE_GAINED_CONSTRAINTS);
    }

    @Test
    public void toModelType_missingScoreGained_defaultsToZero() throws Exception {
        JsonAdaptedOrder adaptedOrder = new JsonAdaptedOrder(
                VALID_ITEM_CODE, VALID_ITEM_NAME, VALID_QUANTITY, null, VALID_RECORDED_AT);

        assertEquals(0, adaptedOrder.toModelType().scoreGained);
    }

    @Test
    public void toModelType_nullRecordedAt_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_CODE, VALID_ITEM_NAME, VALID_QUANTITY,
                VALID_SCORE_GAINED, null, Order.RECORDED_AT_CONSTRAINTS);
    }

    @Test
    public void toModelType_malformedRecordedAt_throwsIllegalValueException() {
        assertInvalidOrder(VALID_ITEM_CODE, VALID_ITEM_NAME, VALID_QUANTITY, VALID_SCORE_GAINED,
                "not-a-timestamp", Order.RECORDED_AT_CONSTRAINTS);
    }

    @Test
    public void toModelType_multipleInvalidValues_reportsItemCodeConstraintFirst() {
        assertInvalidOrder(null, null, null, null, null, Order.ITEM_CODE_CONSTRAINTS);
    }

    private static void assertInvalidOrder(
            String itemCode, String itemName, Integer quantity, Integer scoreGained,
            String recordedAt, String expectedMessage) {
        JsonAdaptedOrder adaptedOrder = new JsonAdaptedOrder(itemCode, itemName, quantity, scoreGained, recordedAt);

        assertThrows(IllegalValueException.class, expectedMessage, adaptedOrder::toModelType);
    }
}
