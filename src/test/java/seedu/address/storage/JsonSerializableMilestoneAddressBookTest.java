package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.milestone.Milestone;

public class JsonSerializableMilestoneAddressBookTest {
    private static final Path TEST_DATA_FOLDER =
            Paths.get("src", "test", "data", "JsonSerializableMilestoneAddressBookTest");
    private static final Path TYPICAL_MILESTONES_FILE = TEST_DATA_FOLDER.resolve("typicalMilestonesAddressBook.json");
    private static final Path INVALID_MILESTONE_FILE = TEST_DATA_FOLDER.resolve("invalidMilestoneAddressBook.json");
    private static final Path DUPLICATE_MILESTONE_FILE =
            TEST_DATA_FOLDER.resolve("duplicateMilestoneAddressBook.json");

    @Test
    public void toModelType_typicalMilestonesFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_MILESTONES_FILE,
                JsonSerializableAddressBook.class).orElseThrow();
        AddressBook expectedAddressBook = new AddressBook();
        expectedAddressBook.addMilestone(new Milestone("FREE500", "Free drink", 500));
        expectedAddressBook.addMilestone(new Milestone("DESSERT500", "Free dessert", 500));

        assertEquals(expectedAddressBook, dataFromFile.toModelType());
    }

    @Test
    public void toModelType_invalidMilestoneFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_MILESTONE_FILE,
                JsonSerializableAddressBook.class).orElseThrow();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateMilestones_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_MILESTONE_FILE,
                JsonSerializableAddressBook.class).orElseThrow();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_MILESTONE,
                dataFromFile::toModelType);
    }
}
