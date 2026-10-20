package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.milestone.Milestone;

/**
 * Jackson-friendly version of {@link Milestone}.
 */
class JsonAdaptedMilestone {
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Milestone's %s field is missing!";

    private static final String MESSAGE_INVALID_FIELD_TYPE = "Milestone's %s must be %s.";
    private static final String CODE_FIELD_NAME = "code";
    private static final String REWARD_DESCRIPTION_FIELD_NAME = "rewardDescription";
    private static final String REQUIRED_SCORE_FIELD_NAME = "requiredScore";

    private final String code;
    private final String rewardDescription;
    private final Integer requiredScore;

    /**
     * Constructs a {@code JsonAdaptedMilestone} with the given milestone details.
     */
    public JsonAdaptedMilestone(String code, String rewardDescription, Integer requiredScore) {
        this.code = code;
        this.rewardDescription = rewardDescription;
        this.requiredScore = requiredScore;
    }

    /**
     * Converts a given {@code Milestone} into this class for Jackson use.
     */
    public JsonAdaptedMilestone(Milestone source) {
        requireNonNull(source);
        code = source.getCode();
        rewardDescription = source.getRewardDescription();
        requiredScore = source.getRequiredScore();
    }

    /**
     * Reads JSON fields without allowing Jackson to coerce field types or truncate required scores.
     *
     * @throws IllegalValueException if a field has the wrong JSON type or the score exceeds the integer range.
     */
    @JsonCreator
    public static JsonAdaptedMilestone fromJson(@JsonProperty("code") JsonNode code,
            @JsonProperty("rewardDescription") JsonNode rewardDescription,
            @JsonProperty("requiredScore") JsonNode requiredScore) throws IllegalValueException {
        if (code != null && !code.isNull() && !code.isTextual()) {
            throw new IllegalValueException(String.format(MESSAGE_INVALID_FIELD_TYPE,
                    CODE_FIELD_NAME, "a JSON string"));
        }
        if (rewardDescription != null && !rewardDescription.isNull() && !rewardDescription.isTextual()) {
            throw new IllegalValueException(String.format(MESSAGE_INVALID_FIELD_TYPE,
                    REWARD_DESCRIPTION_FIELD_NAME, "a JSON string"));
        }
        Integer score = null;
        if (requiredScore != null && !requiredScore.isNull()) {
            if (!requiredScore.isIntegralNumber() || !requiredScore.canConvertToInt()) {
                throw new IllegalValueException(String.format(MESSAGE_INVALID_FIELD_TYPE,
                        REQUIRED_SCORE_FIELD_NAME, "a JSON integer within the 32-bit integer range"));
            }
            score = requiredScore.intValue();
        }
        return new JsonAdaptedMilestone(code == null ? null : code.textValue(),
                rewardDescription == null ? null : rewardDescription.textValue(), score);
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
