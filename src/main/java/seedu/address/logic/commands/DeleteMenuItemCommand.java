package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.menu.FoodItem;

/**
 * Deletes a menu item identified by its case-sensitive code.
 */
public class DeleteMenuItemCommand extends Command {
    public static final String COMMAND_WORD = "delete-menu-item";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes the menu item with the specified code.\n"
            + "Parameters: " + PREFIX_CODE + "CODE\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_CODE + "CR01";
    public static final String MESSAGE_SUCCESS = "Deleted menu item: %s (%s)";
    public static final String MESSAGE_ITEM_NOT_FOUND = "No menu item with this code exists.";

    private final String targetCode;

    /**
     * Creates a command to delete the item with the given valid code.
     */
    public DeleteMenuItemCommand(String targetCode) {
        requireNonNull(targetCode);
        checkArgument(FoodItem.isValidCode(targetCode), FoodItem.MESSAGE_CODE_CONSTRAINTS);
        this.targetCode = targetCode;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        FoodItem toDelete = model.getMenu().getFoodItemList().stream()
                .filter(item -> item.getCode().equals(targetCode))
                .findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_ITEM_NOT_FOUND));
        model.deleteFoodItem(toDelete);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toDelete.getCode(), toDelete.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteMenuItemCommand otherCommand)) {
            return false;
        }
        return targetCode.equals(otherCommand.targetCode);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetCode", targetCode).toString();
    }
}
