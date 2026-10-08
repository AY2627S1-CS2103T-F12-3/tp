package seedu.address.model.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

public class OrderHistoryTest {
    private static final Instant FIRST_RECORDED_AT = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant SECOND_RECORDED_AT = Instant.parse("2026-01-02T00:00:00Z");
    private static final Order FIRST_ORDER = new Order("pancake", 1, FIRST_RECORDED_AT);
    private static final Order SECOND_ORDER = new Order("coffee", 2, SECOND_RECORDED_AT);

    @Test
    public void constructor_emptyHistory_hasNoOrders() {
        assertTrue(new OrderHistory().getOrders().isEmpty());
    }

    @Test
    public void addOrder_validOrder_returnsNewHistoryAndPreservesOriginal() {
        OrderHistory original = new OrderHistory();
        OrderHistory updated = original.addOrder(FIRST_ORDER);

        assertTrue(original.getOrders().isEmpty());
        assertEquals(List.of(FIRST_ORDER), updated.getOrders());
        assertNotSame(original, updated);
    }

    @Test
    public void addOrder_multipleOrders_preservesInsertionOrder() {
        OrderHistory history = new OrderHistory()
                .addOrder(FIRST_ORDER)
                .addOrder(SECOND_ORDER);

        assertEquals(List.of(FIRST_ORDER, SECOND_ORDER), history.getOrders());
    }

    @Test
    public void addOrder_duplicateOrder_preservesBothEntries() {
        OrderHistory history = new OrderHistory()
                .addOrder(FIRST_ORDER)
                .addOrder(FIRST_ORDER);

        assertEquals(List.of(FIRST_ORDER, FIRST_ORDER), history.getOrders());
    }

    @Test
    public void addOrder_nullOrder_throwsNullPointerExceptionAndPreservesHistory() {
        OrderHistory history = new OrderHistory().addOrder(FIRST_ORDER);

        assertThrows(NullPointerException.class, () -> history.addOrder(null));
        assertEquals(List.of(FIRST_ORDER), history.getOrders());
    }

    @Test
    public void getOrders_mutateReturnedList_preservesHistory() {
        OrderHistory history = new OrderHistory().addOrder(FIRST_ORDER);
        List<Order> returnedOrders = history.getOrders();

        returnedOrders.clear();

        assertEquals(List.of(FIRST_ORDER), history.getOrders());
        assertNotSame(returnedOrders, history.getOrders());
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        OrderHistory history = new OrderHistory().addOrder(FIRST_ORDER);

        assertTrue(history.equals(history));
    }

    @Test
    public void equals_sameOrders_returnsTrue() {
        OrderHistory first = new OrderHistory().addOrder(FIRST_ORDER).addOrder(SECOND_ORDER);
        OrderHistory second = new OrderHistory()
                .addOrder(new Order("pancake", 1, FIRST_RECORDED_AT))
                .addOrder(new Order("coffee", 2, SECOND_RECORDED_AT));

        assertEquals(first, second);
    }

    @Test
    public void equals_differentOrderFields_returnsFalse() {
        OrderHistory original = new OrderHistory().addOrder(FIRST_ORDER);
        OrderHistory differentItemName = new OrderHistory()
                .addOrder(new Order("waffle", 1, FIRST_RECORDED_AT));
        OrderHistory differentQuantity = new OrderHistory()
                .addOrder(new Order("pancake", 3, FIRST_RECORDED_AT));
        OrderHistory differentRecordedAt = new OrderHistory()
                .addOrder(new Order("pancake", 1, SECOND_RECORDED_AT));

        assertFalse(original.equals(differentItemName));
        assertFalse(original.equals(differentQuantity));
        assertFalse(original.equals(differentRecordedAt));
    }

    @Test
    public void equals_differentOrderOrder_returnsFalse() {
        OrderHistory first = new OrderHistory().addOrder(FIRST_ORDER).addOrder(SECOND_ORDER);
        OrderHistory reversed = new OrderHistory().addOrder(SECOND_ORDER).addOrder(FIRST_ORDER);

        assertFalse(first.equals(reversed));
    }

    @Test
    public void equals_differentOrderCount_returnsFalse() {
        OrderHistory empty = new OrderHistory();
        OrderHistory oneOrder = empty.addOrder(FIRST_ORDER);

        assertFalse(empty.equals(oneOrder));
    }

    @Test
    public void equals_nullOrDifferentType_returnsFalse() {
        OrderHistory history = new OrderHistory();

        assertFalse(history.equals(null));
        assertFalse(history.equals("not an order history"));
    }

    @Test
    public void hashCode_equalHistories_returnsSameHashCode() {
        OrderHistory first = new OrderHistory().addOrder(FIRST_ORDER);
        OrderHistory second = new OrderHistory()
                .addOrder(new Order("pancake", 1, FIRST_RECORDED_AT));

        assertEquals(first.hashCode(), second.hashCode());
    }
}
