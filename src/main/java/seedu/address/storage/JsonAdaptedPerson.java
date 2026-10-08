package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.order.OrderHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Score;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final String score;
    private final List<JsonAdaptedOrder> orderHistory = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(
            @JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("score") String score,
            @JsonProperty("orderHistory") List<JsonAdaptedOrder> orderHistory) {
        this.name = name;
        this.phone = phone;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.score = score;
        if (orderHistory != null) {
            this.orderHistory.addAll(orderHistory);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        score = source.getScore().toString();
        orderHistory.addAll(source.getOrderHistory().getOrders().stream()
                .map(JsonAdaptedOrder::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.hasValidFormat(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        if (!Name.containsLetter(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS_MISSING_LETTER);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        final Set<Tag> modelTags = new HashSet<>(personTags);

        final Score modelScore;
        if (score == null) {
            modelScore = Score.DEFAULT;
        } else if (!Score.isValidScore(score)) {
            throw new IllegalValueException(Score.MESSAGE_CONSTRAINTS);
        } else {
            modelScore = new Score(Integer.parseInt(score));
        }
        OrderHistory modelOrderHistory = new OrderHistory();
        for (JsonAdaptedOrder adaptedOrder : orderHistory) {
            modelOrderHistory = modelOrderHistory.addOrder(adaptedOrder.toModelType());
        }

        return new Person(modelName, modelPhone, modelTags, modelScore, modelOrderHistory);
    }
}
