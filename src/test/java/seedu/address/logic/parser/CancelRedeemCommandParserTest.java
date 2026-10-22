package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MILESTONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CancelRedeemCommand;

public class CancelRedeemCommandParserTest {

    private static final String VALID_PERSON_INDEX = "1";
    private static final String VALID_MILESTONE_INDEX = "2";

    private final CancelRedeemCommandParser parser = new CancelRedeemCommandParser();

    @Test
    public void parse_validArgs_returnsCancelRedeemCommand() throws Exception {
        CancelRedeemCommand command = parser.parse(VALID_PERSON_INDEX + " "
                + PREFIX_MILESTONE + VALID_MILESTONE_INDEX);

        assertEquals(INDEX_FIRST_PERSON, command.personIndex);
        assertEquals(2, command.milestoneIndex.getOneBased());
    }

    @Test
    public void parse_missingArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                CancelRedeemCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, VALID_PERSON_INDEX, expectedMessage);
        assertParseFailure(parser, PREFIX_MILESTONE + VALID_MILESTONE_INDEX, expectedMessage);
    }

    @Test
    public void parse_invalidIndexes_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                CancelRedeemCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "abc " + PREFIX_MILESTONE + VALID_MILESTONE_INDEX, expectedMessage);
        assertParseFailure(parser, "0 " + PREFIX_MILESTONE + VALID_MILESTONE_INDEX, expectedMessage);
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_MILESTONE + "abc", expectedMessage);
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_MILESTONE + "0", expectedMessage);
    }

    @Test
    public void parse_duplicateMilestonePrefix_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_MILESTONE + "1 "
                        + PREFIX_MILESTONE + "2",
                getErrorMessageForDuplicatePrefixes(PREFIX_MILESTONE));
    }
}
