package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Person;

/**
 * Redeems an existing milestone reward for a person in the address book.
 */
public class RedeemMilestoneCommand extends Command {

    public static final String COMMAND_WORD = "redeem";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Redeem a milestone for a person. "
            + "Parameters: PERSON_INDEX m/MILESTONE_INDEX\n"
            + "Example: " + COMMAND_WORD + " 1 m/2";
    public static final String MESSAGE_REDEEMED = "Milestone reward already redeemed.";
    public static final String MESSAGE_INSUFFICIENT_SCORE = "Person does not have sufficient score.";
    public static final String MESSAGE_SUCCESS = "Milestone reward redeemed successfully.";
    public static final String MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX =
            "The milestone index provided is invalid.";

    public final Index personIndex;
    public final Index milestoneIndex;

    /**
     * Creates a command to redeem a milestone for the person at the given index.
     *
     * @param personIndex index of the person in the filtered person list
     * @param milestoneIndex index of the milestone in the displayed milestone list
     */
    public RedeemMilestoneCommand(Index personIndex, Index milestoneIndex) {
        requireAllNonNull(personIndex, milestoneIndex);

        this.personIndex = personIndex;
        this.milestoneIndex = milestoneIndex;
    }

    /**
     * Redeems the selected milestone for the selected person.
     *
     * @param model the model which stores the person and milestone data
     * @return a command result containing the redemption status
     * @throws CommandException if either index is invalid or the milestone was already redeemed
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToUpdate = getPerson(model);
        Milestone milestoneToRedeem = getMilestone(model);

        validateSufficientScore(personToUpdate, milestoneToRedeem);
        validateMilestoneHasNotBeenRedeemed(personToUpdate, milestoneToRedeem);

        Person updatedPerson = createUpdatedPerson(personToUpdate, milestoneToRedeem);

        model.setPerson(personToUpdate, updatedPerson);
        model.selectPerson(updatedPerson);

        return new CommandResult(MESSAGE_SUCCESS);
    }

    /**
     * Returns a string representation containing the person and milestone indices.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("personIndex", personIndex)
                .add("milestoneIndex", milestoneIndex)
                .toString();
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
     * Ensures that the person has enough score to redeem the selected milestone.
     */
    private void validateSufficientScore(Person person, Milestone milestone)
            throws CommandException {
        if (person.getScore().value < milestone.getRequiredScore()) {
            throw new CommandException(MESSAGE_INSUFFICIENT_SCORE);
        }
    }

    /**
     * Ensures that the person has not already redeemed the selected milestone.
     */
    private void validateMilestoneHasNotBeenRedeemed(Person person, Milestone milestone)
            throws CommandException {
        if (person.hasRedeemed(milestone)) {
            throw new CommandException(MESSAGE_REDEEMED);
        }
    }

    /**
     * Returns a copy of the person with the selected milestone added to their history.
     */
    private Person createUpdatedPerson(Person person, Milestone milestone) {
        return new Person(
                person.getName(),
                person.getPhone(),
                person.getTags(),
                person.getScore(),
                person.getOrderHistory(),
                person.getRedeemedMilestoneRewardHistory().addRedeemedMilestoneReward(milestone));
    }
}
