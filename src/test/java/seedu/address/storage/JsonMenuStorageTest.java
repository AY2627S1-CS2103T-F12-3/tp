package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;

public class JsonMenuStorageTest {
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
    public void readMenu_malformedJson_throwsDataLoadingException() throws IOException {
        for (String json : List.of("", "not JSON", "{", "null", "[]", "42")) {
            assertInvalidFile(json);
        }
    }

    @Test
    public void readMenu_invalidMenuData_throwsDataLoadingException() throws IOException {
        for (String json : List.of("{}", "{\"foodItems\":null}", "{\"foodItems\":[null]}",
                "{\"foodItems\":[{}]}", "{\"foodItems\":{}}")) {
            assertInvalidFile(json);
        }
    }

    @Test
    public void readMenu_invalidItemFields_throwsDataLoadingException() throws IOException {
        for (String item : List.of("{\"code\":\"!\",\"name\":\"Dish\",\"priceInCents\":450}",
                "{\"code\":\"CR01\",\"name\":\"\",\"priceInCents\":450}",
                "{\"code\":\"CR01\",\"name\":\"Dish\",\"priceInCents\":0}",
                "{\"code\":\"CR01\",\"name\":\"Dish\",\"priceInCents\":450.9}",
                "{\"code\":\"CR01\",\"name\":\"Dish\",\"priceInCents\":2147483648}")) {
            assertInvalidFile("{\"foodItems\":[" + item + "]}");
        }
    }

    @Test
    public void readMenu_duplicateCodes_throwsDataLoadingException() throws IOException {
        String first = "{\"code\":\"CR01\",\"name\":\"Chicken Rice\",\"priceInCents\":450}";
        String second = "{\"code\":\"CR01\",\"name\":\"Different Dish\",\"priceInCents\":600}";
        assertInvalidFile("{\"foodItems\":[" + first + "," + second + "]}");
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

    private void assertInvalidFile(String json) throws IOException {
        Path path = testFolder.resolve("invalid.json");
        Files.writeString(path, json);
        JsonMenuStorage storage = new JsonMenuStorage(path);

        assertThrows(DataLoadingException.class, storage::readMenu);
        assertEquals(json, Files.readString(path));
    }
}
