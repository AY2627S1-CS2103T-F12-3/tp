package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.milestone.Milestone;

/**
 * Deletes a milestone reward identified by its displayed index.
 */
public class DeleteMilestoneCommand extends Command {
    public static final String COMMAND_WORD = "delete-milestone";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the milestone identified by the index number used in the displayed milestone list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "Deleted milestone: %s";

    private final Index targetIndex;

    /**
     * Creates a command to delete the milestone at {@code targetIndex}.
     */
    public DeleteMilestoneCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Milestone> lastShownList = model.getMilestoneList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX);
        }

        Milestone milestoneToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deleteMilestone(milestoneToDelete);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(milestoneToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteMilestoneCommand otherDeleteMilestoneCommand)) {
            return false;
        }
        return targetIndex.equals(otherDeleteMilestoneCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
