package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.milestone.ReadOnlyMilestoneCatalog;

/**
 * Accesses milestone catalog data stored as a JSON file on disk.
 */
public class JsonMilestoneCatalogStorage {
    private static final Logger logger = LogsCenter.getLogger(JsonMilestoneCatalogStorage.class);
    private final Path filePath;

    /**
     * Creates storage that reads and saves milestone catalogs at the given path.
     */
    public JsonMilestoneCatalogStorage(Path filePath) {
        this.filePath = requireNonNull(filePath);
    }

    public Path getMilestoneCatalogFilePath() {
        return filePath;
    }

    /**
     * Returns the saved milestone catalog, or {@code Optional.empty()} if the configured file does not exist.
     *
     * @throws DataLoadingException if the file cannot be read or contains invalid milestone catalog data.
     */
    public Optional<ReadOnlyMilestoneCatalog> readMilestoneCatalog() throws DataLoadingException {
        return readMilestoneCatalog(filePath);
    }

    /**
     * Returns the milestone catalog at the given path, or {@code Optional.empty()} if the file does not exist.
     *
     * @throws DataLoadingException if the file cannot be read or contains invalid milestone catalog data.
     */
    public Optional<ReadOnlyMilestoneCatalog> readMilestoneCatalog(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);
        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        try {
            String json = FileUtil.readFromFile(filePath);
            verifySingleJsonValue(json);
            JsonSerializableMilestoneCatalog savedCatalog =
                    JsonUtil.fromJsonString(json, JsonSerializableMilestoneCatalog.class);
            if (savedCatalog == null) {
                throw new IllegalValueException("Milestone catalog data must not be null.");
            }
            return Optional.of(savedCatalog.toModelType());
        } catch (IOException | IllegalValueException e) {
            logger.info("Could not load milestone catalog from " + filePath + ": " + e.getMessage());
            throw new DataLoadingException(e);
        }
    }

    private static void verifySingleJsonValue(String json) throws IOException {
        try (JsonParser parser = new JsonFactory().createParser(json)) {
            parser.nextToken();
            parser.skipChildren();
            if (parser.nextToken() != null) {
                throw new IOException("Milestone catalog file must contain exactly one JSON value.");
            }
        }
    }

    /**
     * Saves the milestone catalog to the configured path.
     *
     * @throws IOException if the file cannot be written.
     */
    public void saveMilestoneCatalog(ReadOnlyMilestoneCatalog milestoneCatalog) throws IOException {
        saveMilestoneCatalog(milestoneCatalog, filePath);
    }

    /**
     * Saves the milestone catalog to the given path, creating the file and parent directories if necessary.
     *
     * @throws IOException if the file cannot be written.
     */
    public void saveMilestoneCatalog(ReadOnlyMilestoneCatalog milestoneCatalog, Path filePath) throws IOException {
        requireNonNull(milestoneCatalog);
        requireNonNull(filePath);

        JsonSerializableMilestoneCatalog savedCatalog = new JsonSerializableMilestoneCatalog(milestoneCatalog);
        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(savedCatalog, filePath);
    }
}
