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

        // A partial phone number does not match a name.
        predicate = new PersonMatchesKeywordsPredicate(List.of("12345"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("91234567").build()));
    }

    @Test
    public void test_eightDigitKeywordExactlyMatchesPhone_returnsTrue() {
        Person person = new PersonBuilder().withPhone("91234567").build();
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of("91234567"));

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_eightDigitKeywordDoesNotMatchPhone_returnsFalse() {
        Person person = new PersonBuilder().withPhone("81234567").build();
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of("91234567"));

        assertFalse(predicate.test(person));
    }

    @Test
    public void test_nonEightDigitKeywordMatchesPhone_returnsFalse() {
        Person person = new PersonBuilder().withPhone("91234567").build();
        PersonMatchesKeywordsPredicate sevenDigitPredicate =
                new PersonMatchesKeywordsPredicate(List.of("1234567"));
        PersonMatchesKeywordsPredicate nineDigitPredicate =
                new PersonMatchesKeywordsPredicate(List.of("123456789"));

        assertFalse(sevenDigitPredicate.test(person));
        assertFalse(nineDigitPredicate.test(person));
    }

    @Test
    public void test_nameOrPhoneMatchesKeyword_returnsTrue() {
        Person person = new PersonBuilder().withName("Alice Bob").withPhone("91234567").build();
        PersonMatchesKeywordsPredicate predicate =
                new PersonMatchesKeywordsPredicate(List.of("Carol", "91234567"));

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_phoneOnlyQuery_matchesCompleteNormalizedEntry() {
        Person person = new PersonBuilder().withName("Alice").withPhone("91234567").build();
        assertTrue(new PersonMatchesKeywordsPredicate(new Phone("+65 9123 4567")).test(person));
        assertFalse(new PersonMatchesKeywordsPredicate(new Phone("Alice")).test(person));
        Person overseas = new PersonBuilder().withPhone("（86）12345678").build();
        assertTrue(new PersonMatchesKeywordsPredicate(new Phone("（86）12345678")).test(overseas));
        assertFalse(new PersonMatchesKeywordsPredicate(new Phone("+8612345678")).test(overseas));
        assertTrue(new PersonMatchesKeywordsPredicate(List.of("911")).test(
                new PersonBuilder().withPhone("911").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(keywords);

        String expected = PersonMatchesKeywordsPredicate.class.getCanonicalName()
                + "{keywords=" + keywords + ", phoneQuery=Optional.empty}";
        assertEquals(expected, predicate.toString());
    }
}
