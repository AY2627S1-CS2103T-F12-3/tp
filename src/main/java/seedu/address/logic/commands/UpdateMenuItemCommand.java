package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.menu.FoodItem;

/**
 * Updates the name, code, and/or price of an existing menu item.
 */
public class UpdateMenuItemCommand extends Command {
    public static final String COMMAND_WORD = "edit-menu-item";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Updates the details of a menu item identified by its code.\n"
            + "Parameters: "
            + "CURRENT_CODE [c/NEW_CODE] [n/NAME] [p/PRICE]\n"
            + "Example: " + COMMAND_WORD + " CR01 c/CR03 n/Roasted Chicken Rice p/5.00";

    public static final String MESSAGE_SUCCESS = "Menu item updated: %s";
    public static final String MESSAGE_ITEM_NOT_FOUND = "A menu item with this code does not exist.";
    public static final String MESSAGE_DUPLICATE_ITEM = "A menu item with the new code already exists.";

    private final String targetCode;
    private final String updatedCode;
    private final String updatedName;
    private final Integer updatedPriceInCents;

    /** Creates a command to update the specified fields of a menu item. */
    public UpdateMenuItemCommand(String targetCode, String updatedCode, String updatedName,
            Integer updatedPriceInCents) {
        requireAllNonNull(targetCode);
        if (updatedCode == null && updatedName == null && updatedPriceInCents == null) {
            throw new IllegalArgumentException("At least one menu item field must be updated.");
        }
        this.targetCode = targetCode;
        this.updatedCode = updatedCode;
        this.updatedName = updatedName;
        this.updatedPriceInCents = updatedPriceInCents;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        FoodItem itemToUpdate = model.getMenu().getFoodItemList().stream()
                .filter(item -> item.getCode().equals(targetCode))
                .findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_ITEM_NOT_FOUND));

        FoodItem updatedItem = new FoodItem(
                updatedCode == null ? itemToUpdate.getCode() : updatedCode,
                updatedName == null ? itemToUpdate.getName() : updatedName,
                updatedPriceInCents == null ? itemToUpdate.getPriceInCents() : updatedPriceInCents);
        if (!itemToUpdate.isSameFoodItem(updatedItem) && model.hasFoodItem(updatedItem)) {
            throw new CommandException(MESSAGE_DUPLICATE_ITEM);
        }
        model.setFoodItem(itemToUpdate, updatedItem);

        return new CommandResult(String.format(MESSAGE_SUCCESS, updatedItem.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UpdateMenuItemCommand otherUpdateMenuItemCommand)) {
            return false;
        }
        return targetCode.equals(otherUpdateMenuItemCommand.targetCode)
                && Objects.equals(updatedCode, otherUpdateMenuItemCommand.updatedCode)
                && Objects.equals(updatedName, otherUpdateMenuItemCommand.updatedName)
                && Objects.equals(updatedPriceInCents, otherUpdateMenuItemCommand.updatedPriceInCents);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetCode", targetCode)
                .add("updatedCode", updatedCode)
                .add("updatedName", updatedName)
                .add("updatedPriceInCents", updatedPriceInCents)
                .toString();
    }
}
