package seedu.address.model.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.menu.exceptions.DuplicateFoodItemException;

public class MenuTest {
    private static final FoodItem CHICKEN_RICE = new FoodItem("CR01", "Chicken Rice", 450);
    private static final FoodItem EDITED_CHICKEN_RICE = new FoodItem("CR01", "Roasted Chicken Rice", 500);
    private static final FoodItem COFFEE = new FoodItem("CF01", "Coffee", 150);

    private final Menu menu = new Menu();

    @Test
    public void constructor_emptyMenu_hasNoItems() {
        assertTrue(menu.getFoodItemList().isEmpty());
    }

    @Test
    public void constructor_readOnlyMenu_copiesContentsIndependently() {
        menu.addFoodItem(CHICKEN_RICE);
        Menu copy = new Menu(menu);

        menu.setFoodItem(CHICKEN_RICE, EDITED_CHICKEN_RICE);
        assertEquals(List.of(CHICKEN_RICE), copy.getFoodItemList());
        copy.addFoodItem(COFFEE);
        assertEquals(List.of(EDITED_CHICKEN_RICE), menu.getFoodItemList());
        assertEquals(List.of(CHICKEN_RICE, COFFEE), copy.getFoodItemList());
    }

    @Test
    public void constructorAndResetData_nullMenu_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Menu(null));
        menu.addFoodItem(CHICKEN_RICE);
        assertThrows(NullPointerException.class, () -> menu.resetData(null));
        assertEquals(List.of(CHICKEN_RICE), menu.getFoodItemList());
    }

    @Test
    public void resetData_validMenu_copiesContentsAndUpdatesExistingView() {
        menu.addFoodItem(CHICKEN_RICE);
        ObservableList<FoodItem> view = menu.getFoodItemList();
        Menu replacement = new Menu();
        replacement.addFoodItem(COFFEE);

        menu.resetData(replacement);
        replacement.removeFoodItem(COFFEE);

        assertEquals(List.of(COFFEE), view);
        menu.resetData(menu);
        assertEquals(List.of(COFFEE), view);
    }

    @Test
    public void resetData_duplicateCodes_throwsAndPreservesMenu() {
        menu.addFoodItem(COFFEE);
        ReadOnlyMenu invalidMenu = () -> FXCollections.unmodifiableObservableList(
                FXCollections.observableArrayList(CHICKEN_RICE, EDITED_CHICKEN_RICE));

        assertThrows(DuplicateFoodItemException.class, () -> menu.resetData(invalidMenu));
        assertEquals(List.of(COFFEE), menu.getFoodItemList());
    }

    @Test
    public void resetData_nullListOrElement_throwsAndPreservesMenu() {
        menu.addFoodItem(COFFEE);
        ReadOnlyMenu nullListMenu = () -> null;
        ReadOnlyMenu nullItemMenu = () -> FXCollections.unmodifiableObservableList(
                FXCollections.observableArrayList(Arrays.asList(CHICKEN_RICE, null)));

        assertThrows(NullPointerException.class, () -> menu.resetData(nullListMenu));
        assertThrows(NullPointerException.class, () -> menu.resetData(nullItemMenu));
        assertEquals(List.of(COFFEE), menu.getFoodItemList());
    }

    @Test
    public void setFoodItems_validList_copiesContentsAndSupportsEmptyList() {
        menu.addFoodItem(CHICKEN_RICE);
        List<FoodItem> replacement = new ArrayList<>(List.of(COFFEE));
        menu.setFoodItems(replacement);
        replacement.clear();

        assertEquals(List.of(COFFEE), menu.getFoodItemList());
        menu.setFoodItems(List.of());
        assertTrue(menu.getFoodItemList().isEmpty());
    }

    @Test
    public void hasFoodItem_comparesCodesAndRejectsNull() {
        assertFalse(menu.hasFoodItem(CHICKEN_RICE));
        menu.addFoodItem(CHICKEN_RICE);
        assertTrue(menu.hasFoodItem(EDITED_CHICKEN_RICE));
        assertFalse(menu.hasFoodItem(COFFEE));
        assertThrows(NullPointerException.class, () -> menu.hasFoodItem(null));
    }

    @Test
    public void itemOperations_updateMenuAndRejectDuplicates() {
        menu.addFoodItem(CHICKEN_RICE);
        menu.addFoodItem(COFFEE);
        assertThrows(DuplicateFoodItemException.class, () -> menu.addFoodItem(EDITED_CHICKEN_RICE));

        menu.setFoodItem(CHICKEN_RICE, EDITED_CHICKEN_RICE);
        assertEquals(List.of(EDITED_CHICKEN_RICE, COFFEE), menu.getFoodItemList());
        assertThrows(DuplicateFoodItemException.class, () -> menu.setFoodItem(COFFEE, CHICKEN_RICE));
        menu.removeFoodItem(COFFEE);
        assertEquals(List.of(EDITED_CHICKEN_RICE), menu.getFoodItemList());
    }

    @Test
    public void getFoodItemList_readOnlyMenuView_rejectsMutationAndReflectsUpdates() {
        ReadOnlyMenu readOnlyMenu = menu;
        ObservableList<FoodItem> view = readOnlyMenu.getFoodItemList();
        menu.addFoodItem(CHICKEN_RICE);

        assertEquals(List.of(CHICKEN_RICE), view);
        assertThrows(UnsupportedOperationException.class, () -> view.add(COFFEE));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        assertEquals(List.of(CHICKEN_RICE), menu.getFoodItemList());
    }

    @Test
    public void equalsAndHashCode_compareContentsAndOrder() {
        menu.setFoodItems(List.of(CHICKEN_RICE, COFFEE));
        Menu copy = new Menu(menu);

        assertTrue(menu.equals(menu));
        assertTrue(menu.equals(copy));
        assertEquals(menu.hashCode(), copy.hashCode());
        assertFalse(menu.equals(null));
        assertFalse(menu.equals(List.of(CHICKEN_RICE, COFFEE)));
        copy.setFoodItems(List.of(COFFEE, CHICKEN_RICE));
        assertFalse(menu.equals(copy));
    }
}
