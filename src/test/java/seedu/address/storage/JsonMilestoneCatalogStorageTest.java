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
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.MilestoneCatalog;

public class JsonMilestoneCatalogStorageTest {
    private static final Milestone FREE_DRINK = new Milestone("FREE500", "Free drink", 500);

    @TempDir
    public Path testFolder;

    @Test
    public void constructor_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new JsonMilestoneCatalogStorage(null));
    }

    @Test
    public void getMilestoneCatalogFilePath_returnsConfiguredPath() {
        Path path = testFolder.resolve("milestones.json");
        assertEquals(path, new JsonMilestoneCatalogStorage(path).getMilestoneCatalogFilePath());
    }

    @Test
    public void readMilestoneCatalog_nullPath_throwsNullPointerException() {
        JsonMilestoneCatalogStorage storage =
                new JsonMilestoneCatalogStorage(testFolder.resolve("milestones.json"));
        assertThrows(NullPointerException.class, () -> storage.readMilestoneCatalog(null));
    }

    @Test
    public void readMilestoneCatalog_missingFile_returnsEmptyWithoutCreatingFile() throws Exception {
        Path path = testFolder.resolve("missing/milestones.json");
        assertEquals(Optional.empty(), new JsonMilestoneCatalogStorage(path).readMilestoneCatalog());
        assertFalse(Files.exists(path.getParent()));
    }

    @Test
    public void readMilestoneCatalog_malformedJson_throwsDataLoadingException() throws IOException {
        for (String json : List.of("", "not JSON", "{", "null", "[]", "42")) {
            assertInvalidFile(json);
        }
    }

    @Test
    public void readMilestoneCatalog_trailingContent_throwsDataLoadingException() throws IOException {
        for (String json : List.of("{\"milestones\":[]} garbage",
                "{\"milestones\":[]} {\"milestones\":[]}",
                "{\"milestones\":[]} 42", "{\"milestones\":[]} null")) {
            assertInvalidFile(json);
        }
    }

    @Test
    public void readMilestoneCatalog_trailingWhitespace_loadsCatalog() throws Exception {
        Path path = testFolder.resolve("milestones.json");
        Files.writeString(path, "{\"milestones\":[]} \t\r\n");
        assertEquals(new MilestoneCatalog(), new MilestoneCatalog(
                new JsonMilestoneCatalogStorage(path).readMilestoneCatalog().orElseThrow()));
    }

    @Test
    public void readMilestoneCatalog_invalidCatalogData_throwsDataLoadingException() throws IOException {
        for (String json : List.of("{}", "{\"milestones\":null}", "{\"milestones\":[null]}",
                "{\"milestones\":[{}]}", "{\"milestones\":{}}")) {
            assertInvalidFile(json);
        }
    }

    @Test
    public void readMilestoneCatalog_invalidMilestoneFields_throwsDataLoadingException() throws IOException {
        for (String milestone : List.of(
                "{\"code\":\"!\",\"rewardDescription\":\"Reward\",\"requiredScore\":500}",
                "{\"code\":\"FREE500\",\"rewardDescription\":\"\",\"requiredScore\":500}",
                "{\"code\":\"FREE500\",\"rewardDescription\":\"Reward\",\"requiredScore\":0}",
                "{\"code\":\"FREE500\",\"rewardDescription\":\"Reward\",\"requiredScore\":500.9}",
                "{\"code\":\"FREE500\",\"rewardDescription\":\"Reward\",\"requiredScore\":2147483648}")) {
            assertInvalidFile("{\"milestones\":[" + milestone + "]}");
        }
    }

    @Test
    public void readMilestoneCatalog_duplicateCodes_throwsDataLoadingException() throws IOException {
        String first = "{\"code\":\"FREE500\",\"rewardDescription\":\"Free drink\",\"requiredScore\":500}";
        String second =
                "{\"code\":\"free500\",\"rewardDescription\":\"Different reward\",\"requiredScore\":700}";
        assertInvalidFile("{\"milestones\":[" + first + "," + second + "]}");
    }

    @Test
    public void readMilestoneCatalog_directory_throwsDataLoadingException() {
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(testFolder);
        assertThrows(DataLoadingException.class, storage::readMilestoneCatalog);
    }

    @Test
    public void readAndSaveMilestoneCatalog_newFileAndOverwrite_preservesCatalog() throws Exception {
        Path path = testFolder.resolve("nested/data/milestones.json");
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(path);
        MilestoneCatalog catalog = new MilestoneCatalog();
        catalog.setMilestones(List.of(FREE_DRINK,
                new Milestone("DESSERT500", "免费甜点", 999999999)));

        storage.saveMilestoneCatalog(catalog);
        assertEquals(catalog, new MilestoneCatalog(storage.readMilestoneCatalog().orElseThrow()));

        catalog.removeMilestone(FREE_DRINK);
        storage.saveMilestoneCatalog(catalog);
        assertEquals(catalog, new MilestoneCatalog(storage.readMilestoneCatalog().orElseThrow()));

        catalog.setMilestones(List.of());
        storage.saveMilestoneCatalog(catalog);
        assertEquals(catalog, new MilestoneCatalog(storage.readMilestoneCatalog().orElseThrow()));
    }

    @Test
    public void readAndSaveMilestoneCatalog_explicitPath_usesOverrideWithoutChangingDefault() throws Exception {
        Path defaultPath = testFolder.resolve("default.json");
        Path overridePath = testFolder.resolve("override.json");
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(defaultPath);
        MilestoneCatalog catalog = new MilestoneCatalog();
        catalog.addMilestone(FREE_DRINK);

        storage.saveMilestoneCatalog(catalog, overridePath);

        assertEquals(catalog, new MilestoneCatalog(storage.readMilestoneCatalog(overridePath).orElseThrow()));
        assertEquals(Optional.empty(), storage.readMilestoneCatalog());
        assertFalse(Files.exists(defaultPath));
        assertEquals(defaultPath, storage.getMilestoneCatalogFilePath());
    }

    @Test
    public void saveMilestoneCatalog_nullArguments_throwsWithoutChangingFile() throws Exception {
        Path path = testFolder.resolve("milestones.json");
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(path);
        MilestoneCatalog catalog = new MilestoneCatalog();
        catalog.addMilestone(FREE_DRINK);
        storage.saveMilestoneCatalog(catalog);
        String original = Files.readString(path);

        assertThrows(NullPointerException.class, () -> storage.saveMilestoneCatalog(null));
        assertThrows(NullPointerException.class, () -> storage.saveMilestoneCatalog(catalog, null));

        assertEquals(original, Files.readString(path));
    }

    @Test
    public void saveMilestoneCatalog_directory_throwsIoException() {
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(testFolder);
        assertThrows(IOException.class, () -> storage.saveMilestoneCatalog(new MilestoneCatalog()));
    }

    private void assertInvalidFile(String json) throws IOException {
        Path path = testFolder.resolve("invalid.json");
        Files.writeString(path, json);
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(path);

        assertThrows(DataLoadingException.class, storage::readMilestoneCatalog);
        assertEquals(json, Files.readString(path));
    }
}
