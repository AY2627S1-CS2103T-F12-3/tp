package seedu.address.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;

/** Displays a member's name, phone number, and list position, or the unselected message. */
public class PersonDetailsPanel extends VBox {

    /**
     * Creates a panel showing the selected member's details.
     *
     * @param person The member whose details are displayed.
     * @param displayedIndex The member's one-based position in the current list, or zero if absent.
     */
    public PersonDetailsPanel(Person person, int displayedIndex) {
        assert displayedIndex >= 0 : "Displayed index must be non-negative";
        setSpacing(4);
        Label name = new Label(person.getName().fullName);
        name.getStyleClass().add("profile-name");
        name.setWrapText(true);

        Label phone = new Label(person.getPhone().value);
        phone.getStyleClass().add("profile-phone");

        String indexText = displayedIndex == 0 ? "Not in current list" : "List index: " + displayedIndex;
        Label index = new Label(indexText);
        index.getStyleClass().add("profile-index");
        index.setWrapText(true);

        getChildren().addAll(name, phone, index);
    }

    /** Creates a panel that says no member is selected. */
    public PersonDetailsPanel() {
        Label message = new Label("No member selected");
        message.getStyleClass().add("no-member-selected-label");
        getChildren().add(message);
    }
}
