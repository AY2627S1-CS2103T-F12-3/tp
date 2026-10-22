package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MILESTONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RedeemMilestoneCommand;

public class RedeemMilestoneCommandParserTest {

    private static final String VALID_PERSON_INDEX = "1";
    private static final String VALID_MILESTONE_INDEX = "2";

    private final RedeemMilestoneCommandParser parser = new RedeemMilestoneCommandParser();

    @Test
    public void parse_validArgs_returnsRedeemMilestoneCommand() throws Exception {
        RedeemMilestoneCommand command = parser.parse(VALID_PERSON_INDEX + " "
                + PREFIX_MILESTONE + VALID_MILESTONE_INDEX);

        assertEquals(INDEX_FIRST_PERSON, command.personIndex);
        assertEquals(2, command.milestoneIndex.getOneBased());
    }

    @Test
    public void parse_missingPersonIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RedeemMilestoneCommand.MESSAGE_USAGE);

        assertParseFailure(parser, PREFIX_MILESTONE + VALID_MILESTONE_INDEX, expectedMessage);
    }

    @Test
    public void parse_missingMilestoneIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RedeemMilestoneCommand.MESSAGE_USAGE);

        assertParseFailure(parser, VALID_PERSON_INDEX, expectedMessage);
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_MILESTONE, expectedMessage);
    }

    @Test
    public void parse_invalidPersonIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RedeemMilestoneCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "abc " + PREFIX_MILESTONE + VALID_MILESTONE_INDEX, expectedMessage);
        assertParseFailure(parser, "0 " + PREFIX_MILESTONE + VALID_MILESTONE_INDEX, expectedMessage);
    }

    @Test
    public void parse_invalidMilestoneIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RedeemMilestoneCommand.MESSAGE_USAGE);

        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_MILESTONE + "abc", expectedMessage);
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_MILESTONE + "0", expectedMessage);
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_MILESTONE + "-1", expectedMessage);
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_MILESTONE + "2 extra", expectedMessage);
    }

    @Test
    public void parse_duplicateMilestonePrefix_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_MILESTONE + "1 "
                        + PREFIX_MILESTONE + "2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MILESTONE));
    }
}
