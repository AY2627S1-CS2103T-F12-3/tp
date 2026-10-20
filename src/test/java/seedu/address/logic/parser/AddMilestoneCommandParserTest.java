package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIRED_SCORE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REWARD;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalMilestones.FREE_DRINK;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddMilestoneCommand;
import seedu.address.model.milestone.Milestone;

public class AddMilestoneCommandParserTest {
    private final AddMilestoneCommandParser parser = new AddMilestoneCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArgsInAnyOrder_returnsCommand() {
        AddMilestoneCommand expected = new AddMilestoneCommand(FREE_DRINK);

        assertParseSuccess(parser, " c/FREE500 r/Free drink s/500", expected);
        assertParseSuccess(parser, " s/500 c/FREE500 r/Free drink", expected);
        assertParseSuccess(parser, " r/Free drink s/500 c/FREE500", expected);
        assertParseSuccess(parser, "   c/ FREE500   r/ Free drink   s/ 500   ", expected);
    }

    @Test
    public void parse_lowercaseCode_normalizesCode() {
        assertParseSuccess(parser, " c/free500 r/Free drink s/500",
                new AddMilestoneCommand(FREE_DRINK));
    }

    @Test
    public void parse_missingPrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddMilestoneCommand.MESSAGE_USAGE);
        for (String args : List.of("", " ", " r/Free drink s/500", " c/FREE500 s/500",
                " c/FREE500 r/Free drink")) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }

    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddMilestoneCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " unexpected c/FREE500 r/Free drink s/500", expectedMessage);
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        for (Prefix prefix : List.of(PREFIX_CODE, PREFIX_REWARD, PREFIX_REQUIRED_SCORE)) {
            assertParseFailure(parser, " c/FREE500 r/Free drink s/500 " + prefix + "duplicate",
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
    }

    @Test
    public void parse_unknownPrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddMilestoneCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " c/FREE500 r/Free drink x/extra s/500", expectedMessage);
        assertParseFailure(parser, " c/FREE500 r/Free drink X/extra s/500", expectedMessage);
    }

    @Test
    public void parse_invalidCode_throwsParseException() {
        for (String code : List.of("", "FREE-500", "FREE 500", "FREE_500")) {
            assertParseFailure(parser, " c/" + code + " r/Free drink s/500",
                    Milestone.MESSAGE_CODE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_blankReward_throwsParseException() {
        assertParseFailure(parser, " c/FREE500 r/   s/500",
                Milestone.MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS);
    }

    @Test
    public void parse_invalidRequiredScore_throwsParseException() {
        for (String requiredScore : List.of("", "0", "-1", "+500", "1.5", "1e3", "2147483648")) {
            assertParseFailure(parser, " c/FREE500 r/Free drink s/" + requiredScore,
                    ParserUtil.MESSAGE_INVALID_REQUIRED_SCORE);
        }
    }

    @Test
    public void parse_tabsBetweenPrefixes_returnsCommand() {
        assertParseSuccess(parser, "\tc/FREE500\tr/Free drink\ts/500",
                new AddMilestoneCommand(FREE_DRINK));
    }

    @Test
    public void parse_controlCharacter_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddMilestoneCommand.MESSAGE_USAGE);
        for (char control : new char[] {'\0', '\n', '\r', '\u001b', '\u007f'}) {
            assertParseFailure(parser, " c/FREE500 r/Free" + control + "drink s/500", expectedMessage);
        }
    }

    @Test
    public void parse_validRewardPunctuationAndUnicode_preservesReward() {
        for (String reward : List.of("Drink/Snack", "Fish & Chips", "Chef's Special", "免费饮料")) {
            assertParseSuccess(parser, " c/FREE500 r/" + reward + " s/500",
                    new AddMilestoneCommand(new Milestone("FREE500", reward, 500)));
        }
    }
}
