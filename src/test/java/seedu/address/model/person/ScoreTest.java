package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ScoreTest {

    @Test
    public void constructor_negativeScore_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Score(-1));
    }

    @Test
    public void isValidScore() {
        // invalid scores
        assertFalse(Score.isValidScore("")); // empty string
        assertFalse(Score.isValidScore("-1")); // negative
        assertFalse(Score.isValidScore("abc")); // non-numeric
        assertFalse(Score.isValidScore("1234567890")); // more than 9 digits

        // valid scores
        assertTrue(Score.isValidScore("0"));
        assertTrue(Score.isValidScore("123456789")); // exactly 9 digits
    }

    @Test
    public void equals() {
        Score score = new Score(5);

        // same values -> returns true
        assertTrue(score.equals(new Score(5)));

        // same object -> returns true
        assertTrue(score.equals(score));

        // null -> returns false
        assertFalse(score.equals(null));

        // different types -> returns false
        assertFalse(score.equals(5.0f));

        // different values -> returns false
        assertFalse(score.equals(new Score(6)));
    }

    @Test
    public void hashCodeMethod() {
        assertEquals(new Score(5).hashCode(), new Score(5).hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("5", new Score(5).toString());
    }
}
