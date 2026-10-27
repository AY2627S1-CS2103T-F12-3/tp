package seedu.address.ui;

import java.util.Locale;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.menu.FoodItem;

/** Displays all food items currently available in the menu. */
public class MenuPanel extends UiPart<Region> {
    private static final String FXML = "MenuPanel.fxml";

    @FXML
    private ListView<FoodItem> menuListView;

    /** Creates a menu panel backed by the given observable list. */
    public MenuPanel(ObservableList<FoodItem> foodItems) {
        super(FXML);
        menuListView.setItems(foodItems);
        menuListView.setPlaceholder(new Label("No menu items"));
        menuListView.setCellFactory(listView -> new MenuItemListViewCell());
    }

    /** Custom list cell that displays the code, name, and price of a food item. */
    static class MenuItemListViewCell extends ListCell<FoodItem> {
        @Override
        protected void updateItem(FoodItem foodItem, boolean empty) {
            super.updateItem(foodItem, empty);

            if (empty || foodItem == null) {
                createEmptyRow();
                return;
            }

            createFilledRow(foodItem);
        }

        /** Clears the cell when it does not represent a menu item. */
        private void createEmptyRow() {
            setGraphic(null);
            setText(null);
        }

        /** Creates the cell content for a populated menu item. */
        private void createFilledRow(FoodItem foodItem) {
            Label code = new Label(foodItem.getCode());
            code.getStyleClass().add("menu-item-code");
            code.setTextOverrun(OverrunStyle.ELLIPSIS);
            code.setWrapText(false);

            Label name = new Label(foodItem.getName());
            name.getStyleClass().add("menu-item-name");
            name.setTextOverrun(OverrunStyle.ELLIPSIS);
            name.setWrapText(false);

            Label price = new Label(formatPrice(foodItem.getPriceInCents()));
            price.getStyleClass().add("menu-item-price");

            VBox item = new VBox(code, name, price);
            item.getStyleClass().add("menu-item");
            item.prefWidthProperty().bind(widthProperty().subtract(20));
            setGraphic(item);
        }
    }

    /** Formats a price stored as cents for display. */
    static String formatPrice(int priceInCents) {
        return String.format(Locale.ROOT, "$%d.%02d", priceInCents / 100, priceInCents % 100);
    }
}
