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
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.order.Order;
import seedu.address.model.person.Person;

public class AddOrderCommandTest {

    private static final String VALID_ITEM_NAME = "Chicken Rice";
    private static final int VALID_QUANTITY = 2;

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        Order order = new Order(VALID_ITEM_NAME, 1);

        assertThrows(NullPointerException.class, () -> new AddOrderCommand(null, order));
    }

    @Test
    public void constructor_nullOrder_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddOrderCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_validOrder_addsOrderToPersonHistory() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Order order = new Order(VALID_ITEM_NAME, VALID_QUANTITY);

        new AddOrderCommand(INDEX_FIRST_PERSON, order).execute(model);

        Person updatedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(originalPerson.getOrderHistory().getOrders().size() + 1,
                updatedPerson.getOrderHistory().getOrders().size());
        assertTrue(updatedPerson.getOrderHistory().getOrders().contains(order));
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Order order = new Order(VALID_ITEM_NAME, 1);

        assertThrows(CommandException.class, () -> new AddOrderCommand(outOfBoundsIndex, order).execute(model));
    }

    @Test
    public void parseOrder_validItem_success() throws ParseException {
        assertEquals(new Order(VALID_ITEM_NAME, VALID_QUANTITY),
                ParserUtil.parseOrder(" " + VALID_ITEM_NAME + " ", " " + VALID_QUANTITY + " "));
    }

    @Test
    public void parseOrder_invalidItem_failure() {
        assertThrows(ParseException.class, () -> ParserUtil.parseOrder("", String.valueOf(VALID_QUANTITY)));
    }

    @Test
    public void parseOrder_invalidQuantity_failure() {
        assertThrows(ParseException.class, () -> ParserUtil.parseOrder(VALID_ITEM_NAME, "two"));
    }

    @Test
    public void parseOrder_zeroQuantity_failure() {
        assertThrows(ParseException.class, () -> ParserUtil.parseOrder(VALID_ITEM_NAME, "0"));
    }

    @Test
    public void parseOrder_negativeQuantity_failure() {
        assertThrows(ParseException.class, () -> ParserUtil.parseOrder(VALID_ITEM_NAME, "-1"));
    }

    @Test
    public void parseOrder_nullItemValidQuantity_failure() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseOrder(null, String.valueOf(VALID_QUANTITY)));
    }

    @Test
    public void parseOrder_nullItemInvalidQuantity_failure() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseOrder(null, "two"));
    }

    @Test
    public void parseOrder_nullQuantity_failure() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseOrder(VALID_ITEM_NAME, null));
    }

    @Test
    public void parseOrder_nullItemNullQuantity_failure() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseOrder(null, null));
    }

    @Test
    public void equals() {
        Order order = new Order(VALID_ITEM_NAME, VALID_QUANTITY);
        AddOrderCommand command = new AddOrderCommand(INDEX_FIRST_PERSON, order);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddOrderCommand(INDEX_FIRST_PERSON, order)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new AddOrderCommand(Index.fromOneBased(2), order)));
        assertFalse(command.equals(new AddOrderCommand(INDEX_FIRST_PERSON, new Order("Coffee", 1))));
    }

    @Test
    public void toStringMethod() {
        Order order = new Order(VALID_ITEM_NAME, VALID_QUANTITY);
        AddOrderCommand command = new AddOrderCommand(INDEX_FIRST_PERSON, order);
        String expected = AddOrderCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_PERSON + ", order=" + order + "}";

        assertEquals(expected, command.toString());
    }
}
