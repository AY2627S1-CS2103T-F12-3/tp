---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete-menu-item c/CR01`. For simplicity, only menu persistence is shown.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete-member 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete-member 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* identifies each person by their saved phone entry using `Person.isSamePerson`. Different people can share a name.
  `Phone` rejects only blank entries, trims surrounding whitespace and normalizes recognized Singapore formats
  to eight digits starting with 6, 8 or 9. It accepts spaces, hyphens, full-width ASCII characters and the country
  prefixes `+65`, `65`, `(+65)` and `(65)`. A bare `65` is removed only from ten digits whose last eight match
  the expected Singapore format. Other entries retain their spelling and use exact, case-sensitive equality.
* uses the same `Phone` constructor to normalize parsed commands and JSON data. `Phone.isStorablePhone` accepts
  nonblank legacy entries; `Phone.isValidPhone` additionally requires a Unicode decimal digit for new phone
  values in `ParserUtil.parsePhone`. Blank and digitless input have separate error messages. Storage and lookup
  use the storable rule so users can retrieve and repair previously saved digitless notes without losing members.
  `add-member` and `edit-member` append the shared `Messages.formatPhoneWarning` after successful changes for nonstandard
  entries containing digits. Editing other fields on a legacy digitless entry also remains possible.
  No country-specific validity or reachability check is performed for foreign numbers. Annotated or multiple
  numbers do not establish exact member identity.
* provides `Model.findPersonsWithSimilarPhone` as a separate advisory query over the full member list.
  `Phone.hasPossibleNumberInCommon` extracts complete digit segments separated by text or delimiters, ignores
  explicit extension values and compares segments of at least six digits. Unicode decimal digits are mapped to
  ASCII for comparison, and a recognized Singapore `65` prefix is removed. Complete sequences whose total digit
  count is a multiple of eight are checked as separate eight-digit Singapore numbers. The check never uses
  arbitrary substring matching, infers other country codes, or joins numbers across text, slashes or plus signs.
  Adjacent digits separated only by formatting characters form one segment, so not every multi-number spelling can
  be identified.
  Exact matches are handled by the existing blocking identity check; potential matches are formatted by
  `Messages.formatPossiblePhoneDuplicates` after a successful add or phone edit. Editing excludes the original
  member from this query, and edits without a phone field do not trigger another overlap warning.
  The advisory query itself does not change phone equality, stored text, scores, histories, filters or selection.
* searches phone entries through the same normalization rules. `find p/PHONE_ENTRY` constructs a phone-only
  predicate so spaces and annotations are retained; ordinary queries keep name-keyword OR matching and exact
  phone matching. A whole recognized Singapore query is normalized before keyword splitting.
* is initialized by `MainApp` only if existing member data can be loaded. A read failure, including a collision
  between normalized legacy numbers, stops startup and preserves the file rather than exposing an empty list
  that a later command could save over it.
* stores each person's name, phone, tags, score, and order history. Editing a person's details preserves
  their existing score and order history.
* stores each person's redeemed milestone rewards in a `RedeemedMilestoneRewardHistory`. The history is immutable:
  adding or deleting a reward returns a new history, and membership checks compare milestone codes.
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* stores a separate `Menu` in `ModelManager`, alongside `AddressBook` and `UserPrefs`. `Model#getMenu()` exposes a
  `ReadOnlyMenu` with an unmodifiable observable item list; `hasFoodItem` and `addFoodItem` provide menu operations.
* stores a separate `MilestoneCatalog` in `ModelManager`. `Model#getMilestoneCatalog()` exposes a
  `ReadOnlyMilestoneCatalog`, while the existing milestone operations continue to act on the catalog owned by the
  model.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* saves customer records, menu items, milestone rewards, and user preferences in separate JSON files and reads them
  back.
* is implemented by `StorageManager`, which delegates file access to `JsonAddressBookStorage`, `JsonMenuStorage`,
  `JsonMilestoneCatalogStorage`, and `JsonUserPrefsStorage` (one class per data file).

`JsonAdaptedPerson` stores name, phone, tags, score, order history and redeemed milestone reward history. Each stored order includes its item code,
the item name captured when the order was recorded, quantity, recorded time, and `scoreGained`. While redeemed milestone reward history stores milestone,
which consist of its code, reward description and the score needed to redeem the reward.
Unknown JSON properties are ignored when loading and omitted when saving. Older order records require both `itemCode` and
`itemName` before loading; missing `scoreGained` defaults to zero, while negative values are rejected.
A missing score defaults to zero.
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Adding a menu item

The `add-menu-item c/CODE n/NAME p/PRICE` command adds a `FoodItem` to the menu owned by `ModelManager`.
The menu is separate from customer records and order histories. `add-order` accepts a menu code and checks for an
exact case-sensitive match in the menu before adding the order. The command copies the matched item's current name
into the new order, alongside its code, quantity, and recorded time. `RecentOrdersPanel` displays the saved name and
points gained, so later menu edits do not change past orders. The command calculates `scoreGained` as
`floor(priceInCents × quantity / 10)` ($1 earns 10 points), saves it with the order, and adds it to the member's
score. The calculation and addition use `long` intermediates before validating the score's nine-digit limit.
`delete-order` selects an order by its history index and subtracts its saved `scoreGained`; it rejects a deletion
that would make the member's score negative. The saved value means a menu price change cannot alter past earnings.

