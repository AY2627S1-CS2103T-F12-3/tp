package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;

public class MessagesTest {

    @Test
    public void format_person_displaysRemainingFields() {
        String expected = "Alice Pauline; Phone: 94351253; Tags: [friends]";

        assertEquals(expected, Messages.format(ALICE));
    }
}
