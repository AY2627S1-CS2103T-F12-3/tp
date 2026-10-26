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
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.MilestoneCatalog;
import seedu.address.model.milestone.ReadOnlyMilestoneCatalog;

public class JsonSerializableMilestoneCatalogTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data",
            "JsonSerializableMilestoneCatalogTest");
    private static final Milestone FREE_DRINK = new Milestone("FREE500", "Free drink", 500);
    private static final Milestone FREE_DESSERT = new Milestone("DESSERT500", "Free dessert", 500);

    @Test
    public void constructor_nullSource_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new JsonSerializableMilestoneCatalog((ReadOnlyMilestoneCatalog) null));
    }

    @Test
    public void toModelType_emptyCatalog_returnsEmptyCatalog() throws Exception {
        assertEquals(new MilestoneCatalog(), readCatalog("emptyMilestoneCatalog.json").toModelType());
        assertEquals(new MilestoneCatalog(),
                new JsonSerializableMilestoneCatalog(new MilestoneCatalog()).toModelType());
    }

    @Test
    public void toModelType_validMilestones_preservesValuesAndSorts() throws Exception {
        JsonSerializableMilestoneCatalog saved = readCatalog("validMilestoneCatalog.json");

        assertEquals(List.of(FREE_DESSERT, FREE_DRINK), saved.toModelType().getMilestoneList());
    }

    @Test
    public void constructor_sourceChanged_preservesSnapshot() throws Exception {
        MilestoneCatalog source = new MilestoneCatalog();
        source.addMilestone(FREE_DRINK);
        JsonSerializableMilestoneCatalog saved = new JsonSerializableMilestoneCatalog(source);

        source.addMilestone(FREE_DESSERT);

        assertEquals(List.of(FREE_DRINK), saved.toModelType().getMilestoneList());
    }

    @Test
    public void constructor_inputListChanged_preservesSnapshot() throws Exception {
        List<JsonAdaptedMilestone> milestones = new ArrayList<>();
        milestones.add(new JsonAdaptedMilestone(FREE_DRINK));
        JsonSerializableMilestoneCatalog saved = new JsonSerializableMilestoneCatalog(milestones);

        milestones.clear();

        assertEquals(List.of(FREE_DRINK), saved.toModelType().getMilestoneList());
    }

    @Test
    public void toModelType_duplicateCodes_throwsIllegalValueException() {
        for (String duplicateCode : List.of("FREE500", "free500")) {
            JsonSerializableMilestoneCatalog saved = new JsonSerializableMilestoneCatalog(List.of(
                    new JsonAdaptedMilestone(FREE_DRINK),
                    new JsonAdaptedMilestone(duplicateCode, "Different reward", 700)));
            assertThrows(IllegalValueException.class,
                    JsonSerializableMilestoneCatalog.MESSAGE_DUPLICATE_MILESTONE, saved::toModelType);
        }
    }

    @Test
    public void toModelType_missingOrNullMilestoneList_throwsIllegalValueException() throws Exception {
        for (String fileName : List.of("missingMilestones.json", "nullMilestones.json")) {
            JsonSerializableMilestoneCatalog saved = readCatalog(fileName);
            assertThrows(IllegalValueException.class,
                    JsonSerializableMilestoneCatalog.MESSAGE_MISSING_MILESTONES, saved::toModelType);
        }
    }

    @Test
    public void toModelType_nullMilestone_throwsIllegalValueException() throws Exception {
        JsonSerializableMilestoneCatalog saved = readCatalog("nullMilestone.json");
        assertThrows(IllegalValueException.class,
                JsonSerializableMilestoneCatalog.MESSAGE_NULL_MILESTONE, saved::toModelType);
    }

    @Test
    public void toModelType_invalidMilestone_throwsIllegalValueException() {
        List<JsonAdaptedMilestone> invalidMilestones = List.of(
                new JsonAdaptedMilestone("!", "Reward", 100),
                new JsonAdaptedMilestone("FREE500", " ", 100),
                new JsonAdaptedMilestone("FREE500", "Reward", 0));
        List<String> messages = List.of(Milestone.MESSAGE_CODE_CONSTRAINTS,
                Milestone.MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS,
                Milestone.MESSAGE_REQUIRED_SCORE_CONSTRAINTS);
        for (int i = 0; i < invalidMilestones.size(); i++) {
            JsonSerializableMilestoneCatalog saved =
                    new JsonSerializableMilestoneCatalog(List.of(invalidMilestones.get(i)));
            assertThrows(IllegalValueException.class, messages.get(i), saved::toModelType);
        }
    }

    @Test
    public void toModelType_repeatedConversion_returnsIndependentCatalogs() throws Exception {
        JsonSerializableMilestoneCatalog saved = new JsonSerializableMilestoneCatalog(
                List.of(new JsonAdaptedMilestone(FREE_DRINK)));
        MilestoneCatalog first = saved.toModelType();
        first.addMilestone(FREE_DESSERT);

        assertEquals(List.of(FREE_DRINK), saved.toModelType().getMilestoneList());
    }

    @Test
    public void jsonRoundTrip_preservesCatalogAndExpectedStructure() throws Exception {
        MilestoneCatalog source = new MilestoneCatalog();
        source.setMilestones(List.of(FREE_DRINK, FREE_DESSERT));
        String json = JsonUtil.toJsonString(new JsonSerializableMilestoneCatalog(source));
        JsonNode fields = JsonUtil.fromJsonString(json, JsonNode.class);

        assertEquals(1, fields.size());
        assertEquals(2, fields.get("milestones").size());
        assertEquals(source,
                JsonUtil.fromJsonString(json, JsonSerializableMilestoneCatalog.class).toModelType());
    }

    @Test
    public void fromJsonString_invalidCatalogStructure_rejectsInput() {
        for (String json : List.of("[]", "42", "{\"milestones\":{}}", "{\"milestones\":[42]}",
                "{\"milestones\":[true]}", "{\"milestones\":[\"reward\"]}", "{\"milestones\":[[]]}")) {
            assertThrows(JsonProcessingException.class, () ->
                    JsonUtil.fromJsonString(json, JsonSerializableMilestoneCatalog.class));
        }
    }

    private JsonSerializableMilestoneCatalog readCatalog(String fileName) throws Exception {
        return JsonUtil.readJsonFile(TEST_DATA_FOLDER.resolve(fileName),
                JsonSerializableMilestoneCatalog.class).orElseThrow();
    }
}
