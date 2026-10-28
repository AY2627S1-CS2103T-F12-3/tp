package seedu.address.logic.commands;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;

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

    public static final String MESSAGE_SUCCESS = "Order added";
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

        FoodItem foodItem = model.getMenu().findFoodItemByCode(itemCode)
                .orElseThrow(() -> new CommandException(MESSAGE_ITEM_NOT_IN_MENU));
        Person person = lastShownList.get(index.getZeroBased());
        Order newOrder = createOrder(foodItem);
        Person personWithOrderAdded = createPersonWithOrder(person, newOrder);

        model.setPerson(person, personWithOrderAdded);
        model.selectPerson(personWithOrderAdded);

        return new CommandResult(generateSuccessMessage(personWithOrderAdded, newOrder));
    }

    /**
     * Creates a valid order using the menu item's current name and price.
     *
     * @throws CommandException if the earned points exceed the maximum allowed score
     */
    private Order createOrder(FoodItem foodItem) throws CommandException {
        long scoreGained = Order.calculateScoreGained(foodItem.getPriceInCents(), quantity);
        if (scoreGained > Score.MAX_SCORE) {
            throw new CommandException(MESSAGE_SCORE_TOO_LARGE);
        }
        return new Order(itemCode, foodItem.getName(), quantity, (int) scoreGained);
    }

    /**
     * Returns the updated member with new order and updated score.
     */
    private Person createPersonWithOrder(Person person, Order order) throws CommandException {
        Score updatedScore = calculateUpdatedScore(person, order);
        return new Person(
                person.getName(),
                person.getPhone(),
                person.getTags(),
                updatedScore,
                person.getOrderHistory().addOrder(order),
                person.getRedeemedMilestoneRewardHistory()
        );
    }

    /**
     * Calculates the person's updated score after adding an order.
     *
     * @param person the person whose score is being updated
     * @param order the order contributing to the score
     * @return the updated score
     * @throws CommandException if the updated score exceeds the maximum allowed value
     */
    private Score calculateUpdatedScore(Person person, Order order) throws CommandException {
        long updatedScore = (long) person.getScore().value + order.scoreGained;
        if (!Score.isValidScore(String.valueOf(updatedScore))) {
            throw new CommandException(MESSAGE_SCORE_TOO_LARGE);
        }
        return new Score((int) updatedScore);
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

    private String generateSuccessMessage(Person personToEdit, Order newOrder) {
        return String.format("%s: %s ordered %s",
                MESSAGE_SUCCESS,
                personToEdit.getName(),
                newOrder
        );
    }
}
