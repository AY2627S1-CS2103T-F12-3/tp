package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedMilestone.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.milestone.Milestone;

public class JsonAdaptedMilestoneTest {
    private static final String VALID_CODE = "FREE500";
    private static final String VALID_REWARD_DESCRIPTION = "Free drink";
    private static final Integer VALID_REQUIRED_SCORE = 500;
    private static final Milestone VALID_MILESTONE =
            new Milestone(VALID_CODE, VALID_REWARD_DESCRIPTION, VALID_REQUIRED_SCORE);

    @Test
    public void toModelType_validMilestoneDetails_returnsMilestone() throws Exception {
        JsonAdaptedMilestone milestone = new JsonAdaptedMilestone(VALID_MILESTONE);
        assertEquals(VALID_MILESTONE, milestone.toModelType());
    }

    @Test
    public void toModelType_lowercaseCode_normalizesCode() throws Exception {
        JsonAdaptedMilestone milestone =
                new JsonAdaptedMilestone("free500", VALID_REWARD_DESCRIPTION, VALID_REQUIRED_SCORE);
        assertEquals(VALID_MILESTONE, milestone.toModelType());
    }

    @Test
    public void toModelType_nullCode_throwsIllegalValueException() {
        JsonAdaptedMilestone milestone =
                new JsonAdaptedMilestone(null, VALID_REWARD_DESCRIPTION, VALID_REQUIRED_SCORE);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "code");
        assertThrows(IllegalValueException.class, expectedMessage, milestone::toModelType);
    }

    @Test
    public void toModelType_invalidCode_throwsIllegalValueException() {
        JsonAdaptedMilestone milestone =
                new JsonAdaptedMilestone("FREE-500", VALID_REWARD_DESCRIPTION, VALID_REQUIRED_SCORE);
        assertThrows(IllegalValueException.class, Milestone.MESSAGE_CODE_CONSTRAINTS, milestone::toModelType);
    }

    @Test
    public void toModelType_nullRewardDescription_throwsIllegalValueException() {
        JsonAdaptedMilestone milestone = new JsonAdaptedMilestone(VALID_CODE, null, VALID_REQUIRED_SCORE);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "rewardDescription");
        assertThrows(IllegalValueException.class, expectedMessage, milestone::toModelType);
    }

    @Test
    public void toModelType_blankRewardDescription_throwsIllegalValueException() {
        JsonAdaptedMilestone milestone = new JsonAdaptedMilestone(VALID_CODE, " ", VALID_REQUIRED_SCORE);
        assertThrows(IllegalValueException.class, Milestone.MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS,
                milestone::toModelType);
    }

    @Test
    public void toModelType_nullRequiredScore_throwsIllegalValueException() {
        JsonAdaptedMilestone milestone =
                new JsonAdaptedMilestone(VALID_CODE, VALID_REWARD_DESCRIPTION, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "requiredScore");
        assertThrows(IllegalValueException.class, expectedMessage, milestone::toModelType);
    }

    @Test
    public void toModelType_nonPositiveRequiredScore_throwsIllegalValueException() {
        JsonAdaptedMilestone milestone = new JsonAdaptedMilestone(VALID_CODE, VALID_REWARD_DESCRIPTION, 0);
        assertThrows(IllegalValueException.class, Milestone.MESSAGE_REQUIRED_SCORE_CONSTRAINTS,
                milestone::toModelType);
    }
}
