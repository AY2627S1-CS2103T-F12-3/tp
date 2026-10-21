package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIRED_SCORE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REWARD;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.milestone.Milestone;

/**
 * Adds a milestone reward to the milestone catalog.
 */
public class AddMilestoneCommand extends Command {
    public static final String COMMAND_WORD = "add-milestone";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a milestone reward to the milestone catalog.\n"
            + "Parameters: "
            + PREFIX_CODE + "CODE "
            + PREFIX_REWARD + "REWARD "
            + PREFIX_REQUIRED_SCORE + "REQUIRED_SCORE\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CODE + "FREE500 "
            + PREFIX_REWARD + "Free drink "
            + PREFIX_REQUIRED_SCORE + "500";

    public static final String MESSAGE_SUCCESS = "New milestone added: %s";
    public static final String MESSAGE_DUPLICATE_MILESTONE = "A milestone with this code already exists.";

    private final Milestone toAdd;

    /**
     * Creates a command to add the specified milestone.
     */
    public AddMilestoneCommand(Milestone milestone) {
        toAdd = requireNonNull(milestone);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasMilestone(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_MILESTONE);
        }

        model.addMilestone(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddMilestoneCommand otherAddMilestoneCommand)) {
            return false;
        }
        return toAdd.equals(otherAddMilestoneCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
