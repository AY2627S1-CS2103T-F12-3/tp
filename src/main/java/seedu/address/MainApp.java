package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.menu.Menu;
import seedu.address.model.menu.ReadOnlyMenu;
import seedu.address.model.milestone.MilestoneCatalog;
import seedu.address.model.milestone.ReadOnlyMilestoneCatalog;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonMenuStorage;
import seedu.address.storage.JsonMilestoneCatalogStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V0.5.1";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path ADDRESS_BOOK_FILE_PATH = Paths.get("data", "memberlist.json");
    private static final Path MENU_FILE_PATH = Paths.get("data", "menu.json");
    private static final Path MILESTONE_CATALOG_FILE_PATH = Paths.get("data", "milestones.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing AddressBook ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(ADDRESS_BOOK_FILE_PATH);
        JsonMenuStorage menuStorage = new JsonMenuStorage(MENU_FILE_PATH);
        JsonMilestoneCatalogStorage milestoneCatalogStorage =
                new JsonMilestoneCatalogStorage(MILESTONE_CATALOG_FILE_PATH);
        storage = new StorageManager(addressBookStorage, userPrefsStorage, menuStorage, milestoneCatalogStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getAddressBookFilePath());
    }

    /**
     * Initializes the model with member data, menu data, milestone data, and user preferences.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        return new ModelManager(initMembers(storage), userPrefs, initMenu(storage), initMilestoneCatalog(storage));
    }

    /**
     * Loads the saved address book.
     * The data from the sample address book will be used instead if {@code storage}'s address book is not found,
     * or an empty address book will be used instead if errors occur when reading {@code storage}'s address book.
     */
    private ReadOnlyAddressBook initMembers(Storage storage) {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            if (addressBookOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                        + " populated with a sample AddressBook.");
            }
            initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getAddressBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty AddressBook.");
            initialData = new AddressBook();
        }

        return initialData;
    }

    /**
     * Loads the saved menu, or starts with a sample menu if the file is missing.
     * An unreadable menu file results in an empty menu.
     */
    protected ReadOnlyMenu initMenu(Storage storage) {
        logger.info("Using menu file : " + storage.getMenuFilePath());
        try {
            return storage.readMenu().orElseGet(SampleDataUtil::getSampleMenu);
        } catch (DataLoadingException e) {
            logger.warning("Menu file at " + storage.getMenuFilePath() + " could not be loaded."
                    + " Will be starting with an empty menu.");
            return new Menu();
        }
    }

    /**
     * Loads the saved milestone catalog, or starts with an empty catalog if the file is missing or cannot be loaded.
     */
    protected ReadOnlyMilestoneCatalog initMilestoneCatalog(Storage storage) {
        logger.info("Using milestone catalog file : " + storage.getMilestoneCatalogFilePath());
        try {
            return storage.readMilestoneCatalog().orElseGet(MilestoneCatalog::new);
        } catch (DataLoadingException e) {
            logger.log(Level.WARNING, "Milestone catalog file at " + storage.getMilestoneCatalogFilePath()
                    + " could not be loaded. Will be starting with an empty milestone catalog.", e);
            return new MilestoneCatalog();
        }
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting AddressBook " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping AddressBook ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
