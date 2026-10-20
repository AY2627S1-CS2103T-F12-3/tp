package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Contains shared tokenization checks for milestone commands.
 */
final class MilestoneCommandParserUtil {
    private static final Pattern ARGUMENT_PREFIX = Pattern.compile("(?<= )[A-Za-z]/");

    private MilestoneCommandParserUtil() {}

    /**
     * Tokenizes milestone command arguments after rejecting unsupported prefixes and control characters.
     */
    static ArgumentMultimap tokenize(String args, String messageUsage, Prefix... supportedPrefixes)
            throws ParseException {
        requireNonNull(args);
        requireNonNull(messageUsage);
        requireNonNull(supportedPrefixes);

        if (args.chars().anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }

        String normalizedArgs = " " + args.replace('\t', ' ');
        Set<String> supportedPrefixValues = Arrays.stream(supportedPrefixes)
                .map(Prefix::getPrefix)
                .collect(Collectors.toUnmodifiableSet());
        Matcher matcher = ARGUMENT_PREFIX.matcher(normalizedArgs);
        while (matcher.find()) {
            if (!supportedPrefixValues.contains(matcher.group())) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
            }
        }

        return ArgumentTokenizer.tokenize(normalizedArgs, supportedPrefixes);
    }
}
