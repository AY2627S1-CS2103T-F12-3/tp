package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's membership score in the address book.
 * Guarantees: immutable; is always a non-negative integer.
 */
public class Score {

    public static final String MESSAGE_CONSTRAINTS = "Score should be a non-negative integer";
    public static final String VALIDATION_REGEX = "\\d{1,9}";
    public static final Score DEFAULT = new Score(0);

    public final int value;

    /**
     * Constructs a {@code Score}.
     *
     * @param score A non-negative score.
     */
    public Score(int score) {
        checkArgument(score >= 0, MESSAGE_CONSTRAINTS);
        value = score;
    }

    public static boolean isValidScore(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Score otherScore && value == otherScore.value);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
