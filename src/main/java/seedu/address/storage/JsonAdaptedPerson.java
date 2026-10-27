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
import seedu.address.model.reward.RedeemedMilestoneRewardHistory;
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
    private final List<JsonAdaptedRedeemedMilestoneReward> redeemedMilestoneRewardHistory = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(
            @JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("score") String score,
            @JsonProperty("orderHistory") List<JsonAdaptedOrder> orderHistory,
            @JsonProperty("redeemedMilestoneRewardHistory")
            List<JsonAdaptedRedeemedMilestoneReward> redeemedMilestoneRewardHistory) {
        this.name = name;
        this.phone = phone;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.score = score;
        if (orderHistory != null) {
            this.orderHistory.addAll(orderHistory);
        }
        if (redeemedMilestoneRewardHistory != null) {
            this.redeemedMilestoneRewardHistory.addAll(redeemedMilestoneRewardHistory);
        }
    }

    /**
     * Constructs a {@code JsonAdaptedPerson} without redeemed milestone rewards.
     * This supports address book data created before redeemed rewards were added.
     */
    public JsonAdaptedPerson(String name, String phone, List<JsonAdaptedTag> tags, String score,
                             List<JsonAdaptedOrder> orderHistory) {
        this(name, phone, tags, score, orderHistory, new ArrayList<>());
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
        redeemedMilestoneRewardHistory.addAll(source.getRedeemedMilestoneRewardHistory()
                .getRedeemedMilestoneRewards().stream()
                .map(JsonAdaptedRedeemedMilestoneReward::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        return new Person(
                toModelName(),
                toModelPhone(),
                toModelTags(),
                toModelScore(),
                toModelOrderHistory(),
                toModelRedeemedMilestoneRewardHistory());
    }

    /**
     * Converts and validates the adapted name.
     */
    private Name toModelName() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.hasValidFormat(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        if (!Name.containsLetter(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS_MISSING_LETTER);
        }
        return new Name(name);
    }

    /**
     * Converts and validates the adapted phone number.
     */
    private Phone toModelPhone() throws IllegalValueException {
        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(phone);
    }

    /**
     * Converts and validates the adapted tags.
     */
    private Set<Tag> toModelTags() throws IllegalValueException {
        Set<Tag> modelTags = new HashSet<>();
        for (JsonAdaptedTag tag : tags) {
            modelTags.add(tag.toModelType());
        }
        return modelTags;
    }

    /**
     * Converts and validates the adapted score, using the default when it is missing.
     */
    private Score toModelScore() throws IllegalValueException {
        if (score == null) {
            return Score.DEFAULT;
        }
        if (!Score.isValidScore(score)) {
            throw new IllegalValueException(Score.MESSAGE_CONSTRAINTS);
        }
        return new Score(Integer.parseInt(score));
    }

    /**
     * Converts the adapted orders into an order history.
     */
    private OrderHistory toModelOrderHistory() throws IllegalValueException {
        OrderHistory modelOrderHistory = new OrderHistory();
        for (JsonAdaptedOrder adaptedOrder : orderHistory) {
            modelOrderHistory = modelOrderHistory.addOrder(adaptedOrder.toModelType());
        }
        return modelOrderHistory;
    }

    /**
     * Converts the adapted rewards into a redeemed milestone reward history.
     */
    private RedeemedMilestoneRewardHistory toModelRedeemedMilestoneRewardHistory()
            throws IllegalValueException {
        RedeemedMilestoneRewardHistory modelHistory = new RedeemedMilestoneRewardHistory();
        for (JsonAdaptedRedeemedMilestoneReward adaptedReward : redeemedMilestoneRewardHistory) {
            if (adaptedReward == null) {
                throw new IllegalValueException(JsonAdaptedRedeemedMilestoneReward.MESSAGE_CONSTRAINTS);
            }
            try {
                modelHistory = modelHistory.addRedeemedMilestoneReward(adaptedReward.toModelType());
            } catch (IllegalArgumentException exception) {
                throw new IllegalValueException(RedeemedMilestoneRewardHistory.MESSAGE_DUPLICATE_MILESTONE);
            }
        }
        return modelHistory;
    }
}
