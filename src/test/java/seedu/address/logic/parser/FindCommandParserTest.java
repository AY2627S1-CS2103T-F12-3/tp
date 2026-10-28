package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.PersonMatchesKeywordsPredicate;
import seedu.address.model.person.Phone;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new PersonMatchesKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_eightDigitPhone_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new PersonMatchesKeywordsPredicate(List.of("98765432")));

        assertParseSuccess(parser, "98765432", expectedFindCommand);
    }

    @Test
    public void parse_formattedSingaporePhone_normalizesQuery() {
        FindCommand expected = new FindCommand(new PersonMatchesKeywordsPredicate(List.of("91234567")));
        for (String input : List.of("9123 4567", "+6591234567", "65 9123 4567", "（＋６５）９１２３－４５６７")) {
            assertParseSuccess(parser, input, expected);
        }
    }

    @Test
    public void parse_explicitPhone_preservesWholeEntry() {
        for (String input : List.of("+86 138119828111", "（86）12345678", "91234567 (HP)", "Not available")) {
            FindCommand expected = new FindCommand(new PersonMatchesKeywordsPredicate(new Phone(input)));
            assertParseSuccess(parser, "p/" + input, expected);
        }
        assertParseFailure(parser, "p/ ", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "\u00a0", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

}
