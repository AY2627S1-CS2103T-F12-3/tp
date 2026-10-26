package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears all customers, menu items, and milestone rewards.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "All customers, menu items, and milestone rewards have been cleared!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.deselectPerson();
        model.setAddressBook(new AddressBook());
        List.copyOf(model.getMenu().getFoodItemList()).forEach(model::deleteFoodItem);
        List.copyOf(model.getMilestoneList()).forEach(model::deleteMilestone);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
