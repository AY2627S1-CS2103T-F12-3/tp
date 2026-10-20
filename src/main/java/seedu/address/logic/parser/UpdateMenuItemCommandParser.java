package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEW_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.UpdateMenuItemCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.menu.FoodItem;

/**
 * Parses input arguments and creates an {@link UpdateMenuItemCommand}.
 */
public class UpdateMenuItemCommandParser implements Parser<UpdateMenuItemCommand> {
    private static final Pattern ARGUMENT_PREFIX = Pattern.compile("(?<= )[A-Za-z]/");
    private static final Set<String> SUPPORTED_PREFIXES = Set.of(
            PREFIX_CODE.getPrefix(), PREFIX_NEW_CODE.getPrefix(), PREFIX_NAME.getPrefix(), PREFIX_PRICE.getPrefix());

    @Override
    public UpdateMenuItemCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = tokenizeArguments(args);

        if (argMultimap.getPreamble().isEmpty() && argMultimap.getValue(PREFIX_CODE).isPresent()
                && (argMultimap.getValue(PREFIX_NAME).isPresent()
                || argMultimap.getValue(PREFIX_PRICE).isPresent()
                || argMultimap.getValue(PREFIX_NEW_CODE).isPresent())) {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CODE, PREFIX_NEW_CODE, PREFIX_NAME, PREFIX_PRICE);

            String code = argMultimap.getValue(PREFIX_CODE).get();
            if (!FoodItem.isValidCode(code)) {
                throw new ParseException(FoodItem.MESSAGE_CODE_CONSTRAINTS);
            }

            String newCode = argMultimap.getValue(PREFIX_NEW_CODE).orElse(null);
            if (newCode != null && !FoodItem.isValidCode(newCode)) {
                throw new ParseException(FoodItem.MESSAGE_CODE_CONSTRAINTS);
            }

            String name = argMultimap.getValue(PREFIX_NAME).orElse(null);
            if (name != null && !FoodItem.isValidName(name)) {
                throw new ParseException(FoodItem.MESSAGE_NAME_CONSTRAINTS);
            }

            Integer priceInCents = argMultimap.getValue(PREFIX_PRICE).isPresent()
                    ? AddMenuItemCommandParser.parsePrice(argMultimap.getValue(PREFIX_PRICE).get())
                    : null;
            return new UpdateMenuItemCommand(code, newCode, name, priceInCents);
        }

        throw invalidFormat();
    }

    private static ArgumentMultimap tokenizeArguments(String args) throws ParseException {
        if (args.chars().anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw invalidFormat();
        }

        String normalizedArgs = " " + args.replace('\t', ' ');
        Matcher matcher = ARGUMENT_PREFIX.matcher(normalizedArgs);
        while (matcher.find()) {
            if (!SUPPORTED_PREFIXES.contains(matcher.group())) {
                throw invalidFormat();
            }
        }
        return ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_CODE, PREFIX_NEW_CODE, PREFIX_NAME, PREFIX_PRICE);
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                UpdateMenuItemCommand.MESSAGE_USAGE));
    }
}
