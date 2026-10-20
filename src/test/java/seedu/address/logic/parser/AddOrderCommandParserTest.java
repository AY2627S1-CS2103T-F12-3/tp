package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddOrderCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.order.Order;

public class AddOrderCommandParserTest {

    private static final String VALID_ITEM_CODE = "CR01";
    private static final String VALID_QUANTITY = "2";
    private static final String VALID_PERSON_INDEX = "1";
    private static final List<Prefix> OTHER_UNSUPPORTED_PREFIXES =
            List.of(PREFIX_NAME, new Prefix("x/"), PREFIX_TAG, new Prefix("y/"));

    private final AddOrderCommandParser parser = new AddOrderCommandParser();

    @Test
    public void parse_validInput_returnsCommandWithExpectedValues() throws ParseException {
        AddOrderCommand command = parser.parse(VALID_PERSON_INDEX + " " + PREFIX_ORDER + VALID_ITEM_CODE
                + " " + PREFIX_QUANTITY + VALID_QUANTITY);

        assertEquals(INDEX_FIRST_PERSON, command.index);
        assertEquals(VALID_ITEM_CODE, command.order.itemCode);
        assertEquals(Integer.parseInt(VALID_QUANTITY), command.order.quantity);
    }

    @Test
    public void parse_quantityBeforeItem_success() throws ParseException {
        AddOrderCommand command = parser.parse(VALID_PERSON_INDEX + " " + PREFIX_QUANTITY + VALID_QUANTITY
                + " " + PREFIX_ORDER + VALID_ITEM_CODE);

        assertEquals(VALID_ITEM_CODE, command.order.itemCode);
        assertEquals(Integer.parseInt(VALID_QUANTITY), command.order.quantity);
    }

    @Test
    public void parse_missingRequiredPrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddOrderCommand.MESSAGE_USAGE);

        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_ORDER + VALID_ITEM_CODE, expectedMessage);
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_QUANTITY + VALID_QUANTITY, expectedMessage);
        assertParseFailure(parser, VALID_PERSON_INDEX, expectedMessage);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddOrderCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "abc " + PREFIX_ORDER + VALID_ITEM_CODE
                + " " + PREFIX_QUANTITY + VALID_QUANTITY, expectedMessage);
    }

    @Test
    public void parse_duplicateOrderPrefix_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_ORDER + VALID_ITEM_CODE
                + " " + PREFIX_ORDER + "CF01 " + PREFIX_QUANTITY + VALID_QUANTITY,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ORDER));
    }

    @Test
    public void parse_duplicateQuantityPrefix_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_ORDER + VALID_ITEM_CODE
                + " " + PREFIX_QUANTITY + VALID_QUANTITY + " " + PREFIX_QUANTITY + "3",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_QUANTITY));
    }

    @Test
    public void parse_invalidItemCode_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_ORDER + " " + PREFIX_QUANTITY + VALID_QUANTITY,
                Order.ITEM_CODE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidQuantity_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_ORDER + VALID_ITEM_CODE
                + " " + PREFIX_QUANTITY + "abc",
                Order.QUANTITY_CONSTRAINTS);
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_ORDER + VALID_ITEM_CODE
                + " " + PREFIX_QUANTITY + "0",
                Order.QUANTITY_CONSTRAINTS);
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_ORDER + VALID_ITEM_CODE
                + " " + PREFIX_QUANTITY + "-1",
                Order.QUANTITY_CONSTRAINTS);
    }

    @Test
    public void parse_unsupportedPrefixBeforeItem_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddOrderCommand.MESSAGE_USAGE);

        assertParseFailure(parser, VALID_PERSON_INDEX + " p/98765432 " + PREFIX_ORDER + VALID_ITEM_CODE
                + " " + PREFIX_QUANTITY + VALID_QUANTITY, expectedMessage);
    }

    @Test
    public void parse_unsupportedPrefixAfterQuantity_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_ORDER + VALID_ITEM_CODE
                + " " + PREFIX_QUANTITY + VALID_QUANTITY + " p/98765432",
                Order.QUANTITY_CONSTRAINTS);
    }

    @Test
    public void parse_unsupportedPrefixAfterItem_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_QUANTITY + VALID_QUANTITY
                + " " + PREFIX_ORDER + VALID_ITEM_CODE + " p/99865432", Order.ITEM_CODE_CONSTRAINTS);
    }

    @Test
    public void parse_otherUnsupportedPrefixesBeforeItem_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddOrderCommand.MESSAGE_USAGE);

        for (Prefix unsupportedPrefix : OTHER_UNSUPPORTED_PREFIXES) {
            assertParseFailure(parser, VALID_PERSON_INDEX + " " + unsupportedPrefix + "value "
                    + PREFIX_ORDER + VALID_ITEM_CODE + " " + PREFIX_QUANTITY + VALID_QUANTITY, expectedMessage);
        }
    }

    @Test
    public void parse_otherUnsupportedPrefixesAfterQuantity_throwsParseException() {
        for (Prefix unsupportedPrefix : OTHER_UNSUPPORTED_PREFIXES) {
            assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_ORDER + VALID_ITEM_CODE
                    + " " + PREFIX_QUANTITY + VALID_QUANTITY + " " + unsupportedPrefix + "value",
                    Order.QUANTITY_CONSTRAINTS);
        }
    }

    @Test
    public void parse_otherUnsupportedPrefixesAfterItem_throwsParseException() {
        for (Prefix unsupportedPrefix : OTHER_UNSUPPORTED_PREFIXES) {
            assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_QUANTITY + VALID_QUANTITY
                    + " " + PREFIX_ORDER + VALID_ITEM_CODE + " " + unsupportedPrefix + "value",
                    Order.ITEM_CODE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_extraTextAfterQuantity_throwsParseException() {
        assertParseFailure(parser, VALID_PERSON_INDEX + " " + PREFIX_ORDER + VALID_ITEM_CODE
                + " " + PREFIX_QUANTITY + VALID_QUANTITY + " extra",
                Order.QUANTITY_CONSTRAINTS);
    }

    @Test
    public void parse_emptyPreamble_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddOrderCommand.MESSAGE_USAGE);

        assertParseFailure(parser, PREFIX_ORDER + VALID_ITEM_CODE + " " + PREFIX_QUANTITY + VALID_QUANTITY,
                expectedMessage);
    }
}
