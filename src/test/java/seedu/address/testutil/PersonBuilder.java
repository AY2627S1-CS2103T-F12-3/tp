package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.order.OrderHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Score;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";

    private Name name;
    private Phone phone;
    private Set<Tag> tags;
    private Score score;
    private OrderHistory orderHistory;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        tags = new HashSet<>();
        score = Score.DEFAULT;
        orderHistory = new OrderHistory();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        tags = new HashSet<>(personToCopy.getTags());
        orderHistory = personToCopy.getOrderHistory();
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Score} of the {@code Person} that we are building.
     */
    public PersonBuilder withScore(Score score) {
        this.score = score;
        return this;
    }


    /**
     * Sets the {@code OrderHistory} of the {@code Person} that we are building
     */
    public PersonBuilder withOrderHistory(OrderHistory orderHistory) {
        this.orderHistory = orderHistory;
        return this;
    }

    public Person build() {
        return new Person(name, phone, tags, score, orderHistory);
    }

}
