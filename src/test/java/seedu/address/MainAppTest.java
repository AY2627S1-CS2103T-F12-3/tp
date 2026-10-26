package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.MilestoneCatalog;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonMenuStorage;
import seedu.address.storage.JsonMilestoneCatalogStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainAppTest {
    @TempDir
    public Path testFolder;

    @Test
    public void initMenu_missingFile_returnsSampleMenu() {
        StorageManager storage = createStorage();
        assertEquals(SampleDataUtil.getSampleMenu().getFoodItemList(),
                new MainApp().initMenu(storage).getFoodItemList());
        assertFalse(Files.exists(storage.getMenuFilePath()));
    }

    @Test
    public void initMenu_invalidFile_returnsEmptyWithoutChangingFile() throws Exception {
        StorageManager storage = createStorage();
        for (String json : List.of("not JSON", "null", "{}", "{\"foodItems\":[null]}")) {
            Files.writeString(storage.getMenuFilePath(), json);
            assertTrue(new MainApp().initMenu(storage).getFoodItemList().isEmpty());
            assertEquals(json, Files.readString(storage.getMenuFilePath()));
        }
    }

    @Test
    public void initMenu_validFile_loadsSavedMenu() throws Exception {
        StorageManager storage = createStorage();
        Menu saved = new Menu();
        saved.addFoodItem(new FoodItem("3A", "Nasi Lemak", 480));
        storage.saveMenu(saved);

        assertEquals(saved, new Menu(new MainApp().initMenu(storage)));
    }

    @Test
    public void initMilestoneCatalog_missingFile_returnsEmptyCatalog() {
        StorageManager storage = createStorage();
        assertTrue(new MainApp().initMilestoneCatalog(storage).getMilestoneList().isEmpty());
        assertFalse(Files.exists(storage.getMilestoneCatalogFilePath()));
    }

    @Test
    public void initMilestoneCatalog_invalidFile_returnsEmptyWithoutChangingFile() throws Exception {
        StorageManager storage = createStorage();
        for (String json : List.of("not JSON", "null", "{}", "{\"milestones\":[null]}")) {
            Files.writeString(storage.getMilestoneCatalogFilePath(), json);
            assertTrue(new MainApp().initMilestoneCatalog(storage).getMilestoneList().isEmpty());
            assertEquals(json, Files.readString(storage.getMilestoneCatalogFilePath()));
        }
    }

    @Test
    public void initMilestoneCatalog_validFile_loadsSavedCatalog() throws Exception {
        StorageManager storage = createStorage();
        MilestoneCatalog saved = new MilestoneCatalog();
        saved.addMilestone(new Milestone("FREE500", "Free drink", 500));
        storage.saveMilestoneCatalog(saved);

        assertEquals(saved, new MilestoneCatalog(new MainApp().initMilestoneCatalog(storage)));
    }

    @Test
    public void addMenuItem_saveAndReload_retainsItemAfterRestart() throws Exception {
        StorageManager storage = createStorage();
        MainApp app = new MainApp();
        ModelManager model = new ModelManager(new AddressBook(), new UserPrefs(), app.initMenu(storage),
                app.initMilestoneCatalog(storage));
        new LogicManager(model, storage).execute("add-menu-item c/12A n/Dessert p/3.50");

        ModelManager reloaded = new ModelManager(storage.readAddressBook().orElseThrow(),
                new UserPrefs(), new MainApp().initMenu(storage), new MainApp().initMilestoneCatalog(storage));

        Menu expectedMenu = new Menu(SampleDataUtil.getSampleMenu());
        expectedMenu.addFoodItem(new FoodItem("12A", "Dessert", 350));
        assertEquals(expectedMenu.getFoodItemList(), reloaded.getMenu().getFoodItemList());
        assertEquals(model, reloaded);
    }

    @Test
    public void milestoneCommands_saveAndReload_persistChangesAfterRestart() throws Exception {
        StorageManager storage = createStorage();
        MainApp app = new MainApp();
        ModelManager model = new ModelManager(new AddressBook(), new UserPrefs(), app.initMenu(storage),
                app.initMilestoneCatalog(storage));
        new LogicManager(model, storage).execute("add-milestone r/Free drink s/500");

        ModelManager reloaded = reloadModel(storage);
        Milestone added = model.getMilestoneList().get(0);
        assertEquals(List.of(added), reloaded.getMilestoneList());

        new LogicManager(reloaded, storage).execute("edit-milestone 1 r/Free drink and snack s/750");

        reloaded = reloadModel(storage);
        assertEquals(List.of(new Milestone(added.getCode(), "Free drink and snack", 750)),
                reloaded.getMilestoneList());

        new LogicManager(reloaded, storage).execute("delete-milestone 1");

        reloaded = reloadModel(storage);
        assertTrue(reloaded.getMilestoneList().isEmpty());
    }

    private ModelManager reloadModel(StorageManager storage) throws Exception {
        MainApp app = new MainApp();
        return new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs(), app.initMenu(storage),
                app.initMilestoneCatalog(storage));
    }

    @Test
    public void deleteMenuItem_saveAndReload_preservesDeletion() throws Exception {
        StorageManager storage = createStorage();
        ModelManager model = new ModelManager();
        LogicManager logic = new LogicManager(model, storage);
        logic.execute("add-menu-item c/CR01 n/Chicken Rice p/4.50");
        logic.execute("add-menu-item c/CF01 n/Coffee p/1.50");
        logic.execute("delete-menu-item c/CR01");

        ModelManager reloaded = new ModelManager(storage.readAddressBook().orElseThrow(),
                new UserPrefs(), new MainApp().initMenu(storage), new MainApp().initMilestoneCatalog(storage));
        assertEquals(List.of(new FoodItem("CF01", "Coffee", 150)), reloaded.getMenu().getFoodItemList());

        new LogicManager(reloaded, storage).execute("add-menu-item c/CR01 n/New Dish p/5.00");
        assertEquals(2, storage.readMenu().orElseThrow().getFoodItemList().size());
    }

    private StorageManager createStorage() {
        return new StorageManager(new JsonAddressBookStorage(testFolder.resolve("members.json")),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")),
                new JsonMenuStorage(testFolder.resolve("menu.json")),
                new JsonMilestoneCatalogStorage(testFolder.resolve("milestones.json")));
    }
}
