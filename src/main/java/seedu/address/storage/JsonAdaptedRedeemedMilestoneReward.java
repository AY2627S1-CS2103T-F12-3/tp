package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.milestone.Milestone;

/**
 * Jackson-friendly version of a redeemed milestone reward stored in a person.
 */
class JsonAdaptedRedeemedMilestoneReward {
    public static final String MESSAGE_CONSTRAINTS = "Redeemed milestone data cannot be null.";

    private final JsonAdaptedMilestone reward;

    /**
     * Constructs a {@code JsonAdaptedRedeemedMilestoneReward} with the given milestone.
     *
     * @param reward the redeemed milestone to adapt
     */
    public JsonAdaptedRedeemedMilestoneReward(Milestone reward) {
        requireNonNull(reward);
        this.reward = new JsonAdaptedMilestone(reward);
    }

    @JsonCreator
    public JsonAdaptedRedeemedMilestoneReward(JsonAdaptedMilestone reward) {
        this.reward = reward;
    }

    @JsonValue
    public JsonAdaptedMilestone getReward() {
        return reward;
    }

    /**
     * Converts this Jackson-friendly redeemed milestone into its model representation.
     *
     * @return the redeemed milestone
     */
    public Milestone toModelType() throws IllegalValueException {
        if (reward == null) {
            throw new IllegalValueException(MESSAGE_CONSTRAINTS);
        }
        return reward.toModelType();
    }
}
