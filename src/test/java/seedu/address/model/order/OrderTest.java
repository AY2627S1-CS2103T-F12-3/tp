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
    public void calculateScoreGained_priceAndQuantity_roundsDownFractionalPoints() {
        assertEquals(45, Order.calculateScoreGained(450, 1));
        assertEquals(90, Order.calculateScoreGained(450, 2));
        assertEquals(12, Order.calculateScoreGained(125, 1));
        assertEquals(0, Order.calculateScoreGained(1, 1));
    }

    @Test
    public void constructor_negativeScoreGained_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Order("PC01", "Pancake", 1, -1));
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
    public void constructor_validItemCodeAndQuantity_setsRecordedAt() {
        Order order = new Order("PC01", "Pancake", 1);

        assertEquals("PC01", order.itemCode);
        assertEquals("Pancake", order.itemName);
        assertEquals(1, order.quantity);
        assertTrue(Order.isValidRecordedAt(order.recordedAt.toString()));
    }

    @Test
    public void constructor_validFields_createsOrder() {
        Order order = new Order("PC01", "Pancake", 1, VALID_RECORDED_AT);

        assertEquals("PC01", order.itemCode);
        assertEquals("Pancake", order.itemName);
        assertEquals(1, order.quantity);
        assertEquals(VALID_RECORDED_AT, order.recordedAt);
    }

    @Test
    public void equals_sameCodeDifferentSavedName_returnsFalse() {
        Order original = new Order("PC01", "Pancake", 1, VALID_RECORDED_AT);
        Order renamed = new Order("PC01", "Blueberry Pancake", 1, VALID_RECORDED_AT);

        assertFalse(original.equals(renamed));
    }

    @Test
    public void constructor_nullItemCode_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Order(null, "Pancake", 1));
    }

    @Test
    public void constructor_nullItemCodeWithTimestamp_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Order(null, "Pancake", 1, VALID_RECORDED_AT));
    }

    @Test
    public void constructor_nullItemName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Order("PC01", null, 1));
    }

    @Test
    public void constructor_negativeQuantity_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Order("PC01", "Pancake", -1));
    }

    @Test
    public void constructor_blankItemCode_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Order("  ", "Pancake", 1, VALID_RECORDED_AT));
    }

    @Test
    public void constructor_blankItemName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Order("PC01", "  ", 1));
    }

    @Test
    public void constructor_zeroQuantity_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Order("PC01", "Pancake", 0, VALID_RECORDED_AT));
    }

    @Test
    public void constructor_nullRecordedAt_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Order("PC01", "Pancake", 1, null));
    }
}