#### Command flow

1. `AddressBookParser` routes the command to `AddMenuItemCommandParser`.
1. The parser checks the required prefixes, rejects duplicates and unexpected preamble text, and validates the code,
   name, and price. Tabs are normalized to spaces for `ArgumentTokenizer`. Unsupported single-letter prefixes and
   control characters other than tabs are rejected before tokenization.
1. The dollar price is converted to integer cents with `BigDecimal#movePointRight(2)` and `intValueExact()`.
   Prices must be positive, have at most two decimal places, and fit in an `int`; no rounding is performed.
1. `AddMenuItemCommand#execute(Model)` checks `Model#hasFoodItem` before calling `Model#addFoodItem`.
   `FoodItem#isSameFoodItem` compares case-sensitive codes, while `equals` compares all three fields.
   Reusing a code throws `CommandException`, leaving the menu unchanged.
1. After execution succeeds, `LogicManager` saves the address book, menu, and milestone catalog in its existing save
   block. It returns the command's success feedback only after all three saves succeed. `ParseException` and command
   execution failures stop before saving; `IOException` during saving is reported as `CommandException`.

#### Data and persistence

`FoodItem` is immutable and holds `code`, `name`, and `priceInCents`. `Menu` uses `UniqueFoodItemList` to enforce
unique case-sensitive codes and preserve insertion order. The three-argument `ModelManager` constructor copies a
loaded `ReadOnlyMenu`; the existing two-argument constructor initializes an empty menu for other callers.

| Class | Responsibility |
|---|---|
| `JsonAdaptedFoodItem` | Converts one item and validates its fields. JSON codes and names must be strings; cents must be an integer in the Java `int` range, then pass the positive-price constraint. |
| `JsonSerializableMenu` | Converts the `foodItems` list, rejecting missing lists, null items, invalid items, and duplicate codes. |
| `JsonMenuStorage` | Reads/writes menu JSON at a configured path. Missing files return `Optional.empty()`; read or validation errors become `DataLoadingException`. |
| `StorageManager` | Delegates `getMenuFilePath`, `readMenu`, and `saveMenu` to `JsonMenuStorage`. |

`MainApp` configures `data/menu.json` relative to the launch directory. Its `initMenu` method loads the saved menu,
uses `SampleDataUtil.getSampleMenu()` if the file is missing, or logs a warning and supplies an empty menu if loading
fails. Sample menu codes match the sample members' order histories. Loading alone leaves an invalid file untouched;
the next successfully executed command other than `help` or `exit` can overwrite it,
matching the existing address-book fallback policy.

All three domain data files are saved after every successfully executed command, including read-only commands
such as `list`, except `help` and `exit`. `LogicManager` returns results requesting help or exit before saving,
so storage failures cannot prevent either action. A save error does not roll back changes already made in memory.
The address-book file is written first, followed by
the menu and milestone catalog, so an earlier file may already be saved if a later write fails; the three writes
are not a transaction. A later successful command other than `help` or `exit` attempts all three saves again
through the same flow. Exiting does not retry domain-data saves, so unsaved in-memory changes are lost on exit.

Each JSON save serializes its contents before writing a unique temporary file in the destination directory.
Only after writing completes does `FileUtil` atomically replace the destination. A failed write or unsupported
atomic replacement leaves the existing destination intact; a failed first save does not leave an empty JSON file.
Missing parent directories are created as needed. Temporary files are removed after saving or failure where
possible; cleanup errors are logged without hiding the save result. There is no non-atomic fallback, so a file
system that cannot atomically replace the destination reports a save error. This protects individual files
against partial writes, but does not provide a transaction across files or guarantee recovery after power loss.

#### Testing

`AddMenuItemCommandTest` isolates command behavior with model stubs. `AddMenuItemCommandParserTest` covers prefix
validation, price boundaries, exact cent conversion, control characters, long inputs, and deterministic generated
inputs. `AddressBookParserTest` checks routing. `ModelManagerTest` checks menu copying and its read-only view.
The JSON adapter, serializable menu, and menu storage test classes cover invalid data and round trips.
`MainAppTest` checks startup fallback and saving/reloading across model initialization; `LogicManagerTest` covers
automatic saving and menu-save errors.

### Milestone catalog and management commands

Milestone rewards are stored in a `MilestoneCatalog` rather than in `AddressBook`. This keeps global loyalty-program
configuration separate from customer records: an address book owns people, while one milestone catalog defines the
rewards available to every customer. `ModelManager` owns both data sources and keeps the existing milestone APIs
stable by delegating them to its catalog.

| Class | Responsibility |
|---|---|
| `MilestoneCatalog` and `ReadOnlyMilestoneCatalog` | Own the sorted, unique milestone list and expose a read-only view to clients. |
| `JsonAdaptedMilestone` | Converts one milestone and validates its code, reward description, and required score. |
| `JsonSerializableMilestoneCatalog` | Converts the `milestones` list, rejecting a missing list, null entries, invalid milestones, and duplicate case-insensitive codes. |
| `JsonMilestoneCatalogStorage` | Reads and writes milestone JSON at a configured path. Missing files return `Optional.empty()`; read or validation errors become `DataLoadingException`. |
| `StorageManager` | Delegates `getMilestoneCatalogFilePath`, `readMilestoneCatalog`, and `saveMilestoneCatalog` to `JsonMilestoneCatalogStorage`. |

