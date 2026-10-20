package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.menu.FoodItem;

public class JsonAdaptedFoodItemTest {
    private static final FoodItem CHICKEN_RICE = new FoodItem("CR01", "Chicken Rice", 450);

    @Test
    public void constructor_nullSource_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new JsonAdaptedFoodItem(null));
    }

    @Test
    public void constructor_fromFoodItem_roundTripsValues() throws Exception {
        assertEquals(CHICKEN_RICE, new JsonAdaptedFoodItem(CHICKEN_RICE).toModelType());
    }

    @Test
    public void toModelType_validFields_returnsFoodItem() throws Exception {
        assertEquals(CHICKEN_RICE, new JsonAdaptedFoodItem("CR01", "Chicken Rice", 450).toModelType());
        assertEquals(new FoodItem("cr01", "鸡饭", 1), new JsonAdaptedFoodItem("cr01", "鸡饭", 1).toModelType());
        assertEquals(new FoodItem("CR01", "Chicken Rice", Integer.MAX_VALUE),
                new JsonAdaptedFoodItem("CR01", "Chicken Rice", Integer.MAX_VALUE).toModelType());
    }

    @Test
    public void toModelType_invalidCode_throwsIllegalValueException() {
        for (String code : Arrays.asList(null, "", "CR-01", "CR 01", " CR01", "鸡")) {
            assertInvalidFoodItem(code, "Chicken Rice", 450, FoodItem.MESSAGE_CODE_CONSTRAINTS);
        }
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        for (String name : Arrays.asList(null, "", "   ", "\t\n", "\u2003")) {
            assertInvalidFoodItem("CR01", name, 450, FoodItem.MESSAGE_NAME_CONSTRAINTS);
        }
    }

    @Test
    public void toModelType_invalidPrice_throwsIllegalValueException() {
        for (Integer price : Arrays.asList(null, 0, -1, Integer.MIN_VALUE)) {
            assertInvalidFoodItem("CR01", "Chicken Rice", price, FoodItem.MESSAGE_PRICE_CONSTRAINTS);
        }
    }

    @Test
    public void toModelType_multipleInvalidFields_reportsCodeConstraintFirst() {
        assertInvalidFoodItem(null, null, null, FoodItem.MESSAGE_CODE_CONSTRAINTS);
    }

    @Test
    public void fromJsonString_missingFields_throwsIllegalValueException() throws Exception {
        List<String> inputs = List.of("{\"name\":\"Chicken Rice\",\"priceInCents\":450}",
                "{\"code\":\"CR01\",\"priceInCents\":450}", "{\"code\":\"CR01\",\"name\":\"Chicken Rice\"}");
        List<String> messages = List.of(FoodItem.MESSAGE_CODE_CONSTRAINTS,
                FoodItem.MESSAGE_NAME_CONSTRAINTS, FoodItem.MESSAGE_PRICE_CONSTRAINTS);
        for (int i = 0; i < inputs.size(); i++) {
            JsonAdaptedFoodItem adapted = JsonUtil.fromJsonString(inputs.get(i), JsonAdaptedFoodItem.class);
            assertThrows(IllegalValueException.class, messages.get(i), adapted::toModelType);
        }
    }

    @Test
    public void jsonRoundTrip_preservesFieldsAndIntegerCents() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedFoodItem(CHICKEN_RICE));
        JsonNode fields = JsonUtil.fromJsonString(json, JsonNode.class);
        assertEquals(3, fields.size());
        assertEquals("CR01", fields.get("code").asText());
        assertEquals("Chicken Rice", fields.get("name").asText());
        assertTrue(fields.get("priceInCents").isIntegralNumber());
        assertEquals(450, fields.get("priceInCents").asInt());
        assertEquals(CHICKEN_RICE, JsonUtil.fromJsonString(json, JsonAdaptedFoodItem.class).toModelType());
    }

    private static void assertInvalidFoodItem(String code, String name, Integer price, String expectedMessage) {
        JsonAdaptedFoodItem adapted = new JsonAdaptedFoodItem(code, name, price);
        assertThrows(IllegalValueException.class, expectedMessage, adapted::toModelType);
    }

    @Test
    public void fromJsonString_wrongFieldTypes_rejectsWithoutCoercion() {
        for (String price : List.of("450.9", "450.0", "1e2", "\"450\"", "true", "[]", "{}",
                "2147483648", "-2147483649")) {
            String json = "{\"code\":\"CR01\",\"name\":\"Chicken Rice\",\"priceInCents\":" + price + "}";
            assertThrows(JsonProcessingException.class, () -> JsonUtil.fromJsonString(json, JsonAdaptedFoodItem.class));
        }
        for (String value : List.of("123", "true", "[]", "{}")) {
            String invalidCode = "{\"code\":" + value + ",\"name\":\"Chicken Rice\",\"priceInCents\":450}";
            String invalidName = "{\"code\":\"CR01\",\"name\":" + value + ",\"priceInCents\":450}";
            assertThrows(JsonProcessingException.class, () ->
                    JsonUtil.fromJsonString(invalidCode, JsonAdaptedFoodItem.class));
            assertThrows(JsonProcessingException.class, () ->
                    JsonUtil.fromJsonString(invalidName, JsonAdaptedFoodItem.class));
        }
    }
}
