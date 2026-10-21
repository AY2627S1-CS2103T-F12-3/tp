package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalMilestones.FREE_DESSERT;
import static seedu.address.testutil.TypicalMilestones.FREE_DRINK;
import static seedu.address.testutil.TypicalMilestones.VOUCHER;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class ListMilestonesCommandTest {

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListMilestonesCommand().execute(null));
    }

    @Test
    public void execute_emptyCatalog_returnsNoMilestonesMessage() {
        CommandResult result = new ListMilestonesCommand().execute(new ModelManager());

        assertEquals(new CommandResult(ListMilestonesCommand.MESSAGE_NO_MILESTONES), result);
    }

    @Test
    public void execute_populatedCatalog_listsNumberedMilestonesInDisplayedOrder() {
        Model model = new ModelManager();
        model.addMilestone(VOUCHER);
        model.addMilestone(FREE_DRINK);
        model.addMilestone(FREE_DESSERT);
        String expectedMilestones = "1. " + Messages.format(FREE_DESSERT)
                + "\n2. " + Messages.format(FREE_DRINK)
                + "\n3. " + Messages.format(VOUCHER);
        String expectedMessage = String.format(ListMilestonesCommand.MESSAGE_MILESTONES_LISTED,
                3, expectedMilestones);

        CommandResult result = new ListMilestonesCommand().execute(model);

        assertEquals(new CommandResult(expectedMessage), result);
    }
}
