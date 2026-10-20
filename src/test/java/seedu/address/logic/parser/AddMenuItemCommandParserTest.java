package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddMenuItemCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.menu.FoodItem;

public class AddMenuItemCommandParserTest {
    private final AddMenuItemCommandParser parser = new AddMenuItemCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArgs_returnsCommand() {
        AddMenuItemCommand expected = new AddMenuItemCommand(new FoodItem("CR01", "Chicken Rice", 450));
        assertParseSuccess(parser, " c/CR01 n/Chicken Rice p/4.50", expected);
        assertParseSuccess(parser, " p/4.50 n/Chicken Rice c/CR01", expected);
        assertParseSuccess(parser, " c/CR01 p/4.50 n/Chicken Rice", expected);
        assertParseSuccess(parser, " n/Chicken Rice c/CR01 p/4.50", expected);
        assertParseSuccess(parser, " n/Chicken Rice p/4.50 c/CR01", expected);
        assertParseSuccess(parser, " p/4.50 c/CR01 n/Chicken Rice", expected);
        assertParseSuccess(parser, "c/CR01 n/Chicken Rice p/4.50", expected);
        assertParseSuccess(parser, "   c/ CR01   n/ Chicken Rice   p/ 4.50   ", expected);
    }

    @Test
    public void parse_caseSensitiveCode_preservesCase() {
        assertParseSuccess(parser, " c/cr01 n/Chicken Rice p/4.50",
                new AddMenuItemCommand(new FoodItem("cr01", "Chicken Rice", 450)));
    }

    @Test
    public void parse_validPrice_convertsDollarsToCents() {
        assertPriceSuccess("4", 400);
        assertPriceSuccess("4.5", 450);
        assertPriceSuccess("0004.50", 450);
        assertPriceSuccess("0.01", 1);
        assertPriceSuccess("21474836.47", Integer.MAX_VALUE);
    }

    @Test
    public void parse_missingPrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMenuItemCommand.MESSAGE_USAGE);
        for (String args : List.of("", " ", " n/Chicken Rice p/4.50", " c/CR01 p/4.50",
                " c/CR01 n/Chicken Rice")) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }

    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMenuItemCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " unexpected c/CR01 n/Chicken Rice p/4.50", expectedMessage);
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        for (Prefix prefix : List.of(PREFIX_CODE, PREFIX_NAME, PREFIX_PRICE)) {
            assertParseFailure(parser, " c/CR01 n/Chicken Rice p/4.50 " + prefix + "duplicate",
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
    }

    @Test
    public void parse_invalidCode_throwsParseException() {
        for (String code : List.of("", "CR-01", "CR 01", "CR_01")) {
            assertParseFailure(parser, " c/" + code + " n/Chicken Rice p/4.50", FoodItem.MESSAGE_CODE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_blankName_throwsParseException() {
        assertParseFailure(parser, " c/CR01 n/   p/4.50", FoodItem.MESSAGE_NAME_CONSTRAINTS);
    }

    @Test
    public void parse_invalidPrice_throwsParseException() {
        for (String price : List.of("", "0", "0.00", "-1", "+4.50", ".50", "4.", "4.501", "1e2",
                "$4.50", "NaN", "Infinity", "4,50", "4 50", "４.５０", "21474836.48",
                "999999999999999999999")) {
            assertParseFailure(parser, " c/CR01 n/Chicken Rice p/" + price,
                    AddMenuItemCommandParser.MESSAGE_PRICE_CONSTRAINTS);
        }
    }

    private void assertPriceSuccess(String price, int expectedCents) {
        assertParseSuccess(parser, " c/CR01 n/Chicken Rice p/" + price,
                new AddMenuItemCommand(new FoodItem("CR01", "Chicken Rice", expectedCents)));
    }

    @Test
    public void parse_unknownPrefixInName_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMenuItemCommand.MESSAGE_USAGE);
        for (String args : List.of(" c/CR01 n/Chicken Rice x/extra p/4.50",
                " c/CR01 p/4.50 n/Chicken Rice x/extra", " c/CR01 n/Chicken Rice X/extra p/4.50")) {
            assertParseFailure(parser, args, message);
        }
    }

    @Test
    public void parse_tabsBetweenPrefixes_returnsCommand() {
        assertParseSuccess(parser, "\tc/CR01\tn/Chicken Rice\tp/4.50",
                new AddMenuItemCommand(new FoodItem("CR01", "Chicken Rice", 450)));
    }

    @Test
    public void parse_duplicatePrefixAfterTab_throwsParseException() {
        assertParseFailure(parser, " c/CR01 n/Chicken Rice\tn/Different Dish p/4.50",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_controlCharacterInName_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMenuItemCommand.MESSAGE_USAGE);
        for (char control : new char[] {'\0', '\n', '\r', '\u001b', '\u007f'}) {
            assertParseFailure(parser, " c/CR01 n/Chicken" + control + "Rice p/4.50", message);
        }
    }

    @Test
    public void parse_validPunctuationAndUnicodeName_preservesName() {
        for (String name : List.of("Sweet/Sour Pork", "Fish & Chips", "Chef's Special", "鸡饭")) {
            assertParseSuccess(parser, " c/CR01 n/" + name + " p/4.50",
                    new AddMenuItemCommand(new FoodItem("CR01", name, 450)));
        }
    }

    @Test
    public void parse_extremelyLongPrice_handlesWithoutOverflow() {
        assertParseFailure(parser, " c/CR01 n/Chicken Rice p/" + "9".repeat(100_000),
                AddMenuItemCommandParser.MESSAGE_PRICE_CONSTRAINTS);
        assertPriceSuccess("0".repeat(100_000) + "4.50", 450);
    }

    @Test
    public void parse_generatedValidPrices_preservesExactCents() {
        Random random = new Random(2103);
        for (int i = 0; i < 2000; i++) {
            int cents = random.nextInt(Integer.MAX_VALUE) + 1;
            String fractionalPart = Integer.toString(cents % 100 + 100).substring(1);
            assertPriceSuccess(cents / 100 + "." + fractionalPart, cents);
        }
    }

    @Test
    public void parse_randomMalformedInput_neverThrowsUnexpectedException() {
        Random random = new Random(2103);
        String alphabet = "cnpx/0123456789 .+-_$\t\n\r\0鸡";
        List<String> chunks = List.of(" c/CR01", " n/Chicken Rice", " p/4.50", " c/", " n/", " p/",
                " x/extra", " p/21474836.48", " n/鱼", "\tc/CR02", "\n", "\0", " unexpected");
        for (int i = 0; i < 5000; i++) {
            StringBuilder input = new StringBuilder(i % 2 == 0 ? " c/CR01 n/Chicken Rice p/" : "");
            int length = random.nextInt(80);
            for (int j = 0; j < length; j++) {
                if (i % 2 == 0) {
                    input.append(alphabet.charAt(random.nextInt(alphabet.length())));
                } else {
                    input.append(chunks.get(random.nextInt(chunks.size())));
                }
            }
            try {
                parser.parse(input.toString());
            } catch (ParseException expected) {
                assertNotNull(expected.getMessage());
            }
        }
    }
}
