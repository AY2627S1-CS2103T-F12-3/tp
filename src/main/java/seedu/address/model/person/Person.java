package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.order.OrderHistory;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;

    // Data fields
    private final Set<Tag> tags = new HashSet<>();
    private final Score score;
    private final OrderHistory orderHistory;

    /**
     * Constructs a person with the given details and a default score and empty order history
     */
    public Person(Name name, Phone phone, Set<Tag> tags) {
        requireAllNonNull(name, phone, tags);
        this(name, phone, tags, Score.DEFAULT, new OrderHistory());
    }

    /**
     * Constructs a person with the given details
     */
    public Person(Name name, Phone phone, Set<Tag> tags, Score score, OrderHistory orderHistory) {
        requireAllNonNull(name, phone, tags, score, orderHistory);
        this.name = name;
        this.phone = phone;
        this.tags.addAll(tags);
        this.score = score;
        this.orderHistory = orderHistory;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Score getScore() {
        return score;
    }

    /**
     *  Returns an immutable order history of Person.
     */
    public OrderHistory getOrderHistory() {
        return orderHistory;
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && tags.equals(otherPerson.tags)
                && score.equals(otherPerson.score)
                && orderHistory.equals(otherPerson.orderHistory);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, tags, score, orderHistory);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("tags", tags)
                .add("score", score)
                .add("order history", orderHistory)
                .toString();
    }
}
