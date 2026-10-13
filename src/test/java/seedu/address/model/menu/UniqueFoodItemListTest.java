package seedu.address.model.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.model.menu.exceptions.DuplicateFoodItemException;
import seedu.address.model.menu.exceptions.FoodItemNotFoundException;

public class UniqueFoodItemListTest {
    private static final FoodItem CHICKEN_RICE = new FoodItem("CR01", "Chicken Rice", 450);
    private static final FoodItem EDITED_CHICKEN_RICE = new FoodItem("CR01", "Roasted Chicken Rice", 500);
    private static final FoodItem COFFEE = new FoodItem("CF01", "Coffee", 150);

    private final UniqueFoodItemList foodItems = new UniqueFoodItemList();

    @Test
    public void contains_nullFoodItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> foodItems.contains(null));
    }

    @Test
    public void contains_comparesCodes() {
        assertFalse(foodItems.contains(CHICKEN_RICE));
        foodItems.add(CHICKEN_RICE);
        assertTrue(foodItems.contains(CHICKEN_RICE));
        assertTrue(foodItems.contains(EDITED_CHICKEN_RICE));
        assertFalse(foodItems.contains(COFFEE));
        assertFalse(foodItems.contains(new FoodItem("cr01", "Chicken Rice", 450)));
    }

    @Test
    public void add_nullFoodItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> foodItems.add(null));
        assertTrue(foodItems.asUnmodifiableObservableList().isEmpty());
    }

    @Test
    public void add_uniqueFoodItems_preservesInsertionOrder() {
        foodItems.add(CHICKEN_RICE);
        foodItems.add(COFFEE);
        assertEquals(List.of(CHICKEN_RICE, COFFEE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void add_duplicateCode_throwsAndPreservesList() {
        foodItems.add(CHICKEN_RICE);
        assertThrows(DuplicateFoodItemException.class, () -> foodItems.add(CHICKEN_RICE));
        assertThrows(DuplicateFoodItemException.class, () -> foodItems.add(EDITED_CHICKEN_RICE));
        assertEquals(List.of(CHICKEN_RICE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void setFoodItem_nullArguments_throwsAndPreservesList() {
        foodItems.add(CHICKEN_RICE);
        assertThrows(NullPointerException.class, () -> foodItems.setFoodItem(null, COFFEE));
        assertThrows(NullPointerException.class, () -> foodItems.setFoodItem(CHICKEN_RICE, null));
        assertEquals(List.of(CHICKEN_RICE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void setFoodItem_missingOrUnequalTarget_throwsAndPreservesList() {
        foodItems.add(CHICKEN_RICE);
        assertThrows(FoodItemNotFoundException.class, () -> foodItems.setFoodItem(COFFEE, COFFEE));
        assertThrows(FoodItemNotFoundException.class, () -> foodItems.setFoodItem(EDITED_CHICKEN_RICE, COFFEE));
        assertEquals(List.of(CHICKEN_RICE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void setFoodItem_sameCode_updatesDetailsInPlace() {
        foodItems.setFoodItems(List.of(CHICKEN_RICE, COFFEE));
        foodItems.setFoodItem(CHICKEN_RICE, EDITED_CHICKEN_RICE);
        assertEquals(List.of(EDITED_CHICKEN_RICE, COFFEE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void setFoodItem_newCode_replacesItem() {
        foodItems.add(CHICKEN_RICE);
        foodItems.setFoodItem(CHICKEN_RICE, COFFEE);
        assertEquals(List.of(COFFEE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void setFoodItem_conflictingCode_throwsAndPreservesList() {
        foodItems.setFoodItems(List.of(CHICKEN_RICE, COFFEE));
        FoodItem editedCoffee = new FoodItem("CF01", "Iced Coffee", 200);
        assertThrows(DuplicateFoodItemException.class, () -> foodItems.setFoodItem(CHICKEN_RICE, editedCoffee));
        assertEquals(List.of(CHICKEN_RICE, COFFEE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void remove_nullFoodItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> foodItems.remove(null));
    }

    @Test
    public void remove_missingOrUnequalItem_throwsAndPreservesList() {
        foodItems.add(CHICKEN_RICE);
        assertThrows(FoodItemNotFoundException.class, () -> foodItems.remove(COFFEE));
        assertThrows(FoodItemNotFoundException.class, () -> foodItems.remove(EDITED_CHICKEN_RICE));
        assertEquals(List.of(CHICKEN_RICE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void remove_equalItem_removesOnlyTarget() {
        foodItems.setFoodItems(List.of(CHICKEN_RICE, COFFEE));
        foodItems.remove(new FoodItem("CR01", "Chicken Rice", 450));
        assertEquals(List.of(COFFEE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void setFoodItems_nullListOrElement_throwsAndPreservesList() {
        foodItems.add(CHICKEN_RICE);
        assertThrows(NullPointerException.class, () -> foodItems.setFoodItems((List<FoodItem>) null));
        assertThrows(NullPointerException.class, () -> foodItems.setFoodItems((UniqueFoodItemList) null));
        assertThrows(NullPointerException.class, () -> foodItems.setFoodItems(Arrays.asList(COFFEE, null)));
        assertEquals(List.of(CHICKEN_RICE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void setFoodItems_duplicateCodes_throwsAndPreservesList() {
        foodItems.add(COFFEE);
        assertThrows(DuplicateFoodItemException.class, () -> {
            foodItems.setFoodItems(List.of(CHICKEN_RICE, COFFEE, EDITED_CHICKEN_RICE));
        });
        assertEquals(List.of(COFFEE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void setFoodItems_validList_copiesContentsAndSupportsEmptyList() {
        foodItems.add(CHICKEN_RICE);
        List<FoodItem> replacement = new ArrayList<>(List.of(COFFEE));
        foodItems.setFoodItems(replacement);
        replacement.clear();
        assertEquals(List.of(COFFEE), foodItems.asUnmodifiableObservableList());
        foodItems.setFoodItems(List.of());
        assertTrue(foodItems.asUnmodifiableObservableList().isEmpty());
    }

    @Test
    public void setFoodItems_uniqueList_copiesContents() {
        UniqueFoodItemList replacement = new UniqueFoodItemList();
        replacement.add(COFFEE);
        foodItems.add(CHICKEN_RICE);
        foodItems.setFoodItems(replacement);
        replacement.add(CHICKEN_RICE);
        assertEquals(List.of(COFFEE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void setFoodItems_selfOrOwnView_preservesContents() {
        foodItems.setFoodItems(List.of(CHICKEN_RICE, COFFEE));
        foodItems.setFoodItems(foodItems);
        assertEquals(List.of(CHICKEN_RICE, COFFEE), foodItems.asUnmodifiableObservableList());
        foodItems.setFoodItems(foodItems.asUnmodifiableObservableList());
        assertEquals(List.of(CHICKEN_RICE, COFFEE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void asUnmodifiableObservableList_mutation_throwsAndPreservesList() {
        foodItems.add(CHICKEN_RICE);
        ObservableList<FoodItem> view = foodItems.asUnmodifiableObservableList();
        assertThrows(UnsupportedOperationException.class, () -> view.add(COFFEE));
        assertThrows(UnsupportedOperationException.class, () -> view.set(0, COFFEE));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        assertThrows(UnsupportedOperationException.class, view::clear);
        assertEquals(List.of(CHICKEN_RICE), view);
    }

    @Test
    public void asUnmodifiableObservableList_changes_updatesViewAndNotifiesListeners() {
        ObservableList<FoodItem> view = foodItems.asUnmodifiableObservableList();
        List<List<FoodItem>> observedContents = new ArrayList<>();
        view.addListener((ListChangeListener<FoodItem>) change -> observedContents.add(new ArrayList<>(view)));
        foodItems.add(CHICKEN_RICE);
        foodItems.setFoodItem(CHICKEN_RICE, EDITED_CHICKEN_RICE);
        foodItems.remove(EDITED_CHICKEN_RICE);
        assertEquals(List.of(List.of(CHICKEN_RICE), List.of(EDITED_CHICKEN_RICE), List.of()), observedContents);
        assertTrue(view.isEmpty());
    }

    @Test
    public void iterator_preservesOrderAndRejectsRemoval() {
        foodItems.setFoodItems(List.of(CHICKEN_RICE, COFFEE));
        Iterator<FoodItem> iterator = foodItems.iterator();
        assertEquals(CHICKEN_RICE, iterator.next());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(COFFEE, iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(List.of(CHICKEN_RICE, COFFEE), foodItems.asUnmodifiableObservableList());
    }

    @Test
    public void equalsAndHashCode_compareContentsAndOrder() {
        foodItems.setFoodItems(List.of(CHICKEN_RICE, COFFEE));
        UniqueFoodItemList copy = new UniqueFoodItemList();
        copy.setFoodItems(foodItems);
        assertTrue(foodItems.equals(foodItems));
        assertTrue(foodItems.equals(copy));
        assertEquals(foodItems.hashCode(), copy.hashCode());
        assertFalse(foodItems.equals(null));
        assertFalse(foodItems.equals(List.of(CHICKEN_RICE, COFFEE)));
        copy.setFoodItems(List.of(COFFEE, CHICKEN_RICE));
        assertFalse(foodItems.equals(copy));
        copy.setFoodItems(List.of(EDITED_CHICKEN_RICE, COFFEE));
        assertFalse(foodItems.equals(copy));
    }

}
