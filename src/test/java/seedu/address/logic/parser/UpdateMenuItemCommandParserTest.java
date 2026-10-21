package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.UpdateMenuItemCommand;
import seedu.address.model.menu.FoodItem;

public class UpdateMenuItemCommandParserTest {
    private final UpdateMenuItemCommandParser parser = new UpdateMenuItemCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArgs_returnsCommand() {
        UpdateMenuItemCommand expected = new UpdateMenuItemCommand("CR01", "CR02", "Roasted Chicken Rice", 500);
        assertParseSuccess(parser, " CR01 c/CR02 n/Roasted Chicken Rice p/5.00", expected);
        assertParseSuccess(parser, " CR01 p/5.00 n/Roasted Chicken Rice c/CR02", expected);
    }

    @Test
    public void parse_singleField_returnsCommand() {
        assertParseSuccess(parser, " CR01 n/Roasted Chicken Rice",
                new UpdateMenuItemCommand("CR01", null, "Roasted Chicken Rice", null));
        assertParseSuccess(parser, " CR01 p/5.00",
                new UpdateMenuItemCommand("CR01", null, null, 500));
        assertParseSuccess(parser, " CR01 c/CR02",
                new UpdateMenuItemCommand("CR01", "CR02", null, null));
    }

    @Test
    public void parse_missingRequiredArguments_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateMenuItemCommand.MESSAGE_USAGE);
        for (String args : List.of("", " c/CR01", " n/Name", " p/5.00", " CR01")) {
            assertParseFailure(parser, args, message);
        }
    }

    @Test
    public void parse_invalidValues_throwsParseException() {
        assertParseFailure(parser, " CR-01 n/Name", FoodItem.MESSAGE_CODE_CONSTRAINTS);
        assertParseFailure(parser, " CR01 c/CR-02", FoodItem.MESSAGE_CODE_CONSTRAINTS);
        assertParseFailure(parser, " CR01 n/   ", FoodItem.MESSAGE_NAME_CONSTRAINTS);
        assertParseFailure(parser, " CR01 p/0", AddMenuItemCommandParser.MESSAGE_PRICE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        assertParseFailure(parser, " CR01 n/Name n/Other",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, " CR01 p/5.00 p/6.00",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PRICE));
        assertParseFailure(parser, " CR01 c/CR02 c/CR03",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CODE));
    }

    @Test
    public void parse_unknownPrefix_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateMenuItemCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " CR01 x/extra", message);
    }

    @Test
    public void parse_controlCharacter_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateMenuItemCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " CR01 n/Di\nsh", message);
    }

    @Test
    public void parse_tabsBetweenArguments_returnsCommand() {
        assertParseSuccess(parser, "\tCR01\tc/CR02\tn/Dish\tp/5.00",
                new UpdateMenuItemCommand("CR01", "CR02", "Dish", 500));
    }
}
