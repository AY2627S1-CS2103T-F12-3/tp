package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteOrderCommand;

public class DeleteOrderCommandParserTest {

    private final DeleteOrderCommandParser parser = new DeleteOrderCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteOrderCommand() {
        assertParseSuccess(parser, "1 " + PREFIX_ORDER + "2",
                new DeleteOrderCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
    }

    @Test
    public void parse_missingPersonIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteOrderCommand.MESSAGE_USAGE);

        assertParseFailure(parser, PREFIX_ORDER + "1", expectedMessage);
    }

    @Test
    public void parse_missingOrderIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteOrderCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "1", expectedMessage);
        assertParseFailure(parser, "1 " + PREFIX_ORDER, expectedMessage);
    }

    @Test
    public void parse_invalidPersonIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteOrderCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "abc " + PREFIX_ORDER + "1", expectedMessage);
        assertParseFailure(parser, "0 " + PREFIX_ORDER + "1", expectedMessage);
    }

    @Test
    public void parse_invalidOrderIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteOrderCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "1 " + PREFIX_ORDER + "abc", expectedMessage);
        assertParseFailure(parser, "1 " + PREFIX_ORDER + "0", expectedMessage);
        assertParseFailure(parser, "1 " + PREFIX_ORDER + "-1", expectedMessage);
        assertParseFailure(parser, "1 " + PREFIX_ORDER + "2 extra", expectedMessage);
    }

    @Test
    public void parse_duplicateOrderPrefix_throwsParseException() {
        assertParseFailure(parser, "1 " + PREFIX_ORDER + "1 " + PREFIX_ORDER + "2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ORDER));
    }

    @Test
    public void parse_unsupportedPrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteOrderCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "1 p/98765432 " + PREFIX_ORDER + "1", expectedMessage);
        assertParseFailure(parser, "1 " + PREFIX_ORDER + "1 p/98765432", expectedMessage);
    }
}
