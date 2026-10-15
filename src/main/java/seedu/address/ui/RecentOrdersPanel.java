package seedu.address.ui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.order.Order;
import seedu.address.model.order.OrderHistory;

/** Displays a member's orders with the newest order first. */
public class RecentOrdersPanel extends UiPart<Region> {
    private static final String FXML = "RecentOrdersPanel.fxml";

    @FXML
    private VBox orders;

    /**
     * Creates a recent orders panel from {@code orderHistory}.
     *
     * @param orderHistory The member's order history.
     */
    public RecentOrdersPanel(OrderHistory orderHistory) {
        super(FXML);

        List<Order> recentOrders = orderHistory.getOrders();
        if (recentOrders.isEmpty()) {
            showNoOrdersMessage();
        } else {
            showRecentOrders(recentOrders);
        }
    }

    /** Shows a message when the member has no orders. */
    private void showNoOrdersMessage() {
        Label emptyOrders = new Label("No orders yet");
        emptyOrders.getStyleClass().add("profile-empty-orders");
        orders.setAlignment(Pos.CENTER);
        orders.getChildren().add(emptyOrders);
    }

    /** Shows the orders from newest to oldest. */
    private void showRecentOrders(List<Order> recentOrders) {
        for (int i = recentOrders.size(); i >= 1; i--) {
            orders.getChildren().add(createOrderRow(i, recentOrders.get(i - 1)));
        }
    }

    /** Creates a row showing an order's index, item name, and quantity. */
    private HBox createOrderRow(int index, Order order) {
        Label orderIndex = new Label(Integer.toString(index));
        orderIndex.getStyleClass().add("profile-order-index");

        Label itemName = new Label(order.itemName);
        itemName.setWrapText(true);
        itemName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(itemName, Priority.ALWAYS);

        Label quantity = new Label(Integer.toString(order.quantity));
        quantity.getStyleClass().add("profile-order-quantity");

        HBox row = new HBox(orderIndex, itemName, quantity);
        row.getStyleClass().add("profile-order-row");
        return row;
    }
}
