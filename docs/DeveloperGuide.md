---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

Ratatouille is built on [AddressBook-Level3 (AB3)](https://github.com/se-edu/addressbook-level3) by the [SE-EDU initiative](https://se-education.org). We adapted its codebase, application architecture, and developer documentation for Ratatouille.

The project uses the following third-party libraries and tools:

* [JavaFX](https://openjfx.io/) for the graphical user interface.
* [Jackson](https://github.com/FasterXML/jackson) for reading and writing JSON data.
* [JUnit 5](https://docs.junit.org/5.14.4/) for automated testing.
* [PlantUML](https://plantuml.com/) for generating UML diagrams.

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

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-F12-3/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-F12-3/tp/blob/master/src/main/java/seedu/address/MainApp.java)) manages the application's lifecycle:

* At startup, it loads stored data and user preferences, initializes the components, and connects them.
* At shutdown, it saves user preferences, including GUI settings.

The application's functionality is organized into four components:

* [**`UI`**](#ui-component): Displays application data and accepts user commands.
* [**`Logic`**](#logic-component): Parses and executes commands, and coordinates saving application data.
* [**`Model`**](#model-component): Holds member records, menu items, the milestone catalog, and user preferences in memory.
* [**`Storage`**](#storage-component): Reads and writes application data and user preferences in JSON files.

[**`Commons`**](#common-classes) provides shared utilities and supporting classes.

**How the architecture components interact with each other**

The sequence diagram below illustrates a successful execution of `delete-menu-item c/CR01`, assuming that the menu contains an item with code `CR01` and saving succeeds. Interactions within each component are abstracted away. For clarity, only menu persistence is shown; saving member data and the milestone catalog is omitted.

<img src="images/ArchitectureSequenceDiagram.png" alt="Architecture sequence diagram" width="740" style="max-width: 100%; height: auto;" />

Each of the four main components exposes an API through an interface: `Ui`, `Logic`, `Model`, or `Storage`. The corresponding implementation classes are `UiManager`, `LogicManager`, `ModelManager`, and `StorageManager`.

The partial class diagram below illustrates this arrangement. `LogicManager` implements `Logic` and holds references to the `Model` and `Storage` interfaces, reducing its dependence on their concrete implementations.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-F12-3/tp/blob/master/src/main/java/seedu/address/ui/Ui.java).

![Structure of the UI Component](images/UiClassDiagram.png)

The UI uses JavaFX. `UiManager` creates and displays `MainWindow`, which contains the command box, result display, member list, member profile, menu panel, and status bar. `MainWindow` also manages a separate help window.

`ProfilePanel` groups the selected member's details, milestone progress, and recent orders through `PersonDetailsPanel`, `MilestoneTimeline`, and `RecentOrdersPanel`.

FXML-backed UI parts extend the abstract `UiPart` class, with layout files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-F12-3/tp/blob/master/src/main/resources/view/MainWindow.fxml) defines the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-F12-3/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java). `PersonDetailsPanel` and `MilestoneTimeline` are constructed in Java and extend `VBox` and `ScrollPane`, respectively.

The `UI` component:

* passes user commands to `Logic` and displays command feedback.
* obtains observable member and menu lists through `Logic`, allowing their list views to update when the lists change.
* refreshes the member profile after command execution, including when a command fails.
* uses model types to display member details, menu items, milestones, and recent orders.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-F12-3/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

`LogicManager` coordinates command parsing, execution, and persistence. In the partial class diagram below, `XYZCommand` represents a concrete command, and Parser Classes groups the parsing classes; `LogicManager` holds one `AddressBookParser` instance.

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates parsing and execution for a successful `delete-member 1` command.

![Interactions Inside the Logic Component for the `delete-member 1` Command](images/DeleteSequenceDiagram.png)

`personToDelete` is the `Person` retrieved at the displayed index. The diagram omits this lookup, clearing the selection when the deleted member is selected, and the persistence steps below; its parser destroy marker does not represent an explicit operation in the code.

How the `Logic` component works:

1. **Parse:** `AddressBookParser` identifies the command word and delegates argument parsing to a matching parser, or directly creates commands such as `help`, `exit`, and `list`.
1. **Execute:** `LogicManager` calls `Command.execute(model)`, which reads or updates `Model` and returns a `CommandResult`.
1. **Save:** Unless the result requests help or exit, `LogicManager` saves member data, the menu, and the milestone catalog through `Storage`, even if the command did not change them.
1. **Return:** After any required saves succeed, the result is returned to the UI. Parsing failures raise `ParseException`; execution and storage I/O failures are reported through `CommandException`. A save failure does not roll back model changes or earlier successful file writes.

The following partial diagram summarizes the parsing classes. Individual parsers use only the helpers they need.

<img src="images/ParserClasses.png" alt="Parser class diagram" width="673" style="max-width: 100%; height: auto;" />

Concrete command parsers implement `Parser<T extends Command>` and construct their corresponding commands. For prefix-based arguments, `ArgumentTokenizer` uses `Prefix` constants from `CliSyntax` to produce an `ArgumentMultimap`. `ParserUtil` provides shared conversions and validation, such as parsing displayed indices.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-F12-3/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" alt="Model class diagram" width="802" style="max-width: 100%; height: auto;" />


`ModelManager` implements `Model` and holds the application's data in memory, independently of the UI, Logic, and Storage components. The partial diagram above is supplemented by the menu, milestone, and member-history structures described below.

* **Members:** `AddressBook` contains a `UniquePersonList`. Each `Person` holds a name, phone entry, tags, score, order history, and redeemed milestone reward history. Both histories are immutable; updates create replacement histories.
* **Menu and milestones:** Separate `Menu` and `MilestoneCatalog` objects store food items and reward definitions, exposed through `ReadOnlyMenu` and `ReadOnlyMilestoneCatalog` with unmodifiable observable lists. The menu and catalog are shared across members, while order and redeemed reward histories belong to individual members.
* **Display state:** A `FilteredList<Person>` provides an unmodifiable observable view of members matching the current filter. The selected member is stored separately as a snapshot; replacing a member does not automatically update that selection. Commands that display the updated member explicitly call `selectPerson(updatedPerson)`.
* **Preferences:** `UserPrefs` stores GUI settings and is exposed through `ReadOnlyUserPrefs`.

**Member identity and phone matching**

`Person.isSamePerson` identifies members by equal saved phone entries, allowing different members to share a name. `UniquePersonList` uses this identity check to reject duplicate members when adding or replacing entries. `Phone` normalizes recognized Singapore formats to eight digits; other nonblank entries retain their spelling apart from surrounding whitespace and use exact, case-sensitive equality. New phone entries supplied through commands must contain a decimal digit, while legacy digitless entries remain readable for lookup and correction.

`findPersonByPhone` searches the full member list for an exact phone match. `findPersonsWithSimilarPhone` separately finds different entries that may contain a common number using `Phone.hasPossibleNumberInCommon`. These potential matches are advisory: they do not establish duplicate identity or change the filter or selection.

<div markdown="span" class="alert alert-info">:information_source: **Note:** The diagram below shows an alternative tag design, not the current implementation. It centralizes tags in `AddressBook`; currently, each `Person` holds its own set of tags.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-F12-3/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" alt="Storage class diagram" width="1422" style="max-width: 100%; height: auto;" />

`StorageManager` implements `Storage` and delegates JSON file access to four classes:

* `JsonAddressBookStorage`: Member records, including scores, order histories, and redeemed reward histories.
* `JsonMenuStorage`: Menu items.
* `JsonMilestoneCatalogStorage`: Milestone reward definitions.
* `JsonUserPrefsStorage`: User preferences.

The domain storage classes use JSON adapters to convert between stored data and model objects, validating fields during loading. Orders retain their recorded item name and points earned, while redeemed rewards retain milestone details. Missing member scores and order `scoreGained` values default to zero; unknown JSON properties are ignored and are not preserved on saving.

Missing files return `Optional.empty()`, while read or validation failures are reported through `DataLoadingException`. Writes use `JsonUtil` and `FileUtil` to atomically replace each destination file after writing a temporary file; failures are reported through `IOException`. This protects individual files from partial writes, but saving multiple files is not a transaction.

### Common classes

The `seedu.address.commons` package provides shared support classes: `LogsCenter` configures logging, `GuiSettings` represents window settings, and `Index` converts between one-based displayed indices and zero-based indices. Utility classes include `JsonUtil` for JSON conversion, `FileUtil` for file access, and validation helpers such as `AppUtil` and `CollectionUtil`. Shared exceptions include `DataLoadingException` and `IllegalValueException`.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section explains selected implementation details, focusing on data identity, historical records, score updates, and persistence.

### Menu item management

The class diagram in [Model component](#model-component) shows how the shared menu relates to member records and their histories.

The menu is shared by all members and stored separately from their order histories. Each immutable `FoodItem` contains a code, name, and price in integer cents. `Menu` uses `UniqueFoodItemList` to enforce unique, case-sensitive codes and preserve insertion order. Prices are converted exactly from decimal input to integer cents to avoid rounding errors; they must be positive, have at most two decimal places, and fit in a Java `int`.

`AddressBookParser` delegates menu commands to their dedicated parsers, which validate arguments before constructing commands. For `add-menu-item c/CODE n/NAME p/PRICE`, `AddMenuItemCommand` checks for an existing code before adding the item through the model. A duplicate code causes a `CommandException` without changing the menu.

For `delete-menu-item c/CODE`, `DeleteMenuItemCommand` resolves the case-sensitive code to the stored `FoodItem` before passing it to `Model#deleteFoodItem`. This is necessary because removal compares the full item, whereas lookup identifies it by code. A missing code is rejected before mutation. Deleting a menu item leaves existing orders unchanged because each order stores its own historical item details.

Tests cover duplicate and case-sensitive codes, exact price conversion, invalid arguments, and deletion without changes to member or milestone data.

### Order recording and score updates

`AddOrderCommand` resolves the supplied menu code using an exact, case-sensitive match. The new `Order` stores the item's current name and code, quantity, recorded time, and earned points (`scoreGained`). Saving these values with the order preserves its historical details when the menu is later edited or an item is deleted.

Earned points are calculated as `floor(priceInCents × quantity / 10)`, equivalent to 10 points per dollar. The calculation and addition to the member's score use `long` intermediates. The command rejects an order if its earned points or the resulting member score exceeds `999999999`, then replaces the member with a copy containing the updated score and order history.

`DeleteOrderCommand` selects the member from the filtered member list and converts the displayed newest-first order index to the oldest-first index used by `OrderHistory`. It subtracts the selected order's saved `scoreGained`, rejecting the deletion if the resulting score would be negative. The order removal and score change are applied together by replacing the member.

**Example:** An order for two items priced at $4.80 each earns 96 points. Changing the menu price to $5.00 does not change that order's saved earnings. Deleting the old order subtracts 96 points, provided the member's current score is at least 96.

Tests cover score reversal, rejection of deletions that would produce negative scores, and selection of the correct member from a filtered list.

### Milestone catalog management

See the class diagram in [Model component](#model-component) for the catalog structure and its relationship to members' redemption histories.

`MilestoneCatalog` stores the rewards available to all members, separately from the member records in `AddressBook`. `ModelManager` delegates catalog operations to it and exposes an unmodifiable observable list through `ReadOnlyMilestoneCatalog`.

Each milestone has an internal, nonblank code that is persisted but omitted from user-facing output. `add-milestone` generates this code as a UUID, while sample and loaded milestones may use other nonblank codes. Codes are compared case-insensitively and remain unchanged when a milestone is edited, so identity does not depend on its reward description, required score, or displayed index. The catalog sorts milestones by ascending required score, using code as the tie-breaker.

`list-milestones` displays the numbered catalog. Add commands require a reward description and a positive required score no greater than `999999999`. Edit commands require a displayed index and at least one editable field; delete commands require a displayed index. Edit and delete resolve their index against the current catalog during execution. Editing a required score can therefore change a milestone's displayed index. Catalog changes do not modify members' scores or existing redemption histories.

Tests cover stable codes, sorting after edits, unique identities, index boundaries, and validation of required scores and command arguments.

### Milestone redemption and cancellation

Each member owns a `RedeemedMilestoneRewardHistory` containing snapshots of redeemed milestones. The history exposes defensive list copies and returns a new history when a reward is added or removed. Duplicate detection and removal use milestone codes rather than descriptions or required scores.

`RedeemMilestoneCommand` resolves the member from the filtered member list and the milestone from the current catalog. It checks score eligibility and rejects duplicate redemption before replacing the member with a copy containing the updated history. Redemption checks the score against the milestone requirement without deducting points.

`CancelRedeemCommand` resolves the same two indexes, checks that the milestone code exists in the member's history, and replaces the member with a copy containing the history without that reward. Cancellation also leaves the member's score unchanged. Both commands select the updated member and reset the member filter to show all members.

**Example:** A member redeems a reward requiring 100 points. Editing that catalog entry's description or required score preserves its code, so the existing redemption is still recognised and can be cancelled using the entry's current index. The history retains the details recorded at redemption. Deleting the catalog entry prevents cancellation through `cancel-redeem`, because the command requires a current catalog entry with the same code.

Tests cover score eligibility, duplicate redemption, cancellation of absent rewards, invalid indexes, and immutable history updates.

### Data persistence and save failures

The class diagram in [Storage component](#storage-component) shows the JSON storage classes and adapters used for member, menu, and milestone data.

Menu and milestone data follow the same storage structure: JSON adapters validate individual entries, serializable containers validate each collection and its unique codes, and dedicated JSON storage classes read and write the files through `StorageManager`.

`MainApp` loads `data/menu.json` and `data/milestones.json` relative to the launch directory. Missing files produce the sample menu or sample milestone catalog respectively. Invalid data causes a warning and an empty menu or catalog. Loading does not overwrite an invalid file, but a later successful command that triggers saving can replace it with the current model contents.

After each successful command except `help` and `exit`, `LogicManager` saves the address book, menu, and milestone catalog in that order, including after read-only commands such as `list`. Parsing or command execution failures stop before saving. Success feedback is returned only after all three saves complete; an `IOException` is reported as a `CommandException`.

Save failures do not roll back in-memory changes or earlier file writes. A later successful command that triggers saving attempts all three files again. Exiting does not retry domain-data saves, so changes that remain unsaved are lost on exit.

Each JSON save writes to a temporary file before atomically replacing its destination, protecting that file against partial writes. Unsupported atomic replacement is reported as a save error. The three saves do not form a transaction, so a failure can leave files representing different stages of the model's updates.

Tests cover invalid persisted data, round trips, startup fallback, saving and reloading, and save-error reporting.

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

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

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

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete-member 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete-member 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete-member`, `delete-member x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Recording an order

1. Recording an order for a person in the displayed list

   1. Prerequisites: Use `list` to show at least one person. The sample menu contains `1` (Chicken Rice),
      `3A` (Nasi Lemak), and `3B` (Nasi Lemak + Chicken).

   1. Test case: `add-order 1 o/1 q/2`<br>
      Expected: The status message confirms the order was added to the first person's order history. Their score
      increases by 90 points. The full person list is shown.

   1. Close and reopen the app, then enter `add-order 1 o/3B q/1`.<br>
      Expected: The status message shows both the earlier Chicken Rice order and the new Nasi Lemak + Chicken order
      in the first person's order history. The new order adds 65 points to their score.

   1. Test case: `add-order 1 o/Chicken-Rice q/2`<br>
      Expected: The item-code constraint is shown, and no order is added.

   1. Test case: `add-order 1 o/UNKNOWN q/2`<br>
      Expected: The unknown-menu-code error is shown, and no order is added.

   1. Test case: `add-order 1 o/1 q/0`<br>
      Expected: The quantity constraint is shown, and no order is added.

### Deleting an order

1. Deleting an order from a person in the displayed list

   1. Prerequisites: Use `list` to show at least one person with an order history. If needed, enter
      `add-order 1 o/1 q/2` and `add-order 1 o/3B q/1` first; both codes are in the sample menu.

   1. Test case: `delete-order 1 o/2`<br>
      Expected: The status message confirms that the second order was deleted. The first order remains, and the full
      person list is shown. The person's score decreases by the deleted order's saved `scoreGained`.

   1. Close and reopen the app after the successful deletion.<br>
      Expected: The deleted order remains absent from the person's order history.

   1. Test case: `delete-order 1 o/999`<br>
      Expected: The order-index error is shown, and no order is deleted.

   1. Test case: `delete-order 999 o/1`<br>
      Expected: The person-index error is shown, and no order is deleted.

   1. Other incorrect delete-order commands to try: `delete-order`, `delete-order 1`,
      `delete-order 1 o/0`, `delete-order 1 o/x`.<br>
      Expected: The command-format error is shown, and no order is deleted.

### Adding a menu item

Use a disposable app home folder for these tests. Start with no `data/menu.json`; the sample menu loads.

1. Test case: `add-menu-item c/12A n/Dessert p/3.50`<br>
   Expected: `New menu item added: Dessert`. `data/menu.json` contains the item with `priceInCents` of `350`.
1. Close and reopen the app, then enter `add-menu-item c/12A n/Different Dish p/5.00`.<br>
   Expected: `A menu item with this code already exists.` The original item's name and price remain unchanged.
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

### Deleting a menu item

1. In a disposable app home folder, add `add-menu-item c/DEL01 n/Test Dish p/4.50` and
   `add-menu-item c/del01 n/Other Dish p/5.00`.
1. Run `delete-menu-item c/DEL01`.<br>
   Expected: `Deleted menu item: DEL01 (Test Dish)`. The `del01` item remains in `data/menu.json`.
1. Close and reopen the app, then run `delete-menu-item c/DEL01` again.<br>
   Expected: `No menu item with this code exists.` The remaining menu is unchanged.
1. Run `delete-menu-item c/del01`.<br>
   Expected: The remaining test item is deleted. The sample menu items remain unchanged.
1. Try `delete-menu-item`, `delete-menu-item c/`, and `delete-menu-item c/DEL01 c/DEL02`.<br>
   Expected: An input error is shown, and no data is changed or saved.

### Managing milestone rewards

Use a disposable app home folder for these tests. Start with no `data/milestones.json`. The milestone list is sorted
by required score, using milestone code to break ties.

1. Test case: `list-milestones` before adding a milestone<br>
   Expected: `No milestones found.`
1. Test case: `add-milestone r/Free drink s/500`<br>
   Expected: `New milestone added: Free drink (Required score: 500)` and `data/milestones.json` contains the
   milestone with a generated internal code.
1. Test case: `add-milestone s/1000 r/$10 voucher`<br>
   Expected: The milestone is added even though the prefixes are in a different order.
1. Test case: `list-milestones`<br>
   Expected: Both milestones are shown with numbered indexes and no codes. The drink appears first because its
   required score is lower.
1. Test case: `add-milestone r/Free drink s/500` again<br>
   Expected: A second milestone is added with a different internal code.
1. Test cases: `add-milestone r/Reward s/+500`, `add-milestone r/Reward s/0`, and
   `add-milestone r/Reward s/2147483648`.<br>
   Expected: The required-score constraint is shown and no milestone is added.
1. Test case: `edit-milestone 1 r/Free drink and snack s/1500`<br>
   Expected: The milestone retains its generated code, both editable fields change, and it moves after the voucher in
   the sorted list.
1. Test case: `edit-milestone 1 c/NEWCODE`<br>
   Expected: The command-format error is shown because internal codes cannot be edited.
1. Test case: `delete-milestone 1`<br>
   Expected: The milestone currently shown at index 1 is deleted without changing member scores.
1. Test cases: `edit-milestone 999 r/Reward` and `delete-milestone 999`.<br>
   Expected: The milestone-index error is shown and the catalog remains unchanged.
1. Close and reopen the app.<br>
   Expected: Added and edited milestones remain, and deleted milestones remain absent.

### Saving data

1. Missing or invalid menu files, using a disposable app home folder

   1. With the app closed, move `data/menu.json` outside the `data` folder. Start the app.<br>
      Expected: The sample menu loads. Enter `list`; a new `data/menu.json` with the sample items is saved.
   1. Close the app and replace `data/menu.json` with malformed JSON or `{"foodItems":[null]}`. Restart.<br>
      Expected: A loading warning is logged and the menu starts empty. The invalid file is unchanged until a
      command executes successfully. Running `list` replaces it with valid empty-menu JSON.

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
