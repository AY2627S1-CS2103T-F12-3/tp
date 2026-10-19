package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;

import java.math.BigDecimal;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddMenuItemCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.menu.FoodItem;

/**
 * Parses input arguments and creates an AddMenuItemCommand object.
 */
public class AddMenuItemCommandParser implements Parser<AddMenuItemCommand> {
    public static final String MESSAGE_PRICE_CONSTRAINTS =
            "Price must be between 0.01 and 21474836.47 dollars, with at most two decimal places.";
    private static final Pattern ARGUMENT_PREFIX = Pattern.compile("(?<= )[A-Za-z]/");
    private static final Set<String> SUPPORTED_PREFIXES = Set.of(
            PREFIX_CODE.getPrefix(), PREFIX_NAME.getPrefix(), PREFIX_PRICE.getPrefix());

    /**
     * Parses the code, name, and dollar price into a command to add a menu item.
     *
     * @throws ParseException if the arguments violate the command format or food item constraints.
     */
    @Override
    public AddMenuItemCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = tokenizeArguments(args);

        if (!arePrefixesPresent(argMultimap, PREFIX_CODE, PREFIX_NAME, PREFIX_PRICE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMenuItemCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CODE, PREFIX_NAME, PREFIX_PRICE);
        String code = argMultimap.getValue(PREFIX_CODE).get();
        String name = argMultimap.getValue(PREFIX_NAME).get();
        if (!FoodItem.isValidCode(code)) {
            throw new ParseException(FoodItem.MESSAGE_CODE_CONSTRAINTS);
        }
        if (!FoodItem.isValidName(name)) {
            throw new ParseException(FoodItem.MESSAGE_NAME_CONSTRAINTS);
        }
        int priceInCents = parsePrice(argMultimap.getValue(PREFIX_PRICE).get());
        FoodItem foodItem = new FoodItem(code, name, priceInCents);
        return new AddMenuItemCommand(foodItem);
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    private static ArgumentMultimap tokenizeArguments(String args) throws ParseException {
        if (args.chars().anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMenuItemCommand.MESSAGE_USAGE));
        }

        // The shared tokenizer recognizes spaces; tabs are also valid separators for this command.
        String normalizedArgs = " " + args.replace('\t', ' ');
        Matcher matcher = ARGUMENT_PREFIX.matcher(normalizedArgs);
        while (matcher.find()) {
            if (!SUPPORTED_PREFIXES.contains(matcher.group())) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMenuItemCommand.MESSAGE_USAGE));
            }
        }
        return ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_CODE, PREFIX_NAME, PREFIX_PRICE);
    }

    private static int parsePrice(String price) throws ParseException {
        if (!price.matches("[0-9]+(\\.[0-9]{1,2})?")) {
            throw new ParseException(MESSAGE_PRICE_CONSTRAINTS);
        }
        try {
            // Convert exactly so a price is never silently rounded or truncated.
            int priceInCents = new BigDecimal(price).movePointRight(2).intValueExact();
            if (!FoodItem.isValidPrice(priceInCents)) {
                throw new ParseException(MESSAGE_PRICE_CONSTRAINTS);
            }
            return priceInCents;
        } catch (ArithmeticException e) {
            throw new ParseException(MESSAGE_PRICE_CONSTRAINTS, e);
        }
    }
}
