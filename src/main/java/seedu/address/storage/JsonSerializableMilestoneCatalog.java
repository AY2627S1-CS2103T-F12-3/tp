package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.MilestoneCatalog;
import seedu.address.model.milestone.ReadOnlyMilestoneCatalog;

/**
 * A snapshot of a milestone catalog that is serializable to JSON format.
 */
@JsonRootName(value = "milestoneCatalog")
class JsonSerializableMilestoneCatalog {
    public static final String MESSAGE_MISSING_MILESTONES =
            "Milestone catalog's milestones field is missing or null.";
    public static final String MESSAGE_NULL_MILESTONE = "Milestone catalog contains a null milestone.";
    public static final String MESSAGE_DUPLICATE_MILESTONE =
            "Milestone catalog contains duplicate milestone codes.";

    private final List<JsonAdaptedMilestone> milestones;

    /**
     * Constructs a saved milestone catalog from the given milestones, copying the list.
     * Missing data is retained for validation by {@link #toModelType()}.
     */
    @JsonCreator
    public JsonSerializableMilestoneCatalog(
            @JsonProperty("milestones") List<JsonAdaptedMilestone> milestones) {
        this.milestones = milestones == null ? null : new ArrayList<>(milestones);
    }

    /**
     * Converts a read-only milestone catalog into a snapshot for Jackson use.
     * Subsequent changes to the source do not affect this snapshot.
     */
    public JsonSerializableMilestoneCatalog(ReadOnlyMilestoneCatalog source) {
        requireNonNull(source);
        milestones = source.getMilestoneList().stream()
                .map(JsonAdaptedMilestone::new)
                .collect(Collectors.toList());
    }

    /**
     * Reconstructs a milestone catalog in ascending required-score and code order.
     *
     * @throws IllegalValueException if the milestone list is missing, a milestone is invalid, or codes are
     *         duplicated.
     */
    public MilestoneCatalog toModelType() throws IllegalValueException {
        if (milestones == null) {
            throw new IllegalValueException(MESSAGE_MISSING_MILESTONES);
        }
        MilestoneCatalog catalog = new MilestoneCatalog();
        for (JsonAdaptedMilestone adaptedMilestone : milestones) {
            if (adaptedMilestone == null) {
                throw new IllegalValueException(MESSAGE_NULL_MILESTONE);
            }
            Milestone milestone = adaptedMilestone.toModelType();
            if (catalog.hasMilestone(milestone)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_MILESTONE);
            }
            catalog.addMilestone(milestone);
        }
        return catalog;
    }
}
