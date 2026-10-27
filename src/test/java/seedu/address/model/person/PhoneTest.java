package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // Invalid lengths, prefixes, characters and separators.
        for (String invalidPhone : List.of("", " ", "91", "911", "9123456", "912345678",
                "124293842033123", "01234567", "12345678", "22345678", "32345678", "42345678",
                "52345678", "72345678", "phone", "9011p041", "9312 1534", "9123-4567",
                "+6591234567", "6591234567", " 91234567", "91234567 ", "9１２３４５６７")) {
            assertFalse(Phone.isValidPhone(invalidPhone), invalidPhone);
            assertThrows(IllegalArgumentException.class, Phone.MESSAGE_CONSTRAINTS, () -> new Phone(invalidPhone));
        }

        // Boundary values for all permitted prefixes.
        for (String validPhone : List.of("60000000", "69999999", "80000000", "89999999",
                "90000000", "99999999")) {
            assertTrue(Phone.isValidPhone(validPhone), validPhone);
            assertEquals(validPhone, new Phone(validPhone).value);
        }
    }

    @Test
    public void equals() {
        Phone phone = new Phone("99999999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("99999999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("99500000")));
    }
}
