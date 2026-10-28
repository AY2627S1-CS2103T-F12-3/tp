package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIRED_SCORE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REWARD;

import java.util.stream.Stream;

import seedu.address.logic.commands.AddMilestoneCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.milestone.Milestone;

/**
 * Parses input arguments and creates an {@link AddMilestoneCommand} object.
 */
public class AddMilestoneCommandParser implements Parser<AddMilestoneCommand> {

    @Override
    public AddMilestoneCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = MilestoneCommandParserUtil.tokenize(args,
                AddMilestoneCommand.MESSAGE_USAGE, PREFIX_REWARD, PREFIX_REQUIRED_SCORE);

        if (!arePrefixesPresent(argMultimap, PREFIX_REWARD, PREFIX_REQUIRED_SCORE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddMilestoneCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_REWARD, PREFIX_REQUIRED_SCORE);
        String reward = argMultimap.getValue(PREFIX_REWARD).get();
        if (!Milestone.isValidRewardDescription(reward)) {
            throw new ParseException(Milestone.MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS);
        }
        int requiredScore = ParserUtil.parseRequiredScore(argMultimap.getValue(PREFIX_REQUIRED_SCORE).get());

        return new AddMilestoneCommand(new Milestone(reward, requiredScore));
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
