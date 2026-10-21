package seedu.address.ui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Person;

/** Displays the selected member's profile, or the milestone catalog when no member is selected. */
public class ProfilePanel extends UiPart<Region> {
    private static final String FXML = "ProfilePanel.fxml";
    private final List<Milestone> milestones;

    @FXML
    private Label noPersonSelected;

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
     * Creates a profile panel backed by the ordered milestone catalog.
     *
     * @param milestones The ordered milestone catalog.
     */
    public ProfilePanel(List<Milestone> milestones) {
        super(FXML);
        this.milestones = milestones;
        showNoPerson();
    }

    /** Shows the given member's profile. */
    void showPerson(Person person, int displayedIndex) {
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        index.setText(displayedIndex == 0 ? "Not in current list" : "List index: " + displayedIndex);
        milestonesPlaceholder.getChildren().setAll(new MilestoneTimeline(person, milestones));
        recentOrdersPlaceholder.getChildren().setAll(new RecentOrdersPanel(person).getRoot());
        showPersonDetails(true);
    }

    /** Shows the milestone catalog without member progress. */
    void showNoPerson() {
        milestonesPlaceholder.getChildren().setAll(new MilestoneTimeline(milestones));
        recentOrdersPlaceholder.getChildren().setAll(new RecentOrdersPanel().getRoot());
        showPersonDetails(false);
    }

    private void showPersonDetails(boolean isSelected) {
        noPersonSelected.setVisible(!isSelected);
        noPersonSelected.setManaged(!isSelected);
        name.setVisible(isSelected);
        name.setManaged(isSelected);
        phone.setVisible(isSelected);
        phone.setManaged(isSelected);
        index.setVisible(isSelected);
        index.setManaged(isSelected);
    }
}
