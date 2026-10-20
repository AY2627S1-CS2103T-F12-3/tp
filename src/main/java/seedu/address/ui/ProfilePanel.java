package seedu.address.ui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Person;

/** Displays the selected person's details and milestones beside their recent orders. */
public class ProfilePanel extends UiPart<Region> {
    private static final String FXML = "ProfilePanel.fxml";

    @FXML
    private Label name;

    @FXML
    private Label phone;

    @FXML
    private Label index;

    @FXML
    private StackPane milestonesPlaceholder;

    @FXML
    private StackPane recentOrdersPlaceholder;

    /**
     * Creates a profile panel for {@code person}.
     *
     * @param person The person to display.
     * @param displayedIndex The person's one-based index in the displayed list, or zero if absent.
     * @param milestones The ordered milestone catalog.
     */
    public ProfilePanel(Person person, int displayedIndex, List<Milestone> milestones) {
        super(FXML);
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        index.setText(displayedIndex == 0 ? "Not in current list" : "List index: " + displayedIndex);
        milestonesPlaceholder.getChildren().add(new MilestoneTimeline(person, milestones));
        recentOrdersPlaceholder.getChildren().add(new RecentOrdersPanel(person.getOrderHistory()).getRoot());
    }
}
