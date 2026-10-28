package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {
    @TempDir
    public Path testFolder;

    @Test
    public void writeToFile_missingParents_createsReadableFile() throws IOException {
        Path destination = testFolder.resolve("nested/data.json");
        FileUtil.writeToFile(destination, "new contents 中文");
        assertEquals("new contents 中文", FileUtil.readFromFile(destination));
        assertOnlyDestinationRemains(destination);
    }

    @Test
    public void writeToFile_existingFile_replacesContents() throws IOException {
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "old contents that are longer");
        FileUtil.writeToFile(destination, "new");
        assertEquals("new", Files.readString(destination));
        assertOnlyDestinationRemains(destination);
    }

    @Test
    public void writeToFile_existingPosixPermissions_preservesPermissions() throws IOException {
        assumeTrue(Files.getFileAttributeView(testFolder, PosixFileAttributeView.class) != null);
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "previous contents");
        var permissions = PosixFilePermissions.fromString("rw-r-----");
        Files.setPosixFilePermissions(destination, permissions);

        FileUtil.writeToFile(destination, "new contents");

        assertEquals("new contents", Files.readString(destination));
        assertEquals(permissions, Files.getPosixFilePermissions(destination));
        assertOnlyDestinationRemains(destination);
    }

    @Test
    public void writeToFile_readOnlyPosixFile_rejectsSaveWithoutChanges() throws IOException {
        assumeTrue(Files.getFileAttributeView(testFolder, PosixFileAttributeView.class) != null);
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "previous contents");
        var permissions = PosixFilePermissions.fromString("r--r--r--");
        Files.setPosixFilePermissions(destination, permissions);
        try {
            assumeFalse(Files.isWritable(destination), "Privileged users may write to read-only files");
            assertThrows(AccessDeniedException.class, () -> FileUtil.writeToFile(destination, "new contents"));
            assertEquals("previous contents", Files.readString(destination));
            assertEquals(permissions, Files.getPosixFilePermissions(destination));
            assertOnlyDestinationRemains(destination);
        } finally {
            Files.setPosixFilePermissions(destination, PosixFilePermissions.fromString("rw-------"));
        }
    }

    @Test
    public void writeToFile_existingAcl_preservesAcl() throws IOException {
        assumeTrue(Files.getFileAttributeView(testFolder, AclFileAttributeView.class) != null);
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "previous contents");
        AclFileAttributeView attributes = Files.getFileAttributeView(destination, AclFileAttributeView.class);
        var customAcl = new ArrayList<>(attributes.getAcl());
        customAcl.add(0, AclEntry.newBuilder().setType(AclEntryType.ALLOW)
                .setPrincipal(attributes.getOwner()).setPermissions(AclEntryPermission.READ_DATA).build());
        attributes.setAcl(customAcl);
        var permissions = attributes.getAcl();

        FileUtil.writeToFile(destination, "new contents");

        assertEquals("new contents", Files.readString(destination));
        assertEquals(permissions, Files.getFileAttributeView(destination, AclFileAttributeView.class).getAcl());
        assertOnlyDestinationRemains(destination);
    }

    @Test
    public void writeToFile_readOnlyDosFile_rejectsSaveWithoutChanges() throws IOException {
        assumeTrue(Files.getFileAttributeView(testFolder, AclFileAttributeView.class) != null);
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "previous contents");
        DosFileAttributeView attributes = Files.getFileAttributeView(destination, DosFileAttributeView.class);
        assumeTrue(attributes != null);
        attributes.setReadOnly(true);
        try {
            assertThrows(AccessDeniedException.class, () -> FileUtil.writeToFile(destination, "new contents"));
            assertEquals("previous contents", Files.readString(destination));
            assertTrue(attributes.readAttributes().isReadOnly());
            assertOnlyDestinationRemains(destination);
        } finally {
            attributes.setReadOnly(false);
        }
    }

    @Test
    public void writeToFile_copyPermissionsFails_preservesExistingFile() throws IOException {
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "previous contents");
        IOException failure = new IOException("Cannot copy permissions");
        FileUtil.FileOperations operations = new FileUtil.FileOperations() {
            @Override
            void copyPermissions(Path source, Path target) throws IOException {
                throw failure;
            }
        };

        assertSaveFailurePreservesFile(destination, operations, failure);
        assertOnlyDestinationRemains(destination);
    }

    @Test
    public void writeToFile_partialWriteFails_preservesExistingFile() throws IOException {
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "previous contents");
        IOException failure = new IOException("Disk full");
        FileUtil.FileOperations operations = new FileUtil.FileOperations() {
            @Override
            void write(Path file, String content) throws IOException {
                Files.writeString(file, "partial");
                throw failure;
            }
        };

        assertSaveFailurePreservesFile(destination, operations, failure);
        assertOnlyDestinationRemains(destination);
    }

    @Test
    public void writeToFile_firstWriteFails_leavesNoDestination() throws IOException {
        Path destination = testFolder.resolve("data.json");
        FileUtil.FileOperations operations = new FileUtil.FileOperations() {
            @Override
            void write(Path file, String content) throws IOException {
                Files.writeString(file, "partial");
                throw new IOException("Disk full");
            }
        };

        assertThrows(IOException.class, () -> FileUtil.writeToFile(destination, "new contents", operations));
        assertFalse(Files.exists(destination));
        try (var files = Files.list(testFolder)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    public void writeToFile_replacementFails_preservesExistingFile() throws IOException {
        assertReplacementFailurePreservesFile(new IOException("Replacement failed"));
    }

    @Test
    public void writeToFile_atomicMoveUnsupported_preservesExistingFile() throws IOException {
        assertReplacementFailurePreservesFile(
                new AtomicMoveNotSupportedException("temporary", "destination", "Unsupported"));
    }

    @Test
    public void writeToFile_cleanupFails_preservesOriginalFailure() throws IOException {
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "previous contents");
        IOException failure = new IOException("Replacement failed");
        FileUtil.FileOperations operations = new FileUtil.FileOperations() {
            @Override
            void replace(Path source, Path target) throws IOException {
                throw failure;
            }

            @Override
            void deleteIfExists(Path file) throws IOException {
                throw new IOException("Cleanup failed");
            }
        };

        assertSaveFailurePreservesFile(destination, operations, failure);
    }

    @Test
    public void writeToFile_cleanupFailsAfterReplacement_saveStillSucceeds() throws IOException {
        Path destination = testFolder.resolve("data.json");
        FileUtil.FileOperations operations = new FileUtil.FileOperations() {
            @Override
            void deleteIfExists(Path file) throws IOException {
                throw new IOException("Cleanup failed");
            }
        };

        FileUtil.writeToFile(destination, "new contents", operations);
        assertEquals("new contents", Files.readString(destination));
        assertOnlyDestinationRemains(destination);
    }

    @Test
    public void writeToFile_destinationIsDirectory_preservesDirectory() throws IOException {
        Path destination = Files.createDirectory(testFolder.resolve("data.json"));
        assertThrows(IOException.class, () -> FileUtil.writeToFile(destination, "new contents"));
        assertTrue(Files.isDirectory(destination));
        assertOnlyDestinationRemains(destination);
    }

    private void assertReplacementFailurePreservesFile(IOException failure) throws IOException {
        Path destination = testFolder.resolve("data.json");
        Files.writeString(destination, "previous contents");
        FileUtil.FileOperations operations = new FileUtil.FileOperations() {
            @Override
            void replace(Path source, Path target) throws IOException {
                assertEquals(destination.getParent(), source.getParent());
                assertEquals("new contents", Files.readString(source));
                throw failure;
            }
        };

        assertSaveFailurePreservesFile(destination, operations, failure);
        assertOnlyDestinationRemains(destination);
    }

    private void assertSaveFailurePreservesFile(Path destination, FileUtil.FileOperations operations,
            IOException expectedFailure) throws IOException {
        IOException actualFailure = assertThrows(IOException.class, () ->
                FileUtil.writeToFile(destination, "new contents", operations));
        assertSame(expectedFailure, actualFailure);
        assertEquals("previous contents", Files.readString(destination));
    }

    private void assertOnlyDestinationRemains(Path destination) throws IOException {
        try (var files = Files.list(destination.getParent())) {
            assertEquals(List.of(destination), files.toList());
        }
    }
}
