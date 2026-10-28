package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalMilestones.FREE_DRINK;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;
import seedu.address.model.milestone.MilestoneCatalog;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        JsonMenuStorage menuStorage = new JsonMenuStorage(getTempFilePath("menu.json"));
        JsonMilestoneCatalogStorage milestoneCatalogStorage =
                new JsonMilestoneCatalogStorage(getTempFilePath("milestones.json"));
        storageManager = new StorageManager(addressBookStorage, userPrefsStorage, menuStorage,
                milestoneCatalogStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void addressBookReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonAddressBookStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonAddressBookStorageTest} class.
         */
        AddressBook original = getTypicalAddressBook();
        storageManager.saveAddressBook(original);
        ReadOnlyAddressBook retrieved = storageManager.readAddressBook().get();
        assertEquals(original, new AddressBook(retrieved));
    }

    @Test
    public void getAddressBookFilePath() {
        assertNotNull(storageManager.getAddressBookFilePath());
    }

    @Test
    public void menuReadSave() throws Exception {
        Menu original = new Menu();
        original.addFoodItem(new FoodItem("CR01", "Chicken Rice", 450));
        storageManager.saveMenu(original);

        assertEquals(original, new Menu(storageManager.readMenu().orElseThrow()));
    }

    @Test
    public void readMenu_missingFile_returnsEmpty() throws Exception {
        assertTrue(storageManager.readMenu().isEmpty());
    }

    @Test
    public void getMenuFilePath_returnsConfiguredPath() {
        assertEquals(getTempFilePath("menu.json"), storageManager.getMenuFilePath());
    }

    @Test
    public void milestoneCatalogReadSave() throws Exception {
        MilestoneCatalog original = new MilestoneCatalog();
        original.addMilestone(FREE_DRINK);
        storageManager.saveMilestoneCatalog(original);

        assertEquals(original, new MilestoneCatalog(storageManager.readMilestoneCatalog().orElseThrow()));
    }

    @Test
    public void readMilestoneCatalog_missingFile_returnsEmpty() throws Exception {
        assertTrue(storageManager.readMilestoneCatalog().isEmpty());
    }

    @Test
    public void getMilestoneCatalogFilePath_returnsConfiguredPath() {
        assertEquals(getTempFilePath("milestones.json"), storageManager.getMilestoneCatalogFilePath());
    }

}
