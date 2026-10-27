package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path EMPTY_ADDRESS_BOOK_FILE = TEST_DATA_FOLDER.resolve("emptyAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_sameNameDifferentPhone_preservesBothPersons() throws Exception {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(TypicalPersons.ALICE);
        addressBook.addPerson(new PersonBuilder(TypicalPersons.ALICE).withPhone("61234567").build());
        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(addressBook));
        JsonSerializableAddressBook reloaded = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class);

        assertEquals(addressBook, reloaded.toModelType());
    }

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_emptyAddressBookFile_returnsEmptyAddressBook() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(EMPTY_ADDRESS_BOOK_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        assertEquals(new AddressBook(), dataFromFile.toModelType());
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void jsonRoundTrip_excludesMilestonesField() throws Exception {
        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(new AddressBook()));
        JsonNode fields = JsonUtil.fromJsonString(json, JsonNode.class);

        assertEquals(1, fields.size());
        assertFalse(fields.has("milestones"));
    }

}
