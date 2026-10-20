package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;
import seedu.address.model.menu.ReadOnlyMenu;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.MilestoneCatalog;
import seedu.address.model.milestone.ReadOnlyMilestoneCatalog;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of member, menu, milestone catalog, and user preference data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final Menu menu;
    private final MilestoneCatalog milestoneCatalog;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private Person selectedPerson;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        this(addressBook, userPrefs, new Menu(), new MilestoneCatalog());
    }

    /**
     * Initializes a ModelManager with the given address book, user preferences, and menu.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs, ReadOnlyMenu menu) {
        this(addressBook, userPrefs, menu, new MilestoneCatalog());
    }

    /**
     * Initializes a ModelManager with the given address book, user preferences, menu, and milestone catalog.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs, ReadOnlyMenu menu,
            ReadOnlyMilestoneCatalog milestoneCatalog) {
        requireAllNonNull(addressBook, userPrefs, menu, milestoneCatalog);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.menu = new Menu(menu);
        this.milestoneCatalog = new MilestoneCatalog(milestoneCatalog);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    //=========== Milestone Operations ======================================================================

    @Override
    public boolean hasMilestone(Milestone milestone) {
        requireNonNull(milestone);
        return milestoneCatalog.hasMilestone(milestone);
    }

    @Override
    public void deleteMilestone(Milestone target) {
        milestoneCatalog.removeMilestone(target);
    }

    @Override
    public void addMilestone(Milestone milestone) {
        milestoneCatalog.addMilestone(milestone);
    }

    @Override
    public void setMilestone(Milestone target, Milestone editedMilestone) {
        requireAllNonNull(target, editedMilestone);
        milestoneCatalog.setMilestone(target, editedMilestone);
    }

    @Override
    public ReadOnlyMilestoneCatalog getMilestoneCatalog() {
        return milestoneCatalog::getMilestoneList;
    }

    //=========== SelectedPerson =============================================================================

    @Override
    public void selectPerson(Person person) {
        requireNonNull(person);
        if (addressBook.getPersonList().stream().noneMatch(person::equals)) {
            throw new IllegalArgumentException("Selected person must be in the address book");
        }
        selectedPerson = person;
    }

    @Override
    public void deselectPerson() {
        selectedPerson = null;
    }

    @Override
    public Optional<Person> getSelectedPerson() {
        return Optional.ofNullable(selectedPerson);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== Milestone List Accessor ===================================================================

    @Override
    public ObservableList<Milestone> getMilestoneList() {
        return milestoneCatalog.getMilestoneList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && Objects.equals(selectedPerson, otherModelManager.selectedPerson)
                && menu.equals(otherModelManager.menu)
                && milestoneCatalog.equals(otherModelManager.milestoneCatalog);
    }

    @Override
    public ReadOnlyMenu getMenu() {
        return menu::getFoodItemList;
    }

    @Override
    public boolean hasFoodItem(FoodItem foodItem) {
        return menu.hasFoodItem(foodItem);
    }

    @Override
    public void addFoodItem(FoodItem foodItem) {
        menu.addFoodItem(foodItem);
    }

    @Override
    public void deleteFoodItem(FoodItem target) {
        menu.removeFoodItem(target);
    }
}
