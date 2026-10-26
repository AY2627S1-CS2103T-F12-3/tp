package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.ModelManager;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;
import seedu.address.model.menu.ReadOnlyMenu;

public class JsonSerializableMenuTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableMenuTest");
    private static final FoodItem CHICKEN_RICE = new FoodItem("CR01", "Chicken Rice", 450);
    private static final FoodItem COFFEE = new FoodItem("CF01", "Coffee", 150);

    @Test
    public void constructor_nullSource_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new JsonSerializableMenu((ReadOnlyMenu) null));
    }

    @Test
    public void toModelType_emptyMenu_returnsEmptyMenu() throws Exception {
        assertEquals(new Menu(), readMenu("emptyMenu.json").toModelType());
        assertEquals(new Menu(), new JsonSerializableMenu(new Menu()).toModelType());
    }

    @Test
    public void toModelType_validItems_preservesValuesAndOrder() throws Exception {
        JsonSerializableMenu saved = readMenu("validMenu.json");

        assertEquals(List.of(COFFEE, CHICKEN_RICE), saved.toModelType().getFoodItemList());
    }

    @Test
    public void constructor_sourceChanged_preservesSnapshot() throws Exception {
        Menu source = new Menu();
        source.addFoodItem(CHICKEN_RICE);
        JsonSerializableMenu saved = new JsonSerializableMenu(source);

        source.addFoodItem(COFFEE);

        assertEquals(List.of(CHICKEN_RICE), saved.toModelType().getFoodItemList());
    }

    @Test
    public void constructor_inputListChanged_preservesSnapshot() throws Exception {
        List<JsonAdaptedFoodItem> items = new ArrayList<>();
        items.add(new JsonAdaptedFoodItem(CHICKEN_RICE));
        JsonSerializableMenu saved = new JsonSerializableMenu(items);

        items.clear();

        assertEquals(List.of(CHICKEN_RICE), saved.toModelType().getFoodItemList());
    }

    @Test
    public void toModelType_duplicateCodes_throwsIllegalValueException() {
        for (FoodItem duplicate : List.of(CHICKEN_RICE, new FoodItem("CR01", "Different Dish", 600))) {
            JsonSerializableMenu saved = new JsonSerializableMenu(List.of(
                    new JsonAdaptedFoodItem(CHICKEN_RICE), new JsonAdaptedFoodItem(duplicate)));
            assertThrows(IllegalValueException.class,
                    JsonSerializableMenu.MESSAGE_DUPLICATE_FOOD_ITEM, saved::toModelType);
        }
    }

    @Test
    public void toModelType_codesDifferingByCase_acceptsBoth() throws Exception {
        FoodItem lowercaseCode = new FoodItem("cr01", "Chicken Rice", 450);
        JsonSerializableMenu saved = new JsonSerializableMenu(List.of(
                new JsonAdaptedFoodItem(CHICKEN_RICE), new JsonAdaptedFoodItem(lowercaseCode)));

        assertEquals(List.of(CHICKEN_RICE, lowercaseCode), saved.toModelType().getFoodItemList());
    }

    @Test
    public void toModelType_missingOrNullItemList_throwsIllegalValueException() throws Exception {
        for (String fileName : List.of("missingFoodItems.json", "nullFoodItems.json")) {
            JsonSerializableMenu saved = readMenu(fileName);
            assertThrows(IllegalValueException.class,
                    JsonSerializableMenu.MESSAGE_MISSING_FOOD_ITEMS, saved::toModelType);
        }
    }

    @Test
    public void toModelType_nullItem_throwsIllegalValueException() throws Exception {
        JsonSerializableMenu saved = readMenu("nullFoodItem.json");
        assertThrows(IllegalValueException.class, JsonSerializableMenu.MESSAGE_NULL_FOOD_ITEM, saved::toModelType);
    }

    @Test
    public void toModelType_invalidItem_throwsIllegalValueException() {
        List<JsonAdaptedFoodItem> invalidItems = List.of(new JsonAdaptedFoodItem("!", "Dish", 100),
                new JsonAdaptedFoodItem("CR01", " ", 100), new JsonAdaptedFoodItem("CR01", "Dish", 0));
        List<String> messages = List.of(FoodItem.MESSAGE_CODE_CONSTRAINTS,
                FoodItem.MESSAGE_NAME_CONSTRAINTS, FoodItem.MESSAGE_PRICE_CONSTRAINTS);
        for (int i = 0; i < invalidItems.size(); i++) {
            JsonSerializableMenu saved = new JsonSerializableMenu(List.of(invalidItems.get(i)));
            assertThrows(IllegalValueException.class, messages.get(i), saved::toModelType);
        }
    }

    @Test
    public void toModelType_repeatedConversion_returnsIndependentMenus() throws Exception {
        JsonSerializableMenu saved = new JsonSerializableMenu(List.of(new JsonAdaptedFoodItem(CHICKEN_RICE)));
        Menu first = saved.toModelType();
        first.addFoodItem(COFFEE);

        assertEquals(List.of(CHICKEN_RICE), saved.toModelType().getFoodItemList());
    }

    @Test
    public void jsonRoundTrip_preservesMenuAndExpectedStructure() throws Exception {
        Menu source = new Menu();
        source.setFoodItems(List.of(CHICKEN_RICE, COFFEE));
        String json = JsonUtil.toJsonString(new JsonSerializableMenu(source));
        JsonNode fields = JsonUtil.fromJsonString(json, JsonNode.class);
        assertEquals(1, fields.size());
        assertEquals(2, fields.get("foodItems").size());
        assertEquals(source, JsonUtil.fromJsonString(json, JsonSerializableMenu.class).toModelType());
    }

    @Test
    public void fromJsonString_invalidMenuStructure_rejectsInput() {
        for (String json : List.of("[]", "42", "{\"foodItems\":{}}", "{\"foodItems\":[42]}",
                "{\"foodItems\":[true]}", "{\"foodItems\":[\"dish\"]}", "{\"foodItems\":[[]]}")) {
            assertThrows(JsonProcessingException.class, () ->
                    JsonUtil.fromJsonString(json, JsonSerializableMenu.class));
        }
    }

    @Test
    public void menuCommand_jsonRoundTrip_preservesModelAndRejectsDuplicate() throws Exception {
        ModelManager model = new ModelManager();
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("add-menu-item c/CR01 n/Chicken Rice p/4.50").execute(model);
        parser.parseCommand("add-menu-item c/cr01 n/Coffee p/0.01").execute(model);
        Menu expected = new Menu(model.getMenu());

        Command duplicate = parser.parseCommand("add-menu-item c/CR01 n/Different Dish p/9.99");
        assertThrows(CommandException.class, () -> duplicate.execute(model));

        assertEquals(expected, new Menu(model.getMenu()));
        String json = JsonUtil.toJsonString(new JsonSerializableMenu(model.getMenu()));
        Menu loaded = JsonUtil.fromJsonString(json, JsonSerializableMenu.class).toModelType();
        assertEquals(expected, loaded);
        assertEquals(List.of(CHICKEN_RICE, new FoodItem("cr01", "Coffee", 1)), loaded.getFoodItemList());
    }

    private JsonSerializableMenu readMenu(String fileName) throws Exception {
        return JsonUtil.readJsonFile(TEST_DATA_FOLDER.resolve(fileName), JsonSerializableMenu.class).orElseThrow();
    }
}
