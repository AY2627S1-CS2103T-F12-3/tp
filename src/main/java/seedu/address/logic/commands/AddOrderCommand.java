package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.order.Order;
import seedu.address.model.person.Person;

/**
 * Add a new order to order history of an existing person in the address book.
 */
public class AddOrderCommand extends Command {
    public static final String COMMAND_WORD = "add-order";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Add an order to the person identified "
            + "by the index number used in the displayed person list. "
            + "New order will be added to the OrderHistory.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_ORDER + "ORDER ITEM "
            + PREFIX_QUANTITY + "QUANTITY\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_ORDER + "Chicken Rice "
            + PREFIX_QUANTITY + "5";

    public static final String MESSAGE_SUCCESS = "New order added successfully!";

    public final Index index;
    public final Order order;

    /**
     * @param index of the person in the filtered person list to add an order
     * @param order order to be added to person's order history
     */
    public AddOrderCommand(Index index, Order order) {
        requireAllNonNull(index, order);

        this.index = index;
        this.order = order;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personWithNewOrder = lastShownList.get(index.getZeroBased());
        Person personWithOrderAdded = new Person(
                personWithNewOrder.getName(),
                personWithNewOrder.getPhone(),
                personWithNewOrder.getTags(),
                personWithNewOrder.getScore(),
                personWithNewOrder.getOrderHistory().addOrder(order),
                personWithNewOrder.getRedeemedMilestoneRewardHistory()
        );

        model.setPerson(personWithNewOrder, personWithOrderAdded);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.selectPerson(personWithOrderAdded);

        return new CommandResult(generateSuccessMessage(personWithOrderAdded));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("order", order)
                .toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddOrderCommand otherAddOrderCommand)) {
            return false;
        }

        return index.equals(otherAddOrderCommand.index) && order.equals(otherAddOrderCommand.order);
    }

    private String generateSuccessMessage(Person personToEdit) {
        return MESSAGE_SUCCESS + " " + personToEdit;
    }
}
