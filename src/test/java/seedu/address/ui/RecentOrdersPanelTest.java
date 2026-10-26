package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seedu.address.model.order.Order;
import seedu.address.model.order.OrderHistory;
import seedu.address.testutil.PersonBuilder;

class RecentOrdersPanelTest {
    @BeforeAll
    static void initializeJavaFx() {
        JavaFxTestUtil.initializeJavaFx();
    }

    @Test
    void constructor_personWithOrders_numbersRowsNewestFirst() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Order oldest = new Order("CR01", "Chicken Rice", 1, 10);
            Order newest = new Order("CF01", "Coffee", 1, 5);
            OrderHistory history = new OrderHistory().addOrder(oldest).addOrder(newest);
            RecentOrdersPanel panel = new RecentOrdersPanel(new PersonBuilder()
                    .withOrderHistory(history).build());

            VBox root = (VBox) panel.getRoot();
            VBox rows = (VBox) ((ScrollPane) root.getChildren().get(2)).getContent();
            List<HBox> orderRows = rows.getChildren().stream().map(HBox.class::cast).toList();

            assertEquals(List.of("1", "2"), orderRows.stream()
                    .map(row -> ((Label) row.getChildren().get(0)).getText()).toList());
            assertEquals(List.of("Coffee", "Chicken Rice"), orderRows.stream()
                    .map(row -> ((Label) row.getChildren().get(1)).getText()).toList());
        });
    }
}
