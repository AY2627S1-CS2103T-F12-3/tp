package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;

public class JsonMenuStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonMenuStorageTest");
    private static final FoodItem CHICKEN_RICE = new FoodItem("CR01", "Chicken Rice", 450);

    @TempDir
    public Path testFolder;

    @Test
    public void constructor_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new JsonMenuStorage(null));
    }

    @Test
    public void getMenuFilePath_returnsConfiguredPath() {
        Path path = testFolder.resolve("menu.json");
        assertEquals(path, new JsonMenuStorage(path).getMenuFilePath());
    }

    @Test
    public void readMenu_nullPath_throwsNullPointerException() {
        JsonMenuStorage storage = new JsonMenuStorage(testFolder.resolve("menu.json"));
        assertThrows(NullPointerException.class, () -> storage.readMenu(null));
    }

    @Test
    public void readMenu_missingFile_returnsEmptyWithoutCreatingFile() throws Exception {
        Path path = testFolder.resolve("missing/menu.json");
        assertEquals(Optional.empty(), new JsonMenuStorage(path).readMenu());
        assertFalse(Files.exists(path.getParent()));
    }

    @Test
    public void readMenu_validFile_loadsMenu() throws Exception {
        Path path = TEST_DATA_FOLDER.resolve("validMenu.json");
        Menu expected = new Menu();
        expected.setFoodItems(List.of(CHICKEN_RICE, new FoodItem("CF01", "Coffee", 150)));

        assertEquals(expected, new Menu(new JsonMenuStorage(path).readMenu().orElseThrow()));
    }

    @Test
    public void readMenu_malformedJson_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("notJsonFormat.json");
        assertThrows(DataLoadingException.class, () -> new JsonMenuStorage(path).readMenu());
    }

    @Test
    public void readMenu_trailingContent_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("trailingObject.json");
        assertThrows(DataLoadingException.class, () -> new JsonMenuStorage(path).readMenu());
    }

    @Test
    public void readMenu_trailingWhitespace_loadsMenu() throws Exception {
        Path path = testFolder.resolve("menu.json");
        Files.writeString(path, "{\"foodItems\":[]} \t\r\n");
        assertEquals(new Menu(), new Menu(new JsonMenuStorage(path).readMenu().orElseThrow()));
    }

    @Test
    public void readMenu_missingFoodItems_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("missingFoodItems.json");
        assertThrows(DataLoadingException.class, () -> new JsonMenuStorage(path).readMenu());
    }

    @Test
    public void readMenu_nullFoodItem_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("nullFoodItem.json");
        assertThrows(DataLoadingException.class, () -> new JsonMenuStorage(path).readMenu());
    }

    @Test
    public void readMenu_invalidCode_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("invalidCode.json");
        assertThrows(DataLoadingException.class, () -> new JsonMenuStorage(path).readMenu());
    }

    @Test
    public void readMenu_blankName_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("blankName.json");
        assertThrows(DataLoadingException.class, () -> new JsonMenuStorage(path).readMenu());
    }

    @Test
    public void readMenu_zeroPrice_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("zeroPrice.json");
        assertThrows(DataLoadingException.class, () -> new JsonMenuStorage(path).readMenu());
    }

    @Test
    public void readMenu_fractionalPrice_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("fractionalPrice.json");
        assertThrows(DataLoadingException.class, () -> new JsonMenuStorage(path).readMenu());
    }

    @Test
    public void readMenu_duplicateCodes_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("duplicateCodesMenu.json");
        assertThrows(DataLoadingException.class, () -> new JsonMenuStorage(path).readMenu());
    }

    @Test
    public void readMenu_directory_throwsDataLoadingException() {
        JsonMenuStorage storage = new JsonMenuStorage(testFolder);
        assertThrows(DataLoadingException.class, storage::readMenu);
    }

    @Test
    public void readAndSaveMenu_newFileAndOverwrite_preservesMenu() throws Exception {
        Path path = testFolder.resolve("nested/data/menu.json");
        JsonMenuStorage storage = new JsonMenuStorage(path);
        Menu menu = new Menu();
        menu.setFoodItems(List.of(CHICKEN_RICE, new FoodItem("cr01", "鸡饭", Integer.MAX_VALUE),
                new FoodItem("CF01", "Coffee", 1)));

        storage.saveMenu(menu);
        assertEquals(menu, new Menu(storage.readMenu().orElseThrow()));

        menu.removeFoodItem(CHICKEN_RICE);
        storage.saveMenu(menu);
        assertEquals(menu, new Menu(storage.readMenu().orElseThrow()));

        menu.setFoodItems(List.of());
        storage.saveMenu(menu);
        assertEquals(menu, new Menu(storage.readMenu().orElseThrow()));
    }

    @Test
    public void readAndSaveMenu_explicitPath_usesOverrideWithoutChangingDefault() throws Exception {
        Path defaultPath = testFolder.resolve("default.json");
        Path overridePath = testFolder.resolve("override.json");
        JsonMenuStorage storage = new JsonMenuStorage(defaultPath);
        Menu menu = new Menu();
        menu.addFoodItem(CHICKEN_RICE);

        storage.saveMenu(menu, overridePath);

        assertEquals(menu, new Menu(storage.readMenu(overridePath).orElseThrow()));
        assertEquals(Optional.empty(), storage.readMenu());
        assertFalse(Files.exists(defaultPath));
        assertEquals(defaultPath, storage.getMenuFilePath());
    }

    @Test
    public void saveMenu_nullArguments_throwsWithoutChangingFile() throws Exception {
        Path path = testFolder.resolve("menu.json");
        JsonMenuStorage storage = new JsonMenuStorage(path);
        Menu menu = new Menu();
        menu.addFoodItem(CHICKEN_RICE);
        storage.saveMenu(menu);
        String original = Files.readString(path);

        assertThrows(NullPointerException.class, () -> storage.saveMenu(null));
        assertThrows(NullPointerException.class, () -> storage.saveMenu(menu, null));

        assertEquals(original, Files.readString(path));
    }

    @Test
    public void saveMenu_directory_throwsIoException() {
        JsonMenuStorage storage = new JsonMenuStorage(testFolder);
        assertThrows(IOException.class, () -> storage.saveMenu(new Menu()));
    }

}
