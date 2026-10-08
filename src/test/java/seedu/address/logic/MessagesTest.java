package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;

public class MessagesTest {

    @Test
    public void format_personWithEmail_emailNotDisplayed() {
        String expected = "Alice Pauline; Phone: 94351253; "
                + "Address: 123, Jurong West Ave 6, #08-111; Tags: [friends]";

        assertEquals(expected, Messages.format(ALICE));
    }
}
