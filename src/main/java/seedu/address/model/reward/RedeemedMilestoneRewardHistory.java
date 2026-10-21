package seedu.address.model.reward;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.address.model.milestone.Milestone;

/**
 * Represents the history of milestone rewards redeemed by a member.
 * Guarantees: The history is immutable; adding or deleting a reward returns a new
 * {@code RedeemedMilestoneRewardHistory}.
 */
public class RedeemedMilestoneRewardHistory {
    public static final String MESSAGE_DUPLICATE_MILESTONE =
            "This milestone has already been redeemed.";

    private final ArrayList<Milestone> redeemedMilestoneRewards;

    /**
     * Constructs an empty {@code RedeemedMilestoneRewardHistory}.
     */
    public RedeemedMilestoneRewardHistory() {
        this.redeemedMilestoneRewards = new ArrayList<>();
    }

    private RedeemedMilestoneRewardHistory(List<Milestone> redeemedMilestoneRewards) {
        this.redeemedMilestoneRewards = new ArrayList<>(redeemedMilestoneRewards);
    }

    /**
     * Returns a copy of all the redeemed milestones.
     *
     * @return a list of redeemed milestones in the history
     */
    public List<Milestone> getRedeemedMilestoneRewards() {
        return new ArrayList<>(redeemedMilestoneRewards);
    }

    /**
     * Returns a new history containing the given redeemed milestone.
     * This history is not modified.
     *
     * @param redeemedMilestoneReward the redeemed milestone to add
     * @return a new history containing the milestone
     * @throws NullPointerException if {@code redeemedMilestoneReward} is null
     */
    public RedeemedMilestoneRewardHistory addRedeemedMilestoneReward(Milestone redeemedMilestoneReward) {
        requireNonNull(redeemedMilestoneReward);
        if (contains(redeemedMilestoneReward)) {
            throw new IllegalArgumentException(MESSAGE_DUPLICATE_MILESTONE);
        }

        ArrayList<Milestone> updatedRewards = new ArrayList<>(redeemedMilestoneRewards);
        updatedRewards.add(redeemedMilestoneReward);
        return new RedeemedMilestoneRewardHistory(updatedRewards);
    }

    /**
     * Returns a new history without the redeemed milestone with the same code as {@code milestone}.
     * This history is not modified.
     *
     * @param milestone the redeemed milestone to delete
     * @return a new history without the specified redeemed milestone
     * @throws IllegalArgumentException if the milestone is not in this history
     */
    public RedeemedMilestoneRewardHistory deleteRedeemedMilestoneReward(Milestone milestone) {
        requireNonNull(milestone);
        ArrayList<Milestone> updatedRewards = new ArrayList<>(redeemedMilestoneRewards);
        boolean wasRemoved = updatedRewards.removeIf(redeemedMilestone -> redeemedMilestone.isSameMilestone(milestone));
        if (!wasRemoved) {
            throw new IllegalArgumentException("Milestone is not in the redeemed history.");
        }
        return new RedeemedMilestoneRewardHistory(updatedRewards);
    }

    /**
     * Returns true if this history contains a milestone with the same code as {@code milestone}.
     *
     * @param milestone the milestone to search for
     * @return true if the history contains the given milestone
     */
    public boolean contains(Milestone milestone) {
        return redeemedMilestoneRewards.stream()
                .anyMatch(redeemedMilestone -> redeemedMilestone.isSameMilestone(milestone));
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
