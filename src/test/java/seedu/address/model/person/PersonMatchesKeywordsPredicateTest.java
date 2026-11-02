package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonMatchesKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        PersonMatchesKeywordsPredicate firstPredicate =
                new PersonMatchesKeywordsPredicate(firstPredicateKeywordList);
        PersonMatchesKeywordsPredicate secondPredicate =
                new PersonMatchesKeywordsPredicate(secondPredicateKeywordList);

        // EP: same predicate instance
        assertTrue(firstPredicate.equals(firstPredicate));

        PersonMatchesKeywordsPredicate firstPredicateCopy =
                new PersonMatchesKeywordsPredicate(firstPredicateKeywordList);
        // EP: different instance with the same keywords
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // EP: object of another type
        assertFalse(firstPredicate.equals(1));

        // EP: null object
        assertFalse(firstPredicate.equals(null));

        // EP: predicate with different keywords
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // EP: one matching name keyword
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // EP: multiple matching name keywords
        predicate = new PersonMatchesKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // EP: one of several name keywords matches
        predicate = new PersonMatchesKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // EP: name keywords differ only in case
        predicate = new PersonMatchesKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // EP: empty keyword list
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of());
        Person person = new PersonBuilder().withName("Alice").build();
        assertFalse(predicate.test(person)); // Boundary value: zero keywords

        // EP: name keyword absent from the person's name
        predicate = new PersonMatchesKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // EP: partial phone keyword does not match the complete phone entry or name
        predicate = new PersonMatchesKeywordsPredicate(List.of("12345"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("91234567").build()));
    }

    @Test
    public void test_eightDigitKeywordExactlyMatchesPhone_returnsTrue() {
        Person person = new PersonBuilder().withPhone("91234567").build();
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of("91234567"));

        // EP: eight-digit keyword equals the complete phone entry
        assertTrue(predicate.test(person));
    }

    @Test
    public void test_eightDigitKeywordDoesNotMatchPhone_returnsFalse() {
        Person person = new PersonBuilder().withPhone("81234567").build();
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of("91234567"));

        // EP: eight-digit keyword differs from the complete phone entry
        assertFalse(predicate.test(person));
    }

    @Test
    public void test_nonEightDigitKeywordMatchesPhone_returnsFalse() {
        Person person = new PersonBuilder().withPhone("91234567").build();
        PersonMatchesKeywordsPredicate sevenDigitPredicate =
                new PersonMatchesKeywordsPredicate(List.of("1234567"));
        PersonMatchesKeywordsPredicate nineDigitPredicate =
                new PersonMatchesKeywordsPredicate(List.of("123456789"));

        // EP: shorter keyword is a substring of the phone entry
        assertFalse(sevenDigitPredicate.test(person)); // Boundary value: seven digits

        // EP: longer keyword is not the complete phone entry
        assertFalse(nineDigitPredicate.test(person)); // Boundary value: nine digits
    }

    @Test
    public void test_exactNonEightDigitPhoneKeyword_returnsTrue() {
        Person sevenDigitPerson = new PersonBuilder().withPhone("1234567").build();
        Person nineDigitPerson = new PersonBuilder().withPhone("123456789").build();
        PersonMatchesKeywordsPredicate sevenDigitPredicate =
                new PersonMatchesKeywordsPredicate(List.of("1234567"));
        PersonMatchesKeywordsPredicate nineDigitPredicate =
                new PersonMatchesKeywordsPredicate(List.of("123456789"));

        // EP: complete nonstandard phone entry matches exactly
        assertTrue(sevenDigitPredicate.test(sevenDigitPerson)); // Boundary value: seven digits
        assertTrue(nineDigitPredicate.test(nineDigitPerson)); // Boundary value: nine digits
    }

    @Test
    public void test_nameOrPhoneMatchesKeyword_returnsTrue() {
        Person person = new PersonBuilder().withName("Alice Bob").withPhone("91234567").build();
        PersonMatchesKeywordsPredicate predicate =
                new PersonMatchesKeywordsPredicate(List.of("Carol", "91234567"));

        // EP: name keywords miss but a complete phone keyword matches
        assertTrue(predicate.test(person));
    }

    @Test
    public void test_phoneOnlyQuery_matchesCompleteNormalizedEntry() {
        Person person = new PersonBuilder().withName("Alice").withPhone("91234567").build();
        // EP: explicit phone query normalizes to the stored Singapore number
        assertTrue(new PersonMatchesKeywordsPredicate(new Phone("+65 9123 4567")).test(person));

        // EP: legacy text query differs from the stored number
        assertFalse(new PersonMatchesKeywordsPredicate(new Phone("Alice")).test(person));

        Person overseas = new PersonBuilder().withPhone("（86）12345678").build();
        // EP: foreign phone format matches exactly
        assertTrue(new PersonMatchesKeywordsPredicate(new Phone("（86）12345678")).test(overseas));

        // EP: differently formatted foreign phone entry does not normalize to the stored entry
        assertFalse(new PersonMatchesKeywordsPredicate(new Phone("+8612345678")).test(overseas));

        // EP: short complete phone entry matches as a keyword
        assertTrue(new PersonMatchesKeywordsPredicate(List.of("911")).test(
                new PersonBuilder().withPhone("911").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(keywords);

        String expected = PersonMatchesKeywordsPredicate.class.getCanonicalName()
                + "{keywords=" + keywords + ", phoneQuery=Optional.empty}";
        // EP: keyword-based predicate with a nonempty list and no explicit phone query
        assertEquals(expected, predicate.toString());
    }
}
