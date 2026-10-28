package seedu.address.model.person;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a {@code Person} matches any of the given keywords.
 * Matches name keywords or complete phone values, with an optional explicit phone-only search.
 */
public class PersonMatchesKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;
    private final Optional<Phone> phoneQuery;

    /**
     * Constructs a predicate that matches a person against any of the given keywords.
     *
     * @param keywords Keywords to match against a person's name or phone number.
     */
    public PersonMatchesKeywordsPredicate(List<String> keywords) {
        this.keywords = List.copyOf(keywords);
        this.phoneQuery = Optional.empty();
    }

    /**
     * Creates a search for an exact phone entry, including spaces and annotations.
     */
    public PersonMatchesKeywordsPredicate(Phone phoneQuery) {
        this.keywords = List.of();
        this.phoneQuery = Optional.of(phoneQuery);
    }

    /**
     * Returns true if the person matches the explicit phone query or at least one name/phone keyword.
     */
    @Override
    public boolean test(Person person) {
        if (phoneQuery.isPresent()) {
            return person.getPhone().equals(phoneQuery.get());
        }
        return keywords.stream().anyMatch(keyword -> matchesKeyword(person, keyword));
    }

    private static boolean matchesKeyword(Person person, String keyword) {
        return person.getPhone().value.equals(Phone.normalize(keyword))
                || StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword);
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

        return keywords.equals(otherPersonMatchesKeywordsPredicate.keywords)
                && phoneQuery.equals(otherPersonMatchesKeywordsPredicate.phoneQuery);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).add("phoneQuery", phoneQuery).toString();
    }
}
