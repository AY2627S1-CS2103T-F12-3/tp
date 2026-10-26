package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
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
        assertThrows(NullPointerException.class, () ->
                new AddMilestoneCommand(new Milestone("Free drink", 500)).execute(null));
    }

    @Test
    public void execute_validMilestone_addsWithGeneratedCode() {
        Model model = new ModelManager();
        Milestone first = new Milestone("Free drink", 500);
        AddMilestoneCommand command = new AddMilestoneCommand(first);

        CommandResult result = command.execute(model);

        assertEquals(new CommandResult(String.format(AddMilestoneCommand.MESSAGE_SUCCESS,
                Messages.format(first))), result);
        assertEquals(UUID.fromString(first.getCode()).toString(), first.getCode().toLowerCase());
        assertFalse(result.getFeedbackToUser().contains(first.getCode()));

        Milestone second = new Milestone("Free drink", 500);
        new AddMilestoneCommand(second).execute(model);
        assertEquals(2, model.getMilestoneList().size());
        assertNotEquals(first.getCode(), second.getCode());
    }

    @Test
    public void execute_duplicateCode_regeneratesCode() {
        Model model = new ModelManager();
        Milestone milestone = new Milestone("Free drink", 500);
        AddMilestoneCommand command = new AddMilestoneCommand(milestone);
        command.execute(model);

        CommandResult result = command.execute(model);
        assertEquals(2, model.getMilestoneList().size());
        Milestone regenerated = model.getMilestoneList().stream()
                .filter(item -> !item.getCode().equals(milestone.getCode()))
                .findFirst().orElseThrow();
        assertNotEquals(milestone.getCode(), regenerated.getCode());
        assertEquals(milestone.getRewardDescription(), regenerated.getRewardDescription());
        assertEquals(milestone.getRequiredScore(), regenerated.getRequiredScore());
        assertEquals(new CommandResult(String.format(AddMilestoneCommand.MESSAGE_SUCCESS,
                Messages.format(regenerated))), result);
    }

    @Test
    public void equals() {
        Milestone milestone = new Milestone("Free drink", 500);
        AddMilestoneCommand command = new AddMilestoneCommand(milestone);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddMilestoneCommand(milestone)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new AddMilestoneCommand(new Milestone("Snack", 500))));
        assertFalse(command.equals(new AddMilestoneCommand(new Milestone("Free drink", 600))));
    }

    @Test
    public void toStringMethod() {
        Milestone milestone = new Milestone("Free drink", 500);
        String expected = AddMilestoneCommand.class.getCanonicalName() + "{toAdd=" + milestone + "}";
        assertEquals(expected, new AddMilestoneCommand(milestone).toString());
    }
}
