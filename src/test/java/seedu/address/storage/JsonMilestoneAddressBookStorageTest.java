package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.milestone.Milestone;

public class JsonMilestoneAddressBookStorageTest {
    @TempDir
    public Path testFolder;

    @Test
    public void readAndSaveAddressBook_withMilestones_preservesMilestones() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = new AddressBook();
        original.addMilestone(new Milestone("FREE500", "Free drink", 500));
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        storage.saveAddressBook(original);
        ReadOnlyAddressBook readBack = storage.readAddressBook().orElseThrow();

        assertEquals(original, new AddressBook(readBack));
    }
}
