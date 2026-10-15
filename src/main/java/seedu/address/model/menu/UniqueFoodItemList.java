package seedu.address.model.menu;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.menu.exceptions.DuplicateFoodItemException;
import seedu.address.model.menu.exceptions.FoodItemNotFoundException;

/**
 * Stores food items in insertion order, enforcing unique case-sensitive codes and disallowing nulls.
 * Duplicate checks use {@link FoodItem#isSameFoodItem(FoodItem)}. Replacement targets and removal use
 * {@link FoodItem#equals(Object)} so that an item with exactly matching fields is selected.
 */
public class UniqueFoodItemList implements Iterable<FoodItem> {
    private final ObservableList<FoodItem> internalList = FXCollections.observableArrayList();
    private final ObservableList<FoodItem> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if an item with the same code as {@code toCheck} exists in the list.
     */
    public boolean contains(FoodItem toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameFoodItem);
    }

    /**
     * Adds an item to the end of the list.
     *
     * @throws DuplicateFoodItemException if its code already exists.
     */
    public void add(FoodItem toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateFoodItemException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces an existing item in place with {@code editedFoodItem}.
     *
     * @throws FoodItemNotFoundException if no item equals {@code target}.
     * @throws DuplicateFoodItemException if the new code belongs to another item.
     */
    public void setFoodItem(FoodItem target, FoodItem editedFoodItem) {
        requireAllNonNull(target, editedFoodItem);
        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new FoodItemNotFoundException();
        }
        if (!target.isSameFoodItem(editedFoodItem) && contains(editedFoodItem)) {
            throw new DuplicateFoodItemException();
        }
        internalList.set(index, editedFoodItem);
    }

    /**
     * Removes the item equal to {@code toRemove}.
     *
     * @throws FoodItemNotFoundException if no item equals {@code toRemove}.
     */
    public void remove(FoodItem toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new FoodItemNotFoundException();
        }
    }

    /**
     * Copies the contents of {@code replacement} into this list, preserving their order.
     */
    public void setFoodItems(UniqueFoodItemList replacement) {
        requireNonNull(replacement);
        setFoodItems(replacement.internalList);
    }

    /**
     * Replaces the contents with a copy of {@code foodItems}, preserving their order.
     * Invalid input leaves the existing contents unchanged.
     *
     * @throws NullPointerException if the list or an item is null.
     * @throws DuplicateFoodItemException if two items have the same code.
     */
    public void setFoodItems(List<FoodItem> foodItems) {
        requireNonNull(foodItems);
        List<FoodItem> replacement = new ArrayList<>(foodItems);
        requireAllNonNull(replacement);
        if (!foodItemsAreUnique(replacement)) {
            throw new DuplicateFoodItemException();
        }
        internalList.setAll(replacement);
    }

    /**
     * Returns an unmodifiable observable view that reflects subsequent changes to this list.
     */
    public ObservableList<FoodItem> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    /**
     * Returns an iterator over the items in order. Removal through the iterator is unsupported.
     */
    @Override
    public Iterator<FoodItem> iterator() {
        return internalUnmodifiableList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UniqueFoodItemList otherFoodItemList)) {
            return false;
        }
        return internalList.equals(otherFoodItemList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    private boolean foodItemsAreUnique(List<FoodItem> foodItems) {
        for (int i = 0; i < foodItems.size() - 1; i++) {
            for (int j = i + 1; j < foodItems.size(); j++) {
                if (foodItems.get(i).isSameFoodItem(foodItems.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
