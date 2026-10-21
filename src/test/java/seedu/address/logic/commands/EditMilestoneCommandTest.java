package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalMilestones.EDITED_FREE_DRINK;
import static seedu.address.testutil.TypicalMilestones.FREE_DRINK;
import static seedu.address.testutil.TypicalMilestones.VOUCHER;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditMilestoneCommand.EditMilestoneDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.milestone.Milestone;

public class EditMilestoneCommandTest {

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new EditMilestoneCommand(null, new EditMilestoneDescriptor()));
    }

    @Test
    public void constructor_nullDescriptor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new EditMilestoneCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void constructor_descriptorMutatedAfterConstruction_usesDefensiveCopy() throws CommandException {
        EditMilestoneDescriptor descriptor = descriptorWithReward("Reward at construction");
        EditMilestoneCommand command = new EditMilestoneCommand(INDEX_FIRST_PERSON, descriptor);
        descriptor.setRewardDescription("Later mutation");
        Model model = modelWithMilestones(FREE_DRINK);

        command.execute(model);

        assertEquals("Reward at construction", model.getMilestoneList().get(0).getRewardDescription());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new EditMilestoneCommand(INDEX_FIRST_PERSON, descriptorWithReward("Reward")).execute(null));
    }

    @Test
    public void execute_rewardAndScoreEdited_editSuccessful() throws CommandException {
        Model model = modelWithMilestones(FREE_DRINK);
        EditMilestoneDescriptor descriptor = new EditMilestoneDescriptor();
        descriptor.setRewardDescription(EDITED_FREE_DRINK.getRewardDescription());
        descriptor.setRequiredScore(EDITED_FREE_DRINK.getRequiredScore());

        CommandResult result = new EditMilestoneCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        assertEquals(new CommandResult(String.format(EditMilestoneCommand.MESSAGE_SUCCESS,
                Messages.format(EDITED_FREE_DRINK))), result);
        assertEquals(List.of(EDITED_FREE_DRINK), model.getMilestoneList());
    }

    @Test
    public void execute_requiredScoreEdited_resortsMilestones() throws CommandException {
        Model model = modelWithMilestones(FREE_DRINK, VOUCHER);
        EditMilestoneDescriptor descriptor = new EditMilestoneDescriptor();
        descriptor.setRequiredScore(1500);
        Milestone movedMilestone = new Milestone("FREE500", "Free drink", 1500);

        new EditMilestoneCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        assertEquals(List.of(VOUCHER, movedMilestone), model.getMilestoneList());
    }

    @Test
    public void execute_sameValues_editSuccessful() throws CommandException {
        Model model = modelWithMilestones(FREE_DRINK);
        EditMilestoneDescriptor descriptor = new EditMilestoneDescriptor();
        descriptor.setRewardDescription(FREE_DRINK.getRewardDescription());
        descriptor.setRequiredScore(FREE_DRINK.getRequiredScore());

        new EditMilestoneCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        assertEquals(List.of(FREE_DRINK), model.getMilestoneList());
    }

    @Test
    public void execute_invalidIndex_throwsCommandExceptionAndDoesNotModifyCatalog() {
        Model model = modelWithMilestones(FREE_DRINK);
        EditMilestoneCommand command = new EditMilestoneCommand(INDEX_SECOND_PERSON,
                descriptorWithReward("New reward"));

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX, () ->
                command.execute(model));
        assertEquals(List.of(FREE_DRINK), model.getMilestoneList());
    }

    @Test
    public void equals() {
        EditMilestoneDescriptor descriptor = descriptorWithReward("New reward");
        EditMilestoneDescriptor sameDescriptor = descriptorWithReward("New reward");
        EditMilestoneDescriptor differentDescriptor = descriptorWithReward("Other reward");
        EditMilestoneCommand command = new EditMilestoneCommand(INDEX_FIRST_PERSON, descriptor);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new EditMilestoneCommand(INDEX_FIRST_PERSON, sameDescriptor)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new EditMilestoneCommand(INDEX_SECOND_PERSON, sameDescriptor)));
        assertFalse(command.equals(new EditMilestoneCommand(INDEX_FIRST_PERSON, differentDescriptor)));
    }

    @Test
    public void toStringMethod() {
        EditMilestoneDescriptor descriptor = descriptorWithReward("New reward");
        String expected = EditMilestoneCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_PERSON + ", editMilestoneDescriptor=" + descriptor + "}";

        assertEquals(expected, new EditMilestoneCommand(INDEX_FIRST_PERSON, descriptor).toString());
    }

    private static EditMilestoneDescriptor descriptorWithReward(String reward) {
        EditMilestoneDescriptor descriptor = new EditMilestoneDescriptor();
        descriptor.setRewardDescription(reward);
        return descriptor;
    }

    private static Model modelWithMilestones(Milestone... milestones) {
        Model model = new ModelManager();
        for (Milestone milestone : milestones) {
            model.addMilestone(milestone);
        }
        return model;
    }
}
