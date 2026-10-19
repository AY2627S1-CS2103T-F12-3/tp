package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.person.Person;

public class AddMenuItemCommandTest {
    private static final FoodItem CHICKEN_RICE = new FoodItem("CR01", "Chicken Rice", 450);

    @Test
    public void constructor_nullFoodItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddMenuItemCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddMenuItemCommand(CHICKEN_RICE).execute(null));
    }

    @Test
    public void execute_foodItemAcceptedByModel_addSuccessful() throws CommandException {
        ModelStubAcceptingFoodItemAdded modelStub = new ModelStubAcceptingFoodItemAdded();

        CommandResult result = new AddMenuItemCommand(CHICKEN_RICE).execute(modelStub);

        assertEquals(new CommandResult(String.format(AddMenuItemCommand.MESSAGE_SUCCESS, CHICKEN_RICE.getName())),
                result);
        assertEquals(List.of(CHICKEN_RICE), modelStub.foodItemsAdded);
    }

    @Test
    public void execute_duplicateCode_throwsCommandException() {
        ModelStub modelStub = new ModelStubWithFoodItem(CHICKEN_RICE);
        FoodItem duplicate = new FoodItem("CR01", "Different Dish", 600);
        AddMenuItemCommand command = new AddMenuItemCommand(duplicate);

        // addFoodItem inherits the failing implementation, detecting any attempt to mutate the model.
        assertThrows(CommandException.class,
                AddMenuItemCommand.MESSAGE_DUPLICATE_ITEM, () -> command.execute(modelStub));
    }

    @Test
    public void equals() {
        AddMenuItemCommand command = new AddMenuItemCommand(CHICKEN_RICE);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddMenuItemCommand(new FoodItem("CR01", "Chicken Rice", 450))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new AddMenuItemCommand(new FoodItem("CR02", "Chicken Rice", 450))));
        assertFalse(command.equals(new AddMenuItemCommand(new FoodItem("CR01", "Different Dish", 450))));
        assertFalse(command.equals(new AddMenuItemCommand(new FoodItem("CR01", "Chicken Rice", 600))));
    }

    @Test
    public void toStringMethod() {
        String expected = AddMenuItemCommand.class.getCanonicalName() + "{toAdd=" + CHICKEN_RICE + "}";
        assertEquals(expected, new AddMenuItemCommand(CHICKEN_RICE).toString());
    }

    /**
     * A default model stub that fails on unexpected method calls.
     */
    private class ModelStub implements Model {
        @Override
        public boolean hasFoodItem(FoodItem foodItem) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addFoodItem(FoodItem foodItem) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook addressBook) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void selectPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deselectPerson() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> getSelectedPerson() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A model stub that reports an existing item without allowing mutations.
     */
    private class ModelStubWithFoodItem extends ModelStub {
        private final FoodItem existingItem;

        ModelStubWithFoodItem(FoodItem existingItem) {
            this.existingItem = requireNonNull(existingItem);
        }

        @Override
        public boolean hasFoodItem(FoodItem foodItem) {
            return existingItem.isSameFoodItem(requireNonNull(foodItem));
        }
    }

    /**
     * A model stub that accepts and records food item additions.
     */
    private class ModelStubAcceptingFoodItemAdded extends ModelStub {
        private final List<FoodItem> foodItemsAdded = new ArrayList<>();

        @Override
        public boolean hasFoodItem(FoodItem foodItem) {
            requireNonNull(foodItem);
            return foodItemsAdded.stream().anyMatch(foodItem::isSameFoodItem);
        }

        @Override
        public void addFoodItem(FoodItem foodItem) {
            foodItemsAdded.add(requireNonNull(foodItem));
        }
    }
}
