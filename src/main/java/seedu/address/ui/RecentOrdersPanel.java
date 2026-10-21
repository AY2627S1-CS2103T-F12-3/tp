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

/** Displays a member's orders with the newest order first. */
public class RecentOrdersPanel extends UiPart<Region> {
    private static final String FXML = "RecentOrdersPanel.fxml";

    @FXML
    private VBox orders;

    /**
     * Creates a recent orders panel for {@code person}.
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

    /** Creates a recent orders panel when no member is selected. */
    public RecentOrdersPanel() {
        super(FXML);
        showEmptyMessage("No member selected");
    }

    /** Shows the given message in place of an order list. */
    private void showEmptyMessage(String message) {
        Label emptyOrders = new Label(message);
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

    /** Creates a row showing an order's index, saved item name, quantity, and points gained. */
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
