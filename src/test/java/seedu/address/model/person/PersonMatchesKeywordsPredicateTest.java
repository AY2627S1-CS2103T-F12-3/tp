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

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        PersonMatchesKeywordsPredicate firstPredicateCopy =
                new PersonMatchesKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new PersonMatchesKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new PersonMatchesKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new PersonMatchesKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new PersonMatchesKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Keywords match phone and address, but do not match name
        predicate = new PersonMatchesKeywordsPredicate(List.of("12345", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345")
                .withAddress("Main Street").build()));
    }

    @Test
    public void test_eightDigitKeywordExactlyMatchesPhone_returnsTrue() {
        Person person = new PersonBuilder().withPhone("12345678").build();
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of("12345678"));

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_eightDigitKeywordPartiallyMatchesPhone_returnsFalse() {
        Person person = new PersonBuilder().withPhone("912345678").build();
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of("12345678"));

        assertFalse(predicate.test(person));
    }

    @Test
    public void test_nonEightDigitKeywordMatchesPhone_returnsFalse() {
        Person person = new PersonBuilder().withPhone("12345678").build();
        PersonMatchesKeywordsPredicate sevenDigitPredicate =
                new PersonMatchesKeywordsPredicate(List.of("1234567"));
        PersonMatchesKeywordsPredicate nineDigitPredicate =
                new PersonMatchesKeywordsPredicate(List.of("123456789"));

        assertFalse(sevenDigitPredicate.test(person));
        assertFalse(nineDigitPredicate.test(person));
    }

    @Test
    public void test_nameOrPhoneMatchesKeyword_returnsTrue() {
        Person person = new PersonBuilder().withName("Alice Bob").withPhone("12345678").build();
        PersonMatchesKeywordsPredicate predicate =
                new PersonMatchesKeywordsPredicate(List.of("Carol", "12345678"));

        assertTrue(predicate.test(person));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(keywords);

        String expected = PersonMatchesKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
