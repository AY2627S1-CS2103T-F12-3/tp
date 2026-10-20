package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalMilestones.FREE_DRINK;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.milestone.Milestone;

public class AddMilestoneCommandTest {

    @Test
    public void constructor_nullMilestone_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddMilestoneCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddMilestoneCommand(FREE_DRINK).execute(null));
    }

    @Test
    public void execute_validMilestone_addSuccessful() throws CommandException {
        Model model = new ModelManager();

        CommandResult result = new AddMilestoneCommand(FREE_DRINK).execute(model);

        assertEquals(new CommandResult(String.format(AddMilestoneCommand.MESSAGE_SUCCESS,
                Messages.format(FREE_DRINK))), result);
        assertEquals(List.of(FREE_DRINK), model.getMilestoneList());
    }

    @Test
    public void execute_duplicateCode_throwsCommandExceptionAndDoesNotModifyCatalog() {
        Model model = new ModelManager();
        model.addMilestone(FREE_DRINK);
        Milestone duplicate = new Milestone("free500", "Different reward", 700);

        assertThrows(CommandException.class, AddMilestoneCommand.MESSAGE_DUPLICATE_MILESTONE, () ->
                new AddMilestoneCommand(duplicate).execute(model));
        assertEquals(List.of(FREE_DRINK), model.getMilestoneList());
    }

    @Test
    public void equals() {
        AddMilestoneCommand command = new AddMilestoneCommand(FREE_DRINK);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddMilestoneCommand(new Milestone("free500", "Free drink", 500))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new AddMilestoneCommand(new Milestone("OTHER", "Free drink", 500))));
        assertFalse(command.equals(new AddMilestoneCommand(new Milestone("FREE500", "Snack", 500))));
        assertFalse(command.equals(new AddMilestoneCommand(new Milestone("FREE500", "Free drink", 600))));
    }

    @Test
    public void toStringMethod() {
        String expected = AddMilestoneCommand.class.getCanonicalName() + "{toAdd=" + FREE_DRINK + "}";
        assertEquals(expected, new AddMilestoneCommand(FREE_DRINK).toString());
    }
}
