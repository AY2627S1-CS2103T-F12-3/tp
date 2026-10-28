package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.testutil.SerializableTestClass;
import seedu.address.testutil.TestUtil;

/**
 * Tests JSON Read and Write
 */
public class JsonUtilTest {

    private static final Path SERIALIZATION_FILE = TestUtil.getFilePathInSandboxFolder("serialize.json");

    @TempDir
    public Path testFolder;

    @Test
    public void readAndSaveJsonFile_leftoverTemporaryFile_ignoresItAndSavesNormally() throws Exception {
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "[\"saved entry\"]");
        Path leftoverFile = Files.createTempFile(testFolder, ".saving-", ".tmp");
        String incompleteJson = "[\"unfinished";
        Files.writeString(leftoverFile, incompleteJson);

        assertArrayEquals(new String[]{"saved entry"},
                JsonUtil.readJsonFile(destination, String[].class).orElseThrow());

        JsonUtil.saveJsonFile(List.of("updated entry", "new entry"), destination);

        assertArrayEquals(new String[]{"updated entry", "new entry"},
                JsonUtil.readJsonFile(destination, String[].class).orElseThrow());
        assertEquals(incompleteJson, Files.readString(leftoverFile));
        try (var files = Files.list(testFolder)) {
            assertEquals(2, files.count());
        }
    }

    @Test
    public void saveJsonFile_serializationFails_preservesExistingFile() throws IOException {
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "previous contents");

        assertThrows(IOException.class, () -> JsonUtil.saveJsonFile(new UnserializableClass(), destination));
        assertEquals("previous contents", Files.readString(destination));
        try (var files = Files.list(testFolder)) {
            assertEquals(1, files.count());
        }
    }

    @Test
    public void saveJsonFile_firstSerializationFails_leavesNoDestination() {
        Path destination = testFolder.resolve("nested/data.json");
        assertThrows(IOException.class, () -> JsonUtil.saveJsonFile(new UnserializableClass(), destination));
        assertFalse(Files.exists(destination.getParent()));
    }

    private static class UnserializableClass {
        @JsonProperty
        public String getValue() throws IOException {
            throw new IOException("Serialization failed");
        }
    }

    @Test
    public void serializeObjectToJsonFile_noExceptionThrown() throws IOException {
        SerializableTestClass serializableTestClass = new SerializableTestClass();
        serializableTestClass.setTestValues();

        JsonUtil.serializeObjectToJsonFile(SERIALIZATION_FILE, serializableTestClass);

        assertEquals(FileUtil.readFromFile(SERIALIZATION_FILE), SerializableTestClass.JSON_STRING_REPRESENTATION);
    }

    @Test
    public void deserializeObjectFromJsonFile_noExceptionThrown() throws IOException {
        FileUtil.writeToFile(SERIALIZATION_FILE, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass serializableTestClass = JsonUtil
                .deserializeObjectFromJsonFile(SERIALIZATION_FILE, SerializableTestClass.class);

        assertEquals(serializableTestClass.getName(), SerializableTestClass.getNameTestValue());
        assertEquals(serializableTestClass.getListOfLocalDateTimes(), SerializableTestClass.getListTestValues());
        assertEquals(serializableTestClass.getMapOfIntegerToString(), SerializableTestClass.getHashMapTestValues());
    }

    //TODO: @Test jsonUtil_readJsonStringToObjectInstance_correctObject()

    //TODO: @Test jsonUtil_writeThenReadObjectToJson_correctObject()
}
