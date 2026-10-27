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
import seedu.address.model.person.Person;

/** Displays a member's order history newest first, or an empty state when no member is selected. */
public class RecentOrdersPanel extends UiPart<Region> {
    private static final String FXML = "RecentOrdersPanel.fxml";

    @FXML
    private VBox orders;

    /**
     * Creates a recent orders panel for {@code person}, showing an empty message if they have no orders.
     *
     * @param person The member whose orders are shown.
     */
    public RecentOrdersPanel(Person person) {
        super(FXML);

        List<Order> recentOrders = person.getOrderHistory().getOrders();
        if (recentOrders.isEmpty()) {
            showEmptyMessage("No orders yet");
        } else {
            showRecentOrders(recentOrders);
        }
    }

    /** Creates a recent orders panel showing that no member is selected. */
    public RecentOrdersPanel() {
        super(FXML);
        showEmptyMessage("No member selected");
    }

    /**
     * Centers an empty-state message in the order list.
     *
     * @param message The message to display in place of order rows.
     */
    private void showEmptyMessage(String message) {
        Label emptyOrders = new Label(message);
        emptyOrders.getStyleClass().add("profile-empty-orders");
        orders.setAlignment(Pos.CENTER);
        orders.getChildren().add(emptyOrders);
    }

    /**
     * Adds order rows from newest to oldest, numbering them in displayed order.
     *
     * @param recentOrders The member's orders in oldest-to-newest order.
     */
    private void showRecentOrders(List<Order> recentOrders) {
        for (int i = recentOrders.size(); i >= 1; i--) {
            int displayedIndex = recentOrders.size() - i + 1;
            orders.getChildren().add(createOrderRow(displayedIndex, recentOrders.get(i - 1)));
        }
    }

    /**
     * Creates a row showing an order's index, saved item name, quantity, and points gained.
     *
     * @param index The order's one-based position in the member's history.
     * @param order The order to display.
     * @return The row for the given order.
     */
    private HBox createOrderRow(int index, Order order) {
        Label orderIndex = new Label(Integer.toString(index));
        orderIndex.getStyleClass().add("profile-order-index");

        Label itemName = new Label(order.itemName);
        itemName.setWrapText(true);
        itemName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(itemName, Priority.ALWAYS);

        Label quantity = new Label(Integer.toString(order.quantity));
        quantity.getStyleClass().add("profile-order-quantity");

        Label pointsGained = new Label("+" + order.scoreGained);
        pointsGained.getStyleClass().add("profile-order-points");

        HBox row = new HBox(orderIndex, itemName, quantity, pointsGained);
        row.getStyleClass().add("profile-order-row");
        return row;
    }
}
