package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.milestone.Milestone;

/**
 * Jackson-friendly version of {@link Milestone}.
 */
class JsonAdaptedMilestone {
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Milestone's %s field is missing!";

    private static final String CODE_FIELD_NAME = "code";
    private static final String REWARD_DESCRIPTION_FIELD_NAME = "rewardDescription";
    private static final String REQUIRED_SCORE_FIELD_NAME = "requiredScore";

    private final String code;
    private final String rewardDescription;
    private final Integer requiredScore;

    /**
     * Constructs a {@code JsonAdaptedMilestone} with the given milestone details.
     */
    @JsonCreator
    public JsonAdaptedMilestone(@JsonProperty("code") String code,
            @JsonProperty("rewardDescription") String rewardDescription,
            @JsonProperty("requiredScore") Integer requiredScore) {
        this.code = code;
        this.rewardDescription = rewardDescription;
        this.requiredScore = requiredScore;
    }

    /**
     * Converts a given {@code Milestone} into this class for Jackson use.
     */
    public JsonAdaptedMilestone(Milestone source) {
        code = source.getCode();
        rewardDescription = source.getRewardDescription();
        requiredScore = source.getRequiredScore();
    }

    /**
     * Converts this Jackson-friendly milestone into the model's {@code Milestone} object.
     *
     * @throws IllegalValueException if a field is missing or violates milestone constraints.
     */
    public Milestone toModelType() throws IllegalValueException {
        if (code == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, CODE_FIELD_NAME));
        }
        if (!Milestone.isValidCode(code)) {
            throw new IllegalValueException(Milestone.MESSAGE_CODE_CONSTRAINTS);
        }

        if (rewardDescription == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, REWARD_DESCRIPTION_FIELD_NAME));
        }
        if (!Milestone.isValidRewardDescription(rewardDescription)) {
            throw new IllegalValueException(Milestone.MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS);
        }

        if (requiredScore == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, REQUIRED_SCORE_FIELD_NAME));
        }
        if (!Milestone.isValidRequiredScore(requiredScore)) {
            throw new IllegalValueException(Milestone.MESSAGE_REQUIRED_SCORE_CONSTRAINTS);
        }

        return new Milestone(code, rewardDescription, requiredScore);
    }
}
