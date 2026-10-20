package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.milestone.Milestone;

public class JsonAdaptedRedeemedMilestoneRewardTest {
    private static final Milestone VALID_REWARD = new Milestone("WELCOME", "Free drink", 10);

    @Test
    public void toModelType_nullReward_throwsIllegalValueException() {
        assertInvalidReward(null, JsonAdaptedRedeemedMilestoneReward.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void toModelType_validMilestone_returnsMilestone() throws Exception {
        assertEquals(VALID_REWARD,
                new JsonAdaptedRedeemedMilestoneReward(VALID_REWARD).toModelType());
    }

    private static void assertInvalidReward(JsonAdaptedMilestone reward, String expectedMessage) {
        JsonAdaptedRedeemedMilestoneReward adaptedReward =
                new JsonAdaptedRedeemedMilestoneReward(reward);

        assertThrows(IllegalValueException.class, expectedMessage, adaptedReward::toModelType);
    }
}
