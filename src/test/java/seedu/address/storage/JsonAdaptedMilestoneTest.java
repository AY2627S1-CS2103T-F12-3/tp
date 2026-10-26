package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedMilestone.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
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
    public void constructor_nullMilestone_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new JsonAdaptedMilestone((Milestone) null));
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
                new JsonAdaptedMilestone(" ", VALID_REWARD_DESCRIPTION, VALID_REQUIRED_SCORE);
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

    @Test
    public void jsonRoundTrip_preservesMilestoneAndExpectedStructure() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedMilestone(VALID_MILESTONE));
        JsonNode fields = JsonUtil.fromJsonString(json, JsonNode.class);

        assertEquals(3, fields.size());
        assertEquals(VALID_MILESTONE,
                JsonUtil.fromJsonString(json, JsonAdaptedMilestone.class).toModelType());
    }

    @Test
    public void fromJsonString_wrongFieldTypes_rejectsWithoutCoercion() {
        String[] inputs = {
            "{\"code\":500,\"rewardDescription\":\"Reward\",\"requiredScore\":500}",
            "{\"code\":\"FREE500\",\"rewardDescription\":true,\"requiredScore\":500}",
            "{\"code\":\"FREE500\",\"rewardDescription\":\"Reward\",\"requiredScore\":\"500\"}",
            "{\"code\":\"FREE500\",\"rewardDescription\":\"Reward\",\"requiredScore\":500.9}",
            "{\"code\":\"FREE500\",\"rewardDescription\":\"Reward\",\"requiredScore\":2147483648}"
        };

        for (String json : inputs) {
            assertThrows(JsonProcessingException.class, () ->
                    JsonUtil.fromJsonString(json, JsonAdaptedMilestone.class));
        }
    }
}
