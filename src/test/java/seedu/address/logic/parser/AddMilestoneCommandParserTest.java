package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIRED_SCORE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REWARD;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddMilestoneCommand;
import seedu.address.model.ModelManager;
import seedu.address.model.milestone.Milestone;

public class AddMilestoneCommandParserTest {
    private final AddMilestoneCommandParser parser = new AddMilestoneCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArgsInAnyOrder_returnsCommand() throws Exception {
        assertParsedMilestone(" r/Free drink s/500", "Free drink", 500);
        assertParsedMilestone(" s/500 r/Free drink", "Free drink", 500);
        assertParsedMilestone("   r/ Free drink   s/ 500   ", "Free drink", 500);
    }

    @Test
    public void parse_missingPrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddMilestoneCommand.MESSAGE_USAGE);
        for (String args : List.of("", " ", " s/500", " r/Free drink")) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }

    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddMilestoneCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " unexpected r/Free drink s/500", expectedMessage);
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        for (Prefix prefix : List.of(PREFIX_REWARD, PREFIX_REQUIRED_SCORE)) {
            assertParseFailure(parser, " r/Free drink s/500 " + prefix + "duplicate",
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
    }

    @Test
    public void parse_unknownPrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddMilestoneCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " r/Free drink c/OLD s/500", expectedMessage);
        assertParseFailure(parser, " r/Free drink x/extra s/500", expectedMessage);
        assertParseFailure(parser, " r/Free drink X/extra s/500", expectedMessage);
    }

    @Test
    public void parse_blankReward_throwsParseException() {
        assertParseFailure(parser, " r/   s/500",
                Milestone.MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS);
    }

    @Test
    public void parse_invalidRequiredScore_throwsParseException() {
        for (String requiredScore : List.of("", "0", "-1", "+500", "1.5", "1e3", "2147483648")) {
            assertParseFailure(parser, " r/Free drink s/" + requiredScore,
                    ParserUtil.MESSAGE_INVALID_REQUIRED_SCORE);
        }
    }

    @Test
    public void parse_tabsBetweenPrefixes_returnsCommand() throws Exception {
        assertParsedMilestone("\tr/Free drink\ts/500", "Free drink", 500);
    }

    @Test
    public void parse_controlCharacter_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddMilestoneCommand.MESSAGE_USAGE);
        for (char control : new char[] {'\0', '\n', '\r', '\u001b', '\u007f'}) {
            assertParseFailure(parser, " r/Free" + control + "drink s/500", expectedMessage);
        }
    }

    @Test
    public void parse_validRewardPunctuationAndUnicode_preservesReward() throws Exception {
        for (String reward : List.of("Drink/Snack", "Fish & Chips", "Chef's Special", "免费饮料")) {
            assertParsedMilestone(" r/" + reward + " s/500", reward, 500);
        }
    }

    private void assertParsedMilestone(String args, String reward, int requiredScore) throws Exception {
        ModelManager model = new ModelManager();
        parser.parse(args).execute(model);
        Milestone milestone = model.getMilestoneList().get(0);
        assertEquals(reward, milestone.getRewardDescription());
        assertEquals(requiredScore, milestone.getRequiredScore());
        assertTrue(milestone.getCode().contains("-"));
    }
}
