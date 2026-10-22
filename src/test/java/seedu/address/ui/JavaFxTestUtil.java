package seedu.address.ui;

import java.util.concurrent.FutureTask;

import javafx.application.Platform;

/** Starts JavaFX once and runs UI assertions on its application thread. */
final class JavaFxTestUtil {
    private static boolean isInitialized;

    private JavaFxTestUtil() {
    }

    static synchronized void initializeJavaFx() {
        if (!isInitialized) {
            Platform.startup(() -> { });
            isInitialized = true;
        }
    }

    static void runOnFxThread(Runnable action) {
        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        try {
            task.get();
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
}
