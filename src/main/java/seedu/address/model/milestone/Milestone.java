package seedu.address.model.milestone;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Locale;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents an immutable milestone reward with an identifying code, reward description, and required score.
 * Codes are case-insensitive and alphanumeric; reward descriptions are not blank; required scores are positive.
 * Codes are normalized to uppercase when milestones are created.
 */
public final class Milestone {
    public static final String MESSAGE_CODE_CONSTRAINTS = "Milestone codes must be alphanumeric.";
    public static final String MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS =
            "Reward descriptions must not be blank.";
    public static final String MESSAGE_REQUIRED_SCORE_CONSTRAINTS = "Required scores must be positive.";
    private static final String CODE_VALIDATION_REGEX = "[A-Za-z0-9]+";

    private final String code;
    private final String rewardDescription;
    private final int requiredScore;

    /**
     * Constructs a milestone reward with the given code, reward description, and required score.
     *
     * @param code A case-insensitive alphanumeric milestone code.
     * @param rewardDescription A nonblank description of the reward.
     * @param requiredScore A positive score required to reach the milestone.
     * @throws NullPointerException if {@code code} or {@code rewardDescription} is null.
     * @throws IllegalArgumentException if a field violates its constraints.
     */
    public Milestone(String code, String rewardDescription, int requiredScore) {
        requireAllNonNull(code, rewardDescription);
        checkArgument(isValidCode(code), MESSAGE_CODE_CONSTRAINTS);
        checkArgument(isValidRewardDescription(rewardDescription), MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS);
        checkArgument(isValidRequiredScore(requiredScore), MESSAGE_REQUIRED_SCORE_CONSTRAINTS);
        this.code = code.toUpperCase(Locale.ROOT);
        this.rewardDescription = rewardDescription;
        this.requiredScore = requiredScore;
    }

    /**
     * Returns true if the given code contains one or more ASCII letters or digits.
     */
    public static boolean isValidCode(String code) {
        return code != null && code.matches(CODE_VALIDATION_REGEX);
    }

    /**
     * Returns true if the given reward description is neither null nor blank.
     */
    public static boolean isValidRewardDescription(String rewardDescription) {
        return rewardDescription != null && !rewardDescription.isBlank();
    }

    /**
     * Returns true if the given required score is positive.
     */
    public static boolean isValidRequiredScore(int requiredScore) {
        return requiredScore > 0;
    }

    public String getCode() {
        return code;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public int getRequiredScore() {
        return requiredScore;
    }

    /**
     * Returns true if both milestones have the same case-insensitive code.
     * This compares identity independently of the reward description and required score.
     */
    public boolean isSameMilestone(Milestone otherMilestone) {
        return otherMilestone != null && code.equals(otherMilestone.code);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Milestone otherMilestone)) {
            return false;
        }
        return code.equals(otherMilestone.code)
                && rewardDescription.equals(otherMilestone.rewardDescription)
                && requiredScore == otherMilestone.requiredScore;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, rewardDescription, requiredScore);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("code", code)
                .add("rewardDescription", rewardDescription)
                .add("requiredScore", requiredScore)
                .toString();
    }
}
