package seedu.address.ui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Person;

/** Coordinates the person details, milestone timeline, and recent orders in one profile view. */
public class ProfilePanel extends UiPart<Region> {
    private static final String FXML = "ProfilePanel.fxml";
    private final List<Milestone> milestones;

    @FXML
    private StackPane personDetailsPlaceholder;

    @FXML
    private StackPane milestonesPlaceholder;

    @FXML
    private StackPane recentOrdersPlaceholder;

    /**
     * Creates a profile panel with no member selected and the given milestone catalog.
     *
     * @param milestones The ordered milestone catalog.
     */
    public ProfilePanel(List<Milestone> milestones) {
        super(FXML);
        this.milestones = milestones;
        showNoPerson();
    }

    /**
     * Replaces all three profile sections with the selected member's information.
     *
     * @param person The member to display.
     * @param displayedIndex The member's one-based position in the current list, or zero if absent.
     */
    void showPerson(Person person, int displayedIndex) {
        personDetailsPlaceholder.getChildren().setAll(new PersonDetailsPanel(person, displayedIndex));
        milestonesPlaceholder.getChildren().setAll(new MilestoneTimeline(person, milestones));
        recentOrdersPlaceholder.getChildren().setAll(new RecentOrdersPanel(person).getRoot());
    }

    /** Shows the empty person and order states alongside milestones without member progress. */
    void showNoPerson() {
        personDetailsPlaceholder.getChildren().setAll(new PersonDetailsPanel());
        milestonesPlaceholder.getChildren().setAll(new MilestoneTimeline(milestones));
        recentOrdersPlaceholder.getChildren().setAll(new RecentOrdersPanel().getRoot());
    }
}
