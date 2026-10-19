package seedu.address.model.reward;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the history of milestone rewards redeemed by a member.
 * Guarantees: The history is immutable; adding or deleting a reward returns a new
 * {@code RedeemedMilestoneRewardHistory}.
 */
public class RedeemedMilestoneRewardHistory {
    private final ArrayList<String> redeemedMilestoneRewards;

    /**
     * Constructs an empty {@code RedeemedMilestoneRewardHistory}.
     */
    public RedeemedMilestoneRewardHistory() {
        this.redeemedMilestoneRewards = new ArrayList<>();
    }

    private RedeemedMilestoneRewardHistory(List<String> redeemedMilestoneRewards) {
        this.redeemedMilestoneRewards = new ArrayList<>(redeemedMilestoneRewards);
    }

    /**
     * Returns a copy of all the redeemed milestone rewards.
     *
     * @return a list of redeemed milestone rewards in the history
     */
    public List<String> getRedeemedMilestoneRewards() {
        return new ArrayList<>(redeemedMilestoneRewards);
    }

    /**
     * Returns a new history containing the given redeemed milestone reward.
     * This history is not modified.
     *
     * @param redeemedMilestoneReward the redeemed milestone reward to add
     * @return a new history containing the reward
     * @throws NullPointerException if {@code redeemedMilestoneReward} is null
     */
    public RedeemedMilestoneRewardHistory addRedeemedMilestoneReward(String redeemedMilestoneReward) {
        ArrayList<String> updatedRewards = new ArrayList<>(redeemedMilestoneRewards);
        updatedRewards.add(requireNonNull(redeemedMilestoneReward));
        return new RedeemedMilestoneRewardHistory(updatedRewards);
    }

    /**
     * Returns a new history without the redeemed reward at the given zero-based index.
     * This history is not modified.
     *
     * @param zeroBasedIndex the zero-based index of the reward to delete
     * @return a new history without the specified redeemed reward
     * @throws IndexOutOfBoundsException if {@code zeroBasedIndex} is outside this history
     */
    public RedeemedMilestoneRewardHistory deleteRedeemedMilestoneReward(int zeroBasedIndex) {
        ArrayList<String> updatedRewards = new ArrayList<>(redeemedMilestoneRewards);
        updatedRewards.remove(zeroBasedIndex);
        return new RedeemedMilestoneRewardHistory(updatedRewards);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RedeemedMilestoneRewardHistory otherRedeemedMilestoneRewardHistory)) {
            return false;
        }
        return redeemedMilestoneRewards.equals(otherRedeemedMilestoneRewardHistory.redeemedMilestoneRewards);
    }

    @Override
    public int hashCode() {
        return redeemedMilestoneRewards.toString().hashCode();
    }

    @Override
    public String toString() {
        return redeemedMilestoneRewards.toString();
    }
}
