package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCORE_CHANGE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UpdateScoreCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates an {@link UpdateScoreCommand} object.
 */
public class UpdateScoreCommandParser implements Parser<UpdateScoreCommand> {

    private static final String SCORE_CHANGE_VALIDATION_REGEX = "[+-]?\\d{1,9}";
    private static final String SCORE_CHANGE_CONSTRAINTS = "Score change must be a signed whole number.";

    @Override
    public UpdateScoreCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_SCORE_CHANGE);

        if (argMultimap.getPreamble().isEmpty() || !argMultimap.getValue(PREFIX_SCORE_CHANGE).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    UpdateScoreCommand.MESSAGE_USAGE));
        }

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    UpdateScoreCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_SCORE_CHANGE);
        String scoreChange = argMultimap.getValue(PREFIX_SCORE_CHANGE).get().trim();
        if (!scoreChange.matches(SCORE_CHANGE_VALIDATION_REGEX)) {
            throw new ParseException(SCORE_CHANGE_CONSTRAINTS);
        }

        return new UpdateScoreCommand(index, Integer.parseInt(scoreChange));
    }
}
