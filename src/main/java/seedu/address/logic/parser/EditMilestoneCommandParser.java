package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIRED_SCORE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REWARD;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditMilestoneCommand;
import seedu.address.logic.commands.EditMilestoneCommand.EditMilestoneDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.milestone.Milestone;

/**
 * Parses input arguments and creates an {@link EditMilestoneCommand} object.
 */
public class EditMilestoneCommandParser implements Parser<EditMilestoneCommand> {

    @Override
    public EditMilestoneCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = MilestoneCommandParserUtil.tokenize(args,
                EditMilestoneCommand.MESSAGE_USAGE, PREFIX_REWARD, PREFIX_REQUIRED_SCORE);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditMilestoneCommand.MESSAGE_USAGE), e);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_REWARD, PREFIX_REQUIRED_SCORE);
        EditMilestoneDescriptor editMilestoneDescriptor = new EditMilestoneDescriptor();
        if (argMultimap.getValue(PREFIX_REWARD).isPresent()) {
            String reward = argMultimap.getValue(PREFIX_REWARD).get();
            if (!Milestone.isValidRewardDescription(reward)) {
                throw new ParseException(Milestone.MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS);
            }
            editMilestoneDescriptor.setRewardDescription(reward);
        }
        if (argMultimap.getValue(PREFIX_REQUIRED_SCORE).isPresent()) {
            int requiredScore = ParserUtil.parseRequiredScore(
                    argMultimap.getValue(PREFIX_REQUIRED_SCORE).get());
            editMilestoneDescriptor.setRequiredScore(requiredScore);
        }

        if (!editMilestoneDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditMilestoneCommand.MESSAGE_NOT_EDITED);
        }
        return new EditMilestoneCommand(index, editMilestoneDescriptor);
    }
}
