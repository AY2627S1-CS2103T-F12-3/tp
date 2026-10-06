package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; has a valid format and contains at least one letter.
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphanumeric characters and spaces, and should not be blank";
    public static final String MESSAGE_CONSTRAINTS_MISSING_LETTER =
            "Names should contain at least one letter (A-Z or a-z)";

    /*
     * The first character of the name must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";
    private static final String LETTER_REGEX = ".*[A-Za-z].*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(hasValidFormat(name), MESSAGE_CONSTRAINTS);
        checkArgument(containsLetter(name), MESSAGE_CONSTRAINTS_MISSING_LETTER);
        fullName = name;
    }

    /**
     * Returns true if a given string contains only permitted characters and is not blank.
     */
    public static boolean hasValidFormat(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string contains at least one ASCII letter.
     */
    public static boolean containsLetter(String test) {
        return test.matches(LETTER_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
