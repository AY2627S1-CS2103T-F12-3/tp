package seedu.address.model.milestone.exceptions;

/**
 * Signals that an operation would result in milestones with duplicate codes.
 */
public class DuplicateMilestoneException extends RuntimeException {
    public DuplicateMilestoneException() {
        super("Operation would result in duplicate milestone codes");
    }
}
