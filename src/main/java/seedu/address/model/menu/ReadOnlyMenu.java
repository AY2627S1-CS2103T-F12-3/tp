package seedu.address.model.menu;

import javafx.collections.ObservableList;

/**
 * Provides read-only access to a menu.
 */
public interface ReadOnlyMenu {
    /**
     * Returns an unmodifiable observable view of the food items in insertion order.
     * Items have unique case-sensitive codes, and the view reflects subsequent menu changes.
     */
    ObservableList<FoodItem> getFoodItemList();
}
