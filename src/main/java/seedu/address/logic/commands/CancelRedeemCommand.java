package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MILESTONE;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Person;

/**
 * Cancels a redeemed milestone reward for a person in the address book.
 */
public class CancelRedeemCommand extends Command {

    public static final String COMMAND_WORD = "cancel-redeem";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Cancel a redeemed milestone reward for a member. "
            + "Parameters: MEMBER_INDEX " + PREFIX_MILESTONE + "MILESTONE_INDEX\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_MILESTONE + "2";
    public static final String MESSAGE_SUCCESS = "Milestone reward cancellation successful.";
    public static final String MESSAGE_NOT_REDEEMED =
            "Unable to unredeem because it was not redeemed.";
    public static final String MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX =
            "The milestone index provided is invalid.";

    public final Index personIndex;
    public final Index milestoneIndex;

    /**
     * Creates a command to cancel a redeemed milestone reward for the person at the given index.
     *
     * @param personIndex index of the person in the filtered person list
     * @param milestoneIndex index of the milestone in the displayed milestone list
     */
    public CancelRedeemCommand(Index personIndex, Index milestoneIndex) {
        requireAllNonNull(personIndex, milestoneIndex);
        this.personIndex = personIndex;
        this.milestoneIndex = milestoneIndex;
    }

    /**
     * Removes the selected milestone from the selected person's redemption history.
     *
     * @param model the model which stores the person and milestone data
     * @return a command result containing the cancellation status
     * @throws CommandException if either index is invalid or the milestone was not redeemed
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToUpdate = getPerson(model);
        Milestone milestoneToCancel = getMilestone(model);
        validateMilestoneHasBeenRedeemed(personToUpdate, milestoneToCancel);

        Person updatedPerson = createUpdatedPerson(personToUpdate, milestoneToCancel);
        model.setPerson(personToUpdate, updatedPerson);
        model.selectPerson(updatedPerson);

        return new CommandResult(MESSAGE_SUCCESS);
    }

    /**
     * Returns the person at the selected index in the filtered person list.
     *
     * @param model the model containing the filtered person list
     * @return the selected person
     * @throws CommandException if the person index is invalid
     */
    private Person getPerson(Model model) throws CommandException {
        List<Person> filteredPersons = model.getFilteredPersonList();
        validateIndex(personIndex, filteredPersons.size(), Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        return filteredPersons.get(personIndex.getZeroBased());
    }

    /**
     * Returns the milestone at the selected index in the milestone list.
     *
     * @param model the model containing the milestone list
     * @return the selected milestone
     * @throws CommandException if the milestone index is invalid
     */
    private Milestone getMilestone(Model model) throws CommandException {
        List<Milestone> milestones = model.getMilestoneList();
        validateIndex(milestoneIndex, milestones.size(), MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX);
        return milestones.get(milestoneIndex.getZeroBased());
    }

    /**
     * Validates that an index refers to an element in a list.
     *
     * @param index the index to validate
     * @param listSize the size of the list being accessed
     * @param errorMessage the message for an invalid index
     * @throws CommandException if the index is outside the list bounds
     */
    private void validateIndex(Index index, int listSize, String errorMessage) throws CommandException {
        if (index.getZeroBased() >= listSize) {
            throw new CommandException(errorMessage);
        }
    }

    /**
     * Ensures that the person has redeemed the selected milestone.
     */
    private void validateMilestoneHasBeenRedeemed(Person person, Milestone milestone)
            throws CommandException {
        if (!person.hasRedeemed(milestone)) {
            throw new CommandException(MESSAGE_NOT_REDEEMED);
        }
    }

    /**
     * Returns a copy of the person with the selected milestone removed from their history.
     */
    private Person createUpdatedPerson(Person person, Milestone milestone) {
        return new Person(
                person.getName(),
                person.getPhone(),
                person.getTags(),
                person.getScore(),
                person.getOrderHistory(),
                person.getRedeemedMilestoneRewardHistory().deleteRedeemedMilestoneReward(milestone));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("personIndex", personIndex)
                .add("milestoneIndex", milestoneIndex)
                .toString();
    }
}
