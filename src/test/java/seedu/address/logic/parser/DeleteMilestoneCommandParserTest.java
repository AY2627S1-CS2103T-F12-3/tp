package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteMilestoneCommand;

public class DeleteMilestoneCommandParserTest {
    private final DeleteMilestoneCommandParser parser = new DeleteMilestoneCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validIndex_returnsCommand() {
        assertParseSuccess(parser, " 1", new DeleteMilestoneCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "\t1\t", new DeleteMilestoneCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteMilestoneCommand.MESSAGE_USAGE);
        for (String args : List.of("", " ", "0", "-1", "abc", "1 2")) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }

    @Test
    public void parse_extraPrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteMilestoneCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 1 c/FREE500", expectedMessage);
        assertParseFailure(parser, " 1 x/extra", expectedMessage);
    }

    @Test
    public void parse_controlCharacter_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteMilestoneCommand.MESSAGE_USAGE);
        for (char control : new char[] {'\0', '\n', '\r', '\u001b', '\u007f'}) {
            assertParseFailure(parser, " 1" + control, expectedMessage);
        }
    }
}
