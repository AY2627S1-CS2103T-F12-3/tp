package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.menu.FoodItem;
import seedu.address.model.menu.Menu;
import seedu.address.model.menu.ReadOnlyMenu;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.milestone.MilestoneCatalog;
import seedu.address.model.milestone.ReadOnlyMilestoneCatalog;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonMenuStorage;
import seedu.address.storage.JsonMilestoneCatalogStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");
    private static final Milestone FREE_DRINK = new Milestone("FREE500", "Free drink", 500);

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        JsonMenuStorage menuStorage = new JsonMenuStorage(temporaryFolder.resolve("menu.json"));
        JsonMilestoneCatalogStorage milestoneCatalogStorage =
                new JsonMilestoneCatalogStorage(temporaryFolder.resolve("milestones.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage, menuStorage,
                milestoneCatalogStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_phoneManualTestWithSampleData_preservesExpectedMembers() throws Exception {
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();
        model.setAddressBook(sampleAddressBook);
        logic.execute("add-member n/PhoneCheck p/61234567");
        Person existingPerson = model.getSelectedPerson().orElseThrow();
        logic.execute("add-member n/PhoneCheck p/81234567");
        Person personToEdit = model.getSelectedPerson().orElseThrow();

        assertThrows(CommandException.class,
                String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, Messages.format(existingPerson)), () ->
                        logic.execute("add-member n/OtherCheck p/61234567"));
        logic.execute("find 81234567");
        assertThrows(CommandException.class,
                String.format(EditCommand.MESSAGE_DUPLICATE_PERSON, Messages.format(existingPerson)), () ->
                        logic.execute("edit-member 1 p/61234567"));
        assertEquals(List.of(personToEdit), model.getFilteredPersonList());

        logic.execute("edit-member 1 p/81234567");
        assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
        logic.execute("find 81234567");
        logic.execute("edit-member 1 p/91234567");
        for (String command : List.of("add-member n/OtherCheck p/", "edit-member 1 p/")) {
            assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> logic.execute(command));
        }

        AddressBook expectedAddressBook = new AddressBook(sampleAddressBook);
        expectedAddressBook.addPerson(existingPerson);
        Person editedPerson = new PersonBuilder(personToEdit).withPhone("91234567").build();
        expectedAddressBook.addPerson(editedPerson);
        ReadOnlyAddressBook reloaded = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow();
        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedAddressBook, reloaded);
        model.setAddressBook(reloaded);
        logic.execute("find PhoneCheck");
        assertEquals(List.of(existingPerson, editedPerson), model.getFilteredPersonList());
    }

    @Test
    public void execute_phoneIdentityRules_persistsValidChanges() throws Exception {
        logic.execute("add-member n/Alice p/91234567");
        logic.execute("add-member n/Alice p/81234567");
        logic.execute("edit-member 2 p/61234567");
        logic.execute("edit-member 2 p/61234567");

        ReadOnlyAddressBook reloaded = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), reloaded);
        assertEquals(2, reloaded.getPersonList().size());
        assertEquals(new Phone("61234567"), reloaded.getPersonList().get(1).getPhone());
    }

    @Test
    public void execute_duplicatePhoneFilteredList_throwsCommandException() throws Exception {
        logic.execute("add-member n/Alice p/91234567");
        Person existingPerson = model.getAddressBook().getPersonList().get(0);
        logic.execute("add-member n/Bob p/81234567");
        logic.execute("find Bob");

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.getName().fullName.equals("Bob"));
        expectedModel.selectPerson(model.getSelectedPerson().orElseThrow());
        String savedData = Files.readString(temporaryFolder.resolve("addressBook.json"));

        assertCommandFailure("add-member n/Carol p/91234567", CommandException.class,
                String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, Messages.format(existingPerson)), expectedModel);
        assertCommandFailure("edit-member 1 p/91234567", CommandException.class,
                String.format(EditCommand.MESSAGE_DUPLICATE_PERSON, Messages.format(existingPerson)), expectedModel);
        assertEquals(savedData, Files.readString(temporaryFolder.resolve("addressBook.json")));
        ReadOnlyAddressBook reloaded = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), reloaded);
    }

    @Test
    public void execute_unusualPhone_addsEditsFindsAndReloadsWithWarning() throws Exception {
        for (String input : List.of("911", "12345678", "+86 138119828111", "（86）12345678",
                "1234 5678 (HP) 1111-3333 (Office)")) {
            String addedFeedback = logic.execute("add-member n/PhoneCheck p/" + input).getFeedbackToUser();
            assertTrue(addedFeedback.contains("\n" + Messages.MESSAGE_PHONE_WARNING));
            Person added = model.getSelectedPerson().orElseThrow();
            assertEquals(input, added.getPhone().value);
            logic.execute("find p/" + input);
            assertEquals(List.of(added), model.getFilteredPersonList());
            String editedFeedback = logic.execute("edit-member 1 n/Renamed").getFeedbackToUser();
            assertTrue(editedFeedback.contains("\n" + Messages.MESSAGE_PHONE_WARNING));
            logic.execute("find p/" + input);
            Person renamed = model.getSelectedPerson().orElseThrow();
            assertEquals(added.getScore(), renamed.getScore());
            assertEquals(added.getOrderHistory(), renamed.getOrderHistory());
            assertEquals(input, renamed.getPhone().value);
            String savedData = Files.readString(temporaryFolder.resolve("addressBook.json"));
            assertThrows(CommandException.class,
                    String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, Messages.format(renamed)), () ->
                            logic.execute("add-member n/Duplicate p/" + input));
            assertEquals(savedData, Files.readString(temporaryFolder.resolve("addressBook.json")));
        }
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow());
    }

    @Test
    public void execute_formattedPhone_detectsDuplicatesAndSearchesCanonicalValue() throws Exception {
        assertFalse(logic.execute("add-member n/Alice p/+65 9123 4567").getFeedbackToUser()
                .contains(Messages.MESSAGE_PHONE_WARNING));
        Person alice = model.getSelectedPerson().orElseThrow();
        assertEquals("91234567", alice.getPhone().value);
        logic.execute("add-member n/Bob p/81234567");
        Person bob = model.getSelectedPerson().orElseThrow();
        logic.execute("find Bob");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.getName().fullName.equals("Bob"));
        expectedModel.selectPerson(bob);
        String savedData = Files.readString(temporaryFolder.resolve("addressBook.json"));
        for (String input : List.of("91234567", "9123-4567", "9123 4567", "6591234567",
                "65 9123 4567", "(+65)91234567", "９１２３４５６７")) {
            assertCommandFailure("add-member n/Other p/" + input, CommandException.class,
                    String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, Messages.format(alice)), expectedModel);
            assertCommandFailure("edit-member 1 p/" + input, CommandException.class,
                    String.format(EditCommand.MESSAGE_DUPLICATE_PERSON, Messages.format(alice)), expectedModel);
        }
        assertEquals(savedData, Files.readString(temporaryFolder.resolve("addressBook.json")));
        for (String query : List.of("91234567", "9123 4567", "+6591234567", "p/+65 9123 4567")) {
            logic.execute("find " + query);
            assertEquals(List.of(alice), model.getFilteredPersonList());
        }
        String feedback = logic.execute("edit-member 1 p/+65-9123-4567").getFeedbackToUser();
        assertFalse(feedback.contains(Messages.MESSAGE_PHONE_WARNING));
        assertEquals(alice, model.getSelectedPerson().orElseThrow());
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow());
    }

    @Test
    public void execute_phoneEdit_toUnusualEntryWarnsAndBlankInputDoesNotMutate() throws Exception {
        logic.execute("add-member n/Alice p/91234567");
        String feedback = logic.execute("edit-member 1 p/（86）12345678").getFeedbackToUser();
        assertTrue(feedback.contains("\n" + Messages.MESSAGE_PHONE_WARNING));
        assertEquals("（86）12345678", model.getSelectedPerson().orElseThrow().getPhone().value);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.selectPerson(model.getSelectedPerson().orElseThrow());
        String savedData = Files.readString(temporaryFolder.resolve("addressBook.json"));
        for (String blank : List.of("", " ", "\u00a0", "\u3000")) {
            assertCommandFailure("add-member n/Bob p/" + blank, ParseException.class,
                    Phone.MESSAGE_CONSTRAINTS, expectedModel);
            assertCommandFailure("edit-member 1 p/" + blank, ParseException.class,
                    Phone.MESSAGE_CONSTRAINTS, expectedModel);
        }
        assertEquals(savedData, Files.readString(temporaryFolder.resolve("addressBook.json")));
    }

    @Test
    public void execute_digitlessPhone_rejectsAddAndEditWithoutChangingStateOrFile() throws Exception {
        logic.execute("add-member n/Alice p/91234567");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.selectPerson(model.getSelectedPerson().orElseThrow());
        String savedData = Files.readString(temporaryFolder.resolve("addressBook.json"));
        for (String text : List.of("Not available", "---", "phone", "未知", "＋（）", "①", "²")) {
            assertCommandFailure("add-member n/Bob p/" + text, ParseException.class,
                    Phone.MESSAGE_MISSING_DIGIT, expectedModel);
            assertCommandFailure("edit-member 1 p/" + text, ParseException.class,
                    Phone.MESSAGE_MISSING_DIGIT, expectedModel);
            assertEquals(savedData, Files.readString(temporaryFolder.resolve("addressBook.json")));
        }
    }

    @Test
    public void execute_legacyDigitlessPhone_canFindEditOtherFieldsAndCorrectAfterReload() throws Exception {
        AddressBook legacy = new AddressBook();
        Person legacyMember = new PersonBuilder(AMY).withName("Legacy").withPhone("Not available").build();
        legacy.addPerson(legacyMember);
        Path membersFile = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage membersStorage = new JsonAddressBookStorage(membersFile);
        membersStorage.saveAddressBook(legacy);
        model.setAddressBook(membersStorage.readAddressBook().orElseThrow());
        logic.execute("find p/Not available");
        assertEquals(1, model.getFilteredPersonList().size());
        logic.execute("edit-member 1 n/Legacy Renamed");
        assertEquals("Not available", model.getSelectedPerson().orElseThrow().getPhone().value);
        assertEquals(model.getAddressBook(), membersStorage.readAddressBook().orElseThrow());
        logic.execute("find p/Not available");
        logic.execute("edit-member 1 p/9123 4567");
        Person corrected = model.getSelectedPerson().orElseThrow();
        assertEquals("91234567", corrected.getPhone().value);
        assertEquals(legacyMember.getScore(), corrected.getScore());
        assertEquals(legacyMember.getOrderHistory(), corrected.getOrderHistory());
        assertEquals(legacyMember.getRedeemedMilestoneRewardHistory(), corrected.getRedeemedMilestoneRewardHistory());
        assertEquals(model.getAddressBook(), membersStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_similarOverseasPhone_hiddenMemberWarnedAndBothEntriesPersist() throws Exception {
        logic.execute("add-member n/Alice p/+86 138119828111");
        Person alice = model.getSelectedPerson().orElseThrow();
        logic.execute("find Nobody");
        String feedback = logic.execute("add-member n/Bob p/+86138119828111").getFeedbackToUser();
        assertTrue(feedback.contains(Messages.format(alice)));
        assertTrue(feedback.contains("No records have been merged."));
        assertEquals("+86138119828111", model.getSelectedPerson().orElseThrow().getPhone().value);
        assertEquals(List.of(alice, model.getSelectedPerson().orElseThrow()), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow());
    }

    @Test
    public void execute_standardPhoneMatchingAnnotatedEntry_warnsButExactAliasStillRejected() throws Exception {
        logic.execute("add-member n/Alice p/92345678 (HP)");
        Person alice = model.getSelectedPerson().orElseThrow();
        String feedback = logic.execute("add-member n/Bob p/92345678").getFeedbackToUser();
        Person bob = model.getSelectedPerson().orElseThrow();
        assertTrue(feedback.contains(Messages.format(alice)));
        assertTrue(feedback.contains("No records have been merged."));
        assertFalse(feedback.contains(Messages.MESSAGE_PHONE_WARNING));
        String savedData = Files.readString(temporaryFolder.resolve("addressBook.json"));
        assertThrows(CommandException.class,
                String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, Messages.format(bob)), () ->
                        logic.execute("add-member n/Other p/+6592345678"));
        assertEquals(2, model.getAddressBook().getPersonList().size());
        assertEquals(savedData, Files.readString(temporaryFolder.resolve("addressBook.json")));
    }

    @Test
    public void execute_multiplePhoneNumbers_identifiesEachOverlappingMemberOnce() throws Exception {
        logic.execute("add-member n/Alice p/92345678");
        Person alice = model.getSelectedPerson().orElseThrow();
        logic.execute("add-member n/Bob p/81234567");
        Person bob = model.getSelectedPerson().orElseThrow();
        String feedback = logic.execute("add-member n/Carol p/92345678 (HP) 92345678 / 81234567 (Office)")
                .getFeedbackToUser();
        assertEquals(1L, feedback.lines().filter(Messages.format(alice)::equals).count());
        assertEquals(1L, feedback.lines().filter(Messages.format(bob)::equals).count());
        assertEquals(3, model.getAddressBook().getPersonList().size());
        assertEquals(alice, model.getAddressBook().getPersonList().get(0));
        assertEquals(bob, model.getAddressBook().getPersonList().get(1));
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow());
    }

    @Test
    public void execute_similarPhoneEdit_excludesSelfAndWarnsOnlyForOtherMembers() throws Exception {
        logic.execute("add-member n/Alice p/92345678");
        String selfFeedback = logic.execute("edit-member 1 p/92345678 (HP)").getFeedbackToUser();
        assertFalse(selfFeedback.contains("No records have been merged."));
        logic.execute("add-member n/Bob p/81234567");
        Person bob = model.getSelectedPerson().orElseThrow();
        logic.execute("find Alice");
        String feedback = logic.execute("edit-member 1 p/92345678 (HP) / 81234567 (Office)").getFeedbackToUser();
        assertTrue(feedback.contains(Messages.format(bob)));
        assertEquals(1L, feedback.lines().filter(Messages.format(bob)::equals).count());
        assertEquals(0L, feedback.lines().filter(line -> line.startsWith("Alice; Phone:")).count());
        assertEquals("92345678 (HP) / 81234567 (Office)",
                model.getSelectedPerson().orElseThrow().getPhone().value);
        logic.execute("find Alice");
        String nameFeedback = logic.execute("edit-member 1 n/Alice Renamed").getFeedbackToUser();
        assertFalse(nameFeedback.contains("No records have been merged."));
        assertEquals(bob, model.getAddressBook().getPersonList().get(1));
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow());
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete-member 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_helpWhileSavingFails_returnsHelpWithoutSaving() throws Exception {
        assertCommandSkipsSaving(HelpCommand.COMMAND_WORD,
                new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE, true, false));
    }

    @Test
    public void execute_exitWhileSavingFails_returnsExitWithoutSaving() throws Exception {
        assertCommandSkipsSaving(ExitCommand.COMMAND_WORD,
                new CommandResult(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true));
    }

    private void assertCommandSkipsSaving(String input, CommandResult expectedResult) throws Exception {
        FailingStorageManager storage = new FailingStorageManager();
        logic = new LogicManager(model, storage);

        assertEquals(expectedResult, logic.execute(input));
        assertEquals(0, storage.saveAttempts);
        assertEquals(new ModelManager(), model);

        String expectedMessage = String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage());
        assertThrows(CommandException.class, expectedMessage, () -> logic.execute(ListCommand.COMMAND_WORD));
        assertEquals(1, storage.saveAttempts);
    }

    @Test
    public void execute_addCommand_selectsAddedPerson() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        Model expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        expectedModel.selectPerson(expectedPerson);

        assertCommandSuccess(addCommand,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPerson)), expectedModel);
        assertEquals(Optional.of(expectedPerson), logic.getSelectedPerson());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    @Test
    public void getMilestoneList_modelUpdated_updatesReturnedList() {
        ObservableList<Milestone> milestones = logic.getMilestoneList();
        assertEquals(List.of(), milestones);

        model.addMilestone(FREE_DRINK);

        assertEquals(List.of(FREE_DRINK), milestones);
    }

    @Test
    public void getMilestoneList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getMilestoneList().add(FREE_DRINK));
    }

    @Test
    public void getFoodItemList_modelUpdated_updatesReturnedList() {
        ObservableList<FoodItem> foodItems = logic.getFoodItemList();
        FoodItem chickenRice = new FoodItem("CR01", "Chicken Rice", 450);

        assertTrue(foodItems.isEmpty());

        model.addFoodItem(chickenRice);

        assertEquals(List.of(chickenRice), foodItems);
    }

    @Test
    public void getFoodItemList_modifyList_throwsUnsupportedOperationException() {
        FoodItem chickenRice = new FoodItem("CR01", "Chicken Rice", 450);

        assertThrows(UnsupportedOperationException.class, () -> logic.getFoodItemList().add(chickenRice));
    }

    @Test
    public void execute_clearCommand_persistsEmptyCustomersMenuAndMilestones() throws Exception {
        model.addPerson(AMY);
        model.selectPerson(AMY);
        model.addFoodItem(new FoodItem("CR01", "Chicken Rice", 450));
        model.addMilestone(FREE_DRINK);
        logic.execute("list");
        ObservableList<FoodItem> displayedMenu = logic.getFoodItemList();
        ObservableList<Milestone> displayedMilestones = logic.getMilestoneList();

        assertCommandSuccess("clear", ClearCommand.MESSAGE_SUCCESS, new ModelManager());

        assertTrue(displayedMenu.isEmpty());
        assertTrue(displayedMilestones.isEmpty());
        assertTrue(new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow().getPersonList().isEmpty());
        assertTrue(new JsonMenuStorage(temporaryFolder.resolve("menu.json"))
                .readMenu().orElseThrow().getFoodItemList().isEmpty());
        assertTrue(new JsonMilestoneCatalogStorage(temporaryFolder.resolve("milestones.json"))
                .readMilestoneCatalog().orElseThrow().getMilestoneList().isEmpty());
    }

    @Test
    public void execute_menuCommand_savesMenu() throws Exception {
        logic.execute("add-menu-item c/CR01 n/Chicken Rice p/4.50");
        JsonMenuStorage saved = new JsonMenuStorage(temporaryFolder.resolve("menu.json"));
        assertEquals(new Menu(model.getMenu()), new Menu(saved.readMenu().orElseThrow()));
    }

    @Test
    public void execute_successfulCommand_savesMilestoneCatalog() throws Exception {
        model.addMilestone(FREE_DRINK);

        logic.execute("list");

        JsonMilestoneCatalogStorage saved =
                new JsonMilestoneCatalogStorage(temporaryFolder.resolve("milestones.json"));
        assertEquals(new MilestoneCatalog(model.getMilestoneCatalog()),
                new MilestoneCatalog(saved.readMilestoneCatalog().orElseThrow()));
    }

    @Test
    public void execute_successfulCommand_savesAllDataSets() throws Exception {
        logic.execute("list");
        assertTrue(Files.exists(temporaryFolder.resolve("addressBook.json")));
        assertTrue(Files.exists(temporaryFolder.resolve("menu.json")));
        assertTrue(Files.exists(temporaryFolder.resolve("milestones.json")));
    }

    @Test
    public void execute_invalidMenuCommand_doesNotSave() {
        assertThrows(ParseException.class, () -> logic.execute("add-menu-item c/CR01 n/Chicken Rice p/4.501"));
        assertFalse(Files.exists(temporaryFolder.resolve("menu.json")));
        assertFalse(Files.exists(temporaryFolder.resolve("addressBook.json")));
        assertFalse(Files.exists(temporaryFolder.resolve("milestones.json")));
    }

    @Test
    public void execute_duplicateMenuItem_preservesSavedFile() throws Exception {
        logic.execute("add-menu-item c/CR01 n/Chicken Rice p/4.50");
        Path path = temporaryFolder.resolve("menu.json");
        String saved = Files.readString(path);

        assertThrows(CommandException.class, () -> logic.execute("add-menu-item c/CR01 n/Different Dish p/9.99"));

        assertEquals(saved, Files.readString(path));
    }

    @Test
    public void execute_menuSaveThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromMenuStorage(DUMMY_IO_EXCEPTION,
                String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_menuSaveThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromMenuStorage(DUMMY_AD_EXCEPTION,
                String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_milestoneCatalogSaveThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromMilestoneCatalogStorage(DUMMY_IO_EXCEPTION,
                String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    private void assertCommandFailureForExceptionFromMenuStorage(IOException failure, String expectedMessage) {
        JsonMenuStorage menuStorage = new JsonMenuStorage(temporaryFolder.resolve("menu.json")) {
            @Override
            public void saveMenu(ReadOnlyMenu menu) throws IOException {
                throw failure;
            }
        };
        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")), menuStorage,
                new JsonMilestoneCatalogStorage(temporaryFolder.resolve("milestones.json")));
        logic = new LogicManager(model, storage);
        Model expected = new ModelManager();
        expected.addFoodItem(new FoodItem("CR01", "Chicken Rice", 450));

        assertCommandFailure("add-menu-item c/CR01 n/Chicken Rice p/4.50",
                CommandException.class, expectedMessage, expected);
    }

    @Test
    public void execute_invalidMenuDeletion_preservesSavedData() throws Exception {
        logic.execute("add-menu-item c/CR01 n/Chicken Rice p/4.50");
        List<Path> paths = List.of(temporaryFolder.resolve("addressBook.json"), temporaryFolder.resolve("menu.json"),
                temporaryFolder.resolve("milestones.json"));
        List<String> saved = List.of(Files.readString(paths.get(0)), Files.readString(paths.get(1)),
                Files.readString(paths.get(2)));

        for (String input : List.of("delete-menu-item", "delete-menu-item c/", "delete-menu-item 1",
                "delete-menu-item c/CR01 c/CR02", "delete-menu-item c/CR01 n/extra",
                "delete-menu-item c/CR01 p/4.50", "delete-menu-item c/CR01!")) {
            assertThrows(ParseException.class, () -> logic.execute(input));
        }
        for (String code : List.of("MISSING", "cr01")) {
            assertThrows(CommandException.class, () -> logic.execute("delete-menu-item c/" + code));
        }

        assertEquals(List.of(new FoodItem("CR01", "Chicken Rice", 450)), model.getMenu().getFoodItemList());
        for (int i = 0; i < paths.size(); i++) {
            assertEquals(saved.get(i), Files.readString(paths.get(i)));
        }
    }

    @Test
    public void execute_deleteMenuSaveFails_reportsErrorAndCanSaveLater() throws Exception {
        assertFailedDeletionSaveCanBeRetried(DUMMY_IO_EXCEPTION,
                String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_deleteMenuAccessDenied_reportsErrorAndCanSaveLater() throws Exception {
        assertFailedDeletionSaveCanBeRetried(DUMMY_AD_EXCEPTION,
                String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    private void assertFailedDeletionSaveCanBeRetried(IOException failure, String expectedMessage) throws Exception {
        logic.execute("add-menu-item c/CR01 n/Chicken Rice p/4.50");
        Path menuPath = temporaryFolder.resolve("menu.json");
        String saved = Files.readString(menuPath);
        JsonMenuStorage failingMenuStorage = new JsonMenuStorage(menuPath) {
            @Override
            public void saveMenu(ReadOnlyMenu menu) throws IOException {
                throw failure;
            }
        };
        StorageManager storage = createStorageForDeletion(failingMenuStorage);
        logic = new LogicManager(model, storage);

        assertThrows(CommandException.class, expectedMessage, () -> logic.execute("delete-menu-item c/CR01"));
        assertTrue(model.getMenu().getFoodItemList().isEmpty());
        assertEquals(saved, Files.readString(menuPath));

        storage = createStorageForDeletion(new JsonMenuStorage(menuPath));
        logic = new LogicManager(model, storage);
        logic.execute("list");
        assertTrue(storage.readMenu().orElseThrow().getFoodItemList().isEmpty());
    }

    private StorageManager createStorageForDeletion(JsonMenuStorage menuStorage) {
        return new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")), menuStorage,
                new JsonMilestoneCatalogStorage(temporaryFolder.resolve("milestones.json")));
    }

    private void assertCommandFailureForExceptionFromMilestoneCatalogStorage(
            IOException failure, String expectedMessage) {
        JsonMilestoneCatalogStorage milestoneCatalogStorage =
                new JsonMilestoneCatalogStorage(temporaryFolder.resolve("milestones.json")) {
                    @Override
                    public void saveMilestoneCatalog(ReadOnlyMilestoneCatalog milestoneCatalog) throws IOException {
                        throw failure;
                    }
                };
        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")),
                new JsonMenuStorage(temporaryFolder.resolve("menu.json")), milestoneCatalogStorage);
        logic = new LogicManager(model, storage);

        assertCommandFailure("list", CommandException.class, expectedMessage, new ModelManager());
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs(),
                model.getMenu(), model.getMilestoneCatalog());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        JsonMenuStorage menuStorage = new JsonMenuStorage(temporaryFolder.resolve("ExceptionMenu.json"));
        JsonMilestoneCatalogStorage milestoneCatalogStorage =
                new JsonMilestoneCatalogStorage(temporaryFolder.resolve("ExceptionMilestones.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage, menuStorage,
                milestoneCatalogStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        expectedModel.selectPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    /** Storage that counts and rejects every attempt to save domain data. */
    private class FailingStorageManager extends StorageManager {
        private int saveAttempts;

        FailingStorageManager() {
            super(new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")),
                    new JsonMenuStorage(temporaryFolder.resolve("menu.json")),
                    new JsonMilestoneCatalogStorage(temporaryFolder.resolve("milestones.json")));
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            saveAttempts++;
            throw DUMMY_IO_EXCEPTION;
        }

        @Override
        public void saveMenu(ReadOnlyMenu menu) throws IOException {
            saveAttempts++;
            throw DUMMY_IO_EXCEPTION;
        }

        @Override
        public void saveMilestoneCatalog(ReadOnlyMilestoneCatalog milestoneCatalog) throws IOException {
            saveAttempts++;
            throw DUMMY_IO_EXCEPTION;
        }
    }
}
