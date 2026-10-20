package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.UniqueMilestoneList;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicate persons and case-insensitive milestone codes are not allowed.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final UniqueMilestoneList milestones = new UniqueMilestoneList();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Replaces the contents of the milestone list with {@code milestones}.
     * {@code milestones} must not contain duplicate case-insensitive milestone codes.
     */
    public void setMilestones(List<Milestone> milestones) {
        this.milestones.setMilestones(milestones);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     * Invalid input leaves the existing data unchanged.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        UniquePersonList replacementPersons = new UniquePersonList();
        replacementPersons.setPersons(newData.getPersonList());
        UniqueMilestoneList replacementMilestones = new UniqueMilestoneList();
        replacementMilestones.setMilestones(newData.getMilestoneList());

        persons.setPersons(replacementPersons);
        milestones.setMilestones(replacementMilestones);
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    //// milestone-level operations

    /**
     * Returns true if a milestone with the same case-insensitive code as {@code milestone} exists.
     */
    public boolean hasMilestone(Milestone milestone) {
        requireNonNull(milestone);
        return milestones.contains(milestone);
    }

    /**
     * Adds a milestone to the address book.
     * Its code must not already exist in the address book.
     */
    public void addMilestone(Milestone milestone) {
        milestones.add(milestone);
    }

    /**
     * Replaces {@code target} with {@code editedMilestone}.
     * The target must exist, and both milestones must have the same case-insensitive code.
     */
    public void setMilestone(Milestone target, Milestone editedMilestone) {
        milestones.setMilestone(target, editedMilestone);
    }

    /**
     * Removes {@code milestone}, which must exist in the address book.
     */
    public void removeMilestone(Milestone milestone) {
        milestones.remove(milestone);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("milestones", milestones)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Milestone> getMilestoneList() {
        return milestones.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons)
                && milestones.equals(otherAddressBook.milestones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, milestones);
    }
}
