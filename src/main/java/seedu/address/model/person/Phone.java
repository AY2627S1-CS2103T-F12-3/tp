package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a member's phone entry, including legacy contact notes.
 * Guarantees: immutable; is storable as declared in {@link #isStorablePhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS = "Phone numbers must not be blank";
    public static final String MESSAGE_MISSING_DIGIT = "Phone numbers must contain at least one digit";
    private static final String SINGAPORE_PHONE_REGEX = "[689][0-9]{7}";
    private static final String SEPARATOR_REGEX = "[\\s\\p{Zs}-]+";
    private static final String FORMATTED_PHONE_REGEX = "[0-9](?:[0-9\\s\\p{Zs}-]*[0-9])?";
    private static final int MIN_SIMILAR_NUMBER_DIGITS = 7;
    private static final Pattern NUMBER_SEGMENT_PATTERN =
            Pattern.compile("\\p{Nd}(?:[\\p{Nd}\\s\\p{Zs}()（）.．\\-－–—]*\\p{Nd})?");
    private static final int FULL_WIDTH_ASCII_OFFSET = 0xFEE0;
    public final String value;

    /**
     * Constructs a phone number, normalizing recognized Singapore formats.
     * Other nonblank values retain their original spelling, apart from surrounding whitespace.
     *
     * @param phone A nonblank phone number or contact note.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isStorablePhone(phone), MESSAGE_CONSTRAINTS);
        value = normalize(phone);
    }

    /**
     * Returns true if the input can be supplied as a new phone entry in add or edit.
     * At least one Unicode decimal digit is required; unusual formats still receive a warning.
     */
    public static boolean isValidPhone(String test) {
        return isStorablePhone(test) && test.codePoints().anyMatch(Character::isDigit);
    }

    /**
     * Returns true if an entry can be retained in the model, storage or a lookup query.
     * Legacy nonblank notes without digits remain readable so users can find and correct them.
     */
    public static boolean isStorablePhone(String test) {
        requireNonNull(test);
        return !trimWhitespace(test).isEmpty();
    }

    /**
     * Returns true if this value has the expected eight-digit Singapore format.
     * This check does not establish that the number is assigned or reachable.
     */
    public boolean isStandardSingaporeNumber() {
        return value.matches(SINGAPORE_PHONE_REGEX);
    }

    /**
     * Returns a canonical Singapore number for recognized formats, or the trimmed original input.
     * Only full-width ASCII characters and explicitly supported separators are interpreted.
     * Foreign numbers, annotations and multiple numbers are never combined or guessed.
     */
    public static String normalize(String phone) {
        requireNonNull(phone);
        String original = trimWhitespace(phone);
        String candidate = original.chars()
                .map(character -> character >= '！' && character <= '～'
                        ? character - FULL_WIDTH_ASCII_OFFSET : character)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        String numberBody = candidate.replaceFirst("^(?:\\(\\+?65\\)|\\+65)[\\s\\p{Zs}-]*", "");
        boolean hasExplicitCountryPrefix = !numberBody.equals(candidate);
        if (!numberBody.matches(FORMATTED_PHONE_REGEX)) {
            return original;
        }
        String compact = numberBody.replaceAll(SEPARATOR_REGEX, "");
        if (!hasExplicitCountryPrefix && compact.matches("65" + SINGAPORE_PHONE_REGEX)) {
            return compact.substring(2);
        }
        return compact.matches(SINGAPORE_PHONE_REGEX) ? compact : original;
    }

    /**
     * Returns true if the entries may contain a common phone number of at least seven digits.
     * Formatting separators and a recognized Singapore country prefix are ignored for this advisory check.
     * Text, slashes and plus signs separate number segments; country codes are not otherwise inferred.
     * This heuristic must not establish member identity or block a command.
     */
    public boolean hasPossibleNumberInCommon(Phone other) {
        requireNonNull(other);
        Set<String> numbers = extractComparisonNumbers();
        return other.extractComparisonNumbers().stream().anyMatch(numbers::contains);
    }

    private Set<String> extractComparisonNumbers() {
        return NUMBER_SEGMENT_PATTERN.matcher(value).results()
                .map(MatchResult::group)
                .map(Phone::toComparisonNumber)
                .filter(number -> number.length() >= MIN_SIMILAR_NUMBER_DIGITS)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static String toComparisonNumber(String segment) {
        String digits = segment.codePoints()
                .filter(Character::isDigit)
                .map(character -> '0' + Character.digit(character, 10))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return digits.matches("65" + SINGAPORE_PHONE_REGEX) ? digits.substring(2) : digits;
    }

    private static String trimWhitespace(String text) {
        int start = 0;
        int end = text.length();
        while (start < end && isWhitespace(text.charAt(start))) {
            start++;
        }
        while (end > start && isWhitespace(text.charAt(end - 1))) {
            end--;
        }
        return text.substring(start, end);
    }

    private static boolean isWhitespace(char character) {
        return Character.isWhitespace(character) || Character.isSpaceChar(character);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
