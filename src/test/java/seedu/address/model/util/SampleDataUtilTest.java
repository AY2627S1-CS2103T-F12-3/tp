package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class SampleDataUtilTest {

    @Test
    public void getSamplePersons_returnsExpectedPersons() {
        Person[] samplePersons = SampleDataUtil.getSamplePersons();

        assertEquals(6, samplePersons.length);
        assertEquals(List.of("Alex Yeoh", "Bernice Yu", "Charlotte Oliveiro", "David Li", "Irfan Ibrahim",
                "Roy Balakrishnan"), Arrays.stream(samplePersons)
                        .map(person -> person.getName().fullName)
                        .toList());
        assertEquals(new Name("Alex Yeoh"), samplePersons[0].getName());
        assertEquals(new Phone("87438807"), samplePersons[0].getPhone());
        assertEquals(List.of(118, 169, 106, 94, 137, 153), Arrays.stream(samplePersons)
                .map(person -> person.getScore().value)
                .toList());
        assertEquals(List.of(Set.of(new Tag("dairyFree")), Set.of(), Set.of(new Tag("noBeef")),
                Set.of(new Tag("vegetarian")), Set.of(new Tag("noPork")), Set.of(new Tag("dairyFree"))),
                Arrays.stream(samplePersons).map(Person::getTags).toList());
        Person charlotte = samplePersons[2];
        assertEquals("1", charlotte.getOrderHistory().getOrders().get(1).itemCode);
        assertEquals(1, charlotte.getOrderHistory().getOrders().get(1).quantity);
        assertEquals(Instant.parse("2026-10-14T00:22:00Z"),
                charlotte.getOrderHistory().getOrders().get(1).recordedAt);
    }

    @Test
    public void getSampleMenu_matchesEverySampleOrder() {
        Map<String, String> menuNamesByCode = SampleDataUtil.getSampleMenu().getFoodItemList().stream()
                .collect(Collectors.toMap(item -> item.getCode(), item -> item.getName()));

        for (Person person : SampleDataUtil.getSamplePersons()) {
            person.getOrderHistory().getOrders().forEach(order ->
                    assertEquals(menuNamesByCode.get(order.itemCode), order.itemName));
        }
    }

}
