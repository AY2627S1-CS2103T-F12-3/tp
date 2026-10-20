package seedu.address.model.menu;

import java.util.Optional;

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

    /** Returns whether an item with the same code exists in the menu. */
    boolean hasFoodItem(FoodItem foodItem);

    /** Finds an item by its case-sensitive code. */
    Optional<FoodItem> findFoodItemByCode(String code);
}
