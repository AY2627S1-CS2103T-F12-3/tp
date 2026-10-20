package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;
import seedu.address.model.menu.ReadOnlyMenu;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.MilestoneCatalog;
import seedu.address.model.milestone.ReadOnlyMilestoneCatalog;
import seedu.address.model.person.PersonMatchesKeywordsPredicate;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {
    private static final Milestone FREE_DRINK = new Milestone("FREE500", "Free drink", 500);
    private static final Milestone EDITED_FREE_DRINK = new Milestone("FREE500", "Free drink and snack", 600);
    private static final Milestone FREE_DESSERT = new Milestone("DESSERT500", "Free dessert", 500);

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void constructor_loadedMenu_copiesContentsIndependently() {
        Menu source = new Menu();
        FoodItem item = new FoodItem("CR01", "Chicken Rice", 450);
        source.addFoodItem(item);
        ModelManager loaded = new ModelManager(new AddressBook(), new UserPrefs(), source);

        source.removeFoodItem(item);

        assertEquals(List.of(item), loaded.getMenu().getFoodItemList());
        loaded.addFoodItem(new FoodItem("CF01", "Coffee", 150));
        assertTrue(source.getFoodItemList().isEmpty());
    }

    @Test
    public void constructor_loadedMilestoneCatalog_copiesContentsIndependently() {
        MilestoneCatalog source = new MilestoneCatalog();
        source.addMilestone(FREE_DRINK);
        ModelManager loaded = new ModelManager(new AddressBook(), new UserPrefs(), new Menu(), source);

        source.removeMilestone(FREE_DRINK);

        assertEquals(List.of(FREE_DRINK), loaded.getMilestoneList());
        loaded.addMilestone(FREE_DESSERT);
        assertTrue(source.getMilestoneList().isEmpty());
    }

    @Test
    public void constructor_nullMenu_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ModelManager(new AddressBook(), new UserPrefs(), null));
    }

    @Test
    public void constructor_nullMilestoneCatalog_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new ModelManager(new AddressBook(), new UserPrefs(), new Menu(), null));
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasMilestone_nullMilestone_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasMilestone(null));
    }

    @Test
    public void hasMilestone_milestoneNotInCatalog_returnsFalse() {
        assertFalse(modelManager.hasMilestone(FREE_DRINK));
    }

    @Test
    public void hasMilestone_milestoneInCatalog_returnsTrue() {
        modelManager.addMilestone(FREE_DRINK);
        assertTrue(modelManager.hasMilestone(FREE_DRINK));
    }

    @Test
    public void milestoneOperations_addSetDelete_updatesSortedList() {
        modelManager.addMilestone(FREE_DRINK);
        modelManager.addMilestone(FREE_DESSERT);
        assertEquals(List.of(FREE_DESSERT, FREE_DRINK), modelManager.getMilestoneList());

        modelManager.setMilestone(FREE_DRINK, EDITED_FREE_DRINK);
        assertEquals(List.of(FREE_DESSERT, EDITED_FREE_DRINK), modelManager.getMilestoneList());

        modelManager.deleteMilestone(FREE_DESSERT);
        assertEquals(List.of(EDITED_FREE_DRINK), modelManager.getMilestoneList());
    }

    @Test
    public void getMilestoneList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getMilestoneList().add(FREE_DRINK));
    }

    @Test
    public void getMilestoneCatalog_readOnlyView_reflectsUpdatesAndRejectsMutation() {
        ReadOnlyMilestoneCatalog view = modelManager.getMilestoneCatalog();

        modelManager.addMilestone(FREE_DRINK);

        assertEquals(List.of(FREE_DRINK), view.getMilestoneList());
        assertThrows(UnsupportedOperationException.class, () -> view.getMilestoneList().clear());
    }

    @Test
    public void selectPerson_personInAddressBook_updatesSelection() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);

        assertEquals(Optional.empty(), modelManager.getSelectedPerson());
        modelManager.selectPerson(ALICE);
        assertEquals(Optional.of(ALICE), modelManager.getSelectedPerson());

        modelManager.selectPerson(BENSON);
        assertEquals(Optional.of(BENSON), modelManager.getSelectedPerson());
    }

    @Test
    public void selectPerson_personNotInAddressBook_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> modelManager.selectPerson(ALICE));
    }

    @Test
    public void selectPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.selectPerson(null));
    }

    @Test
    public void deselectPerson_selectedPerson_clearsSelection() {
        modelManager.addPerson(ALICE);
        modelManager.selectPerson(ALICE);

        modelManager.deselectPerson();

        assertEquals(Optional.empty(), modelManager.getSelectedPerson());
    }

    @Test
    public void deselectPerson_noSelectedPerson_keepsSelectionEmpty() {
        modelManager.deselectPerson();

        assertEquals(Optional.empty(), modelManager.getSelectedPerson());
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void getMenu_readOnlyView_reflectsAdditionsAndRejectsMutation() {
        ReadOnlyMenu view = modelManager.getMenu();
        FoodItem item = new FoodItem("CR01", "Chicken Rice", 450);
        assertTrue(view.getFoodItemList().isEmpty());

        modelManager.addFoodItem(item);

        assertEquals(List.of(item), view.getFoodItemList());
        assertThrows(UnsupportedOperationException.class, () -> view.getFoodItemList().clear());
        assertEquals(List.of(item), modelManager.getMenu().getFoodItemList());
    }

    @Test
    public void equals_differentMenuContents_returnsFalse() {
        ModelManager otherModel = new ModelManager();
        FoodItem item = new FoodItem("CR01", "Chicken Rice", 450);
        modelManager.addFoodItem(item);

        assertFalse(modelManager.equals(otherModel));

        otherModel.addFoodItem(item);
        assertTrue(modelManager.equals(otherModel));
    }

    @Test
    public void equals_differentMilestoneCatalogContents_returnsFalse() {
        ModelManager otherModel = new ModelManager();
        modelManager.addMilestone(FREE_DRINK);

        assertFalse(modelManager.equals(otherModel));

        otherModel.addMilestone(FREE_DRINK);
        assertTrue(modelManager.equals(otherModel));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new PersonMatchesKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different selected person -> returns false
        modelManager.selectPerson(ALICE);
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));
        modelManager = new ModelManager(addressBook, userPrefs);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
