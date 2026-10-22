package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteMenuItemCommand;
import seedu.address.model.menu.FoodItem;

public class DeleteMenuItemCommandParserTest {
    private final DeleteMenuItemCommandParser parser = new DeleteMenuItemCommandParser();

    @Test
    public void parse_validCode_preservesCaseAndAcceptsWhitespace() {
        for (String args : List.of(" c/CR01", "c/CR01", "   c/ CR01   ", "\tc/CR01\t")) {
            assertParseSuccess(parser, args, new DeleteMenuItemCommand("CR01"));
        }
        assertParseSuccess(parser, " c/cr01", new DeleteMenuItemCommand("cr01"));
        assertParseSuccess(parser, " c/0", new DeleteMenuItemCommand("0"));
    }

    @Test
    public void parse_missingCodeOrUnexpectedPreamble_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMenuItemCommand.MESSAGE_USAGE);
        for (String args : List.of("", " ", " CR01", " 1 c/CR01", " x/extra c/CR01")) {
            assertParseFailure(parser, args, message);
        }
    }

    @Test
    public void parse_invalidCodeOrExtraArguments_throwsParseException() {
        for (String code : List.of("", "CR-01", "CR 01", "CR01 n/Dish", "CR01 x/extra", "CR01 1", "鸡")) {
            assertParseFailure(parser, " c/" + code, FoodItem.MESSAGE_CODE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedCodePrefix_throwsParseException() {
        String message = Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CODE);
        for (String args : List.of(" c/CR01 c/CR02", " c/CR01 c/CR01", " c/CR01\tc/CR02", " c/CR01 c/")) {
            assertParseFailure(parser, args, message);
        }
    }

    @Test
    public void parse_controlCharacters_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMenuItemCommand.MESSAGE_USAGE);
        for (char control : new char[] {'\0', '\n', '\r', '\u001b', '\u007f'}) {
            assertParseFailure(parser, " c/CR01" + control, message);
        }
    }

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_longInputs_reportsValidationErrorsWithoutCrashing() {
        String code = "A".repeat(100_000);
        assertParseSuccess(parser, " c/" + code, new DeleteMenuItemCommand(code));
        assertParseFailure(parser, " c/" + code + "!", FoodItem.MESSAGE_CODE_CONSTRAINTS);
        assertParseFailure(parser, " c/CR01".repeat(10_000),
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CODE));
    }
}
