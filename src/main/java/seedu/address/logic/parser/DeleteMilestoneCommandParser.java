package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteMilestoneCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a {@link DeleteMilestoneCommand} object.
 */
public class DeleteMilestoneCommandParser implements Parser<DeleteMilestoneCommand> {

    @Override
    public DeleteMilestoneCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = MilestoneCommandParserUtil.tokenize(args,
                DeleteMilestoneCommand.MESSAGE_USAGE);

        try {
            Index index = ParserUtil.parseIndex(argMultimap.getPreamble());
            return new DeleteMilestoneCommand(index);
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteMilestoneCommand.MESSAGE_USAGE), e);
        }
    }
}
