package seedu.address.model.milestone;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Stores the milestone rewards available in the loyalty program.
 */
public class MilestoneCatalog implements ReadOnlyMilestoneCatalog {
    private final UniqueMilestoneList milestones = new UniqueMilestoneList();

    /**
     * Creates an empty milestone catalog.
     */
    public MilestoneCatalog() {}

    /**
     * Creates a milestone catalog containing a copy of the milestones in {@code toBeCopied}.
     * Subsequent changes to either catalog do not affect the other.
     */
    public MilestoneCatalog(ReadOnlyMilestoneCatalog toBeCopied) {
        resetData(toBeCopied);
    }

    /**
     * Replaces the contents with a sorted copy of {@code milestones}.
     * The list and its milestones must not be null, and milestone codes must be unique.
     * Invalid input leaves this catalog unchanged.
     */
    public void setMilestones(List<Milestone> milestones) {
        this.milestones.setMilestones(milestones);
    }

    /**
     * Replaces this catalog's contents with a copy of the milestones in {@code newData}.
     * Invalid input leaves this catalog unchanged.
     */
    public void resetData(ReadOnlyMilestoneCatalog newData) {
        requireNonNull(newData);
        setMilestones(newData.getMilestoneList());
    }

    /**
     * Returns true if a milestone with the same case-insensitive code as {@code milestone} exists in the catalog.
     */
    public boolean hasMilestone(Milestone milestone) {
        return milestones.contains(milestone);
    }

    /**
     * Adds a milestone to the catalog. Its code must not already exist in the catalog.
     */
    public void addMilestone(Milestone milestone) {
        milestones.add(milestone);
    }

    /**
     * Replaces {@code target} with {@code editedMilestone} and restores the catalog's sorted order.
     * The target must equal an existing milestone, and the milestone code cannot be changed.
     */
    public void setMilestone(Milestone target, Milestone editedMilestone) {
        milestones.setMilestone(target, editedMilestone);
    }

    /**
     * Removes the milestone equal to {@code milestone}, which must exist in the catalog.
     */
    public void removeMilestone(Milestone milestone) {
        milestones.remove(milestone);
    }

    @Override
    public ObservableList<Milestone> getMilestoneList() {
        return milestones.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MilestoneCatalog otherCatalog)) {
            return false;
        }
        return milestones.equals(otherCatalog.milestones);
    }

    @Override
    public int hashCode() {
        return milestones.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("milestones", milestones)
                .toString();
    }
}