`MainApp` configures `data/milestones.json` relative to the launch directory. A valid file is loaded into
`ModelManager`; a missing or invalid file produces an empty catalog, with invalid file contents left untouched until
the next successfully executed command other than `help` or `exit` saves the model.
Legacy `milestones` properties in `memberlist.json` are
ignored, so the address-book file remains responsible only for customer data.

The `list-milestones`, `add-milestone`, `edit-milestone`, and `delete-milestone` commands expose catalog access and
mutation through the CLI. `ListMilestonesCommand` formats `Model#getMilestoneList()` as a numbered list, making the
indexes used by edit and delete discoverable. `AddressBookParser` routes each mutating command to its dedicated
parser. The milestone parsers share strict tokenization that normalizes tabs, rejects unsupported single-letter
prefixes, and rejects control characters other than tabs.

`AddMilestoneCommandParser` requires a reward description and required score in any order. `Milestone` generates a
UUID for its internal code when the parser creates it. The parser passes the milestone to `AddMilestoneCommand`, which
creates a replacement milestone if its code already exists. The UUID is stored with the milestone and redemption
snapshots but omitted from user-facing output.
`EditMilestoneCommandParser` requires a displayed index and at least one editable field; its
`EditMilestoneDescriptor` is defensively copied by `EditMilestoneCommand`. Codes are excluded from the descriptor
because they are stable identities. `DeleteMilestoneCommandParser` accepts only a displayed index. Required scores
are parsed as unsigned decimal digits in the positive Java `int` range, so inputs such as `+500`, `0`, and values
above `2147483647` are rejected.

Edit and delete commands resolve their one-based index against `Model#getMilestoneList()` during execution, then pass
the selected `Milestone` to the model. The catalog uses its case-insensitive code internally and maintains ascending
required-score order with code as the tie-breaker. Editing a required score can therefore change the milestone's
displayed index. None of these commands reads or modifies a person's score or reward-claim records.

The milestone model tests cover validation, sorting, uniqueness, mutation, and read-only views. Command and parser
tests cover numbered listing, empty catalogs, generated code persistence, successful mutations, defensive descriptor copying,
reordered edits, index boundaries, arbitrary prefix order, duplicate and unsupported prefixes, control characters,
and required-score boundaries.
`AddressBookParserTest` checks command routing. The JSON adapter, serializable catalog, storage, `MainApp`, and
`LogicManager` tests cover invalid data, round trips, startup fallback, and automatic saving.

### Deleting a menu item

`AddressBookParser` routes `delete-menu-item c/CODE` to `DeleteMenuItemCommandParser`. The parser requires one
`c/` prefix, rejects preamble text, repeated prefixes, invalid codes, extra arguments, and control characters other
than tabs. It preserves the code's case and constructs `DeleteMenuItemCommand`.

The command finds the exact code through `Model#getMenu()`. A missing code produces `CommandException` before
any mutation. For an existing code, the command passes the actual stored `FoodItem` to `Model#deleteFoodItem`,
which delegates to `Menu#removeFoodItem`. This matters because removal uses full item equality, not only code
identity. The existing `LogicManager` save block persists successful deletions without command-specific changes.
Customer records, order histories, and milestones are unaffected.

`DeleteMenuItemCommandTest` checks deletion, case sensitivity, failure without mutation, and unchanged customer
and milestone data using the real model. Parser tests cover syntax errors and whitespace, and `MainAppTest`
checks that deletion survives saving and reloading and that the deleted code can be reused.

### Redeemed milestone reward history

`RedeemedMilestoneRewardHistory` owns the redeemed milestone rewards belonging to one person. It returns defensive
copies when exposing the list and treats add/delete operations as immutable transformations:

| Operation | Responsibility |
|---|---|
| `addRedeemedMilestoneReward(Milestone)` | Returns a new history containing the milestone, rejecting duplicate codes. |
| `contains(Milestone)` | Checks whether a milestone with the same code has been redeemed. |
| `deleteRedeemedMilestoneReward(Milestone)` | Returns a new history without the milestone with the same code. |

`RedeemMilestoneCommand` validates the person and current milestone indexes, checks score eligibility and duplicate
redemption, then replaces the person with a copy containing the new history. `CancelRedeemCommand` validates the
person and current milestone indexes, checks that the selected milestone is present in the person's history, then
replaces the person with a copy containing the history returned by `deleteRedeemedMilestoneReward(Milestone)`.

Both commands identify the milestone through the current `MilestoneCatalog` index. Consequently, cancelling a
   historical redemption requires the current catalog to contain a milestone with the same code. Deleting the milestone
   makes that historical redemption unavailable to `cancel-redeem` by design; changing other
milestone details while preserving its code does not change its identity.

The command parsers use the shared `m/` prefix and `ParserUtil.parseIndex`, so person and milestone arguments must be
positive integers. `AddressBookParser` routes `redeem` and `cancel-redeem` to their respective parsers.

