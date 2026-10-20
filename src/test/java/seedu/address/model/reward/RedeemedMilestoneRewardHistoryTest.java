package seedu.address.model.reward;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.milestone.Milestone;

public class RedeemedMilestoneRewardHistoryTest {
    private static final Milestone FIRST_REWARD = new Milestone("FIRST", "Free drink", 10);
    private static final Milestone SECOND_REWARD = new Milestone("SECOND", "Free dessert", 20);

    @Test
    public void constructor_emptyHistory_hasNoMilestoneRewards() {
        assertTrue(new RedeemedMilestoneRewardHistory().getRedeemedMilestoneRewards().isEmpty());
    }

    @Test
    public void addRedeemedMilestoneReward_validReward_returnsNewHistoryAndPreservesOriginal() {
        RedeemedMilestoneRewardHistory original = new RedeemedMilestoneRewardHistory();
        RedeemedMilestoneRewardHistory updated = original.addRedeemedMilestoneReward(FIRST_REWARD);

        assertTrue(original.getRedeemedMilestoneRewards().isEmpty());
        assertEquals(List.of(FIRST_REWARD), updated.getRedeemedMilestoneRewards());
        assertNotSame(original, updated);
    }

    @Test
    public void addRedeemedMilestoneReward_multipleRewards_preservesInsertionOrder() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(FIRST_REWARD)
                .addRedeemedMilestoneReward(SECOND_REWARD);

        assertEquals(List.of(FIRST_REWARD, SECOND_REWARD), history.getRedeemedMilestoneRewards());
    }

    @Test
    public void getRedeemedMilestoneRewards_mutateReturnedList_preservesHistory() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(FIRST_REWARD);
        List<Milestone> returnedRewards = history.getRedeemedMilestoneRewards();

        returnedRewards.clear();

        assertEquals(List.of(FIRST_REWARD), history.getRedeemedMilestoneRewards());
        assertNotSame(returnedRewards, history.getRedeemedMilestoneRewards());
    }

    @Test
    public void contains_existingMilestone_returnsTrue() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(FIRST_REWARD);

        assertTrue(history.contains(FIRST_REWARD));
    }

    @Test
    public void contains_sameCodeDifferentDetails_returnsTrue() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(FIRST_REWARD);
        Milestone sameCodeMilestone = new Milestone("first", "Different reward", 50);

        assertTrue(history.contains(sameCodeMilestone));
    }

    @Test
    public void contains_absentMilestone_returnsFalse() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(FIRST_REWARD);

        assertFalse(history.contains(SECOND_REWARD));
    }

    @Test
    public void addRedeemedMilestoneReward_nullReward_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new RedeemedMilestoneRewardHistory()
                        .addRedeemedMilestoneReward(null));
    }

    @Test
    public void addRedeemedMilestoneReward_duplicateCode_throwsIllegalArgumentException() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(FIRST_REWARD);
        Milestone duplicateMilestone = new Milestone("first", "Different reward", 50);

        assertThrows(IllegalArgumentException.class, () ->
                history.addRedeemedMilestoneReward(duplicateMilestone));
    }

    @Test
    public void deleteRedeemedMilestoneReward_validMilestone_returnsNewHistoryAndPreservesOriginal() {
        RedeemedMilestoneRewardHistory original = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(FIRST_REWARD)
                .addRedeemedMilestoneReward(SECOND_REWARD);
        RedeemedMilestoneRewardHistory updated = original.deleteRedeemedMilestoneReward(FIRST_REWARD);

        assertEquals(List.of(FIRST_REWARD, SECOND_REWARD), original.getRedeemedMilestoneRewards());
        assertEquals(List.of(SECOND_REWARD), updated.getRedeemedMilestoneRewards());
        assertNotSame(original, updated);
    }

    @Test
    public void deleteRedeemedMilestoneReward_absentMilestone_throwsAndPreservesHistory() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(FIRST_REWARD);

        assertThrows(IllegalArgumentException.class, () -> history.deleteRedeemedMilestoneReward(SECOND_REWARD));
        assertEquals(List.of(FIRST_REWARD), history.getRedeemedMilestoneRewards());
    }

    @Test
    public void equals_sameRewards_returnsTrue() {
        RedeemedMilestoneRewardHistory first = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(FIRST_REWARD);
        RedeemedMilestoneRewardHistory second = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(FIRST_REWARD);

        assertTrue(first.equals(second));
        assertEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(null));
    }
}
