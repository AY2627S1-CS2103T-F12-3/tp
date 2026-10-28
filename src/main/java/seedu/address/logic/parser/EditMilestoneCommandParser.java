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

        Index index = parseMilestoneIndex(argMultimap.getPreamble());
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_REWARD, PREFIX_REQUIRED_SCORE);
        EditMilestoneDescriptor descriptor = parseEditDescriptor(argMultimap);
        return new EditMilestoneCommand(index, descriptor);
    }

    /**
     * Parses the milestone index and adds the command usage to invalid-index errors.
     */
    private Index parseMilestoneIndex(String preamble) throws ParseException {
        try {
            return ParserUtil.parseIndex(preamble);
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditMilestoneCommand.MESSAGE_USAGE), e);
        }
    }

    /**
     * Parses the supplied editable fields and rejects an edit with no fields.
     */
    private EditMilestoneDescriptor parseEditDescriptor(ArgumentMultimap argMultimap) throws ParseException {
        EditMilestoneDescriptor descriptor = new EditMilestoneDescriptor();
        if (argMultimap.getValue(PREFIX_REWARD).isPresent()) {
            String reward = argMultimap.getValue(PREFIX_REWARD).get();
            if (!Milestone.isValidRewardDescription(reward)) {
                throw new ParseException(Milestone.MESSAGE_REWARD_DESCRIPTION_CONSTRAINTS);
            }
            descriptor.setRewardDescription(reward);
        }
        if (argMultimap.getValue(PREFIX_REQUIRED_SCORE).isPresent()) {
            int requiredScore = ParserUtil.parseRequiredScore(
                    argMultimap.getValue(PREFIX_REQUIRED_SCORE).get());
            descriptor.setRequiredScore(requiredScore);
        }

        if (!descriptor.isAnyFieldEdited()) {
            throw new ParseException(EditMilestoneCommand.MESSAGE_NOT_EDITED);
        }
        return descriptor;
    }
}
