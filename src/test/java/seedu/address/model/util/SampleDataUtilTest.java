package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Score;
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
        assertTrue(samplePersons[0].getTags().contains(new Tag("friends")));
        assertEquals(Score.DEFAULT, samplePersons[0].getScore());
    }
}
