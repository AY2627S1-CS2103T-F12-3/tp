package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEW_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.UpdateMenuItemCommand;
import seedu.address.model.menu.FoodItem;

public class UpdateMenuItemCommandParserTest {
    private final UpdateMenuItemCommandParser parser = new UpdateMenuItemCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() {
        UpdateMenuItemCommand expected = new UpdateMenuItemCommand("CR01", "CR02", "Roasted Chicken Rice", 500);
        assertParseSuccess(parser, " c/CR01 nc/CR02 n/Roasted Chicken Rice p/5.00", expected);
        assertParseSuccess(parser, " c/CR01 p/5.00 n/Roasted Chicken Rice nc/CR02", expected);
    }

    @Test
    public void parse_singleField_returnsCommand() {
        assertParseSuccess(parser, " c/CR01 n/Roasted Chicken Rice",
                new UpdateMenuItemCommand("CR01", null, "Roasted Chicken Rice", null));
        assertParseSuccess(parser, " c/CR01 p/5.00",
                new UpdateMenuItemCommand("CR01", null, null, 500));
        assertParseSuccess(parser, " c/CR01 nc/CR02",
                new UpdateMenuItemCommand("CR01", "CR02", null, null));
    }

    @Test
    public void parse_missingRequiredArguments_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateMenuItemCommand.MESSAGE_USAGE);
        for (String args : List.of("", " c/CR01", " n/Name", " p/5.00")) {
            assertParseFailure(parser, args, message);
        }
    }

    @Test
    public void parse_invalidValues_throwsParseException() {
        assertParseFailure(parser, " c/CR-01 n/Name", FoodItem.MESSAGE_CODE_CONSTRAINTS);
        assertParseFailure(parser, " c/CR01 nc/CR-02", FoodItem.MESSAGE_CODE_CONSTRAINTS);
        assertParseFailure(parser, " c/CR01 n/   ", FoodItem.MESSAGE_NAME_CONSTRAINTS);
        assertParseFailure(parser, " c/CR01 p/0", AddMenuItemCommandParser.MESSAGE_PRICE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        assertParseFailure(parser, " c/CR01 n/Name n/Other",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, " c/CR01 p/5.00 p/6.00",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PRICE));
        assertParseFailure(parser, " c/CR01 nc/CR02 nc/CR03",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NEW_CODE));
        assertParseFailure(parser, " c/CR01 n/Name c/CR02",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CODE));
    }
}
