package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIRED_SCORE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REWARD;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditMilestoneCommand;
import seedu.address.logic.commands.EditMilestoneCommand.EditMilestoneDescriptor;
import seedu.address.model.milestone.Milestone;

public class EditMilestoneCommandParserTest {
    private final EditMilestoneCommandParser parser = new EditMilestoneCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_allFieldsPresent_returnsCommand() {
        EditMilestoneDescriptor descriptor = new EditMilestoneDescriptor();
        descriptor.setRewardDescription("Free drink and snack");
        descriptor.setRequiredScore(600);
        EditMilestoneCommand expected = new EditMilestoneCommand(INDEX_FIRST_PERSON, descriptor);

        assertParseSuccess(parser, " 1 r/Free drink and snack s/600", expected);
        assertParseSuccess(parser, " 1 s/600 r/Free drink and snack", expected);
    }

    @Test
    public void parse_oneFieldPresent_returnsCommand() {
        EditMilestoneDescriptor rewardDescriptor = new EditMilestoneDescriptor();
        rewardDescriptor.setRewardDescription("Free dessert");
        EditMilestoneDescriptor scoreDescriptor = new EditMilestoneDescriptor();
        scoreDescriptor.setRequiredScore(800);

        assertParseSuccess(parser, " 1 r/Free dessert",
                new EditMilestoneCommand(INDEX_FIRST_PERSON, rewardDescriptor));
        assertParseSuccess(parser, " 1 s/800",
                new EditMilestoneCommand(INDEX_FIRST_PERSON, scoreDescriptor));
    }

    @Test
    public void parse_noField_throwsParseException() {
        assertParseFailure(parser, " 1", EditMilestoneCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EditMilestoneCommand.MESSAGE_USAGE);
        for (String index : List.of("", "0", "-1", "abc")) {
            assertParseFailure(parser, index + " r/Free drink", expectedMessage);
        }
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        assertParseFailure(parser, " 1 r/Free drink r/Free snack",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REWARD));
        assertParseFailure(parser, " 1 s/500 s/600",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REQUIRED_SCORE));
    }

    @Test
    public void parse_unknownOrCodePrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EditMilestoneCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 1 x/extra r/Free drink", expectedMessage);
        assertParseFailure(parser, " 1 c/NEWCODE r/Free drink", expectedMessage);
    }

    @Test
    public void parse_blankReward_throwsParseException() {
        assertParseFailure(parser, " 1 r/   ", Milestone.MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS);
    }

    @Test
    public void parse_invalidRequiredScore_throwsParseException() {
        for (String requiredScore : List.of("", "0", "-1", "+500", "1.5", "2147483648")) {
            assertParseFailure(parser, " 1 s/" + requiredScore,
                    ParserUtil.MESSAGE_INVALID_REQUIRED_SCORE);
        }
    }

    @Test
    public void parse_tabsBetweenPrefixes_returnsCommand() {
        EditMilestoneDescriptor descriptor = new EditMilestoneDescriptor();
        descriptor.setRewardDescription("Free drink");
        descriptor.setRequiredScore(500);

        assertParseSuccess(parser, "\t1\tr/Free drink\ts/500",
                new EditMilestoneCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_controlCharacter_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EditMilestoneCommand.MESSAGE_USAGE);
        for (char control : new char[] {'\0', '\n', '\r', '\u001b', '\u007f'}) {
            assertParseFailure(parser, " 1 r/Free" + control + "drink", expectedMessage);
        }
    }
}
