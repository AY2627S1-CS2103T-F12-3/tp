package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.menu.FoodItem;

/**
 * Adds a food item to the menu.
 */
public class AddMenuItemCommand extends Command {

    public static final String COMMAND_WORD = "add-menu-item";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a food item to the menu.\n"
            + "Parameters: "
            + PREFIX_CODE + "CODE "
            + PREFIX_NAME + "NAME "
            + PREFIX_PRICE + "PRICE\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CODE + "CR01 "
            + PREFIX_NAME + "Chicken Rice "
            + PREFIX_PRICE + "4.50";

    public static final String MESSAGE_SUCCESS = "New menu item added: %s";
    public static final String MESSAGE_DUPLICATE_ITEM =
            "A menu item with this code already exists.";

    private final FoodItem toAdd;

    /**
     * Creates a command to add the specified food item.
     */
    public AddMenuItemCommand(FoodItem foodItem) {
        toAdd = requireNonNull(foodItem);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasFoodItem(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_ITEM);
        }

        model.addFoodItem(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddMenuItemCommand otherAddMenuItemCommand)) {
            return false;
        }

        return toAdd.equals(otherAddMenuItemCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
