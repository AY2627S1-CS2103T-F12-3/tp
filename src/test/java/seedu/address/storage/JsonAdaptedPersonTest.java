package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.reward.RedeemedMilestoneRewardHistory;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String NAME_WITHOUT_LETTER = "12345678";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());
    private static final String VALID_SCORE = "1";
    private static final List<JsonAdaptedOrder> VALID_ORDER_HISTORY = BENSON.getOrderHistory().getOrders().stream()
            .map(JsonAdaptedOrder::new)
            .collect(Collectors.toList());
    private static final List<JsonAdaptedRedeemedMilestoneReward> VALID_REDEEMED_MILESTONE_REWARDS =
            BENSON.getRedeemedMilestoneRewardHistory().getRedeemedMilestoneRewards().stream()
                    .map(JsonAdaptedRedeemedMilestoneReward::new)
                    .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_redeemedMilestoneRewards_returnsPersonWithRewards() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_TAGS, VALID_SCORE,
                VALID_ORDER_HISTORY,
                List.of(new JsonAdaptedRedeemedMilestoneReward(
                        new Milestone("WELCOME", "Free drink", 10))));

        assertEquals(List.of(new Milestone("WELCOME", "Free drink", 10)),
                person.toModelType().getRedeemedMilestoneRewardHistory().getRedeemedMilestoneRewards());
    }

    @Test
    public void toModelType_nullRedeemedMilestoneReward_throwsIllegalValueException() {
        List<JsonAdaptedRedeemedMilestoneReward> rewards = new ArrayList<>();
        rewards.add(null);
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_TAGS, VALID_SCORE, VALID_ORDER_HISTORY, rewards);

        assertThrows(IllegalValueException.class,
                JsonAdaptedRedeemedMilestoneReward.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_duplicateRedeemedMilestone_throwsIllegalValueException() {
        JsonAdaptedMilestone milestone = new JsonAdaptedMilestone(new Milestone("WELCOME", "Free drink", 10));
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_TAGS, VALID_SCORE, VALID_ORDER_HISTORY,
                List.of(new JsonAdaptedRedeemedMilestoneReward(milestone),
                        new JsonAdaptedRedeemedMilestoneReward(milestone)));

        assertThrows(IllegalValueException.class,
                RedeemedMilestoneRewardHistory.MESSAGE_DUPLICATE_MILESTONE, person::toModelType);
    }

    @Test
    public void toJsonString_personWithRedeemedMilestoneRewards_includesRewards() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(BENSON));

        assertEquals(BENSON.getRedeemedMilestoneRewardHistory().getRedeemedMilestoneRewards().size(),
                VALID_REDEEMED_MILESTONE_REWARDS.size());
        assertTrue(json.contains("redeemedMilestoneRewardHistory"));
    }

    @Test
    public void toJsonString_validPerson_omitsAddressField() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(BENSON));
        assertFalse(json.contains("\"address\""));
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(
                        INVALID_NAME, VALID_PHONE,
                        VALID_TAGS, VALID_SCORE,
                        VALID_ORDER_HISTORY, VALID_REDEEMED_MILESTONE_REWARDS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nameWithoutLetter_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(
                        NAME_WITHOUT_LETTER, VALID_PHONE,
                        VALID_TAGS, VALID_SCORE,
                        VALID_ORDER_HISTORY, VALID_REDEEMED_MILESTONE_REWARDS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS_MISSING_LETTER;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                null, VALID_PHONE,
                VALID_TAGS, VALID_SCORE,
                VALID_ORDER_HISTORY, VALID_REDEEMED_MILESTONE_REWARDS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(
                        VALID_NAME, INVALID_PHONE,
                        VALID_TAGS, VALID_SCORE,
                        VALID_ORDER_HISTORY, VALID_REDEEMED_MILESTONE_REWARDS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, null,
                VALID_TAGS, VALID_SCORE,
                VALID_ORDER_HISTORY, VALID_REDEEMED_MILESTONE_REWARDS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(
                        VALID_NAME, VALID_PHONE,
                        invalidTags, VALID_SCORE,
                        VALID_ORDER_HISTORY, VALID_REDEEMED_MILESTONE_REWARDS);
        assertThrows(IllegalValueException.class, person::toModelType);
    }
}
