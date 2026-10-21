package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIRED_SCORE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REWARD;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.milestone.Milestone;

/**
 * Edits a milestone reward identified by its displayed index.
 */
public class EditMilestoneCommand extends Command {
    public static final String COMMAND_WORD = "edit-milestone";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the milestone identified by the index number used in the displayed milestone list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_REWARD + "REWARD] "
            + "[" + PREFIX_REQUIRED_SCORE + "REQUIRED_SCORE]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_REWARD + "Free drink and snack "
            + PREFIX_REQUIRED_SCORE + "600";

    public static final String MESSAGE_SUCCESS = "Edited milestone: %s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    private final Index index;
    private final EditMilestoneDescriptor editMilestoneDescriptor;

    /**
     * Creates a command to edit the milestone at {@code index} using the specified fields.
     */
    public EditMilestoneCommand(Index index, EditMilestoneDescriptor editMilestoneDescriptor) {
        requireNonNull(index);
        requireNonNull(editMilestoneDescriptor);
        this.index = index;
        this.editMilestoneDescriptor = new EditMilestoneDescriptor(editMilestoneDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Milestone> lastShownList = model.getMilestoneList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX);
        }

        Milestone milestoneToEdit = lastShownList.get(index.getZeroBased());
        Milestone editedMilestone = createEditedMilestone(milestoneToEdit, editMilestoneDescriptor);
        model.setMilestone(milestoneToEdit, editedMilestone);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(editedMilestone)));
    }

    private static Milestone createEditedMilestone(Milestone milestoneToEdit,
            EditMilestoneDescriptor editMilestoneDescriptor) {
        assert milestoneToEdit != null;
        assert editMilestoneDescriptor != null;

        String updatedReward = editMilestoneDescriptor.getRewardDescription()
                .orElse(milestoneToEdit.getRewardDescription());
        int updatedRequiredScore = editMilestoneDescriptor.getRequiredScore()
                .orElse(milestoneToEdit.getRequiredScore());
        return new Milestone(milestoneToEdit.getCode(), updatedReward, updatedRequiredScore);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditMilestoneCommand otherEditMilestoneCommand)) {
            return false;
        }
        return index.equals(otherEditMilestoneCommand.index)
                && editMilestoneDescriptor.equals(otherEditMilestoneCommand.editMilestoneDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editMilestoneDescriptor", editMilestoneDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit in a milestone. Each non-empty field replaces the existing field value.
     */
    public static class EditMilestoneDescriptor {
        private String rewardDescription;
        private Integer requiredScore;

        public EditMilestoneDescriptor() {}

        /**
         * Creates a defensive copy of {@code toCopy}.
         */
        public EditMilestoneDescriptor(EditMilestoneDescriptor toCopy) {
            requireNonNull(toCopy);
            setRewardDescription(toCopy.rewardDescription);
            setRequiredScore(toCopy.requiredScore);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(rewardDescription, requiredScore);
        }

        public void setRewardDescription(String rewardDescription) {
            this.rewardDescription = rewardDescription;
        }

        public Optional<String> getRewardDescription() {
            return Optional.ofNullable(rewardDescription);
        }

        public void setRequiredScore(Integer requiredScore) {
            this.requiredScore = requiredScore;
        }

        public Optional<Integer> getRequiredScore() {
            return Optional.ofNullable(requiredScore);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof EditMilestoneDescriptor otherDescriptor)) {
                return false;
            }
            return Objects.equals(rewardDescription, otherDescriptor.rewardDescription)
                    && Objects.equals(requiredScore, otherDescriptor.requiredScore);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("rewardDescription", rewardDescription)
                    .add("requiredScore", requiredScore)
                    .toString();
        }
    }
}
