package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Person;
import seedu.address.model.person.Score;

public class CancelRedeemCommandTest {

    private static final Milestone FREE_DRINK = new Milestone("FREE100", "Free drink", 100);
    private static final Milestone FREE_DESSERT = new Milestone("DESSERT300", "Free dessert", 300);
    private static final Milestone FREE_MEAL = new Milestone("MEAL600", "Free meal", 600);
    private static final ModelStub MODEL = new ModelStub();

    @Test
    public void execute_redeemedMilestone_removesMilestoneFromHistory() throws Exception {
        MODEL.reset();
        Person originalPerson = MODEL.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        CommandResult result = new CancelRedeemCommand(INDEX_FIRST_PERSON, Index.fromOneBased(1)).execute(MODEL);

        Person updatedPerson = MODEL.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(originalPerson.getRedeemedMilestoneRewardHistory().getRedeemedMilestoneRewards().size() - 1,
                updatedPerson.getRedeemedMilestoneRewardHistory().getRedeemedMilestoneRewards().size());
        assertFalse(updatedPerson.hasRedeemed(FREE_DRINK));
        assertEquals(CancelRedeemCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
    }

    @Test
    public void execute_milestoneNotRedeemed_throwsCommandException() {
        MODEL.reset();

        CommandException exception = assertThrows(CommandException.class, () ->
                new CancelRedeemCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2)).execute(MODEL));

        assertEquals(CancelRedeemCommand.MESSAGE_NOT_REDEEMED, exception.getMessage());
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        MODEL.reset();
        Index invalidPersonIndex = Index.fromOneBased(MODEL.getFilteredPersonList().size() + 1);

        assertThrows(CommandException.class, () ->
                new CancelRedeemCommand(invalidPersonIndex, Index.fromOneBased(1)).execute(MODEL));
    }

    @Test
    public void execute_invalidMilestoneIndex_throwsCommandException() {
        MODEL.reset();
        Index invalidMilestoneIndex = Index.fromOneBased(MODEL.getMilestoneList().size() + 1);

        assertThrows(CommandException.class, () ->
                new CancelRedeemCommand(INDEX_FIRST_PERSON, invalidMilestoneIndex).execute(MODEL));
    }

    @Test
    public void toStringMethod() {
        Index milestoneIndex = Index.fromOneBased(2);
        CancelRedeemCommand command = new CancelRedeemCommand(INDEX_FIRST_PERSON, milestoneIndex);

        String expected = CancelRedeemCommand.class.getCanonicalName()
                + "{personIndex=" + INDEX_FIRST_PERSON
                + ", milestoneIndex=" + milestoneIndex + "}";

        assertEquals(expected, command.toString());
    }

    private static class ModelStub extends ModelManager {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();
        private final ObservableList<Milestone> milestones = FXCollections.observableArrayList();

        private void reset() {
            persons.clear();
            milestones.clear();

            Person person = getTypicalAddressBook().getPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
            persons.add(new Person(person.getName(), person.getPhone(), person.getTags(),
                    new Score(500), person.getOrderHistory(), person.getRedeemedMilestoneRewardHistory()
                            .addRedeemedMilestoneReward(FREE_DRINK)));
            milestones.add(FREE_DRINK);
            milestones.add(FREE_DESSERT);
            milestones.add(FREE_MEAL);
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return persons;
        }

        @Override
        public ObservableList<Milestone> getMilestoneList() {
            return milestones;
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            persons.set(persons.indexOf(target), editedPerson);
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            // Filtering is not relevant to this command test.
        }

        @Override
        public void selectPerson(Person person) {
            // Selection is not relevant to this command test.
        }
    }
}
