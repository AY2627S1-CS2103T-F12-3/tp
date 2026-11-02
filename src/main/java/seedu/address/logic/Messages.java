package seedu.address.logic;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The member index provided is invalid.";
    public static final String MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX =
            "The milestone index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d member(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    public static final String MESSAGE_PHONE_WARNING =
            "Warning: This phone entry was not recognized as a standard Singapore number. "
            + "It has been saved; please check it. You may ignore this warning for an overseas number "
            + "or an intentional special format. Only exact text duplicates are blocked for these entries.";

    public static final String MESSAGE_POSSIBLE_PHONE_DUPLICATES =
            "Warning: This entry has been saved, but its phone digits may overlap with these existing members:\n"
            + "%s\nCheck whether these are separate memberships. No records have been merged.";

    /**
     * Returns a nonblocking warning for an unusual phone entry, or an empty string for a standard number.
     */
    public static String formatPhoneWarning(Phone phone) {
        return phone.isStandardSingaporeNumber() ? "" : "\n" + MESSAGE_PHONE_WARNING;
    }

    /**
     * Formats a nonblocking warning identifying all possible phone overlaps, or nothing if there are none.
     */
    public static String formatPossiblePhoneDuplicates(List<Person> persons) {
        if (persons.isEmpty()) {
            return "";
        }
        String members = persons.stream().map(Messages::format).collect(Collectors.joining("\n"));
        return "\n" + String.format(MESSAGE_POSSIBLE_PHONE_DUPLICATES, members);
    }

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats the {@code milestone} for display to the user.
     */
    public static String format(Milestone milestone) {
        return String.format("%s (Required score: %d)",
                milestone.getRewardDescription(), milestone.getRequiredScore());
    }

}
