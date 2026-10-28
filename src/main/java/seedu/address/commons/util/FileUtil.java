package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";
    private static final Logger logger = LogsCenter.getLogger(FileUtil.class);

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes the given string to a sibling temporary file, then atomically replaces the destination.
     * Creates missing parent directories, but does not create the destination until replacement succeeds.
     * Does not fall back to a non-atomic replacement if atomic replacement is unsupported.
     * Rejects existing files that are not writable and preserves their POSIX permissions or Windows ACLs.
     *
     * @throws IOException if writing or atomic replacement fails.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        writeToFile(file, content, new FileOperations());
    }

    static void writeToFile(Path file, String content, FileOperations operations) throws IOException {
        Path destination = file.toAbsolutePath();
        if (Files.isDirectory(destination)) {
            throw new IOException("Cannot save data to a directory: " + destination);
        }
        if (Files.exists(destination) && !Files.isWritable(destination)) {
            throw new AccessDeniedException(destination.toString());
        }
        createParentDirsOfFile(destination);
        Path temporaryFile = Files.createTempFile(destination.getParent(), ".saving-", ".tmp");
        try {
            operations.write(temporaryFile, content);
            operations.copyPermissions(destination, temporaryFile);
            operations.replace(temporaryFile, destination);
        } finally {
            try {
                operations.deleteIfExists(temporaryFile);
            } catch (IOException e) {
                // Cleanup must not hide a save failure or report an already completed save as failed.
                logger.log(Level.WARNING, "Could not remove temporary save file: " + temporaryFile, e);
            }
        }
    }

    /** File operations separated to allow deterministic tests of failures during saving. */
    static class FileOperations {
        void write(Path file, String content) throws IOException {
            Files.write(file, content.getBytes(CHARSET));
        }

        void copyPermissions(Path source, Path target) throws IOException {
            if (!Files.exists(source)) {
                return;
            }
            PosixFileAttributeView posixView = Files.getFileAttributeView(source, PosixFileAttributeView.class);
            if (posixView != null) {
                Files.setPosixFilePermissions(target, posixView.readAttributes().permissions());
                return;
            }
            AclFileAttributeView aclView = Files.getFileAttributeView(source, AclFileAttributeView.class);
            if (aclView != null) {
                Files.getFileAttributeView(target, AclFileAttributeView.class).setAcl(aclView.getAcl());
            }
        }

        void replace(Path source, Path destination) throws IOException {
            Files.move(source, destination, StandardCopyOption.ATOMIC_MOVE);
        }

        void deleteIfExists(Path file) throws IOException {
            Files.deleteIfExists(file);
        }
    }
}
