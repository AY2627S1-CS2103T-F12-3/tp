package seedu.address.model.order;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the history of orders made by a member.
 * Guarantees: The history is immutable; adding an order returns a new {@code OrderHistory}.
 */
public class OrderHistory {
    private final ArrayList<Order> orders;

    /**
     * Constructs an empty {@code OrderHistory}.
     */
    public OrderHistory() {
        this.orders = new ArrayList<>();
    }

    /**
     * Constructs an {@code OrderHistory} given a list of orders
     *
     * @param orders the orders to include into this new history
     */
    private OrderHistory(List<Order> orders) {
        this.orders = new ArrayList<>(orders);
    }

    /**
     * Returns a copy of all the orders.
     *
     * @return a list of orders in the history
     */
    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    /**
     * Returns a new history containing all existing orders and the given order.
     * This history is not modified.
     *
     * @param order the order to add
     * @return a new {@code OrderHistory} containing {@code order}
     * @throws NullPointerException if {@code order} is null
     */
    public OrderHistory addOrder(Order order) {
        ArrayList<Order> updatedOrders = new ArrayList<>(orders);
        updatedOrders.add(requireNonNull(order));
        return new OrderHistory(updatedOrders);
    }

    @Override
    public int hashCode() {
        return orders.toString().hashCode();
    }

    /**
     * Returns a string representation of this history's orders.
     *
     * @return a string representation of the orders
     */
    @Override
    public String toString() {
        return orders.toString();
    }
}
