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
    public void isValidPhone_blankInput_rejected() {
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));
        for (String blank : List.of("", " ", "\t\r\n", "\u00a0", "\u3000", " \u2003 ")) {
            assertFalse(Phone.isValidPhone(blank), blank);
            assertThrows(IllegalArgumentException.class, Phone.MESSAGE_CONSTRAINTS, () -> new Phone(blank));
        }
    }

    @Test
    public void constructor_recognizedSingaporeFormats_normalizesWithoutWarning() {
        for (String input : List.of("91234567", " 91234567 ", "9123 4567", "9123   4567", "9123-4567",
                "9123 - 4567", "+6591234567", "+65 9123 4567", "+65-9123-4567", "(+65) 9123 4567",
                "(65) 9123 4567", "65 9123 4567", "6591234567", "65-9123-4567", "９１２３４５６７",
                "＋６５　９１２３　４５６７", "（＋６５）９１２３－４５６７", "9123\u00a04567",
                "\u300091234567\u3000")) {
            Phone phone = new Phone(input);
            assertEquals("91234567", phone.value, input);
            assertTrue(phone.isStandardSingaporeNumber(), input);
            assertEquals(phone, new Phone(phone.value), input);
            assertEquals(phone.hashCode(), new Phone("91234567").hashCode(), input);
        }
        for (String boundary : List.of("60000000", "65000000", "69999999", "80000000",
                "89999999", "90000000", "99999999")) {
            assertTrue(new Phone(boundary).isStandardSingaporeNumber(), boundary);
        }
    }

    @Test
    public void constructor_unrecognizedEntries_preservesTextAndNeedsWarning() {
        for (String input : List.of("91", "911", "9123456", "912345678", "12345678", "72345678",
                "+86 138119828111", "（86）12345678", "(86)12345678", "86 138119828111", "006591234567",
                "+65 12345678", "+65 6591234567", "(+65) 6591234567", "65 12345678",
                "9011p041", "91234567 (HP)", "91234567 ext 123", "91234567 / 81234567",
                "1234 5678 (HP) 1111-3333 (Office)", "(91234567", "91234567)", "++6591234567",
                "-91234567", "91234567-")) {
            assertTrue(Phone.isValidPhone(input), input);
            Phone phone = new Phone(" " + input + " ");
            assertEquals(input, phone.value, input);
            assertFalse(phone.isStandardSingaporeNumber(), input);
            assertEquals(phone, new Phone(phone.value), input);
        }
    }

    @Test
    public void isValidPhone_digitlessText_rejectedForNewInputButRetainedForLegacyData() {
        for (String text : List.of("Not available", "---", "phone", "未知", "＋（）", "①", "²")) {
            assertFalse(Phone.isValidPhone(text), text);
            assertTrue(Phone.isStorablePhone(text), text);
            assertEquals(text, new Phone(text).value, text);
        }
        for (String text : List.of("0", "９", "١", "1234 (HP)", "+86 138119828111")) {
            assertTrue(Phone.isValidPhone(text), text);
        }
    }

    @Test
    public void equals_unrecognizedEntries_comparesExactText() {
        assertFalse(new Phone("（86）12345678").equals(new Phone("+8612345678")));
        assertFalse(new Phone("+86 138119828111").equals(new Phone("+86138119828111")));
        assertFalse(new Phone("91234567 (HP)").equals(new Phone("91234567")));
        assertFalse(new Phone("Office").equals(new Phone("office")));
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
