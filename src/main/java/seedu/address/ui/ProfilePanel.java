package seedu.address.ui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.order.Order;
import seedu.address.model.person.Person;

/** Displays the selected person's details and recent orders. */
public class ProfilePanel extends UiPart<Region> {
    private static final String FXML = "ProfilePanel.fxml";

    @FXML
    private Label name;

    @FXML
    private Label phone;

    @FXML
    private Label score;

    @FXML
    private VBox orders;

    /** Creates a profile panel for {@code person}. */
    public ProfilePanel(Person person) {
        super(FXML);
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        score.setText("Current score: " + person.getScore());

        List<Order> recentOrders = person.getOrderHistory().getOrders();
        if (recentOrders.isEmpty()) {
            Label emptyOrders = new Label("No orders yet");
            emptyOrders.getStyleClass().add("profile-empty-orders");
            orders.getChildren().add(emptyOrders);
        } else {
            recentOrders.stream()
                    .map(this::createOrderRow)
                    .forEach(orders.getChildren()::add);
        }
    }

    private HBox createOrderRow(Order order) {
        Label itemName = new Label(order.itemName);
        itemName.setWrapText(true);
        itemName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(itemName, Priority.ALWAYS);

        Label quantity = new Label(Integer.toString(order.quantity));
        quantity.getStyleClass().add("profile-order-quantity");

        HBox row = new HBox(itemName, quantity);
        row.getStyleClass().add("profile-order-row");
        return row;
    }
}
