package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.menu.ReadOnlyMenu;

/**
 * Accesses menu data stored as a JSON file on disk.
 */
public class JsonMenuStorage {
    private static final Logger logger = LogsCenter.getLogger(JsonMenuStorage.class);
    private final Path filePath;

    /**
     * Creates storage that reads and saves menus at the given path.
     */
    public JsonMenuStorage(Path filePath) {
        this.filePath = requireNonNull(filePath);
    }

    public Path getMenuFilePath() {
        return filePath;
    }

    /**
     * Returns the saved menu, or {@code Optional.empty()} if the configured file does not exist.
     *
     * @throws DataLoadingException if the file cannot be read or contains invalid menu data.
     */
    public Optional<ReadOnlyMenu> readMenu() throws DataLoadingException {
        return readMenu(filePath);
    }

    /**
     * Returns the menu at the given path, or {@code Optional.empty()} if the file does not exist.
     *
     * @throws DataLoadingException if the file cannot be read or contains invalid menu data.
     */
    public Optional<ReadOnlyMenu> readMenu(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);
        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        try {
            JsonSerializableMenu savedMenu = JsonUtil.fromJsonString(
                    FileUtil.readFromFile(filePath), JsonSerializableMenu.class);
            if (savedMenu == null) {
                throw new IllegalValueException("Menu data must not be null.");
            }
            return Optional.of(savedMenu.toModelType());
        } catch (IOException | IllegalValueException e) {
            logger.info("Could not load menu from " + filePath + ": " + e.getMessage());
            throw new DataLoadingException(e);
        }
    }

    /**
     * Saves the menu to the configured path.
     *
     * @throws IOException if the file cannot be written.
     */
    public void saveMenu(ReadOnlyMenu menu) throws IOException {
        saveMenu(menu, filePath);
    }

    /**
     * Saves the menu to the given path, creating the file and parent directories if necessary.
     *
     * @throws IOException if the file cannot be written.
     */
    public void saveMenu(ReadOnlyMenu menu, Path filePath) throws IOException {
        requireNonNull(menu);
        requireNonNull(filePath);

        JsonSerializableMenu savedMenu = new JsonSerializableMenu(menu);
        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(savedMenu, filePath);
    }
}
