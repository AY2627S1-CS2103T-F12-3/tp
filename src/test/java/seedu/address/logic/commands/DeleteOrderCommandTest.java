package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.order.Order;
import seedu.address.model.person.Person;

public class DeleteOrderCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullTargetIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteOrderCommand(null, INDEX_FIRST_PERSON));
    }

    @Test
    public void constructor_nullOrderIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteOrderCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_validIndexes_deletesOrderFromPersonHistory() throws Exception {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        List<Order> originalOrders = originalPerson.getOrderHistory().getOrders();
        Order orderToDelete = originalOrders.get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteOrderCommand command = new DeleteOrderCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON);

        CommandResult result = command.execute(model);

        Person updatedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(List.of(originalOrders.get(INDEX_SECOND_PERSON.getZeroBased())),
                updatedPerson.getOrderHistory().getOrders());
        assertEquals(originalPerson.getName(), updatedPerson.getName());
        assertEquals(originalPerson.getPhone(), updatedPerson.getPhone());
        assertEquals(originalPerson.getTags(), updatedPerson.getTags());
        assertEquals(originalPerson.getScore(), updatedPerson.getScore());
        assertEquals(String.format(DeleteOrderCommand.MESSAGE_DELETE_ORDER_SUCCESS, orderToDelete),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_validIndexesFilteredList_deletesOrderFromCorrectPerson() throws Exception {
        model.updateFilteredPersonList(person -> person.getName().fullName.equals("Benson Meier"));
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertFalse(personToEdit.getOrderHistory().getOrders().isEmpty());

        new DeleteOrderCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON).execute(model);

        Person updatedPerson = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().equals(personToEdit.getName()))
                .findFirst()
                .orElseThrow();
        assertTrue(updatedPerson.getOrderHistory().getOrders().isEmpty());
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Index invalidPersonIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteOrderCommand command = new DeleteOrderCommand(invalidPersonIndex, INDEX_FIRST_PERSON);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, exception.getMessage());
    }

    @Test
    public void execute_invalidOrderIndex_throwsCommandException() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Index invalidOrderIndex = Index.fromOneBased(person.getOrderHistory().getOrders().size() + 1);
        DeleteOrderCommand command = new DeleteOrderCommand(INDEX_FIRST_PERSON, invalidOrderIndex);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(DeleteOrderCommand.MESSAGE_INVALID_ORDER_INDEX, exception.getMessage());
    }

    @Test
    public void execute_personWithNoOrders_throwsCommandException() {
        Index personWithNoOrdersIndex = Index.fromOneBased(3);
        DeleteOrderCommand command = new DeleteOrderCommand(personWithNoOrdersIndex, INDEX_FIRST_PERSON);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(DeleteOrderCommand.MESSAGE_INVALID_ORDER_INDEX, exception.getMessage());
    }

    @Test
    public void equals() {
        DeleteOrderCommand command = new DeleteOrderCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteOrderCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new DeleteOrderCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON)));
        assertFalse(command.equals(new DeleteOrderCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));
    }

    @Test
    public void toStringMethod() {
        DeleteOrderCommand command = new DeleteOrderCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        String expected = DeleteOrderCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_PERSON + ", orderIndex=" + INDEX_SECOND_PERSON + "}";

        assertEquals(expected, command.toString());
    }
}
