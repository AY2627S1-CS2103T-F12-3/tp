package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import seedu.address.model.menu.FoodItem;

class MenuPanelTest {
    @org.junit.jupiter.api.BeforeAll
    static void initializeJavaFx() {
        JavaFxTestUtil.initializeJavaFx();
    }

    @Test
    void constructor_loadsMenuPanel() {
        JavaFxTestUtil.runOnFxThread(() -> {
            MenuPanel menuPanel = new MenuPanel(FXCollections.observableArrayList());
            VBox root = (VBox) menuPanel.getRoot();

            assertEquals("Menu", ((Label) root.getChildren().get(0)).getText());
            ListView<FoodItem> listView = (ListView<FoodItem>) root.getChildren().get(1);
            assertNotNull(listView.getCellFactory().call(listView));
        });
    }

    @Test
    void menuItemCell_nonEmptyItem_displaysCodeNameAndPrice() {
        JavaFxTestUtil.runOnFxThread(() -> {
            MenuPanel.MenuItemListViewCell cell = new MenuPanel.MenuItemListViewCell();
            cell.updateItem(new FoodItem("CR01", "Chicken Rice", 405), false);
            VBox item = (VBox) cell.getGraphic();

            assertEquals("CR01", ((Label) item.getChildren().get(0)).getText());
            assertEquals("Chicken Rice", ((Label) item.getChildren().get(1)).getText());
            assertEquals("$4.05", ((Label) item.getChildren().get(2)).getText());
        });
    }

    @Test
    void menuItemCell_emptyItem_clearsGraphicAndText() {
        JavaFxTestUtil.runOnFxThread(() -> {
            MenuPanel.MenuItemListViewCell cell = new MenuPanel.MenuItemListViewCell();
            cell.updateItem(null, true);

            assertNull(cell.getGraphic());
            assertNull(cell.getText());
        });
    }

    @Test
    void formatPrice_wholeAmount_formatsWithTwoDecimalPlaces() {
        assertEquals("$4.00", MenuPanel.formatPrice(400));
    }

    @Test
    void formatPrice_amountWithCents_preservesLeadingZero() {
        assertEquals("$4.05", MenuPanel.formatPrice(405));
    }

    @Test
    void formatPrice_largeAmount_formatsDollarsAndCents() {
        assertEquals("$123456.78", MenuPanel.formatPrice(12_345_678));
    }

}
