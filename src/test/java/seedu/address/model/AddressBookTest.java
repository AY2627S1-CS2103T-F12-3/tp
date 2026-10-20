package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.exceptions.DuplicateMilestoneException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {
    private static final Milestone FREE_DRINK = new Milestone("FREE500", "Free drink", 500);
    private static final Milestone EDITED_FREE_DRINK = new Milestone("FREE500", "Free drink and snack", 600);
    private static final Milestone FREE_DESSERT = new Milestone("DESSERT500", "Free dessert", 500);

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
        assertEquals(List.of(), addressBook.getMilestoneList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        newData.addMilestone(FREE_DRINK);
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons, List.of(FREE_DESSERT));
        addressBook.addPerson(ALICE);
        addressBook.addMilestone(FREE_DRINK);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
        assertEquals(List.of(ALICE), addressBook.getPersonList());
        assertEquals(List.of(FREE_DRINK), addressBook.getMilestoneList());
    }

    @Test
    public void resetData_withDuplicateMilestones_throwsAndPreservesData() {
        AddressBookStub newData = new AddressBookStub(List.of(), List.of(FREE_DRINK, EDITED_FREE_DRINK));
        addressBook.addPerson(ALICE);
        addressBook.addMilestone(FREE_DESSERT);

        assertThrows(DuplicateMilestoneException.class, () -> addressBook.resetData(newData));
        assertEquals(List.of(ALICE), addressBook.getPersonList());
        assertEquals(List.of(FREE_DESSERT), addressBook.getMilestoneList());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void hasMilestone_nullMilestone_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasMilestone(null));
    }

    @Test
    public void hasMilestone_milestoneNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasMilestone(FREE_DRINK));
    }

    @Test
    public void hasMilestone_milestoneInAddressBook_returnsTrue() {
        addressBook.addMilestone(FREE_DRINK);
        assertTrue(addressBook.hasMilestone(FREE_DRINK));
    }

    @Test
    public void hasMilestone_milestoneWithSameCodeInAddressBook_returnsTrue() {
        addressBook.addMilestone(FREE_DRINK);
        assertTrue(addressBook.hasMilestone(EDITED_FREE_DRINK));
    }

    @Test
    public void milestoneOperations_addSetRemove_updatesSortedList() {
        addressBook.addMilestone(FREE_DRINK);
        addressBook.addMilestone(FREE_DESSERT);
        assertEquals(List.of(FREE_DESSERT, FREE_DRINK), addressBook.getMilestoneList());

        addressBook.setMilestone(FREE_DRINK, EDITED_FREE_DRINK);
        assertEquals(List.of(FREE_DESSERT, EDITED_FREE_DRINK), addressBook.getMilestoneList());

        addressBook.removeMilestone(FREE_DESSERT);
        assertEquals(List.of(EDITED_FREE_DRINK), addressBook.getMilestoneList());
    }

    @Test
    public void getMilestoneList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getMilestoneList().add(FREE_DRINK));
    }

    @Test
    public void copyConstructor_copiesPersonsAndMilestonesWithoutAliasing() {
        addressBook.addPerson(ALICE);
        addressBook.addMilestone(FREE_DRINK);
        AddressBook copy = new AddressBook(addressBook);

        addressBook.removePerson(ALICE);
        addressBook.removeMilestone(FREE_DRINK);
        assertEquals(List.of(ALICE), copy.getPersonList());
        assertEquals(List.of(FREE_DRINK), copy.getMilestoneList());
    }

    @Test
    public void equalsAndHashCode_comparePersonsAndMilestones() {
        addressBook.addPerson(ALICE);
        addressBook.addMilestone(FREE_DRINK);
        AddressBook copy = new AddressBook(addressBook);
        assertEquals(addressBook, copy);
        assertEquals(addressBook.hashCode(), copy.hashCode());

        copy.setMilestone(FREE_DRINK, EDITED_FREE_DRINK);
        assertFalse(addressBook.equals(copy));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName()
                + "{persons=" + addressBook.getPersonList()
                + ", milestones=" + addressBook.getMilestoneList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose lists can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();
        private final ObservableList<Milestone> milestones = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons, Collection<Milestone> milestones) {
            this.persons.setAll(persons);
            this.milestones.setAll(milestones);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public ObservableList<Milestone> getMilestoneList() {
            return milestones;
        }
    }

}
