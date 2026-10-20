package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MILESTONE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RedeemMilestoneCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a {@link RedeemMilestoneCommand} object.
 */
public class RedeemMilestoneCommandParser implements Parser<RedeemMilestoneCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * {@link RedeemMilestoneCommand} and returns a {@code RedeemMilestoneCommand} object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public RedeemMilestoneCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_MILESTONE);

        if (argMultimap.getPreamble().isEmpty()
                || !argMultimap.getValue(PREFIX_MILESTONE).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RedeemMilestoneCommand.MESSAGE_USAGE));
        }

        try {
            Index personIndex = ParserUtil.parseIndex(argMultimap.getPreamble());
            Index milestoneIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_MILESTONE).get());
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MILESTONE);
            return new RedeemMilestoneCommand(personIndex, milestoneIndex);
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RedeemMilestoneCommand.MESSAGE_USAGE), exception);
        }
    }
}
