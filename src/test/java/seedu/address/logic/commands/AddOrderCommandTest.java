package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;
import seedu.address.model.order.Order;
import seedu.address.model.person.Person;

public class AddOrderCommandTest {
    private static final String VALID_ITEM_CODE = "3A";
    private static final int VALID_QUANTITY = 2;

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddOrderCommand(null, VALID_ITEM_CODE, 1));
    }

    @Test
    public void constructor_nullItemCode_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddOrderCommand(INDEX_FIRST_PERSON, null, 1));
    }

    @Test
    public void execute_validCode_savesCurrentMenuNameInHistory() throws Exception {
        Model model = modelWithMenuItem();
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        new AddOrderCommand(INDEX_FIRST_PERSON, VALID_ITEM_CODE, VALID_QUANTITY).execute(model);

        Person updatedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(originalPerson.getOrderHistory().getOrders().size() + 1,
                updatedPerson.getOrderHistory().getOrders().size());
        Order addedOrder = updatedPerson.getOrderHistory().getOrders()
                .get(updatedPerson.getOrderHistory().getOrders().size() - 1);
        assertEquals(VALID_ITEM_CODE, addedOrder.itemCode);
        assertEquals("Nasi Lemak", addedOrder.itemName);
        assertEquals(VALID_QUANTITY, addedOrder.quantity);
        assertEquals(96, addedOrder.scoreGained);
        assertEquals(originalPerson.getScore().value + 96, updatedPerson.getScore().value);
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Model model = modelWithMenuItem();
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertThrows(CommandException.class, () ->
                new AddOrderCommand(outOfBoundsIndex, VALID_ITEM_CODE, 1).execute(model));
    }

    @Test
    public void execute_codeNotInMenu_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        CommandException exception = assertThrows(CommandException.class, () ->
                new AddOrderCommand(INDEX_FIRST_PERSON, VALID_ITEM_CODE, VALID_QUANTITY).execute(model));

        assertEquals(AddOrderCommand.MESSAGE_ITEM_NOT_IN_MENU, exception.getMessage());
    }

    @Test
    public void execute_codeWithDifferentCase_throwsCommandException() {
        Model model = modelWithMenuItem();

        CommandException exception = assertThrows(CommandException.class, () ->
                new AddOrderCommand(INDEX_FIRST_PERSON, "3a", VALID_QUANTITY).execute(model));

        assertEquals(AddOrderCommand.MESSAGE_ITEM_NOT_IN_MENU, exception.getMessage());
    }

    @Test
    public void equals() {
        AddOrderCommand command = new AddOrderCommand(INDEX_FIRST_PERSON, VALID_ITEM_CODE, VALID_QUANTITY);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddOrderCommand(INDEX_FIRST_PERSON, VALID_ITEM_CODE, VALID_QUANTITY)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new AddOrderCommand(Index.fromOneBased(2), VALID_ITEM_CODE, VALID_QUANTITY)));
        assertFalse(command.equals(new AddOrderCommand(INDEX_FIRST_PERSON, "3B", VALID_QUANTITY)));
        assertFalse(command.equals(new AddOrderCommand(INDEX_FIRST_PERSON, VALID_ITEM_CODE, 1)));
    }

    @Test
    public void toStringMethod() {
        AddOrderCommand command = new AddOrderCommand(INDEX_FIRST_PERSON, VALID_ITEM_CODE, VALID_QUANTITY);
        String expected = AddOrderCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_PERSON + ", itemCode=" + VALID_ITEM_CODE
                + ", quantity=" + VALID_QUANTITY + "}";

        assertEquals(expected, command.toString());
    }

    private static Model modelWithMenuItem() {
        Menu menu = new Menu();
        menu.addFoodItem(new FoodItem(VALID_ITEM_CODE, "Nasi Lemak", 480));
        return new ModelManager(getTypicalAddressBook(), new UserPrefs(), menu);
    }
}
