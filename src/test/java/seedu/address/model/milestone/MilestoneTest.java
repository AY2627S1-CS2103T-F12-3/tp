package seedu.address.model.milestone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MilestoneTest {
    private static final Milestone FREE_DRINK = new Milestone("FREE500", "Free drink", 500);

    @Test
    public void constructor_validFields_preservesValues() {
        assertEquals("FREE500", FREE_DRINK.getCode());
        assertEquals("Free drink", FREE_DRINK.getRewardDescription());
        assertEquals(500, FREE_DRINK.getRequiredScore());
        assertEquals(1, new Milestone("A", "Small reward", 1).getRequiredScore());
        assertEquals(Integer.MAX_VALUE,
                new Milestone("B", "Large reward", Integer.MAX_VALUE).getRequiredScore());
    }

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Milestone(null, "Free drink", 500));
        assertThrows(NullPointerException.class, () -> new Milestone("FREE500", null, 500));
    }

    @Test
    public void constructor_invalidCode_throwsIllegalArgumentException() {
        String expectedMessage = Milestone.MESSAGE_CODE_CONSTRAINTS;
        String[] invalidCodes = {
            "", " ", "FREE 500", "FREE-500", " FREE500", "FREE500 ", "FREE500\n",
            "FREE\t500", "ＦＲＥＥ５００", "FREE\u200B500", "免费饮料"
        };
        for (String code : invalidCodes) {
            assertThrows(IllegalArgumentException.class, expectedMessage, () ->
                    new Milestone(code, "Free drink", 500));
        }
    }

    @Test
    public void constructor_blankRewardDescription_throwsIllegalArgumentException() {
        String expectedMessage = Milestone.MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS;
        for (String rewardDescription : new String[] {"", " ", "\t\n", "\u2003"}) {
            assertThrows(IllegalArgumentException.class, expectedMessage, () ->
                    new Milestone("FREE500", rewardDescription, 500));
        }
    }

    @Test
    public void constructor_nonPositiveRequiredScore_throwsIllegalArgumentException() {
        String expectedMessage = Milestone.MESSAGE_REQUIRED_SCORE_CONSTRAINTS;
        for (int requiredScore : new int[] {0, -1, Integer.MIN_VALUE}) {
            assertThrows(IllegalArgumentException.class, expectedMessage, () ->
                    new Milestone("FREE500", "Free drink", requiredScore));
        }
    }

    @Test
    public void isValidCode() {
        assertFalse(Milestone.isValidCode(null));
        assertFalse(Milestone.isValidCode(""));
        assertFalse(Milestone.isValidCode("FREE-500"));
        assertTrue(Milestone.isValidCode("FREE500"));
        assertTrue(Milestone.isValidCode("free500"));
        assertTrue(Milestone.isValidCode("500"));
    }

    @Test
    public void isValidRewardDescription() {
        assertFalse(Milestone.isValidRewardDescription(null));
        assertFalse(Milestone.isValidRewardDescription(""));
        assertFalse(Milestone.isValidRewardDescription("\t\n"));
        assertTrue(Milestone.isValidRewardDescription("Free drink"));
        assertTrue(Milestone.isValidRewardDescription("Chef's special"));
        assertTrue(Milestone.isValidRewardDescription("免费饮料"));
    }

    @Test
    public void isValidRequiredScore() {
        assertFalse(Milestone.isValidRequiredScore(0));
        assertFalse(Milestone.isValidRequiredScore(-1));
        assertTrue(Milestone.isValidRequiredScore(1));
        assertTrue(Milestone.isValidRequiredScore(Integer.MAX_VALUE));
    }

    @Test
    public void isSameMilestone_comparesCodeOnly() {
        assertTrue(FREE_DRINK.isSameMilestone(FREE_DRINK));
        assertTrue(FREE_DRINK.isSameMilestone(new Milestone("FREE500", "Free dessert", 600)));
        assertFalse(FREE_DRINK.isSameMilestone(null));
        assertFalse(FREE_DRINK.isSameMilestone(new Milestone("FREE600", "Free drink", 500)));
        assertFalse(FREE_DRINK.isSameMilestone(new Milestone("free500", "Free drink", 500)));
    }

    @Test
    public void equals_comparesAllFields() {
        assertTrue(FREE_DRINK.equals(FREE_DRINK));
        assertTrue(FREE_DRINK.equals(new Milestone("FREE500", "Free drink", 500)));
        assertFalse(FREE_DRINK.equals(null));
        assertFalse(FREE_DRINK.equals("FREE500"));
        assertFalse(FREE_DRINK.equals(new Milestone("FREE600", "Free drink", 500)));
        assertFalse(FREE_DRINK.equals(new Milestone("FREE500", "Free dessert", 500)));
        assertFalse(FREE_DRINK.equals(new Milestone("FREE500", "Free drink", 600)));
    }

    @Test
    public void hashCode_equalMilestones_sameHashCode() {
        assertEquals(FREE_DRINK.hashCode(), new Milestone("FREE500", "Free drink", 500).hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Milestone.class.getCanonicalName()
                + "{code=FREE500, rewardDescription=Free drink, requiredScore=500}";
        assertEquals(expected, FREE_DRINK.toString());
    }
}
