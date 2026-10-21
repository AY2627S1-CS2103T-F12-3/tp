package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalMilestones.FREE_DRINK;
import static seedu.address.testutil.TypicalMilestones.VOUCHER;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class DeleteMilestoneCommandTest {

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteMilestoneCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new DeleteMilestoneCommand(INDEX_FIRST_PERSON).execute(null));
    }

    @Test
    public void execute_validIndex_deleteSuccessful() throws CommandException {
        Model model = new ModelManager();
        model.addMilestone(FREE_DRINK);
        model.addMilestone(VOUCHER);

        CommandResult result = new DeleteMilestoneCommand(INDEX_FIRST_PERSON).execute(model);

        assertEquals(new CommandResult(String.format(DeleteMilestoneCommand.MESSAGE_SUCCESS,
                Messages.format(FREE_DRINK))), result);
        assertEquals(List.of(VOUCHER), model.getMilestoneList());
    }

    @Test
    public void execute_invalidIndex_throwsCommandExceptionAndDoesNotModifyCatalog() {
        Model model = new ModelManager();
        model.addMilestone(FREE_DRINK);
        DeleteMilestoneCommand command = new DeleteMilestoneCommand(INDEX_SECOND_PERSON);

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX, () ->
                command.execute(model));
        assertEquals(List.of(FREE_DRINK), model.getMilestoneList());
    }

    @Test
    public void execute_emptyCatalog_throwsCommandException() {
        Model model = new ModelManager();

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX, () ->
                new DeleteMilestoneCommand(INDEX_FIRST_PERSON).execute(model));
        assertTrue(model.getMilestoneList().isEmpty());
    }

    @Test
    public void equals() {
        DeleteMilestoneCommand command = new DeleteMilestoneCommand(INDEX_FIRST_PERSON);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteMilestoneCommand(INDEX_FIRST_PERSON)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new DeleteMilestoneCommand(INDEX_SECOND_PERSON)));
    }

    @Test
    public void toStringMethod() {
        String expected = DeleteMilestoneCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_PERSON + "}";
        assertEquals(expected, new DeleteMilestoneCommand(INDEX_FIRST_PERSON).toString());
    }
}
