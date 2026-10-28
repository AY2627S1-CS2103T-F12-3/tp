package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonMenuStorage;
import seedu.address.storage.JsonMilestoneCatalogStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class MemberLoadingTest {
    @TempDir
    public Path testFolder;

    @Test
    public void initMembers_missingFile_usesSampleData() {
        assertEquals(SampleDataUtil.getSampleAddressBook(), new MainApp().initMembers(createStorage()));
    }

    @Test
    public void initMembers_unusualLegacyPhone_preservesMemberAndHistory() throws Exception {
        AddressBook saved = new AddressBook();
        saved.addPerson(new PersonBuilder().withPhone("9482424").build());
        saved.addPerson(new PersonBuilder().withPhone("（86）12345678").build());
        saved.addPerson(new PersonBuilder().withPhone("Not available").build());
        new JsonAddressBookStorage(testFolder.resolve("members.json")).saveAddressBook(saved);
        ReadOnlyAddressBook loaded = new MainApp().initMembers(createStorage());
        assertEquals(saved, loaded);
    }

    @Test
    public void initMembers_normalizedPhoneCollision_stopsBeforeOverwritingFile() throws Exception {
        AddressBook saved = new AddressBook();
        saved.addPerson(new PersonBuilder().withPhone("91234567").build());
        saved.addPerson(new PersonBuilder().withPhone("81234567").build());
        Path members = testFolder.resolve("members.json");
        new JsonAddressBookStorage(members).saveAddressBook(saved);
        String json = Files.readString(members).replace("81234567", "6591234567");
        assertStartupStopsWithoutChangingFile(json);
    }

    @Test
    public void initMembers_unreadableData_stopsBeforeOverwritingFile() throws Exception {
        for (String json : List.of("not JSON", "{\"persons\":[{\"name\":\"Alice\",\"phone\":\" \"}]}")) {
            assertStartupStopsWithoutChangingFile(json);
        }
    }

    private void assertStartupStopsWithoutChangingFile(String json) throws Exception {
        Path members = testFolder.resolve("members.json");
        Files.writeString(members, json);
        assertThrows(IllegalStateException.class, String.format(MainApp.MESSAGE_MEMBER_LOAD_FAILURE, members), () ->
                new MainApp().initMembers(createStorage()));
        assertEquals(json, Files.readString(members));
    }

    private Storage createStorage() {
        return new StorageManager(new JsonAddressBookStorage(testFolder.resolve("members.json")),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")),
                new JsonMenuStorage(testFolder.resolve("menu.json")),
                new JsonMilestoneCatalogStorage(testFolder.resolve("milestones.json")));
    }
}
