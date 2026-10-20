package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.order.Order;
import seedu.address.model.order.OrderHistory;
import seedu.address.model.person.Person;
import seedu.address.model.person.Score;

/**
 * Deletes an order from the order history of an existing person in the address book.
 */
public class DeleteOrderCommand extends Command {
    public static final String COMMAND_WORD = "delete-order";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes the order identified by its order index "
            + "from the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_ORDER + "ORDER_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_ORDER + "2";

    public static final String MESSAGE_DELETE_ORDER_SUCCESS = "Deleted order: %1$s";
    public static final String MESSAGE_INVALID_ORDER_INDEX = "The order index provided is invalid.";
    public static final String MESSAGE_SCORE_BELOW_ZERO = "Deleting this order would make the score negative.";

    private final Index targetIndex;
    private final Index orderIndex;

    /**
     * Creates a {@code DeleteOrderCommand} to delete an order from a person.
     *
     * @param targetIndex index of the person in the filtered person list
     * @param orderIndex index of the order in the person's order history
     */
    public DeleteOrderCommand(Index targetIndex, Index orderIndex) {
        requireAllNonNull(targetIndex, orderIndex);
        this.targetIndex = targetIndex;
        this.orderIndex = orderIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(targetIndex.getZeroBased());
        List<Order> orders = personToEdit.getOrderHistory().getOrders();
        if (orderIndex.getZeroBased() >= orders.size()) {
            throw new CommandException(MESSAGE_INVALID_ORDER_INDEX);
        }

        Order orderToDelete = orders.get(orderIndex.getZeroBased());
        long updatedScore = (long) personToEdit.getScore().value - orderToDelete.scoreGained;
        if (updatedScore < 0) {
            throw new CommandException(MESSAGE_SCORE_BELOW_ZERO);
        }
        OrderHistory updatedOrderHistory = personToEdit.getOrderHistory().deleteOrder(orderIndex.getZeroBased());
        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getTags(),
                new Score((int) updatedScore),
                updatedOrderHistory
        );

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.selectPerson(editedPerson);

        return new CommandResult(String.format(MESSAGE_DELETE_ORDER_SUCCESS, orderToDelete));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeleteOrderCommand otherDeleteOrderCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteOrderCommand.targetIndex)
                && orderIndex.equals(otherDeleteOrderCommand.orderIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("orderIndex", orderIndex)
                .toString();
    }
}
