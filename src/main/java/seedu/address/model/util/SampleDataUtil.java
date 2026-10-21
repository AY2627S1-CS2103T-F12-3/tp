package seedu.address.model.util;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;
import seedu.address.model.order.Order;
import seedu.address.model.order.OrderHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Score;
import seedu.address.model.reward.RedeemedMilestoneRewardHistory;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    /**
     * Returns sample hawker stall members with dietary tags, scores, and order histories.
     */
    public static Person[] getSamplePersons() {
        return new Person[] {
            sampleMember("Alex Yeoh", "87438807", getTagSet("dairyFree"), 100,
                    sampleOrder("1", 1, "2026-10-10T04:10:00Z"),
                    sampleOrder("2", 1, "2026-10-12T04:15:00Z")),
            sampleMember("Bernice Yu", "99272758", getTagSet(), 193,
                    sampleOrder("3A", 1, "2026-10-09T10:20:00Z"),
                    sampleOrder("4", 1, "2026-10-11T01:45:00Z"),
                    sampleOrder("3B", 2, "2026-10-12T10:30:00Z")),
            sampleMember("Charlotte Oliveiro", "93210283", getTagSet("noBeef"), 95,
                    sampleOrder("5", 1, "2026-10-11T05:30:00Z"),
                    sampleOrder("1", 1, "2026-10-14T00:22:00Z")),
            sampleMember("David Li", "91031282", getTagSet("vegetarian"), 99,
                    sampleOrder("6", 1, "2026-10-08T04:50:00Z"),
                    sampleOrder("7", 1, "2026-10-10T02:15:00Z"),
                    sampleOrder("6", 1, "2026-10-13T05:20:00Z")),
            sampleMember("Irfan Ibrahim", "92492021", getTagSet("noPork"), 101,
                    sampleOrder("8", 1, "2026-10-09T09:40:00Z"),
                    sampleOrder("9", 2, "2026-10-13T01:10:00Z")),
            sampleMember("Roy Balakrishnan", "92624417", getTagSet("dairyFree"), 125,
                    sampleOrder("10", 1, "2026-10-07T11:15:00Z"),
                    sampleOrder("11", 1, "2026-10-11T05:05:00Z"),
                    sampleOrder("10", 1, "2026-10-13T11:45:00Z"))
        };
    }

    /**
     * Creates a sample member with the supplied score and orders.
     */
    private static Person sampleMember(String name, String phone, Set<Tag> tags, int score, Order... orders) {
        OrderHistory orderHistory = new OrderHistory();
        for (Order order : orders) {
            orderHistory = orderHistory.addOrder(order);
        }
        return new Person(new Name(name), new Phone(phone), tags, new Score(score), orderHistory,
                new RedeemedMilestoneRewardHistory());
    }

    /**
     * Creates a sample order from an ISO-8601 timestamp.
     */
    private static Order sampleOrder(String itemCode, int quantity, String recordedAt) {
        FoodItem foodItem = getSampleMenu().findFoodItemByCode(itemCode).orElseThrow();
        int scoreGained = (int) Order.calculateScoreGained(foodItem.getPriceInCents(), quantity);
        return new Order(itemCode, foodItem.getName(), quantity, scoreGained, Instant.parse(recordedAt));
    }

    /**
     * Returns an address book populated with the sample members.
     */
    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a menu containing the items referenced by sample order histories.
     */
    public static Menu getSampleMenu() {
        Menu sampleMenu = new Menu();
        sampleMenu.addFoodItem(new FoodItem("1", "Chicken Rice", 450));
        sampleMenu.addFoodItem(new FoodItem("2", "Laksa", 550));
        sampleMenu.addFoodItem(new FoodItem("3A", "Nasi Lemak", 480));
        sampleMenu.addFoodItem(new FoodItem("3B", "Nasi Lemak + Chicken", 650));
        sampleMenu.addFoodItem(new FoodItem("4", "Kopi", 150));
        sampleMenu.addFoodItem(new FoodItem("5", "Wanton Mee", 500));
        sampleMenu.addFoodItem(new FoodItem("6", "Vegetarian Bee Hoon", 420));
        sampleMenu.addFoodItem(new FoodItem("7", "Teh", 150));
        sampleMenu.addFoodItem(new FoodItem("8", "Chicken Murtabak", 650));
        sampleMenu.addFoodItem(new FoodItem("9", "Roti Prata", 180));
        sampleMenu.addFoodItem(new FoodItem("10", "Mee Goreng", 500));
        sampleMenu.addFoodItem(new FoodItem("11", "Lime Juice", 250));
        return sampleMenu;
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
