package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.menu.ReadOnlyMenu;
import seedu.address.model.milestone.ReadOnlyMilestoneCatalog;

/**
 * Manages storage of address book, menu, milestone catalog, and user preference data.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private JsonAddressBookStorage addressBookStorage;
    private JsonUserPrefsStorage userPrefsStorage;
    private final JsonMenuStorage menuStorage;
    private final JsonMilestoneCatalogStorage milestoneCatalogStorage;

    /**
     * Creates a {@code StorageManager} with the given address book, user prefs, menu, and milestone catalog storage.
     */
    public StorageManager(JsonAddressBookStorage addressBookStorage, JsonUserPrefsStorage userPrefsStorage,
            JsonMenuStorage menuStorage, JsonMilestoneCatalogStorage milestoneCatalogStorage) {
        this.addressBookStorage = addressBookStorage;
        this.userPrefsStorage = userPrefsStorage;
        this.menuStorage = menuStorage;
        this.milestoneCatalogStorage = milestoneCatalogStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + addressBookStorage.getAddressBookFilePath());
        return addressBookStorage.readAddressBook();
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        logger.fine("Attempting to write to data file: " + addressBookStorage.getAddressBookFilePath());
        addressBookStorage.saveAddressBook(addressBook);
    }

    // ================ Menu methods =====================================

    @Override
    public Path getMenuFilePath() {
        return menuStorage.getMenuFilePath();
    }

    @Override
    public Optional<ReadOnlyMenu> readMenu() throws DataLoadingException {
        logger.fine("Attempting to read menu from file: " + menuStorage.getMenuFilePath());
        return menuStorage.readMenu();
    }

    @Override
    public void saveMenu(ReadOnlyMenu menu) throws IOException {
        logger.fine("Attempting to write menu to file: " + menuStorage.getMenuFilePath());
        menuStorage.saveMenu(menu);
    }

    // ================ Milestone catalog methods ========================

    @Override
    public Path getMilestoneCatalogFilePath() {
        return milestoneCatalogStorage.getMilestoneCatalogFilePath();
    }

    @Override
    public Optional<ReadOnlyMilestoneCatalog> readMilestoneCatalog() throws DataLoadingException {
        logger.fine("Attempting to read milestone catalog from file: "
                + milestoneCatalogStorage.getMilestoneCatalogFilePath());
        return milestoneCatalogStorage.readMilestoneCatalog();
    }

    @Override
    public void saveMilestoneCatalog(ReadOnlyMilestoneCatalog milestoneCatalog) throws IOException {
        logger.fine("Attempting to write milestone catalog to file: "
                + milestoneCatalogStorage.getMilestoneCatalogFilePath());
        milestoneCatalogStorage.saveMilestoneCatalog(milestoneCatalog);
    }

}
