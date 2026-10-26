package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HelpWindowTest {
    @BeforeAll
    static void initializeJavaFx() {
        JavaFxTestUtil.initializeJavaFx();
    }

    @Test
    void focus_minimizedWindow_restoresWindow() {
        JavaFxTestUtil.runOnFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            helpWindow.getRoot().setIconified(true);

            helpWindow.focus();

            assertFalse(helpWindow.getRoot().isIconified());
            helpWindow.hide();
        });
    }
}
