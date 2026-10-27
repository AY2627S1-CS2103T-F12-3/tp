package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS = "Phone numbers must not be blank";
    private static final String SINGAPORE_PHONE_REGEX = "[689][0-9]{7}";
    private static final String SEPARATOR_REGEX = "[\\s\\p{Zs}-]+";
    private static final String FORMATTED_PHONE_REGEX = "[0-9](?:[0-9\\s\\p{Zs}-]*[0-9])?";
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
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = normalize(phone);
    }

    /**
     * Returns true if the input can be stored. Unusual formats are accepted with a command warning.
     */
    public static boolean isValidPhone(String test) {
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
