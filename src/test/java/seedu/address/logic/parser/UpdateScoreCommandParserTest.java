package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCORE_CHANGE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.UpdateScoreCommand;

public class UpdateScoreCommandParserTest {

    private static final String VALID_PERSON_INDEX = "1";
    private static final String VALID_SCORE_CHANGE = "10";

    private final UpdateScoreCommandParser parser = new UpdateScoreCommandParser();

    @Test
    public void parse_validPositiveChange_returnsCommand() throws Exception {
        UpdateScoreCommand command = parser.parse(VALID_PERSON_INDEX + " "
                + PREFIX_SCORE_CHANGE + VALID_SCORE_CHANGE);

        assertEquals(INDEX_FIRST_PERSON, command.index);
        assertEquals(10, command.scoreChange);
    }

    @Test
    public void parse_validNegativeChange_returnsCommand() throws Exception {
        UpdateScoreCommand command = parser.parse(VALID_PERSON_INDEX + " " + PREFIX_SCORE_CHANGE + "-5");

        assertEquals(-5, command.scoreChange);
    }

    @Test
    public void parse_missingRequiredPrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateScoreCommand.MESSAGE_USAGE);

        assertParseFailure(parser, VALID_PERSON_INDEX, expectedMessage);
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateScoreCommand.MESSAGE_USAGE);

        assertParseFailure(parser, PREFIX_SCORE_CHANGE + VALID_SCORE_CHANGE, expectedMessage);
    }

    @Test
    public void parse_emptyArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateScoreCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "", expectedMessage);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateScoreCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "abc " + PREFIX_SCORE_CHANGE + VALID_SCORE_CHANGE, expectedMessage);
    }

    @Test
    public void parse_invalidScoreChange_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_SCORE_CHANGE + "abc",
                "Score change must be a signed whole number.");
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_SCORE_CHANGE + "1000000000",
                "Score change must be a signed whole number.");
    }

    @Test
    public void parse_duplicateScoreChangePrefix_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_SCORE_CHANGE + "1 "
                        + PREFIX_SCORE_CHANGE + "2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SCORE_CHANGE));
    }
}
