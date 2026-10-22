package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;

import seedu.address.logic.commands.DeleteMenuItemCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.menu.FoodItem;

/**
 * Parses an item code and creates a DeleteMenuItemCommand.
 */
public class DeleteMenuItemCommandParser implements Parser<DeleteMenuItemCommand> {
    @Override
    public DeleteMenuItemCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (args.chars().anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMenuItemCommand.MESSAGE_USAGE));
        }
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + args.replace('\t', ' '), PREFIX_CODE);
        if (arguments.getValue(PREFIX_CODE).isEmpty() || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMenuItemCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_CODE);
        String code = arguments.getValue(PREFIX_CODE).get();
        if (!FoodItem.isValidCode(code)) {
            throw new ParseException(FoodItem.MESSAGE_CODE_CONSTRAINTS);
        }
        return new DeleteMenuItemCommand(code);
    }
}
