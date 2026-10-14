package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Score;

public class UpdateScoreCommandTest {

    @Test
    public void execute_positiveChange_updatesScoreAndPreservesOtherFields() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        int originalScore = originalPerson.getScore().value;

        new UpdateScoreCommand(INDEX_FIRST_PERSON, 10).execute(model);

        Person updatedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(originalScore + 10, updatedPerson.getScore().value);
        assertEquals(originalPerson.getName(), updatedPerson.getName());
        assertEquals(originalPerson.getPhone(), updatedPerson.getPhone());
        assertEquals(originalPerson.getTags(), updatedPerson.getTags());
        assertEquals(originalPerson.getOrderHistory(), updatedPerson.getOrderHistory());
    }

    @Test
    public void execute_negativeChange_updatesScore() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person scoredPerson = new Person(originalPerson.getName(), originalPerson.getPhone(), originalPerson.getTags(),
                new Score(10), originalPerson.getOrderHistory());
        model.setPerson(originalPerson, scoredPerson);

        new UpdateScoreCommand(INDEX_FIRST_PERSON, -3).execute(model);

        assertEquals(7, model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getScore().value);
    }

    @Test
    public void execute_filteredList_updatesSelectedPersonAndKeepsFilter() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        new UpdateScoreCommand(INDEX_FIRST_PERSON, 5).execute(model);

        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(5, model.getFilteredPersonList().get(0).getScore().value);
    }

    @Test
    public void execute_resultingScoreBelowZero_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        assertCommandFailure(new UpdateScoreCommand(INDEX_FIRST_PERSON, -1), model,
                UpdateScoreCommand.MESSAGE_SCORE_BELOW_ZERO);
    }

    @Test
    public void execute_resultingScoreTooLarge_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person scoredPerson = new Person(originalPerson.getName(), originalPerson.getPhone(), originalPerson.getTags(),
                new Score(999999999), originalPerson.getOrderHistory());
        model.setPerson(originalPerson, scoredPerson);

        assertCommandFailure(new UpdateScoreCommand(INDEX_FIRST_PERSON, 1), model,
                UpdateScoreCommand.MESSAGE_SCORE_TOO_LARGE);
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertCommandFailure(new UpdateScoreCommand(outOfBoundsIndex, 1), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        UpdateScoreCommand command = new UpdateScoreCommand(INDEX_FIRST_PERSON, 10);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new UpdateScoreCommand(INDEX_FIRST_PERSON, 10)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new UpdateScoreCommand(Index.fromOneBased(2), 10)));
        assertFalse(command.equals(new UpdateScoreCommand(INDEX_FIRST_PERSON, 5)));
    }

    @Test
    public void toStringMethod() {
        UpdateScoreCommand command = new UpdateScoreCommand(INDEX_FIRST_PERSON, 10);
        String expected = UpdateScoreCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_PERSON + ", scoreChange=10}";

        assertEquals(expected, command.toString());
    }
}
