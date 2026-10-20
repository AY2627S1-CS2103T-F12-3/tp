package seedu.address.model.milestone;

import javafx.collections.ObservableList;

/**
 * Provides read-only access to a milestone catalog.
 */
public interface ReadOnlyMilestoneCatalog {
    /**
     * Returns an unmodifiable observable view of the milestones in ascending required-score and code order.
     * Milestones have unique case-insensitive codes, and the view reflects subsequent catalog changes.
     */
    ObservableList<Milestone> getMilestoneList();
}
