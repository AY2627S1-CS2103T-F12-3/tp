package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;

public class JsonAdaptedRedeemedMilestoneRewardTest {
    private static final String VALID_REWARD = "Free drink (20% off)!";

    @Test
    public void toModelType_validReward_returnsReward() throws Exception {
        assertEquals(VALID_REWARD,
                new JsonAdaptedRedeemedMilestoneReward(VALID_REWARD).toModelType());
    }

    @Test
    public void constructor_validReward_roundTripsValue() throws Exception {
        assertEquals(VALID_REWARD,
                new JsonAdaptedRedeemedMilestoneReward(VALID_REWARD).toModelType());
    }

    @Test
    public void toModelType_nullReward_throwsIllegalValueException() {
        assertInvalidReward(null, JsonAdaptedRedeemedMilestoneReward.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void toModelType_emptyReward_throwsIllegalValueException() {
        assertInvalidReward("", JsonAdaptedRedeemedMilestoneReward.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void toModelType_blankReward_throwsIllegalValueException() {
        assertInvalidReward("   ", JsonAdaptedRedeemedMilestoneReward.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void toModelType_rewardWithSpecialCharacters_returnsReward() throws Exception {
        String reward = "Birthday reward: 50% off / free delivery";

        assertEquals(reward, new JsonAdaptedRedeemedMilestoneReward(reward).toModelType());
    }

    @Test
    public void toModelType_multipleInvalidValues_reportsRewardConstraintFirst() {
        assertInvalidReward(null, JsonAdaptedRedeemedMilestoneReward.MESSAGE_CONSTRAINTS);
    }

    private static void assertInvalidReward(String reward, String expectedMessage) {
        JsonAdaptedRedeemedMilestoneReward adaptedReward =
                new JsonAdaptedRedeemedMilestoneReward(reward);

        assertThrows(IllegalValueException.class, expectedMessage, adaptedReward::toModelType);
    }
}
