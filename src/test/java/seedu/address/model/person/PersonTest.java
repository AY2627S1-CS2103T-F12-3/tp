package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import seedu.address.model.milestone.Milestone;
import seedu.address.model.order.Order;
import seedu.address.model.order.OrderHistory;
import seedu.address.model.reward.RedeemedMilestoneRewardHistory;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void getTags_modifyReturnedSet_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson_sameAndDifferentDetails_returnsExpectedResult() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals_sameAndDifferentPersons_returnsExpectedResult() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void equals_differentOrderHistory_returnsFalse() {
        OrderHistory differentOrderHistory = ALICE.getOrderHistory()
                .addOrder(new Order("Pancake", 1, Instant.parse("2026-02-01T00:00:00Z")));
        Person editedAlice = new PersonBuilder(ALICE).withOrderHistory(differentOrderHistory).build();

        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void equals_emptyOrderHistories_returnsTrue() {
        Person aliceWithEmptyHistory = new PersonBuilder(ALICE).withOrderHistory(new OrderHistory()).build();
        Person aliceCopyWithEmptyHistory = new PersonBuilder(ALICE).withOrderHistory(new OrderHistory()).build();

        assertEquals(aliceWithEmptyHistory, aliceCopyWithEmptyHistory);
    }

    @Test
    public void equals_sameOrderHistory_returnsTrue() {
        Instant recordedAt = Instant.parse("2026-02-01T00:00:00Z");
        OrderHistory firstHistory = new OrderHistory().addOrder(new Order("Pancake", 1, recordedAt));
        OrderHistory secondHistory = new OrderHistory().addOrder(new Order("Pancake", 1, recordedAt));
        Person aliceWithHistory = new PersonBuilder(ALICE).withOrderHistory(firstHistory).build();
        Person aliceCopyWithSameHistory = new PersonBuilder(ALICE).withOrderHistory(secondHistory).build();

        assertEquals(aliceWithHistory, aliceCopyWithSameHistory);
    }
    @Test
    public void constructor_nullRedeemedMilestoneRewardHistory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Person(
                ALICE.getName(), ALICE.getPhone(), ALICE.getTags(), ALICE.getScore(),
                ALICE.getOrderHistory(), null));
    }

    @Test
    public void getRedeemedMilestoneRewardHistory_existingHistory_returnsHistory() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(new Milestone("WELCOME", "Free drink", 10));
        Person person = new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getTags(), ALICE.getScore(),
                ALICE.getOrderHistory(), history);

        assertEquals(history, person.getRedeemedMilestoneRewardHistory());
    }

    @Test
    public void hasRedeemed_existingMilestone_returnsTrue() {
        Milestone milestone = new Milestone("WELCOME", "Free drink", 10);
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(milestone);
        Person person = new PersonBuilder(ALICE).withRedeemedMilestoneRewardHistory(history).build();

        assertTrue(person.hasRedeemed(milestone));
    }

    @Test
    public void hasRedeemed_equalMilestone_returnsTrue() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(new Milestone("WELCOME", "Free drink", 10));
        Person person = new PersonBuilder(ALICE).withRedeemedMilestoneRewardHistory(history).build();

        assertTrue(person.hasRedeemed(new Milestone("WELCOME", "Free drink", 10)));
    }

    @Test
    public void hasRedeemed_absentMilestone_returnsFalse() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(new Milestone("WELCOME", "Free drink", 10));
        Person person = new PersonBuilder(ALICE).withRedeemedMilestoneRewardHistory(history).build();

        assertFalse(person.hasRedeemed(new Milestone("BIRTHDAY", "Birthday reward", 20)));
    }

    @Test
    public void hasRedeemed_nullMilestone_returnsFalse() {
        Person person = new PersonBuilder(ALICE).build();

        assertFalse(person.hasRedeemed(null));
    }

    @Test
    public void equals_differentRedeemedMilestoneRewardHistory_returnsFalse() {
        RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                .addRedeemedMilestoneReward(new Milestone("WELCOME", "Free drink", 10));
        Person personWithReward = new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getTags(), ALICE.getScore(),
                ALICE.getOrderHistory(), history);

        assertFalse(ALICE.equals(personWithReward));
    }

    @Test
    public void hashCode_sameFields_sameHashCode() {
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertEquals(ALICE.hashCode(), aliceCopy.hashCode());
    }

    @Test
    public void toString_allFields_returnsExpectedString() {
        String expected = Person.class.getCanonicalName()
                + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", tags=" + ALICE.getTags() + ", score=" + ALICE.getScore()
                + ", order history=" + ALICE.getOrderHistory()
                + ", redeemed milestone rewards=" + ALICE.getRedeemedMilestoneRewardHistory() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
