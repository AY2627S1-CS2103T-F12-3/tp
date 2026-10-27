package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.milestone.Milestone;

public class ClearCommandTest {

    @Test
    public void execute_populatedMenuAndMilestones_clearsAllDataAndUpdatesViews() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.selectPerson(model.getFilteredPersonList().get(0));
        model.addFoodItem(new FoodItem("CR01", "Chicken Rice", 450));
        model.addFoodItem(new FoodItem("T01", "Tea", 150));
        model.addMilestone(new Milestone("M1", "Free drink", 100));
        model.addMilestone(new Milestone("M2", "Free meal", 500));
        ObservableList<FoodItem> menuItems = model.getMenu().getFoodItemList();
        ObservableList<Milestone> milestones = model.getMilestoneList();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, new ModelManager());
        assertEquals(List.of(), menuItems);
        assertEquals(List.of(), milestones);
        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, new ModelManager());
    }

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.selectPerson(model.getFilteredPersonList().get(0));
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_populatedMenuAndMilestonesWithoutCustomers_clearsAllData() {
        Model model = new ModelManager();
        model.addFoodItem(new FoodItem("CR01", "Chicken Rice", 450));
        model.addFoodItem(new FoodItem("T01", "Tea", 150));
        model.addMilestone(new Milestone("M1", "Free drink", 100));
        model.addMilestone(new Milestone("M2", "Free meal", 500));

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, new ModelManager());
        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, new ModelManager());
    }

    @Test
    public void execute_allDataPopulated_clearsAllDataAndSelection() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.selectPerson(model.getFilteredPersonList().get(0));
        model.updateFilteredPersonList(person -> false);
        model.addFoodItem(new FoodItem("CR01", "Chicken Rice", 450));
        model.addMilestone(new Milestone("M1", "Free drink", 100));

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, new ModelManager());
    }

}
