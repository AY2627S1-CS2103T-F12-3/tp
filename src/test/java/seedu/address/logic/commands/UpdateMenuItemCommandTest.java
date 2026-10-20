package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.menu.FoodItem;

public class UpdateMenuItemCommandTest {
    private static final FoodItem CHICKEN_RICE = new FoodItem("CR01", "Chicken Rice", 450);

    @Test
    public void execute_updatesNameAndPrice() throws CommandException {
        ModelManager model = new ModelManager();
        model.addFoodItem(CHICKEN_RICE);

        CommandResult result = new UpdateMenuItemCommand("CR01", "Roasted Chicken Rice", 500).execute(model);

        assertEquals(new CommandResult("Menu item updated: Roasted Chicken Rice"), result);
        assertEquals(new FoodItem("CR01", "Roasted Chicken Rice", 500),
                model.getMenu().getFoodItemList().get(0));
    }

    @Test
    public void execute_updatesOnlyName() throws CommandException {
        ModelManager model = new ModelManager();
        model.addFoodItem(CHICKEN_RICE);

        new UpdateMenuItemCommand("CR01", "Roasted Chicken Rice", null).execute(model);

        assertEquals(new FoodItem("CR01", "Roasted Chicken Rice", 450),
                model.getMenu().getFoodItemList().get(0));
    }

    @Test
    public void execute_updatesOnlyPrice() throws CommandException {
        ModelManager model = new ModelManager();
        model.addFoodItem(CHICKEN_RICE);

        new UpdateMenuItemCommand("CR01", null, 500).execute(model);

        assertEquals(new FoodItem("CR01", "Chicken Rice", 500),
                model.getMenu().getFoodItemList().get(0));
    }

    @Test
    public void execute_missingCode_throwsCommandException() {
        ModelManager model = new ModelManager();
        UpdateMenuItemCommand command = new UpdateMenuItemCommand("CR01", "Roasted Chicken Rice", null);

        String expectedMessage = UpdateMenuItemCommand.MESSAGE_ITEM_NOT_FOUND;
        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
    }

    @Test
    public void constructor_noUpdatedFields_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new UpdateMenuItemCommand("CR01", null, null));
    }
}
