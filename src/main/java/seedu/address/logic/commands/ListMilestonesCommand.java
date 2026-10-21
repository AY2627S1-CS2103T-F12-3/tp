package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.milestone.Milestone;

/**
 * Lists all milestone rewards in their displayed order.
 */
public class ListMilestonesCommand extends Command {
    public static final String COMMAND_WORD = "list-milestones";

    public static final String MESSAGE_NO_MILESTONES = "No milestones found.";
    public static final String MESSAGE_MILESTONES_LISTED = "%d milestone(s) listed!\n%s";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Milestone> milestones = model.getMilestoneList();
        if (milestones.isEmpty()) {
            return new CommandResult(MESSAGE_NO_MILESTONES);
        }

        String numberedMilestones = IntStream.range(0, milestones.size())
                .mapToObj(index -> String.format("%d. %s", index + 1, Messages.format(milestones.get(index))))
                .collect(Collectors.joining("\n"));
        return new CommandResult(String.format(MESSAGE_MILESTONES_LISTED,
                milestones.size(), numberedMilestones));
    }
}
