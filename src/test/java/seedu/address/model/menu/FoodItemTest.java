package seedu.address.model.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class FoodItemTest {
    private static final FoodItem CHICKEN_RICE = new FoodItem("CR01", "Chicken Rice", 450);

    @Test
    public void constructor_validFields_preservesValues() {
        assertEquals("CR01", CHICKEN_RICE.getCode());
        assertEquals("Chicken Rice", CHICKEN_RICE.getName());
        assertEquals(450, CHICKEN_RICE.getPriceInCents());
        assertEquals(1, new FoodItem("A", "Tea", 1).getPriceInCents());
        assertEquals(Integer.MAX_VALUE, new FoodItem("B", "Coffee", Integer.MAX_VALUE).getPriceInCents());
    }

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FoodItem(null, "Chicken Rice", 450));
        assertThrows(NullPointerException.class, () -> new FoodItem("CR01", null, 450));
    }

    @Test
    public void constructor_invalidCode_throwsIllegalArgumentException() {
        for (String code : new String[] {"", " ", "CR 01", "CR-01", " CR01", "CR01 "}) {
            assertThrows(IllegalArgumentException.class,
                    FoodItem.MESSAGE_CODE_CONSTRAINTS, () -> new FoodItem(code, "Chicken Rice", 450));
        }
    }

    @Test
    public void constructor_blankName_throwsIllegalArgumentException() {
        for (String name : new String[] {"", " ", "\t\n", "\u2003"}) {
            assertThrows(IllegalArgumentException.class,
                    FoodItem.MESSAGE_NAME_CONSTRAINTS, () -> new FoodItem("CR01", name, 450));
        }
    }

    @Test
    public void constructor_nonPositivePrice_throwsIllegalArgumentException() {
        for (int price : new int[] {0, -1, Integer.MIN_VALUE}) {
            assertThrows(IllegalArgumentException.class,
                    FoodItem.MESSAGE_PRICE_CONSTRAINTS, () -> new FoodItem("CR01", "Chicken Rice", price));
        }
    }

    @Test
    public void isValidCode() {
        assertFalse(FoodItem.isValidCode(null));
        assertFalse(FoodItem.isValidCode(""));
        assertFalse(FoodItem.isValidCode("CR-01"));
        assertTrue(FoodItem.isValidCode("CR01"));
        assertTrue(FoodItem.isValidCode("cr01"));
        assertTrue(FoodItem.isValidCode("123"));
    }

    @Test
    public void isValidName() {
        assertFalse(FoodItem.isValidName(null));
        assertFalse(FoodItem.isValidName(""));
        assertFalse(FoodItem.isValidName("\t\n"));
        assertTrue(FoodItem.isValidName("Chicken Rice"));
        assertTrue(FoodItem.isValidName("Chef's Special"));
        assertTrue(FoodItem.isValidName("鸡饭"));
    }

    @Test
    public void isValidPrice() {
        assertFalse(FoodItem.isValidPrice(0));
        assertFalse(FoodItem.isValidPrice(-1));
        assertTrue(FoodItem.isValidPrice(1));
        assertTrue(FoodItem.isValidPrice(Integer.MAX_VALUE));
    }

    @Test
    public void isSameFoodItem_comparesCodeOnly() {
        assertTrue(CHICKEN_RICE.isSameFoodItem(CHICKEN_RICE));
        assertTrue(CHICKEN_RICE.isSameFoodItem(new FoodItem("CR01", "Roasted Chicken Rice", 500)));
        assertFalse(CHICKEN_RICE.isSameFoodItem(null));
        assertFalse(CHICKEN_RICE.isSameFoodItem(new FoodItem("CR02", "Chicken Rice", 450)));
        assertFalse(CHICKEN_RICE.isSameFoodItem(new FoodItem("cr01", "Chicken Rice", 450)));
    }

    @Test
    public void equals_comparesAllFields() {
        assertTrue(CHICKEN_RICE.equals(CHICKEN_RICE));
        assertTrue(CHICKEN_RICE.equals(new FoodItem("CR01", "Chicken Rice", 450)));
        assertFalse(CHICKEN_RICE.equals(null));
        assertFalse(CHICKEN_RICE.equals("CR01"));
        assertFalse(CHICKEN_RICE.equals(new FoodItem("CR02", "Chicken Rice", 450)));
        assertFalse(CHICKEN_RICE.equals(new FoodItem("CR01", "Roasted Chicken Rice", 450)));
        assertFalse(CHICKEN_RICE.equals(new FoodItem("CR01", "Chicken Rice", 500)));
    }

    @Test
    public void hashCode_equalItems_sameHashCode() {
        assertEquals(CHICKEN_RICE.hashCode(), new FoodItem("CR01", "Chicken Rice", 450).hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = FoodItem.class.getCanonicalName()
                + "{code=CR01, name=Chicken Rice, priceInCents=450}";
        assertEquals(expected, CHICKEN_RICE.toString());
    }

    @Test
    public void constructor_unusualInvalidCodes_throwsIllegalArgumentException() {
        String[] codes = {"CR01\n", "CR01\r\n", "CR\t01", "CR\u000001", "ＣＲ０１", "CR\u200B01", "鸡饭"};
        for (String code : codes) {
            assertThrows(IllegalArgumentException.class,
                    FoodItem.MESSAGE_CODE_CONSTRAINTS, () -> new FoodItem(code, "Chicken Rice", 450));
        }
    }

}
