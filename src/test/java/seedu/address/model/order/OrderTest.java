package seedu.address.model.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class OrderTest {
    private static final Instant VALID_RECORDED_AT = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    public void isValidItemName_validName_returnsTrue() {
        assertTrue(Order.isValidItemName("pancake"));
    }

    @Test
    public void isValidItemName_invalidNames_returnsFalse() {
        assertFalse(Order.isValidItemName(""));
        assertFalse(Order.isValidItemName("  "));
        assertFalse(Order.isValidItemName(null));
    }

    @Test
    public void isValidQuantity_positiveQuantity_returnsTrue() {
        assertTrue(Order.isValidQuantity(1));
        assertTrue(Order.isValidQuantity(10));
    }

    @Test
    public void isValidQuantity_nonPositiveQuantities_returnsFalse() {
        assertFalse(Order.isValidQuantity(0));
        assertFalse(Order.isValidQuantity(-1));
    }

    @Test
    public void isValidRecordedAt_validTimestamp_returnsTrue() {
        assertTrue(Order.isValidRecordedAt(VALID_RECORDED_AT.toString()));
    }

    @Test
    public void isValidRecordedAt_invalidTimestamps_returnsFalse() {
        assertFalse(Order.isValidRecordedAt("not-a-timestamp"));
        assertFalse(Order.isValidRecordedAt(null));
    }

    @Test
    public void constructor_validItemNameAndQuantity_setsRecordedAt() {
        Order order = new Order("pancake", 1);

        assertEquals("pancake", order.itemName);
        assertEquals(1, order.quantity);
        assertTrue(Order.isValidRecordedAt(order.recordedAt.toString()));
    }

    @Test
    public void constructor_validFields_createsOrder() {
        Order order = new Order("pancake", 1, VALID_RECORDED_AT);

        assertEquals("pancake", order.itemName);
        assertEquals(1, order.quantity);
        assertEquals(VALID_RECORDED_AT, order.recordedAt);
    }

    @Test
    public void constructor_nullItemName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Order(null, 1));
    }

    @Test
    public void constructor_nullItemNameWithTimestamp_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Order(null, 1, VALID_RECORDED_AT));
    }

    @Test
    public void constructor_negativeQuantity_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Order("pancake", -1));
    }

    @Test
    public void constructor_blankItemName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Order("  ", 1, VALID_RECORDED_AT));
    }

    @Test
    public void constructor_zeroQuantity_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Order("pancake", 0, VALID_RECORDED_AT));
    }

    @Test
    public void constructor_nullRecordedAt_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Order("pancake", 1, null));
    }
}
