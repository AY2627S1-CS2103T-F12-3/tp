package seedu.address.model.util;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.order.Order;
import seedu.address.model.order.OrderHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Score;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            sampleMember("Alex Yeoh", "87438807", getTagSet("dairyFree"), 118,
                    sampleOrder("chicken rice", 1, "2026-10-10T04:10:00Z"),
                    sampleOrder("laksa", 1, "2026-10-12T04:15:00Z")),
            sampleMember("Bernice Yu", "99272758", getTagSet(), 169,
                    sampleOrder("nasi lemak", 1, "2026-10-09T10:20:00Z"),
                    sampleOrder("kopi", 1, "2026-10-11T01:45:00Z"),
                    sampleOrder("nasi lemak", 2, "2026-10-12T10:30:00Z")),
            sampleMember("Charlotte Oliveiro", "93210283", getTagSet("noBeef"), 106,
                    sampleOrder("wanton mee", 1, "2026-10-11T05:30:00Z"),
                    sampleOrder("chicken rice", 1, "2026-10-14T00:22:00Z")),
            sampleMember("David Li", "91031282", getTagSet("vegetarian"), 94,
                    sampleOrder("vegetarian bee hoon", 1, "2026-10-08T04:50:00Z"),
                    sampleOrder("teh", 1, "2026-10-10T02:15:00Z"),
                    sampleOrder("vegetarian bee hoon", 1, "2026-10-13T05:20:00Z")),
            sampleMember("Irfan Ibrahim", "92492021", getTagSet("noPork"), 137,
                    sampleOrder("chicken murtabak", 1, "2026-10-09T09:40:00Z"),
                    sampleOrder("roti prata", 2, "2026-10-13T01:10:00Z")),
            sampleMember("Roy Balakrishnan", "92624417", getTagSet("dairyFree"), 153,
                    sampleOrder("mee goreng", 1, "2026-10-07T11:15:00Z"),
                    sampleOrder("lime juice", 1, "2026-10-11T05:05:00Z"),
                    sampleOrder("mee goreng", 1, "2026-10-13T11:45:00Z"))
        };
    }

    private static Person sampleMember(String name, String phone, Set<Tag> tags, int score, Order... orders) {
        OrderHistory orderHistory = new OrderHistory();
        for (Order order : orders) {
            orderHistory = orderHistory.addOrder(order);
        }
        return new Person(new Name(name), new Phone(phone), tags, new Score(score), orderHistory);
    }

    private static Order sampleOrder(String itemName, int quantity, String recordedAt) {
        return new Order(itemName, quantity, Instant.parse(recordedAt));
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
