package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a {@code Person} matches any of the given keywords.
 * An eight-digit keyword is matched exactly against the phone number; other keywords are matched against the name.
 */
public class PersonMatchesKeywordsPredicate implements Predicate<Person> {
    private static final String EIGHT_DIGIT_PHONE_REGEX = "\\d{8}";

    private final List<String> keywords;

    /**
     * Constructs a predicate that matches a person against any of the given keywords.
     *
     * @param keywords Keywords to match against a person's name or phone number.
     */
    public PersonMatchesKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Returns true if the person matches at least one keyword by name or exact eight-digit phone number.
     */
    @Override
    public boolean test(Person person) {
        return keywords.stream().anyMatch(keyword -> matchesKeyword(person, keyword));
    }

    private static boolean matchesKeyword(Person person, String keyword) {
        if (keyword.matches(EIGHT_DIGIT_PHONE_REGEX)) {
            return person.getPhone().value.equals(keyword);
        }
        return StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonMatchesKeywordsPredicate otherPersonMatchesKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherPersonMatchesKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
