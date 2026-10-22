package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.milestone.Milestone;

public class DeleteMenuItemCommandTest {
    private static final FoodItem CHICKEN_RICE = new FoodItem("CR01", "Chicken Rice", 450);

    @Test
    public void constructor_invalidCode_throwsException() {
        assertThrows(NullPointerException.class, () -> new DeleteMenuItemCommand(null));
        assertThrows(IllegalArgumentException.class, () -> new DeleteMenuItemCommand(""));
        assertThrows(IllegalArgumentException.class, () -> new DeleteMenuItemCommand("CR-01"));
    }

    @Test
    public void execute_existingCode_removesOnlyMatchingItem() throws CommandException {
        ModelManager model = new ModelManager();
        FoodItem lowercase = new FoodItem("cr01", "Chicken Rice", 600);
        Milestone milestone = new Milestone("M1", "Free drink", 10);
        model.addPerson(ALICE);
        model.selectPerson(ALICE);
        model.addMilestone(milestone);
        model.addFoodItem(CHICKEN_RICE);
        model.addFoodItem(lowercase);

        CommandResult result = new DeleteMenuItemCommand("CR01").execute(model);

        assertEquals(new CommandResult("Deleted menu item: CR01 (Chicken Rice)"), result);
        assertEquals(List.of(lowercase), model.getMenu().getFoodItemList());
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
        assertEquals(ALICE, model.getSelectedPerson().orElseThrow());
        assertEquals(List.of(milestone), model.getMilestoneList());
    }

    @Test
    public void execute_missingOrWrongCaseCode_throwsWithoutChangingMenu() {
        ModelManager model = new ModelManager();
        model.addFoodItem(CHICKEN_RICE);
        for (String code : List.of("UNKNOWN", "cr01")) {
            DeleteMenuItemCommand command = new DeleteMenuItemCommand(code);
            assertThrows(CommandException.class, DeleteMenuItemCommand.MESSAGE_ITEM_NOT_FOUND, () ->
                    command.execute(model));
            assertEquals(List.of(CHICKEN_RICE), model.getMenu().getFoodItemList());
        }
    }

    @Test
    public void execute_emptyMenu_throwsCommandException() {
        DeleteMenuItemCommand command = new DeleteMenuItemCommand("CR01");
        assertThrows(CommandException.class, DeleteMenuItemCommand.MESSAGE_ITEM_NOT_FOUND, () ->
                command.execute(new ModelManager()));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteMenuItemCommand("CR01").execute(null));
    }

    @Test
    public void execute_sameCommandTwice_secondAttemptFailsWithoutChangingMenu() throws CommandException {
        ModelManager model = new ModelManager();
        model.addFoodItem(CHICKEN_RICE);
        DeleteMenuItemCommand command = new DeleteMenuItemCommand("CR01");
        command.execute(model);

        assertThrows(CommandException.class,
                DeleteMenuItemCommand.MESSAGE_ITEM_NOT_FOUND, () -> command.execute(model));
        assertTrue(model.getMenu().getFoodItemList().isEmpty());
    }

    @Test
    public void equalsAndToString_compareAndDescribeTargetCode() {
        DeleteMenuItemCommand command = new DeleteMenuItemCommand("CR01");
        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteMenuItemCommand("CR01")));
        assertFalse(command.equals(new DeleteMenuItemCommand("cr01")));
        assertFalse(command.equals(null));
        assertFalse(command.equals("CR01"));
        assertEquals(DeleteMenuItemCommand.class.getCanonicalName() + "{targetCode=CR01}", command.toString());
    }
}
