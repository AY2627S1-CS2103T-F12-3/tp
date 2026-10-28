package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);
        expectedModel.selectPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_sameNameDifferentPhone_success() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person newPerson = new PersonBuilder(personInList).withPhone("61234567").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(newPerson);
        expectedModel.selectPerson(newPerson);

        assertCommandSuccess(new AddCommand(newPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(newPerson)), expectedModel);
    }

    @Test
    public void execute_samePhoneDifferentNameFilteredList_failure() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person duplicatePerson = new PersonBuilder(personInList).withName("Another Person").build();
        model.updateFilteredPersonList(person -> false);

        assertCommandFailure(new AddCommand(duplicatePerson), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, Messages.format(personInList)));
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, Messages.format(personInList)));
    }

}
