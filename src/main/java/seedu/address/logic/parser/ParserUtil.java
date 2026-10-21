package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.order.Order;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_INVALID_REQUIRED_SCORE =
            "Required score must be a positive whole number no greater than 2147483647.";
    private static final String REQUIRED_SCORE_VALIDATION_REGEX = "[0-9]+";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses {@code requiredScore} into a positive integer.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the specified score is not a positive integer within the supported range.
     */
    public static int parseRequiredScore(String requiredScore) throws ParseException {
        requireNonNull(requiredScore);
        String trimmedRequiredScore = requiredScore.trim();
        if (!trimmedRequiredScore.matches(REQUIRED_SCORE_VALIDATION_REGEX)) {
            throw new ParseException(MESSAGE_INVALID_REQUIRED_SCORE);
        }

        try {
            int parsedRequiredScore = Integer.parseInt(trimmedRequiredScore);
            if (parsedRequiredScore <= 0) {
                throw new ParseException(MESSAGE_INVALID_REQUIRED_SCORE);
            }
            return parsedRequiredScore;
        } catch (NumberFormatException e) {
            throw new ParseException(MESSAGE_INVALID_REQUIRED_SCORE, e);
        }
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.hasValidFormat(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        if (!Name.containsLetter(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS_MISSING_LETTER);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses and validates an item code.
     *
     * @throws ParseException if the item code is invalid.
     */
    public static String parseOrderCode(String itemCode) throws ParseException {
        requireNonNull(itemCode);
        String trimmedItemCode = itemCode.trim();
        if (!FoodItem.isValidCode(trimmedItemCode)) {
            throw new ParseException(Order.ITEM_CODE_CONSTRAINTS);
        }
        return trimmedItemCode;
    }

    /**
     * Parses and validates an order quantity.
     *
     * @throws ParseException if the quantity is invalid.
     */
    public static int parseOrderQuantity(String quantity) throws ParseException {
        requireNonNull(quantity);
        int numericQuantity;
        try {
            numericQuantity = Integer.parseInt(quantity.trim());
        } catch (NumberFormatException e) {
            throw new ParseException(Order.QUANTITY_CONSTRAINTS, e);
        }

        if (!Order.isValidQuantity(numericQuantity)) {
            throw new ParseException(Order.QUANTITY_CONSTRAINTS);
        }

        return numericQuantity;
    }

}
