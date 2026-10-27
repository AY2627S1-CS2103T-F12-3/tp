package seedu.address.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.ReadOnlyMenu;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.ReadOnlyMilestoneCatalog;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Returns the person with the given phone number, or an empty optional if no person matches.
     * Searches all persons regardless of the current display filter.
     *
     * @throws NullPointerException if {@code phone} is null.
     */
    Optional<Person> findPersonByPhone(Phone phone);

    /**
     * Returns members with different phone entries that may contain a common number.
     * Searches the complete member list without changing the display filter or selection.
     * This advisory query does not establish duplicate member identities.
     *
     * @throws NullPointerException if {@code phone} is null.
     */
    List<Person> findPersonsWithSimilarPhone(Phone phone);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Returns true if a milestone with the same case-insensitive code as {@code milestone} exists.
     */
    boolean hasMilestone(Milestone milestone);

    /**
     * Deletes the given milestone.
     * The milestone must exist in the milestone catalog.
     */
    void deleteMilestone(Milestone target);

    /**
     * Adds the given milestone.
     * Its code must not already exist in the milestone catalog.
     */
    void addMilestone(Milestone milestone);

    /**
     * Replaces the given milestone {@code target} with {@code editedMilestone}.
     * {@code target} must exist, and both milestones must have the same case-insensitive code.
     */
    void setMilestone(Milestone target, Milestone editedMilestone);

    /**
     * Returns a read-only view of the milestone catalog.
     */
    ReadOnlyMilestoneCatalog getMilestoneCatalog();

    /**
     * Selects {@code person} for display. The person must be equal to a person in the address book.
     * The selected person acts as a snapshot of the person's state and is not updated when the person's data changes.
     *
     * @param person The person to select.
     * @throws NullPointerException If {@code person} is null.
     * @throws IllegalArgumentException If no equal person is in the address book.
     */
    void selectPerson(Person person);

    /**
     * Clears the selected person. Does nothing if no person is selected.
     */
    void deselectPerson();

    /**
     * Returns the selected person, or an empty optional if no person is selected.
     */
    Optional<Person> getSelectedPerson();

    /**
     * Returns a read-only view of the menu.
     */
    ReadOnlyMenu getMenu();

    /**
     * Returns true if a menu item with the same code exists.
     */
    boolean hasFoodItem(FoodItem foodItem);

    /**
     * Adds a food item whose code is not already in the menu.
     */
    void addFoodItem(FoodItem foodItem);

    /**
     * Deletes the food item equal to {@code target}, which must exist in the menu.
     */
    void deleteFoodItem(FoodItem target);

    /**
     * Replaces the given menu item with an edited menu item.
     * The target must exist, and the edited item's code must not belong to another item.
     */
    void setFoodItem(FoodItem target, FoodItem editedFoodItem);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /** Returns an unmodifiable view of the milestone list. */
    ObservableList<Milestone> getMilestoneList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);
}
