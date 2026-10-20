package seedu.address.logic.commands;

import static seedu.address.commons.util.AppUtil.checkArgument;
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
import seedu.address.model.menu.FoodItem;
import seedu.address.model.order.Order;
import seedu.address.model.person.Person;
import seedu.address.model.person.Score;

/**
 * Add a new order to order history of an existing person in the address book.
 */
public class AddOrderCommand extends Command {
    public static final String COMMAND_WORD = "add-order";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Add an order to the person identified "
            + "by the index number used in the displayed person list. "
            + "New order will be added to the OrderHistory.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_ORDER + "ITEM_CODE "
            + PREFIX_QUANTITY + "QUANTITY\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_ORDER + "1 "
            + PREFIX_QUANTITY + "5";

    public static final String MESSAGE_SUCCESS = "New order added successfully!";
    public static final String MESSAGE_ITEM_NOT_IN_MENU = "The item code is not in the menu.";
    public static final String MESSAGE_SCORE_TOO_LARGE = "Score cannot exceed 999999999.";

    public final Index index;
    public final String itemCode;
    public final int quantity;

    /**
     * @param index of the person in the filtered person list to add an order
     * @param itemCode code of the menu item to order
     * @param quantity number of units ordered
     */
    public AddOrderCommand(Index index, String itemCode, int quantity) {
        requireAllNonNull(index, itemCode);
        checkArgument(FoodItem.isValidCode(itemCode), Order.ITEM_CODE_CONSTRAINTS);
        checkArgument(Order.isValidQuantity(quantity), Order.QUANTITY_CONSTRAINTS);

        this.index = index;
        this.itemCode = itemCode;
        this.quantity = quantity;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        FoodItem foodItem = model.findFoodItemByCode(itemCode)
                .orElseThrow(() -> new CommandException(MESSAGE_ITEM_NOT_IN_MENU));
        Person person = lastShownList.get(index.getZeroBased());
        Person personWithOrderAdded = createPersonWithOrder(person, foodItem);

        model.setPerson(person, personWithOrderAdded);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.selectPerson(personWithOrderAdded);

        return new CommandResult(generateSuccessMessage(personWithOrderAdded));
    }

    /** Creates the updated member using the menu item's current name and price. */
    private Person createPersonWithOrder(Person person, FoodItem foodItem) throws CommandException {
        long scoreGained = Order.calculateScoreGained(foodItem.getPriceInCents(), quantity);
        long updatedScore = person.getScore().value + scoreGained;
        if (!Score.isValidScore(String.valueOf(updatedScore))) {
            throw new CommandException(MESSAGE_SCORE_TOO_LARGE);
        }
        Order order = new Order(itemCode, foodItem.getName(), quantity, (int) scoreGained);

        return new Person(
                person.getName(),
                person.getPhone(),
                person.getTags(),
                new Score((int) updatedScore),
                person.getOrderHistory().addOrder(order)
        );
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("itemCode", itemCode)
                .add("quantity", quantity)
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

        return index.equals(otherAddOrderCommand.index)
                && itemCode.equals(otherAddOrderCommand.itemCode)
                && quantity == otherAddOrderCommand.quantity;
    }

    private String generateSuccessMessage(Person personToEdit) {
        return MESSAGE_SUCCESS + " " + personToEdit;
    }
}
