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
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.MilestoneCatalog;

public class JsonMilestoneCatalogStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data",
            "JsonMilestoneCatalogStorageTest");
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
    public void readMilestoneCatalog_validFile_loadsCatalog() throws Exception {
        Path path = TEST_DATA_FOLDER.resolve("validMilestoneCatalog.json");
        MilestoneCatalog expected = new MilestoneCatalog();
        expected.setMilestones(List.of(FREE_DRINK, new Milestone("DESSERT500", "Free dessert", 500)));

        assertEquals(expected, new MilestoneCatalog(
                new JsonMilestoneCatalogStorage(path).readMilestoneCatalog().orElseThrow()));
    }

    @Test
    public void readMilestoneCatalog_malformedJson_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("notJsonFormat.json");
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(path);
        assertThrows(DataLoadingException.class, storage::readMilestoneCatalog);
    }

    @Test
    public void readMilestoneCatalog_trailingContent_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("trailingObject.json");
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(path);
        assertThrows(DataLoadingException.class, storage::readMilestoneCatalog);
    }

    @Test
    public void readMilestoneCatalog_trailingWhitespace_loadsCatalog() throws Exception {
        Path path = testFolder.resolve("milestones.json");
        Files.writeString(path, "{\"milestones\":[]} \t\r\n");
        assertEquals(new MilestoneCatalog(), new MilestoneCatalog(
                new JsonMilestoneCatalogStorage(path).readMilestoneCatalog().orElseThrow()));
    }

    @Test
    public void readMilestoneCatalog_missingMilestones_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("missingMilestones.json");
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(path);
        assertThrows(DataLoadingException.class, storage::readMilestoneCatalog);
    }

    @Test
    public void readMilestoneCatalog_nullMilestone_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("nullMilestone.json");
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(path);
        assertThrows(DataLoadingException.class, storage::readMilestoneCatalog);
    }

    @Test
    public void readMilestoneCatalog_invalidCode_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("invalidCode.json");
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(path);
        assertThrows(DataLoadingException.class, storage::readMilestoneCatalog);
    }

    @Test
    public void readMilestoneCatalog_blankRewardDescription_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("blankRewardDescription.json");
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(path);
        assertThrows(DataLoadingException.class, storage::readMilestoneCatalog);
    }

    @Test
    public void readMilestoneCatalog_zeroScore_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("zeroScore.json");
        JsonMilestoneCatalogStorage storage = new JsonMilestoneCatalogStorage(path);
        assertThrows(DataLoadingException.class, storage::readMilestoneCatalog);
    }

    @Test
    public void readMilestoneCatalog_duplicateCodes_throwsDataLoadingException() {
        Path path = TEST_DATA_FOLDER.resolve("duplicateCodesMilestoneCatalog.json");
        assertThrows(DataLoadingException.class, () -> new JsonMilestoneCatalogStorage(path).readMilestoneCatalog());
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
                new Milestone("DESSERT500", "Ice Cream", Integer.MAX_VALUE)));

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

}
