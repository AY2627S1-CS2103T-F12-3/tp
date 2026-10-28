package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesKeywordsPredicate;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = Phone.normalize(args);
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        if (trimmedArgs.startsWith(CliSyntax.PREFIX_PHONE.toString())) {
            String query = trimmedArgs.substring(CliSyntax.PREFIX_PHONE.toString().length());
            if (!Phone.isStorablePhone(query)) {
                throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
            }
            return new FindCommand(new PersonMatchesKeywordsPredicate(new Phone(query)));
        }
        Phone wholeQuery = new Phone(trimmedArgs);
        if (wholeQuery.isStandardSingaporeNumber()) {
            return new FindCommand(new PersonMatchesKeywordsPredicate(List.of(wholeQuery.value)));
        }
        String[] nameKeywords = trimmedArgs.split("\\s+");

        return new FindCommand(new PersonMatchesKeywordsPredicate(List.of(nameKeywords)));
    }

}
