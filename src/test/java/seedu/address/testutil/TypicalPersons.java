package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.order.Order;
import seedu.address.model.order.OrderHistory;
import seedu.address.model.person.Person;
import seedu.address.model.person.Score;

/**
 * A utility class containing a list of {@code Person} objects to be used in tests.
 */
public class TypicalPersons {

    public static final Instant ALICE_CHICKEN_ORDER_TIME = Instant.parse("2026-01-01T00:00:00Z");
    public static final Instant ALICE_POTATO_ORDER_TIME = Instant.parse("2026-01-02T00:00:00Z");
    public static final Instant BENSON_POTATO_ORDER_TIME = Instant.parse("2026-01-03T00:00:00Z");
    public static final Instant ELLE_CHICKEN_ORDER_TIME = Instant.parse("2026-01-04T00:00:00Z");
    public static final Instant ELLE_PORK_ORDER_TIME = Instant.parse("2026-01-05T00:00:00Z");
    public static final Instant FIONA_CHICKEN_ORDER_TIME = Instant.parse("2026-01-06T00:00:00Z");

    public static final Person ALICE = new PersonBuilder()
            .withName("Alice Pauline")
            .withPhone("94351253")
            .withTags("friends")
            .withScore(Score.DEFAULT)
            .withOrderHistory(
                    new OrderHistory()
                            .addOrder(new Order("Chicken", 2, ALICE_CHICKEN_ORDER_TIME))
                            .addOrder(new Order("Potato", 1, ALICE_POTATO_ORDER_TIME))
            )
            .build();
    public static final Person BENSON = new PersonBuilder()
            .withName("Benson Meier")
            .withPhone("98765432")
            .withTags("owesMoney", "friends")
            .withScore(Score.DEFAULT)
            .withOrderHistory(
                    new OrderHistory()
                            .addOrder(new Order("Potato", 1, BENSON_POTATO_ORDER_TIME))
            )
            .build();
    public static final Person CARL = new PersonBuilder()
            .withName("Carl Kurz")
            .withPhone("95352563")
            .withScore(Score.DEFAULT)
            .withOrderHistory(new OrderHistory())
            .build();
    public static final Person DANIEL = new PersonBuilder()
            .withName("Daniel Meier")
            .withPhone("87652533")
            .withScore(Score.DEFAULT)
            .withOrderHistory(new OrderHistory())
            .build();
    public static final Person ELLE = new PersonBuilder()
            .withName("Elle Meyer")
            .withPhone("9482224")
            .withScore(Score.DEFAULT)
            .withOrderHistory(
                    new OrderHistory()
                            .addOrder(new Order("Chicken", 2, ELLE_CHICKEN_ORDER_TIME))
                            .addOrder(new Order("Pork", 9, ELLE_PORK_ORDER_TIME))
            )
            .build();
    public static final Person FIONA = new PersonBuilder()
            .withName("Fiona Kunz")
            .withPhone("9482427")
            .withScore(Score.DEFAULT)
            .withOrderHistory(
                    new OrderHistory()
                            .addOrder(new Order("Chicken", 9, FIONA_CHICKEN_ORDER_TIME))
            )
            .build();
    public static final Person GEORGE = new PersonBuilder()
            .withName("George Best")
            .withPhone("9482442")
            .build();

    // Manually added
    public static final Person HOON = new PersonBuilder()
            .withName("Hoon Meier")
            .withPhone("8482424")
            .withScore(Score.DEFAULT)
            .withOrderHistory(
                    new OrderHistory()
                            .addOrder(new Order("pancake", 1))
            )
            .build();
    public static final Person IDA = new PersonBuilder()
            .withName("Ida Mueller")
            .withPhone("8482131")
            .withScore(Score.DEFAULT)
            .withOrderHistory(
                    new OrderHistory()
                            .addOrder(new Order("Soup", 1))
            )
            .build();

    // Manually added - Person's details found in {@code CommandTestUtil}
    public static final Person AMY = new PersonBuilder()
            .withName(VALID_NAME_AMY)
            .withPhone(VALID_PHONE_AMY)
            .withTags(VALID_TAG_FRIEND)
            .withScore(Score.DEFAULT)
            .withOrderHistory(new OrderHistory())
            .build();
    public static final Person BOB = new PersonBuilder()
            .withName(VALID_NAME_BOB)
            .withPhone(VALID_PHONE_BOB)
            .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND)
            .withScore(Score.DEFAULT)
            .withOrderHistory(new OrderHistory())
            .withTags(VALID_TAG_FRIEND)
            .build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalPersons() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical persons.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Person person : getTypicalPersons()) {
            ab.addPerson(person);
        }
        return ab;
    }

    public static List<Person> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