The related tests cover command success and validation, parser syntax and duplicate prefixes, full parser routing,
immutable history updates, duplicate redemption, absent milestones, and `toString` output.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete-member 5` command to delete the 5th person in the address book. The `delete-member` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete-member 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add-member n/David …​` to add a new person. The `add-member` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add-member n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete-member`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Small food stall owner in hawker centres
* Prefers using CLI over GUI
* Record rewards program for customers

**Value proposition**:
* Fast access to member's contact, order history and membership status
* CLI optimised for faster retrieval and updates of customer information than GUI


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​             | I want to …​                                            | So that I can…​                                                                                  |
|----------|---------------------|---------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| `* * *`  | Food stall owner    | Create a new member                                     | I can enroll a returning customer in the rewards program                                          |
| `* * *`  | Food stall owner    | Store member information persistently                   | I do not lose customer records when the application closes or crashes                            |
| `* * *`  | Food stall owner    | View all members                                        | I can review and manage my membership records                                                    |
| `* * *`  | Food stall owner    | Update a member’s membership points                     | I can keep the member’s progress in the rewards program accurate                                 |
| `* * *`  | Food stall owner    | View a member’s details                                 | I can check the member’s membership points, purchase history, and reward eligibility             |
| `* * *`  | Food stall owner    | Delete a member                                         | I can remove duplicate, invalid, or obsolete members                                             |
| `* * *`  | Food stall owner    | Search a member by name or phone number                 | I can quickly retrieve the correct member while serving the customer                             |
| `* * *`  | Food stall owner    | Record an order for a member                            | I can update the member’s membership points and maintain an accurate purchase history            |
| `* * *`  | Food stall owner    | Delete an order from a member's order history           | I can correct an order that was recorded by mistake                                               |
| `* *`    | Food stall owner    | Create a milestone reward                               | I can encourage customers to return and earn rewards                                             |
| `* *`    | Food stall owner    | Edit a milestone reward                                 | I can change its membership points requirement or prize                                           |
| `* *`    | Food stall owner    | Delete a milestone reward                               | Customers are not offered rewards that are no longer available                                   |
| `* *`    | Food stall owner    | View all milestone rewards                              | I can review the rewards currently available to members                                          |
| `* *`    | Food stall owner    | Mark a milestone reward as claimed by a member          | I can prevent the same reward from being issued to that member twice                             |
| `* *`    | Food stall owner    | Cancel a member's redeemed milestone reward             | I can correct a redemption recorded by mistake                                                   |
| `* *`    | A new user          | Clear all sample data                                   | I can begin using the application with my actual business data                                   |
| `* *`    | A new user          | View a list of available commands and their usage       | I can learn how to use the application quickly                                                   |
| `* *`    | Food stall owner    | Configure the number of membership points awarded per dollar spent | I can adjust the rewards program to suit my business                                    |
| `* *`    | Food stall owner    | Edit a menu item’s details and price                    | I can ensure orders and revenue calculations use accurate information                            |
| `* *`    | Food stall owner    | Add a menu item                                         | I can record orders containing that item and its price                                           |
| `* *`    | Food stall owner    | View all menu items                                     | I can check the items and prices currently recorded in the application                           |
| `* *`    | Food stall owner    | Delete a menu item                                      | Unavailable or discontinued items cannot be added to new orders                                  |
| `*`      | An experienced user | Create aliases for frequently used commands             | Allow for faster typing to handle more customers                                                 |
| `*`      | An experienced user | Undo my most recent reversible command                  | I can recover quickly from an input mistake                                                      |
| `*`      | Food stall owner    | Sort menu items by purchase frequency or total revenue  | I can identify popular and high-earning menu items to make informed business decisions           |
| `*`      | Food stall owner    | Add a pre-order for a customer                          | I can record an order for later collection                                                       |
| `*`      | Food stall owner    | View all pre-orders                                     | I can keep track of the orders that I need to prepare                                            |
| `*`      | Food stall owner    | Edit the pre-order                                      | I can correct the mistakes or change the details of the pre-order                                |
| `*`      | Food stall owner    | Delete the pre-order                                    | I can delete the pre-order if the customer no longer wants or I cannot provide what he/she wants |
| `*`      | Food stall owner    | Mark the pre-order as completed                         | I can distinguish the completed orders from pending ones                                         |

### Use cases

(For all use cases below, the **System** is the `App` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Search for a member**

**MSS**

1.  User requests to search for members using name keywords or a complete phone entry.
2.  App shows a numbered list of matching members.

    Use case ends.

**Extensions**

* 1a. No member matches the given search term.

    * 1a1. App shows an empty list and informs the user that no members were found.

      Use case ends.
    * 2a3. App shows the selected member's details.

      Use case ends.

**Use case: Add a new member**

**MSS**

1.  User requests to add a new member, providing the member's phone number and name.
2.  App adds the member and shows a confirmation.

    Use case ends.

**Extensions**

* 1a. The name is invalid, or the supplied phone entry is blank or contains no decimal digits.

    * 1a1. App shows an error message.

      Use case ends.

* 1b. The given phone number already exists as a member.

    * 1b1. App shows an error message identifying the existing member.

      Use case ends.

**Use case: Add a menu item**

**MSS**

1. User requests to add a menu item, providing its code, name, and price.
2. App adds the item, saves the data, and shows a confirmation.

   Use case ends.

**Extensions**

* 1a. Required details are missing or invalid, or the code already exists.

    * 1a1. App shows an error and leaves the menu unchanged.

      Use case resumes at step 1.

* 2a. Saving fails after the item has been added in memory.

    * 2a1. App shows a save error. The item remains in memory.
    * 2a2. User resolves the file-access problem and requests another command, such as `list`,
      other than `help` or `exit`.
    * 2a3. App executes that command and attempts to save all three data files again.

      Use case ends if saving succeeds; otherwise, extension 2a repeats.

**Use case: Log an order for a member**

**MSS**

1.  User requests to log an order for a member, specifying the member's index in the displayed list, menu item code, and quantity.
2.  App records the current menu item name and calculated points earned, adds the order to that member's order
    history, increases the member's score, and shows a confirmation.

    Use case ends.

**Extensions**

* 1a. The index is invalid or does not refer to a member in the displayed list.

    * 1a1. App shows an error message and does not add an order.

      Use case ends.

* 1b. The item code is blank or contains characters other than letters and digits.

    * 1b1. App shows an error message and does not add an order.

      Use case ends.

* 1c. The item code is not in the menu.

    * 1c1. App shows an error message and does not add an order.

      Use case ends.

* 1d. The quantity is not a positive whole number.

    * 1d1. App shows an error message and does not add an order.

      Use case ends.

* 1e. The earned points would increase the member's score beyond `999999999`.

    * 1e1. App shows an error message and does not add an order or change the score.

      Use case ends.

**Use case: Delete an order from a member's order history**

**MSS**

1.  User requests to delete an order, specifying the member's index in the displayed list and the order's index in
    that member's order history.
2.  App deletes the order, subtracts its saved points earned from the member's score, and shows a confirmation.

    Use case ends.

**Extensions**

* 1a. The member index is invalid or does not refer to a member in the displayed list.

    * 1a1. App shows an error message and does not delete an order.

      Use case ends.

* 1b. The order index is invalid or does not refer to an order in that member's order history.

    * 1b1. App shows an error message and does not delete an order.

      Use case ends.

* 1c. Subtracting the order's saved points would make the member's score negative.

    * 1c1. App shows an error message and does not delete the order or change the score.

      Use case ends.

**Use case: Update membership points for a member**

**MSS**

1.  User requests to search for a member by phone number.
2.  App shows the member's details, including their current membership points.
3.  User requests to update the member's membership points, specifying the points change.
4.  App applies the change and shows the member's previous and new membership points.

    Use case ends.

**Extensions**

* 1a. No member matches the given phone number.

    * 1a1. App shows an error message.

      Use case ends.

* 3a. The given membership points change is invalid in format or exceeds the allowed range.

    * 3a1. App shows an error message.

      Use case resumes at step 3.

* 3b. Applying the membership points change would bring the member's membership points below zero.

    * 3b1. App shows an error message and does not apply the change.

      Use case resumes at step 3.

**Use case: Redeem a milestone reward for a member**

**MSS**

1. User requests to redeem a milestone reward, specifying the member's index and current milestone index.
2. App verifies that the member has sufficient membership points and has not redeemed the milestone before.
3. App adds the milestone to the member's redeemed milestone reward history and shows a confirmation.

    Use case ends.

**Extensions**

* 1a. The member or milestone index is invalid.

    * 1a1. App shows an error message and does not change the member.

      Use case ends.

* 2a. The member has insufficient membership points or has already redeemed the milestone.

    * 2a1. App shows an error message and does not change the member.

      Use case ends.

**Use case: Cancel a redeemed milestone reward for a member**

**MSS**

1. User requests to cancel a redeemed milestone reward, specifying the member's index and current milestone index.
2. App verifies that the current milestone has the same code as a milestone in the member's redemption history.
3. App removes that milestone from the member's redeemed milestone reward history and shows a confirmation.

    Use case ends.

**Extensions**

* 1a. The member or current milestone index is invalid.

    * 1a1. App shows an error message and does not change the member.

      Use case ends.

* 2a. The selected milestone was not redeemed by the member.

    * 2a1. App shows `Unable to unredeem because it was not redeemed.` and does not change the member.

      Use case ends.

* 2b. The milestone was deleted from the catalog after redemption.

    * 2b1. The historical redemption cannot be selected by `cancel-redeem`, because the command uses the current
      milestone catalog index by design.

      Use case ends.

**Use case: Delete a member**

**MSS**

1.  User requests to search for a member by phone number.
2.  App shows the member's details.
3.  User requests to delete the member.
4.  App deletes the member and shows a confirmation.

    Use case ends.

**Extensions**

* 1a. No member matches the given phone number.

    * 1a1. App shows an error message.

      Use case ends.

### Non-Functional Requirements

1.  Should work on Windows, Linux, and macOS computers with Java `25` or above installed.
2.  Should support up to 1000 members and their associated records without noticeable sluggishness during typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Should be portable and work without requiring installation. It should be distributed as a single JAR file, or as a single ZIP file if additional files are required.
5.  Should operate as a single-user application without depending on a remote server or a database management system.
6.  Should store member, order, and membership data locally in a human-editable text file.
7.  Should save changes made by successful data-modifying commands automatically. A failed command or save operation should not corrupt previously saved data.
8.  Common commands, such as finding a member, recording an order, and updating membership points, should complete without noticeable delay under typical usage.
9.  Invalid commands should provide clear error messages that identify the problem without modifying existing data.

### Glossary

* **Customer**: Anyone who buys food from the stall. A customer is not tracked by Ratatouille unless they sign up as a *member*.
* **Hawker centres**: Open-air food complexes in Singapore housing many small, independently run food stalls.
* **Member**: A customer enrolled in the stall's *rewards program*, uniquely identified by their saved *phone entry*.
* **Membership points**: A non-negative whole number representing a member's standing in the rewards program, used to determine eligibility for *milestone rewards*.
* **Rewards program**: The stall's loyalty scheme, in which members accumulate *membership points* and redeem them for *milestone rewards*.
* **Menu item**: A dish or drink sold by the stall, recorded with a unique case-sensitive code, name, and price.
* **Milestone reward**: A prize a member becomes eligible for upon reaching a set number of membership points. Each milestone reward can be claimed at most once per member.
* **Order**: A record of a menu item's code and name, quantity, time recorded, and points earned, stored in a
  member's order history.
* **Order history**: All orders recorded for a member. Also referred to as *purchase history*.
* **Pre-order**: An order recorded in advance for later collection. It is *pending* until marked *completed*.
* **Reversible command**: A command that changes stored data and can therefore be undone (e.g. `add-order`). Commands that only read or display data (e.g. `find`) or close the app (`exit`) are not reversible.
* **Sample data**: Placeholder members loaded when Ratatouille is launched for the first time, so new users can try out commands.
* **Phone entry**: A nonblank contact value identifying a member. Recognized Singapore formats are saved as eight
  digits starting with 6, 8 or 9. New entries must contain a decimal digit; other digit-containing formats receive
  a nonblocking warning. Legacy digitless notes can still be loaded and corrected.
* **Stall owner**: The main user of Ratatouille, who runs a food stall at a hawker centre. Also covers staff operating the app on the owner's behalf.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Copy the JAR file into a new, writable folder. Start the app from that folder using
   `java -jar addressbook.jar`. Double-clicking the JAR file may not work on all operating systems.
   Expected: The GUI opens with the sample members, menu items, and milestone rewards because the
   corresponding data files do not yet exist.

1. Resize and move the window, then close the app. Start it again from the same folder.
   Expected: The most recent window size and position are restored.

1. Type `help` and press Enter.
   Expected: The help window opens with a link to the User Guide.

1. Type `exit` and press Enter.
   Expected: The app closes after displaying the exit acknowledgement.

1. Repeat the launch test with the JAR in a write-protected folder, then enter `list`.<br>
   Expected: The command reports a file-permission error because the app cannot save its data. Use a writable
   folder for normal operation.

### Adding and editing phone numbers

1. Prerequisites: Launch in a fresh folder with sample data. The numbers below must not be registered.

1. Test case: `add-member n/PhoneCheck p/61234567`, then `add-member n/PhoneCheck p/81234567`.<br>
   Expected: Both people are added despite sharing a name.

1. Test case: `add-member n/OtherCheck p/61234567`.<br>
   Expected: An error identifies the existing PhoneCheck person. No person is added.

1. Test case: `find 81234567`, then `edit-member 1 p/61234567`.<br>
   Expected: An error identifies the existing person even though that person is hidden by the search.
   The displayed person's number remains `81234567`.

1. Test case: `edit-member 1 p/81234567`, then `find 81234567`, then `edit-member 1 p/91234567`.<br>
   Expected: Keeping the current number succeeds and restores the full list. Search again to select the intended
   person before changing their number. Changing to an unused valid number also succeeds.

1. Test case: `find +65 9123 4567`, then `edit-member 1 p/(+65)9123-4567`.<br>
   Expected: Search finds the member with `91234567`. The edit succeeds without a warning and keeps that number.

1. Test case: `add-member n/DuplicateCheck p/65 9123 4567`.<br>
   Expected: A duplicate error identifies the existing member; the formatted alias does not create a new member.

1. Test cases: `add-member n/OverseasCheck p/+86 138119828111`, `add-member n/SpecialCheck p/（86）12345678`,
   and `add-member n/LegacyCheck p/911`.<br>
   Expected: All three succeed with the same nonblocking warning, preserving each phone entry.

1. Test case: `find p/（86）12345678`, then `edit-member 1 p/12345678`.<br>
   Expected: Search selects SpecialCheck. The edit saves `12345678` and shows the shared warning.

1. Test cases: `add-member n/BlankCheck p/` and `edit-member 1 p/`.<br>
   Expected: Both report that phone numbers must not be blank without changing any member.

1. Test case: `add-member n/NoteCheck p/91234567 (HP)`.<br>
   Expected: The entry is saved unchanged. In addition to the format warning, a possible duplicate warning
   identifies the PhoneCheck member with `91234567`. Neither member's points or history are merged.

1. Test case: `find Nobody`, then `add-member n/OverseasAlias p/+86138119828111`.<br>
   Expected: The compact entry is saved and a possible duplicate warning identifies OverseasCheck, even though
   the search hid that member. The original overseas entry remains unchanged.

1. Test cases: `add-member n/NoPhone p/Not available` and `edit-member 1 p/---`.<br>
   Expected: Both report `Phone numbers must contain at least one digit`, with no change to any member.

1. Close and relaunch the app, then run `find PhoneCheck`.<br>
   Expected: Both people remain, with phone numbers `61234567` and `91234567`.
   `find p/+86 138119828111` and `find p/12345678` still find the saved nonstandard entries.

1. In a disposable copy of the saved member file, give two distinct members the phone entries `91234567`
   and `6591234567`, then launch against that copy.<br>
   Expected: Startup stops with a member-file recovery message. The file is unchanged; no members or histories
   are silently merged. Restore the original copy before continuing.

### Deleting a person

1. Prerequisites: Use `list` to display multiple members.

1. Test case: `delete-member 1`<br>
   Expected: The first displayed member is removed. The success message includes the deleted member's details.

1. Test case: `find Alex`, followed by `delete-member 1`.<br>
   Expected: The matching member is removed from the filtered list and from the saved member data.

1. Test cases: `delete-member 0`, `delete-member`, `delete-member x`, and `delete-member 999`.<br>
   Expected: An input or invalid-index error is shown. No member or related data is deleted.

1. Close and relaunch the app, then run `find` using the deleted member's name.<br>
   Expected: The deleted member is still absent. Their order history and redeemed-reward history are no longer
   available through the member list.

### Recording an order

1. Prerequisites: Use `list` to display at least one member. The sample menu includes `1` (Chicken Rice),
   `3A` (Nasi Lemak), and `3B` (Nasi Lemak + Chicken).

1. Test case: `add-order 1 o/1 q/2`<br>
   Expected: An order for two Chicken Rice items is added to the first displayed member. Their score increases
   by 90 points, because Chicken Rice costs $4.50 and earns 10 points per dollar.

1. Test case: `add-order 1 o/3B q/1`<br>
   Expected: The order is added and increases the member's score by 65 points. The member's profile shows the
   saved item name, quantity, and score gained.

1. Close and relaunch the app, then inspect the same member.
   Expected: Both orders and the updated score remain.

1. Test cases: `add-order 1 o/Chicken-Rice q/2`, `add-order 1 o/UNKNOWN q/2`, and `add-order 1 o/1 q/0`.<br>
   Expected: The relevant input, menu-code, or quantity error is shown. No order or score is changed.

1. Change or delete a menu item that has already been ordered, then inspect the member's order history.
   Expected: The existing order retains the item details and score that were saved when the order was recorded.

### Deleting an order

1. Prerequisites: Use `list` to display a member with at least two orders. If needed, enter
   `add-order 1 o/1 q/2` and `add-order 1 o/3B q/1` first.

1. Test case: `delete-order 1 o/1`<br>
   Expected: The newest displayed order is deleted from the first member's history. The member's score decreases
   by that order's saved score, and the older order remains. Order indexes are shown newest-first.

1. Close and relaunch the app.
   Expected: The deleted order remains absent and the score remains reduced.

1. Test cases: `delete-order 1 o/999` and `delete-order 999 o/1`.<br>
   Expected: The relevant order-index or member-index error is shown. No data is changed.

1. Test case: In a fresh test folder, enter `add-member n/OrderCheck p/88888888`, `find OrderCheck`,
   `add-order 1 o/1 q/1`, `update-score 1 s/-45`, then `delete-order 1 o/1`.<br>
   Expected: The command is rejected with `Deleting this order would make the score negative.`

1. Other incorrect commands to try: `delete-order`, `delete-order 1`, `delete-order 1 o/0`,
   and `delete-order 1 o/x`.<br>
   Expected: The command-format error is shown and no data is changed.

### Adding a menu item

Use a disposable, writable app home folder. If `data/menu.json` is absent, the sample menu loads.

1. Test case: `add-menu-item c/12A n/Dessert p/3.50`<br>
   Expected: The menu item is added at $3.50. `data/menu.json` stores the price as `350` cents.
1. Close and reopen the app, then enter `add-menu-item c/12A n/Different Dish p/5.00`.<br>
   Expected: The duplicate-code error is shown. The original item's name and price remain unchanged.
1. Test case: `add-menu-item p/0.01 c/12a n/Coffee`<br>
   Expected: The item is added at one cent. The lowercase code is distinct from `12A`.
1. Test case: `add-menu-item c/13 n/Dish`<br>
   Expected: The usage message is shown for the missing price. No item is added.
1. Test cases: `add-menu-item c/13 n/Dish p/0`, `add-menu-item c/13 n/Dish p/4.501`,
   `add-menu-item c/13 n/Dish p/21474836.48`.<br>
   Expected: The price constraint is shown, no item is added, and saved files are unchanged.
1. Test case: `add-menu-item c/13 n/Dish n/Other p/4.50`<br>
   Expected: A duplicate-prefix error is shown and the menu remains unchanged.
1. Test case: `add-menu-item c/13 n/Dish x/extra p/4.50`<br>
   Expected: The command-format error is shown and the menu remains unchanged.
1. Test case: `add-order 1 o/12A q/1`.<br>
   Expected: The new item can be ordered immediately. After restarting, both the menu item and the recorded order
   remain saved.

### Deleting a menu item

1. In a disposable app home folder, add `add-menu-item c/DEL01 n/Test Dish p/4.50` and
   `add-menu-item c/del01 n/Other Dish p/5.00`.
1. Run `delete-menu-item c/DEL01`.<br>
   Expected: The `DEL01` item is deleted. The case-distinct `del01` item remains in `data/menu.json`.
1. Close and reopen the app, then run `delete-menu-item c/DEL01` again.<br>
   Expected: The item-not-found error is shown. The remaining menu is unchanged.
1. Run `delete-menu-item c/del01`.<br>
   Expected: The remaining test item is deleted. The sample menu items remain unchanged.
1. Try `delete-menu-item`, `delete-menu-item c/`, and `delete-menu-item c/DEL01 c/DEL02`.<br>
   Expected: An input error is shown, and no data is changed or saved.
1. Record an order using a menu item, then delete that menu item and inspect the member's history.<br>
   Expected: New orders using the deleted code are rejected, but the existing order remains in the history with
   its saved item details and score.

### Managing milestone rewards

Use a new disposable app home folder, with no `data/memberlist.json`. Before starting the app, create
`data/milestones.json` containing `{"milestones": []}` so that the catalog begins empty. The milestone list is
sorted by required score; equal-score order is arbitrary.

1. Test case: `list-milestones` before adding a milestone.<br>
   Expected: `No milestones found.`
1. Test case: `add-milestone r/Free drink s/100`.<br>
   Expected: The milestone is added and `data/milestones.json` contains it with a generated internal code.
1. Test case: `add-milestone s/200 r/$10 voucher`.<br>
   Expected: The milestone is added even though the prefixes are in a different order.
1. Test case: `list-milestones`.<br>
   Expected: Both milestones are shown with numbered indexes and no internal codes. Free drink is listed first
   because it requires fewer points.
1. Test case: `redeem 1 m/1`.<br>
   Expected: The first sample member, who has 100 points, redeems Free drink successfully. The reward appears in
   the member's profile and the member's score does not change.
1. Repeat `redeem 1 m/1`.<br>
   Expected: The command reports that the milestone has already been redeemed.
1. Test case: `redeem 1 m/2`.<br>
   Expected: The command reports insufficient score because the member has fewer than 200 points.
1. Test case: `cancel-redeem 1 m/1` after the successful redemption.<br>
   Expected: The redemption is removed from the member's profile without changing the member's score.
1. Test `cancel-redeem 1 m/1` again.<br>
   Expected: The command reports that the milestone was not redeemed.
1. Test cases: `add-milestone r/Reward s/+500`, `add-milestone r/Reward s/0`, and
   `add-milestone r/Reward s/2147483648`.<br>
   Expected: The required-score constraint is shown and no milestone is added.
1. Test case: `edit-milestone 1 r/Free drink and snack s/1500`.<br>
   Expected: The first milestone retains its generated code, both editable fields change, and it moves after the
   $10 voucher in the sorted list.
1. Test case: `edit-milestone 1 c/NEWCODE`.<br>
   Expected: The command-format error is shown because internal codes cannot be edited.
1. Test case: `delete-milestone 1`.<br>
   Expected: The milestone currently shown at index 1 is deleted without changing member scores.
1. Test cases: `redeem 1 m/999`, `edit-milestone 999 r/Reward`, and `delete-milestone 999`.<br>
   Expected: The relevant milestone-index error is shown and the catalog remains unchanged.
1. Close and reopen the app.<br>
   Expected: Added and edited milestones remain, deleted milestones remain absent, and redemption changes remain
   persisted.

### Saving data

1. Use a new writable folder and start the app without any data files.<br>
   Expected: Sample members, menu items, and milestones load.

1. Add a member, add a menu item, and add a milestone. Close and relaunch the app.<br>
   Expected: All three changes remain. The data is stored in `data/memberlist.json`, `data/menu.json`, and
   `data/milestones.json` respectively.

1. With the app closed, move `data/menu.json` outside the `data` folder and restart the app.<br>
   Expected: The sample menu loads. After any successful command, the current menu is saved to a new
   `data/menu.json`.

1. Replace `data/menu.json` with malformed JSON or `{"foodItems":[null]}`, then restart the app.<br>
   Expected: A loading warning is logged and the app starts with an empty menu. The invalid file is not replaced
   during startup. After a successful command, the current empty menu can be saved over it.

1. Replace `data/milestones.json` with malformed JSON, then restart the app.<br>
   Expected: A loading warning is logged and the app starts with an empty milestone catalog. The invalid file is
   not replaced during startup.

1. Replace `data/memberlist.json` with malformed or otherwise invalid member data, then restart the app.<br>
   Expected: Startup stops with a member-data recovery message to prevent the invalid file from being overwritten.

1. Resize and move the app window, close it, and relaunch it.<br>
   Expected: The window preferences remain in `preferences.json` and are restored.

1. Start the app from a write-protected folder and execute a data-changing command.<br>
   Expected: A file-permission error is shown. Use a writable folder for normal operation.
