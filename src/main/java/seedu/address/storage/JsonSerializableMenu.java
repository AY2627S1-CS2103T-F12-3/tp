package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;
import seedu.address.model.menu.ReadOnlyMenu;

/**
 * A snapshot of a menu that is serializable to JSON format.
 */
@JsonRootName(value = "menu")
class JsonSerializableMenu {
    public static final String MESSAGE_MISSING_FOOD_ITEMS = "Menu's foodItems field is missing or null.";
    public static final String MESSAGE_NULL_FOOD_ITEM = "Menu contains a null food item.";
    public static final String MESSAGE_DUPLICATE_FOOD_ITEM = "Menu contains duplicate food item codes.";

    private final List<JsonAdaptedFoodItem> foodItems;

    /**
     * Constructs a saved menu from the given items, copying the list.
     * Missing data is retained for validation by {@link #toModelType()}.
     */
    @JsonCreator
    public JsonSerializableMenu(@JsonProperty("foodItems") List<JsonAdaptedFoodItem> foodItems) {
        this.foodItems = foodItems == null ? null : new ArrayList<>(foodItems);
    }

    /**
     * Converts a read-only menu into a snapshot for Jackson use.
     * Subsequent changes to the source do not affect this snapshot.
     */
    public JsonSerializableMenu(ReadOnlyMenu source) {
        requireNonNull(source);
        foodItems = source.getFoodItemList().stream().map(JsonAdaptedFoodItem::new).collect(Collectors.toList());
    }

    /**
     * Reconstructs a menu, preserving item order and case-sensitive codes.
     *
     * @throws IllegalValueException if the item list is missing, an item is invalid, or codes are duplicated.
     */
    public Menu toModelType() throws IllegalValueException {
        if (foodItems == null) {
            throw new IllegalValueException(MESSAGE_MISSING_FOOD_ITEMS);
        }
        Menu menu = new Menu();
        for (JsonAdaptedFoodItem adaptedItem : foodItems) {
            if (adaptedItem == null) {
                throw new IllegalValueException(MESSAGE_NULL_FOOD_ITEM);
            }
            FoodItem foodItem = adaptedItem.toModelType();
            if (menu.hasFoodItem(foodItem)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_FOOD_ITEM);
            }
            menu.addFoodItem(foodItem);
        }
        return menu;
    }
}
