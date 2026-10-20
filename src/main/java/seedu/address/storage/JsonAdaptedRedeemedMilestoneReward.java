package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;

/**
 * Jackson-friendly version of a redeemed milestone reward stored in a person.
 */
class JsonAdaptedRedeemedMilestoneReward {
    public static final String MESSAGE_CONSTRAINTS = "Redeemed milestone rewards cannot be blank.";

    private final String reward;

    /**
     * Constructs a {@code JsonAdaptedRedeemedMilestoneReward} with the given reward.
     *
     */
    @JsonCreator
    public JsonAdaptedRedeemedMilestoneReward(String reward) {
        this.reward = reward;
    }

    @JsonValue
    public String getReward() {
        return reward;
    }

    /**
     * Converts this Jackson-friendly redeemed reward into its model representation.
     *
     * @return the redeemed milestone reward
     */
    public String toModelType() throws IllegalValueException {
        if (reward == null || reward.isBlank()) {
            throw new IllegalValueException(MESSAGE_CONSTRAINTS);
        }
        return reward;
    }
}
