package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Score;

/**
 * Updates the score of a person in the address book by a specified amount.
 */
public class UpdateScoreCommand extends Command {

    public static final String COMMAND_WORD = "update-score";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Updates a person's score by the specified amount. "
            + "Parameters: INDEX (must be a positive integer) s/SCORE_CHANGE\n"
            + "Example: " + COMMAND_WORD + " 1 s/10";

    public static final String MESSAGE_SUCCESS = "Score updated from %d to %d.";
    public static final String MESSAGE_SCORE_BELOW_ZERO = "Score cannot be less than zero.";
    public static final String MESSAGE_SCORE_TOO_LARGE = "Score cannot exceed 999999999.";

    public final Index index;
    public final int scoreChange;

    /**
     * Creates an {@code UpdateScoreCommand} for the specified person and score change.
     */
    public UpdateScoreCommand(Index index, int scoreChange) {
        requireNonNull(index);
        this.index = index;
        this.scoreChange = scoreChange;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(index.getZeroBased());
        int oldScore = personToUpdate.getScore().value;
        long updatedScore = (long) oldScore + scoreChange;

        if (updatedScore < 0) {
            throw new CommandException(MESSAGE_SCORE_BELOW_ZERO);
        }
        if (!Score.isValidScore(String.valueOf(updatedScore))) {
            throw new CommandException(MESSAGE_SCORE_TOO_LARGE);
        }

        Person updatedPerson = new Person(
                personToUpdate.getName(),
                personToUpdate.getPhone(),
                personToUpdate.getTags(),
                new Score((int) updatedScore),
                personToUpdate.getOrderHistory(),
                personToUpdate.getRedeemedMilestoneRewardHistory());

        model.setPerson(personToUpdate, updatedPerson);
        model.selectPerson(updatedPerson);

        return new CommandResult(String.format(MESSAGE_SUCCESS, oldScore, updatedScore));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UpdateScoreCommand otherUpdateScoreCommand)) {
            return false;
        }

        return index.equals(otherUpdateScoreCommand.index) && scoreChange == otherUpdateScoreCommand.scoreChange;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("scoreChange", scoreChange)
                .toString();
    }
}
