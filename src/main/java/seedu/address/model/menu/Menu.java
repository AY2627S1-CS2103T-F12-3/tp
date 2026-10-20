package seedu.address.model.menu;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Stores the menu's food items, enforcing unique case-sensitive item codes.
 */
public class Menu implements ReadOnlyMenu {
    private final UniqueFoodItemList foodItems = new UniqueFoodItemList();

    /**
     * Creates an empty menu.
     */
    public Menu() {}

    /**
     * Creates a menu containing a copy of the items in {@code toBeCopied}.
     * Subsequent changes to either menu do not affect the other.
     */
    public Menu(ReadOnlyMenu toBeCopied) {
        resetData(toBeCopied);
    }

    /**
     * Replaces the contents with a copy of {@code foodItems}, preserving their order.
     * The list and its items must not be null, and item codes must be unique.
     * Invalid input leaves this menu unchanged.
     */
    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems.setFoodItems(foodItems);
    }

    /**
     * Replaces this menu's contents with a copy of the items in {@code newData}.
     * Invalid input leaves this menu unchanged.
     */
    public void resetData(ReadOnlyMenu newData) {
        requireNonNull(newData);
        setFoodItems(newData.getFoodItemList());
    }

    /**
     * Returns true if an item with the same code as {@code foodItem} exists in the menu.
     */
    public boolean hasFoodItem(FoodItem foodItem) {
        return foodItems.contains(foodItem);
    }

    /**
     * Finds an item by its case-sensitive code.
     */
    public Optional<FoodItem> findFoodItemByCode(String code) {
        requireNonNull(code);
        return getFoodItemList().stream()
                .filter(item -> item.getCode().equals(code))
                .findFirst();
    }

    /**
     * Adds an item to the menu. Its code must not already exist in the menu.
     */
    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
    }

    /**
     * Replaces {@code target} in place with {@code editedFoodItem}.
     * The target must equal an existing item, and the new code must not belong to another item.
     */
    public void setFoodItem(FoodItem target, FoodItem editedFoodItem) {
        foodItems.setFoodItem(target, editedFoodItem);
    }

    /**
     * Removes the item equal to {@code foodItem}, which must exist in the menu.
     */
    public void removeFoodItem(FoodItem foodItem) {
        foodItems.remove(foodItem);
    }

    @Override
    public ObservableList<FoodItem> getFoodItemList() {
        return foodItems.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Menu otherMenu)) {
            return false;
        }
        return foodItems.equals(otherMenu.foodItems);
    }

    @Override
    public int hashCode() {
        return foodItems.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("foodItems", foodItems)
                .toString();
    }
}
