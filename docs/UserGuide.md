---
layout: page
title: User Guide
---

Ratatouille is a **desktop application for hawker stall owners to manage member registrations, orders and loyalty rewards**. It is optimized for typing commands through a Command Line Interface (CLI), with a Graphical User Interface (GUI) for viewing member details, order histories, menu items and milestone rewards. You can register members using their phone numbers, record orders, track membership points and manage reward redemptions while serving members.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` is installed on your computer. Check with `java -version` in a terminal.

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-F12-3/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for Ratatouille.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar ratatouille.jar`.<br>
   A GUI similar to the one below should appear. When no saved data files exist, the app loads sample members,
   menu items and milestone rewards. The screenshot shows an example session; its values may differ from the initial samples.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try. Examples that refer to members, menu items or orders require those entries
   to exist. Adding a member requires an unused phone entry, and adding a menu item requires an unused code:

   * `list` : Lists all members.

   * `add-member n/John Doe p/98765432` : Registers a member named `John Doe` with zero points, an empty order history and an empty redemption history.

   * `add-menu-item c/12A n/Dessert p/3.50` : Adds Dessert to the menu at $3.50.

   * `edit-menu-item 12A c/12B p/4.00` : Updates Dessert's code and price.

   * `add-milestone r/Free drink s/500` : Adds a milestone reward that requires 500 points.

   * `list-milestones` : Lists all milestone rewards with their current indexes.

   * `add-order 1 o/1 q/2` : With the sample menu, records two Chicken Rice for the 1st member shown and adds 90 points.

   * `delete-order 1 o/2` : Deletes the 2nd-newest order from the 1st member shown and subtracts its recorded points, provided the resulting score is not negative.

   * `delete-member 3` : Deletes the 3rd member shown in the current list.

   * `update-score 1 s/10` : Increases the score of the 1st member shown by 10. The command is rejected if the result exceeds `999999999`.

   * `clear` : Deletes all members, menu items, and milestone rewards.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add-member n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…` can appear zero or more times.<br>
  For example, `[t/TAG]…` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Prefixed fields for the commands in this section can be in any order. An unprefixed index or current menu-item code must come first.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.
  Use `edit-member 1 n/John Doe`, with the index before `n/`.

* Command words and prefixes are case-sensitive. Type them exactly as shown, using ordinary spaces between fields.
  For example, use `help`, not `Help`.

* Successful commands other than `help` and `exit` automatically save member records, the menu and milestone rewards.
  If saving fails, an error is shown and changes may remain only in memory. Follow the recovery instructions in
  [Saving the data](#saving-the-data) before closing the app.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help:
Command word: `help`

Opens the help window, which shows the User Guide address and a button to copy it.
The address is `https://ay2627s1-cs2103t-f12-3.github.io/tp/UserGuide.html`.
If the window is already open, `help` brings it into focus and restores it if minimized.

**Format:** `help`

**Parameters and constraints:** None. Extra arguments are ignored.

**Example:** `help` opens the help window and displays `Opened help window.`

**Common error and recovery:** `Help` produces `Unknown command.` Use lowercase `help`.
The command displays the guide address; use **Copy URL** and paste it into your browser to read the guide.

![help message](images/helpMessage.png)


### Adding a member:
Command word: `add-member`

Registers a member and selects their profile. The full member list is shown after a successful addition.

**Format:** `add-member n/NAME p/PHONE_NUMBER [t/TAG]…`

**Parameters and constraints:**

* Supply `n/NAME` and `p/PHONE_NUMBER` exactly once each. Tags are optional and `t/TAG` may be repeated.
* `NAME` must contain only letters (`A`–`Z`, `a`–`z`), digits and spaces, with at least one letter.
  Blank names, punctuation and names consisting only of digits are rejected.
* Each `TAG` must contain one or more letters (`A`–`Z`, `a`–`z`) or digits, without spaces or punctuation.
  Duplicate tags are stored only once.
* New members start with zero points, no orders and no redeemed milestones.
* Leading and trailing ordinary spaces around field values are removed. Internal spaces in a name are preserved.
* A phone entry is required. Empty or whitespace-only entries are rejected. New entries supplied to `add-member` or
  the `p/` field of `edit-member` must contain at least one decimal digit (including full-width digits). Inputs such as `Not available`
  and `---` are rejected with `Phone numbers must contain at least one digit`.
* Recognized Singapore numbers contain eight digits starting with 6, 8 or 9. Spaces and hyphens between
  digit groups are accepted. A country prefix of `+65`, `65`, `(+65)` or `(65)` is also accepted.
  These entries are saved as the same eight-digit number; full-width versions of these characters are accepted too.
  A bare `65` is treated as a country prefix only when followed by an otherwise recognized eight-digit number.
* Other entries containing digits, including overseas numbers, unusual lengths, notes and multiple numbers, are saved
  as entered apart from surrounding whitespace. They show the same **nonblocking warning** after a successful
  add or edit. The app does not verify whether any number is assigned or reachable. Hover over a phone label to read the full
  entry if it is shortened in the member list or profile.
* Each saved phone entry identifies one member. Members can share a name if their phone entries differ.
  If the entry is already registered, the command rejects it and identifies the existing member, even if that
  member is hidden by a search. Recognized Singapore formats are compared after normalization.
* For all other entries, only an exact, case-sensitive text match (apart from surrounding whitespace) blocks a
  duplicate. A separate **possible duplicate warning** checks complete number segments of at least six digits
  against all existing members, including members hidden by a search. Spaces, parentheses, dots and common
  dashes are ignored for this advisory comparison; a recognized Singapore `65` prefix is also ignored.
  For example, `（86）12345678` and `+8612345678` remain different saved entries but trigger a warning.
* A possible duplicate warning lists each related member once, with their name and phone entry. The new entry
  is still saved: check whether the records represent separate memberships, and use `delete-member INDEX` to remove an
  unintended extra record after checking the displayed list. No records, points or order histories are merged.
  When editing, the member being edited is excluded; this warning is checked only when `p/` is supplied.
* Letters and delimiters such as `/`, `,`, `;` and `+` separate number segments. Explicit extension values
  introduced by `ext`, `extension` or `x` are excluded from this warning when the marker is separated from the
  preceding number or word, for example `92345678 ext 123456`. Letter case does not matter for these markers.
  An attached marker such as `92345678x123456` does not exclude the extension from comparison.
  Complete sequences of adjacent eight-digit
  Singapore numbers are checked as separate numbers. Shorter segments, partial digit matches and inferred foreign
  country codes are not checked. For example, `+86 138119828111` matches a compact
  `+86138119828111` for a warning, but the app does not guess that `138119828111` belongs to country code `86`.
  Other numbers separated only by spaces or dashes can still be treated as one segment; use `/` or a label to
  make their boundaries clear. The absence of a warning does not establish that two entries belong to different people.
* Command prefixes such as `n/`, `p/` and `t/` still delimit fields and cannot be embedded literally in a phone
  value when preceded by an ordinary space. Use spaces between fields; tabs alone do not separate fields
  for `add-member` or `edit-member`.

Phone input examples (assuming a valid name and tags, and that the saved phone entry is not already registered):

| Value after `p/` | Saved phone entry | Result |
|---|---|---|
| `91234567`, `9123 4567`, `9123-4567` | `91234567` | Success |
| `+6591234567`, `+65 9123 4567`, `65 9123 4567`, `6591234567` | `91234567` | Success |
| `(+65) 9123 4567`, `（＋６５）９１２３－４５６７` | `91234567` | Success |
| `12345678`, `911`, `912345678` | Unchanged | Success with warning |
| `+86 138119828111`, `（86）12345678` | Unchanged | Success with warning |
| `91234567 (HP)`, `1234 5678 (HP) 1111-3333 (Office)` | Unchanged | Success with warning |
| `006591234567`, `+65 12345678` | Unchanged | Success with warning |
| `Not available`, `---`, `phone` | Not saved | Error: `Phone numbers must contain at least one digit` |
| Empty or only whitespace | Not saved | Error: `Phone numbers must not be blank` |

The nonstandard-format warning is:

> Warning: This phone entry was not recognized as a standard Singapore number. It has been saved; please check it.
> You may ignore this warning for an overseas number or an intentional special format.
> Only exact text duplicates are blocked for these entries.

Duplicate-check examples:

| Existing entry | New input | Result |
|---|---|---|
| `92345678` | `+6592345678` | Rejected as an exact normalized duplicate |
| `92345678` | `92345678 (HP)` | Saved; format warning and possible duplicate warning identify the existing member |
| `92345678 (HP)` | `92345678` | Saved; possible duplicate warning identifies the existing member |
| `+86 138119828111` | `+86138119828111` | Saved; format warning and possible duplicate warning |
| `92345678` and `81234567` in separate members | `92345678 (HP) / 81234567 (Office)` | Saved; possible duplicate warning identifies both members |

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A member can have any number of tags, including zero.
</div>

**Examples:**

Assuming the phone entries are not already registered:

* `add-member n/John Doe p/98765432`
* `add-member n/Betsy Crowe t/regular p/81234567 t/vegetarian`

**Common errors and recovery:**

| Input or situation | Result and recovery |
|---|---|
| `add-member n/John Doe` | The command format is shown. Supply `p/PHONE_NUMBER`. |
| `add-member n/123 p/98765432` | `Names should contain at least one letter (A-Z or a-z)`. Use a name such as `John Doe`. |
| `add-member n/John Doe p/phone` | `Phone numbers must contain at least one digit`. Enter a phone entry containing a digit. |
| The phone entry is already registered | `This phone number already belongs to a member in the address book: ` followed by the existing member's name, phone entry and tags. The addition is rejected. Check that record before entering a different phone entry. |
| `add-member n/John Doe p/98765432 t/close friend` | `Tag names should be alphanumeric`. Use `t/closeFriend`. |

### Listing all members:
Command word: `list`

Shows all members, clears any search filter, and deselects the current member.

**Format:** `list`

**Parameters and constraints:** None. Extra arguments are ignored.

**Example:** After `find Betsy`, run `list` to show all members again. The feedback is `Listed all members.`

**Common error and recovery:** `list` displays members. To see milestone rewards and their indexes,
use `list-milestones`.

### Editing a member:
Command word: `edit-member`

Edits an existing member's name, phone entry or tags.

**Format:** `edit-member INDEX [n/NAME] [p/PHONE] [t/TAG]…`

**Parameters and constraints:**

* Edits the member at the specified `INDEX`. Use the index number shown in the current member list,
  including filtered search results. The index **must be a positive integer** 1, 2, 3, …
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* Names and tags follow the same constraints as `add-member`. Supply `n/` and `p/` at most once each.
* Phone numbers follow the same format and uniqueness rules as `add-member`. Changing a phone number to one already
  registered to another member is rejected; keeping the member's current number is allowed.
* When editing tags, all of the member's existing tags are removed; adding tags is not cumulative.
* To remove all of a member's tags, enter `t/` without a tag after it.
* Omitted fields, the member's score, order history and redemption history are preserved.
  After a successful edit, the full member list is shown and the edited member is selected.

**Examples:**

Assuming the displayed member list contains at least two members:

* `edit-member 1 p/91234567` changes the 1st displayed member's phone entry to `91234567`, if it is not registered to another member.
* `edit-member 2 n/Betsy Crower t/` changes the 2nd displayed member's name to `Betsy Crower` and clears their tags.

**Common errors and recovery:**

| Input or situation | Result and recovery |
|---|---|
| `edit-member 1` | `At least one field to edit must be provided.` Supply a name, phone entry or tags. |
| `edit-member 0 n/John Doe` | The command format is shown. Use a positive index from the displayed member list. |
| The index is beyond the displayed member list | `The member index provided is invalid.` Run `list` and use the correct displayed index. |
| The new phone entry belongs to another member | `This phone number already belongs to a member in the address book: ` followed by the existing member's name, phone entry and tags. The edit is rejected. Check that record and correct the phone entry. |

### Adding a menu item:
Command word: `add-menu-item`

Adds a food item with a unique code, name, and price to the menu. The item is saved for future sessions.
On a fresh installation, the menu starts with sample items such as `1` (Chicken Rice), `3A` (Nasi Lemak),
and `3B` (Nasi Lemak + Chicken).

**Format:** `add-menu-item c/CODE n/NAME p/PRICE`

**Parameters and constraints:**

* Provide all three prefixes exactly once. They may appear in any order, separated by spaces or tabs.
* `CODE` must contain one or more letters (`A`–`Z`, `a`–`z`) or digits (`0`–`9`), without spaces or punctuation.
* Codes are case-sensitive: `3A` and `3a` identify different items. A code already in the menu cannot be reused,
  even if the new name or price differs. Different codes may have the same item name.
* `NAME` must not be blank. Spaces, punctuation, and non-English characters are accepted.
  **Exception:** A prefix-like pattern consisting of a space, one letter and `/`, such as ` x/` or ` p/`,
  cannot be entered literally because the app interprets it as a command field. Unsupported patterns are rejected,
  while supported prefixes are parsed as separate fields.
* Single-letter prefixes such as `c/`, `n/`, and `p/` are interpreted as arguments when preceded by a space or tab;
  avoid these tokens inside names. Unsupported single-letter prefixes such as `x/` are rejected.
* `PRICE` is in dollars, from `0.01` to `21474836.47`, inclusive. Use digits with up to two decimal places;
  `4`, `4.5`, and `4.50` are accepted. Enter `0.50` for fifty cents, without a currency symbol.
* Missing fields, repeated prefixes, invalid values, and control characters such as newlines are rejected.

**Examples:**

Assuming codes `12A` and `12B` are unused:

* `add-menu-item c/12A n/Dessert p/3.50`
* `add-menu-item p/1.20 n/Coffee c/12B`

The first example shows `New menu item added: Dessert` after saving succeeds. The member list stays as it is;
`list` displays members, not the menu. Menu items are stored separately; `clear` deletes them along with all members and milestone rewards.

**Common errors and recovery:**

| Input | Result and recovery |
|---|---|
| Reusing `c/12A` after adding that code | `A menu item with this code already exists.` Choose a different code. |
| `add-menu-item c/13 n/Dish` | The command format is shown. Supply the missing `p/PRICE`. |
| `add-menu-item c/13 n/Dish p/4.501` | `Price must be between 0.01 and 21474836.47 dollars, with at most two decimal places.` Use at most two decimal places. |
| `add-menu-item c/13 n/Dish p/0` | `Price must be between 0.01 and 21474836.47 dollars, with at most two decimal places.` Use a positive price of at least `0.01`. |

These validation failures leave the existing menu unchanged and do not trigger an automatic save.

### Deleting a menu item:
Command word: `delete-menu-item`

Deletes the menu item with the specified code and saves the updated menu.

**Format:** `delete-menu-item c/CODE`

**Parameters and constraints:**

* Provide `c/` exactly once, with a nonblank alphanumeric code. Extra arguments are rejected.
* Codes are case-sensitive. Deleting `CR01` leaves an item with code `cr01` unchanged.
* If the code does not exist, the app shows `No menu item with this code exists.` and leaves the menu unchanged.
* Successful deletion shows, for example, `Deleted menu item: CR01 (Chicken Rice)`.
* Member records, existing order histories, and milestone rewards are unchanged. The deleted code can be reused.

**Example:** `delete-menu-item c/CR01` deletes the item with code `CR01`, if it exists.

**Common errors and recovery:**

| Input or situation | Result and recovery |
|---|---|
| `delete-menu-item CR01` | The command format is shown. Use `delete-menu-item c/CR01`. |
| `delete-menu-item c/CR-01` | `Item codes must be alphanumeric.` Enter an alphanumeric code without punctuation. |
| The code does not exist | `No menu item with this code exists.` Check the item's exact code, including letter case. |

### Editing a menu item:
Command word: `edit-menu-item`

Updates an existing menu item using its current code. The item is saved for future sessions.

**Format:** `edit-menu-item CURRENT_CODE [c/NEW_CODE] [n/NAME] [p/PRICE]`

**Parameters and constraints:**

* `CURRENT_CODE` identifies the existing menu item. It must contain one or more letters (`A`–`Z`, `a`–`z`) or
  digits (`0`–`9`), without spaces or punctuation.
* Codes are case-sensitive. `c/NEW_CODE` changes the item's code; it must be alphanumeric and must not
  belong to another menu item. Keeping the current code is accepted.
* `n/NAME` changes the item's name. The name must not be blank.
* `p/PRICE` changes the item's price. Prices use the same dollar format as `add-menu-item`.
* At least one of `c/NEW_CODE`, `n/NAME`, or `p/PRICE` must be provided. They may appear in any order.
* Fields that are not provided retain their existing values.
* Supply each optional prefix at most once. Unsupported single-letter prefixes and control characters other
  than tabs are rejected, as for `add-menu-item`.
* Existing orders retain their recorded item code, name and points gained. The new details apply to future orders.

**Examples:**

Assuming code `12A` exists and `12C` is unused:

* `edit-menu-item 12A n/Chocolate Cake` changes only the name.
* `edit-menu-item 12A p/4.00` changes only the price.
* `edit-menu-item 12A c/12C n/Chocolate Cake p/4.00` changes the code, name, and price.

**Common errors and recovery:**

| Input | Result and recovery |
|---|---|
| `edit-menu-item UNKNOWN n/Dish` when code `UNKNOWN` does not exist | `A menu item with this code does not exist.` Enter the current code of an existing item. |
| `edit-menu-item 12A c/3A` when `3A` belongs to another item | `A menu item with the new code already exists.` Choose a code that is not already in the menu. |
| `edit-menu-item 12A` | The command format is shown. Supply at least one field to update. |
| `edit-menu-item 12A p/4.001` | `Price must be between 0.01 and 21474836.47 dollars, with at most two decimal places.` Use at most two decimal places. |

Validation failures leave the existing menu item unchanged.

### Listing milestone rewards:
Command word: `list-milestones`

Lists all milestone rewards with the indexes used by `edit-milestone`, `delete-milestone`, `redeem` and `cancel-redeem`.

**Format:** `list-milestones`

**Parameters and constraints:**

* No parameters are required. Extra arguments are ignored.
* Milestones are ordered by required score from lowest to highest. Ties are ordered by their internal codes,
  so do not assume creation order for milestones with equal scores.
  Each entry shows its index, reward description and required score.
* Adding, editing or deleting milestones can change their indexes. Run `list-milestones` again before using an index.

**Example:** `list-milestones` produces the following output when the catalog contains these two rewards:

```text
2 milestone(s) listed!
1. Free drink (Required score: 500)
2. $10 voucher (Required score: 1000)
```

**Common error and recovery:** `No milestones found.` means the catalog is empty. Add a reward with
`add-milestone r/Free drink s/500`, then run `list-milestones` again.

### Adding a milestone reward:
Command word: `add-milestone`

Adds a milestone reward with a reward description and required score to the milestone catalog.

**Format:** `add-milestone r/REWARD s/REQUIRED_SCORE`

**Parameters and constraints:**

* Provide both prefixes exactly once. They may appear in any order, separated by spaces or tabs.
* `REWARD` must not be blank. Spaces, punctuation, and non-English characters are accepted.
  **Exception:** A prefix-like pattern consisting of a space, one letter and `/`, such as ` c/` or ` s/`,
  cannot be entered literally because the app interprets it as a command field. Unsupported patterns are rejected,
  while supported prefixes are parsed as separate fields.
* `REQUIRED_SCORE` must be a positive whole number from `1` to `999999999`, inclusive.
  Do not include a sign, decimal point or thousands separator.
* Single-letter prefixes such as `r/` and `s/` are interpreted as arguments when preceded by a space or tab;
  avoid these tokens inside reward descriptions. Unsupported single-letter prefixes and control characters
  other than tabs are rejected.
* A new milestone gets an automatically generated identity. The same reward description and required score can
  be added more than once; each addition is a separate milestone with its own redemption status.

**Examples:**

* `add-milestone r/Free drink s/500`
* `add-milestone s/1000 r/$10 voucher`

A successful command displays the reward and required score, for example
`New milestone added: Free drink (Required score: 500)`. The app generates and stores an internal code to track
redemptions; you do not need to enter or use it.

**Common errors and recovery:**

| Input | Result and recovery |
|---|---|
| `add-milestone r/Free drink` | The command format is shown. Supply `s/REQUIRED_SCORE`. |
| `add-milestone r/Free drink s/+500` | `Required score must be a positive whole number no greater than 999999999.` Use `s/500`. |
| `add-milestone r/Free drink s/1000000000` | `Required score must be a positive whole number no greater than 999999999.` Enter a whole number from `1` to `999999999`. |
| `add-milestone r/ s/500` | `Reward descriptions must not be blank.` Supply a reward description. |
| `add-milestone c/FREE500 r/Free drink s/500` | The command format is shown. Remove `c/`; codes are generated automatically. |

### Editing a milestone reward:
Command word: `edit-milestone`

Edits the reward description or required score of an existing milestone.

**Format:** `edit-milestone INDEX [r/REWARD] [s/REQUIRED_SCORE]`

**Parameters and constraints:**

* `INDEX` is the positive index number shown in the current milestone list.
* Provide at least one of `r/REWARD` or `s/REQUIRED_SCORE`. Existing values for omitted fields are preserved.
* Supply each prefix at most once, in either order. Reward descriptions and scores follow the same constraints
  as `add-milestone`: a nonblank description and a whole-number score from `1` to `999999999`.
* Changing a required score can move the milestone to a different index.
* Entering values that are already stored is accepted and leaves the milestone unchanged.
* Editing a milestone **does not delete members' existing redemptions**. The milestone keeps its identity,
  so a member who has redeemed it still has it marked as redeemed, even after its description or required score
  changes. Editing does not allow that member to redeem it again.
* Previously stored redemption records retain the description and required score recorded when redeemed.
  The catalog and timeline show the edited details. Member scores are unchanged.

**Examples:**

Assuming the current milestone list contains at least two milestones:

* `edit-milestone 1 r/Free drink and snack`
* `edit-milestone 2 s/1200 r/$15 voucher`

**Common errors and recovery:**

| Input or situation | Result and recovery |
|---|---|
| `edit-milestone 1` | `At least one field to edit must be provided.` Supply `r/REWARD` or `s/REQUIRED_SCORE`. |
| `edit-milestone 1 c/NEWCODE` | The command format is shown. Only the reward description and required score can be edited. |
| `edit-milestone 1 s/0` | `Required score must be a positive whole number no greater than 999999999.` Use a whole number from `1` to `999999999`. |
| The index is beyond the current milestone list | `The milestone index provided is invalid.` Run `list-milestones` and use a current index. |

### Deleting a milestone reward:
Command word: `delete-milestone`

Deletes a milestone from the catalog.

**Format:** `delete-milestone INDEX`

**Parameters and constraints:**

* `INDEX` is the positive index number shown in the current milestone list.
* Supply only the index, without a prefix or additional arguments. Use the latest displayed list because
  adding, editing or deleting a milestone can change the indexes.
* Deleting a milestone removes it from the catalog and members' milestone timelines, including its displayed
  redemption status. It does not change any member's score.
* **Stored redemption records are retained.** The current implementation does not delete the matching records
  from members' redemption histories. Once the milestone is deleted, `cancel-redeem` cannot target it because
  that command requires an entry in the current catalog.
* Adding the same description and required score later creates a new milestone. Previous redemptions of the
  deleted milestone do not mark the new milestone as redeemed.

**Example:** Run `list-milestones`, then `delete-milestone 1` to delete the first listed milestone.

**Common errors and recovery:**

| Input | Result and recovery |
|---|---|
| `delete-milestone 0` | The command format is shown. Use a positive index from `list-milestones`. |
| `delete-milestone 1 r/Free drink` | The command format is shown. Supply only the index: `delete-milestone 1`. |
| `delete-milestone 999` when fewer than 999 milestones exist | `The milestone index provided is invalid.` Run `list-milestones` and use a current index. |

Failed milestone commands leave the milestone catalog unchanged. Successful commands are saved in
`data/milestones.json`; `clear` removes all members, menu items, and milestone rewards.

### Recording an order:
Command word: `add-order`

Records an order for a member and adds it to that member's order history.

Format: `add-order INDEX o/ITEM_CODE q/QUANTITY`

* `INDEX` is the positive index number shown in the current member list. Use `list` or `find` to identify the member.
* `ITEM_CODE` must be an existing menu item's case-sensitive code. Add the item with `add-menu-item` first.
  Codes contain only letters and digits, without spaces or punctuation.
* `QUANTITY` must be a positive whole number from `1` to `2147483647`, inclusive.
* Provide `o/` and `q/` once each. They can appear in either order.
* The order earns 10 points for each $1 spent (`price × quantity`), rounded down to a whole point. These points are
  added to the member's score. An order that would raise the score above `999999999` is rejected.
* After a successful order, the current member list is updated and the member is selected.

Examples:
* With the sample menu, `add-order 1 o/1 q/2` records two Chicken Rice for the 1st member shown and adds 90 points
  to their score ($4.50 × 2 × 10).
* With the sample menu, `add-order 2 q/1 o/3B` records one Nasi Lemak + Chicken for the 2nd member shown.

An unknown code shows `The item code is not in the menu.` and leaves the member's history unchanged. Each order saves
the code, the menu item's name at the time of the order, the quantity, the recorded time, and `scoreGained`. The
order history shows the saved name and points gained for each order, even if the menu item's name later changes. The
saved `scoreGained` also stays the same if the menu price changes.

**Common errors and recovery**

| Input or situation                | Result and recovery                                                                       |
|-----------------------------------|-------------------------------------------------------------------------------------------|
| `add-order 1 o/UNKNOWN q/1`       | `The item code is not in the menu.` Add the menu item first or use an existing item code. |
| `add-order 1 o/1 q/0`             | `Quantity must be a positive whole number` Use a positive whole-number quantity.          |
| `add-order 1 o/1 q/2147483648` | `Quantity must be a positive whole number` Use a quantity no greater than `2147483647`.  |

### Deleting an order:
Command word: `delete-order`

Deletes an order from a member's order history.

Format: `delete-order INDEX o/ORDER_INDEX`

* `INDEX` is the positive index number shown in the current member list. Use `list` or `find` to identify the member.
* `ORDER_INDEX` is the positive index number of the order as displayed in that member's order history, counted from the newest order.
* Provide `o/` exactly once.
* Deleting an order subtracts its saved `scoreGained` from the member's score. If this would make the score negative,
  the deletion is rejected; update the member's score first if needed.
* After a successful deletion, the current member list is updated and the member remains selected.

Examples:
* `delete-order 1 o/2` deletes the 2nd-newest order from the 1st member shown.
* `find Betsy` followed by `delete-order 1 o/1` deletes Betsy's newest order.

**Common errors and recovery**

| Input or situation                                   | Result and recovery |
|------------------------------------------------------|---|
| `delete-order 1 o/99`                                | `The order index provided is invalid.` Use an index from the member's displayed order history. |
| Deleting an order that would make the score negative | `Deleting this order would make the score negative.` Update the member's score first if needed. |

### Updating a member's score:
Command word: `update-score`

Updates a member's membership score by the specified amount.

Format: `update-score INDEX s/SCORE_CHANGE`

* `INDEX` is the positive index number shown in the current member list. Use `list` or `find` to identify the member.
* `SCORE_CHANGE` must contain an optional `+` or `-` sign followed by one to nine digits. Positive values increase
  the score, while negative values decrease it. Leading zeroes count towards the nine-digit limit.
* The resulting score must be between 0 and 999999999, inclusive.
* After a successful update, the previous and new scores are shown.

Examples:
* `update-score 1 s/10` increases the score of the 1st member shown by 10.
* `update-score 1 s/-5` decreases the score of the 1st member shown by 5.

**Common errors and recovery**

| Input or situation                                        | Result and recovery                                                   |
|-----------------------------------------------------------|-----------------------------------------------------------------------|
| `update-score 1 s/-999999999` when score is not 999999999 | `Score cannot be less than zero.` Use a smaller decrease.             |
| `update-score 1 s/999999999`  when score is not 0         | `Score cannot exceed 999999999.` Use a smaller increase.              |
| `update-score 1 s/1999999999`                             | `Score change must be a signed whole number.` Use no more than nine digits. |
| `update-score 1 s/-1999999999`                            | `Score change must be a signed whole number.` Use no more than nine digits. |

### Redeeming a milestone:
Command word: `redeem`

Redeems an available milestone reward for a member who has sufficient membership points.

Format: `redeem MEMBER_INDEX m/MILESTONE_INDEX`

* `MEMBER_INDEX` is the positive index number shown in the current member list.
* `MILESTONE_INDEX` is the positive index number shown in the current milestone list.
* The member must have enough membership points for the selected milestone.
* A member cannot redeem the same milestone more than once.
* Redemption uses the current milestone catalog. Therefore, the selected index refers to the milestone currently
  displayed at that position. If milestones are added, deleted, or reordered, the same index may refer to a different
  milestone. This is intentional because the command identifies milestones by their current displayed index.
* On successful redeem, points of member is not spent.
* Regardless of success or failure, the current member list displayed is not modified.

Examples:
* `redeem 1 m/2` redeems the 2nd milestone for the 1st member shown.

**Common errors and recovery**

| Input or situation                 | Result and recovery |
|------------------------------------|---|
| Redeeming without enough points    | `Member does not have sufficient score.` Choose a milestone within the member's score. |
| Redeeming the same milestone twice | `Milestone reward already redeemed.` Choose another milestone. |

### Cancelling a redeemed milestone:
Command word: `cancel-redeem`

Cancels a milestone reward that has already been redeemed by a member.

Format: `cancel-redeem MEMBER_INDEX m/MILESTONE_INDEX`

* `MEMBER_INDEX` is the positive index number shown in the current member list.
* `MILESTONE_INDEX` is the positive index number shown in the current milestone list.
* The selected milestone must already be present in that member's redeemed milestone history.
* Cancellation uses the current milestone catalog. Therefore, the milestone's current catalog entry must still exist
  and have the same internal code as the redeemed milestone. If the milestone was deleted, it cannot
  be cancelled through this command. This is intentional because the command identifies milestones by their current
  displayed index.
* Regardless of success or failure, the current member list displayed is not modified.

Examples:
* `cancel-redeem 1 m/2` cancels the 2nd currently displayed milestone for the 1st member shown.

Cancellation does not refund or change the member's membership points. It only removes the matching milestone from
the member's redeemed milestone reward history.

**Common errors and recovery:**

| Input or situation                                                  | Result and recovery                                                                  |
|---------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `cancel-redeem 1 m/99`                                              | `The milestone index provided is invalid.` Use the current milestone list to choose an index. |
| `cancel-redeem 1 m/2` when milestone 2 was not redeemed by member 1 | `Unable to unredeem because it was not redeemed.` No action needed.                  |

### Locating members by name or phone number:
Command word: `find`

Finds members by name keywords or an exact phone entry.

Formats: `find KEYWORD [MORE_KEYWORDS]…​` or `find p/PHONE_NUMBER`

* Name searches are case-insensitive and match whole words; `hans` matches `Hans`, but `Han` does not.
* Without `p/`, each keyword searches names and complete phone entries. Matching at least one keyword is enough
  (an `OR` search); `Hans Bo` returns `Hans Gruber` and `Bo Yang`, regardless of keyword order.
* If the whole unprefixed query is a recognized Singapore phone format, it is treated as a single normalized
  phone keyword. For example, `find 9123 4567` searches for `91234567`.
* Use `find p/PHONE_NUMBER` to search only phone entries, including an overseas number or a note containing spaces.
  Everything after this first `p/` belongs to one phone query. Recognized Singapore formats are normalized as for
  `add-member`; other entries require an exact, case-sensitive match, including internal spaces and punctuation.
  Partial phone entries do not match.
* Searches do not change the stored phone entry or show a phone-format warning. Previously saved entries without
  digits still load and can be found with `find p/Not available`, for example. You can edit their other fields
  or replace the phone entry with one containing digits; supplying digitless text in the `p/` field of `edit-member` is rejected.

Examples:
* `find John` returns `john` and `John Doe`.
* `find +65 9876 5432` finds the member whose saved phone entry is `98765432`.
* `find p/+86 138119828111` finds that exact overseas entry.
* `find p/（86）12345678` finds that exact entry, not `+8612345678`.
* `find alex david` returns `Alex Yeoh` and `David Li`.

### Deleting a member:
Command word: `delete-member`

Deletes the specified member from the address book.

Format: `delete-member INDEX`

* Deletes the member at the specified `INDEX`.
* The index refers to the index number shown in the displayed member list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete-member 2` deletes the 2nd member in the address book.
* `find Betsy` followed by `delete-member 1` deletes the 1st member in the results of the `find` command.

**Common errors and recovery:**


| Input or situation                                          | Result and recovery |
|-------------------------------------------------------------|---|
| `delete-member 99` when fewer than 99 members are displayed | `The member index provided is invalid.` Use an index from the current member list. |

### Clearing all entries:
Command word: `clear`

Clears all members, menu items, and milestone rewards, and deselects the current member.
This also saves the empty data files, so the cleared data stays empty after restarting the application.

Format: `clear`

### Exiting the program:
Command word: `exit`

Exits the program.

Format: `exit`

### Saving the data

The app automatically saves member records, the menu, and the milestone catalog after every command that executes
successfully, except `help` and `exit`. These two commands remain available even when data cannot be saved.
You do not need to save manually. All three are loaded again at startup.

If a save fails, an error is shown instead of the normal success feedback. Changes already made remain in memory;
after fixing the file-access problem, run a command such as `list` to attempt saving again before closing the app.
The `exit` command does not retry saving; closing the app loses any changes that have not been saved successfully.

Each file is replaced only after its new contents have been fully written, so a failed write does not leave that
file half-written. The three data files are saved separately: if a later file fails to save, earlier files may
already contain the latest changes. If your storage location does not support safe file replacement, use a
different location that does.

### Editing the data file

Member records are saved in `data/memberlist.json`, menu items in `data/menu.json`, and milestone rewards in
`data/milestones.json`. These paths are relative to the directory from which you launch the app. With the quick-start
instructions above, this is the JAR's folder. Advanced users can edit these files while the app is closed.

Member records contain a name, phone number, tags, score, and order history.
Unknown properties in older data files are ignored and omitted the next time the data is saved.
Each entry in `orderHistory` uses `itemCode` (for example, `"itemCode": "3A"`), `itemName`, `quantity`,
`recordedAt`, and `scoreGained` (a non-negative whole number of points). `itemName` is the name recorded with that
order, not a live lookup from the menu. Older orders without `scoreGained` load with zero points. If an existing file
lacks either `itemCode` or `itemName`, back it up and add the missing field to each order before opening this version.
Use the corresponding menu item's code or name where possible. Orders missing either field cannot be loaded.

In `menu.json`, each entry in `foodItems` has a string `code`, a string `name`, and a positive integer
`priceInCents`. For example, the command price `p/4.50` is saved as `"priceInCents": 450`.

In `milestones.json`, each entry in `milestones` has an automatically generated, non-blank string `code`, a non-blank
string `rewardDescription`, and a positive integer `requiredScore`. Existing milestone codes remain valid when
loading older data. The internal codes are case-insensitive, saved in uppercase, and must be unique.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If an existing member file cannot be loaded, startup stops to protect it from being overwritten. Back it up,
then restore a valid copy or correct the invalid fields before restarting. Old nonblank phone entries still load,
but entries that normalize to the same Singapore number conflict: resolve those duplicate member records in a
backup copy, preserving their points and histories, before upgrading. The app does not merge records automatically.
A missing member file loads sample contacts. A missing menu file loads the sample menu, and a missing milestone
file loads the sample milestone catalog. Invalid menu or milestone files use empty catalogs. The next successfully
executed command other than `help` or `exit` attempts to save all three files and can overwrite those invalid
catalog files, so back up all three files before editing them.<br>
Furthermore, certain edits can cause the member list to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>


--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer. With both apps closed, copy `data/memberlist.json`, `data/menu.json`,
and `data/milestones.json` from the old home folder into the new home folder's `data` directory.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
[**Add member**](#adding-a-member) | `add-member n/NAME p/PHONE_NUMBER [t/TAG]…​` <br> e.g., `add-member n/James Ho p/98765544 t/friend t/colleague`
[**Add menu item**](#adding-a-menu-item) | `add-menu-item c/CODE n/NAME p/PRICE`<br> e.g., `add-menu-item c/12A n/Dessert p/3.50`
[**Add milestone**](#adding-a-milestone-reward) | `add-milestone r/REWARD s/REQUIRED_SCORE`<br> e.g., `add-milestone r/10% OFF s/250`
[**Add order**](#recording-an-order) | `add-order INDEX o/ITEM_CODE q/QUANTITY`<br> e.g., `add-order 1 o/12A q/3`
[**Delete member**](#deleting-a-member) | `delete-member INDEX`<br> e.g., `delete-member 3`
[**Delete menu item**](#deleting-a-menu-item) | `delete-menu-item c/CODE`<br> e.g., `delete-menu-item c/CR01`
[**Delete milestone**](#deleting-a-milestone-reward) | `delete-milestone INDEX`<br> e.g., `delete-milestone 1`
[**Delete order**](#deleting-an-order) | `delete-order INDEX o/ORDER_INDEX`<br> e.g., `delete-order 1 o/2`
[**Edit member**](#editing-a-member) | `edit-member INDEX [n/NAME] [p/PHONE_NUMBER] [t/TAG]…​`<br> e.g., `edit-member 2 n/James Lee`
[**Edit milestone**](#editing-a-milestone-reward) | `edit-milestone INDEX [r/REWARD] [s/REQUIRED_SCORE]`<br> e.g., `edit-milestone 1 s/600`
[**Edit menu item**](#editing-a-menu-item) | `edit-menu-item CURRENT_CODE [c/NEW_CODE] [n/NAME] [p/PRICE]`<br> e.g., `edit-menu-item 12A c/12B`
[**Redeem milestone**](#redeeming-a-milestone) | `redeem MEMBER_INDEX m/MILESTONE_INDEX`<br> e.g., `redeem 1 m/2`
[**Cancel redeemed milestone**](#cancelling-a-redeemed-milestone) | `cancel-redeem MEMBER_INDEX m/MILESTONE_INDEX`<br> e.g., `cancel-redeem 1 m/2`
[**Update score**](#updating-a-members-score) | `update-score INDEX s/SCORE_CHANGE`<br> e.g., `update-score 1 s/10`
[**Find**](#locating-members-by-name-or-phone-number) | `find KEYWORD [MORE_KEYWORDS]…​`, `find p/PHONE_NUMBER`<br> e.g., `find James Jake`, `find p/98765432`
[**List**](#listing-all-members) | `list`
[**List milestones**](#listing-milestone-rewards) | `list-milestones`
[**Clear all data**](#clearing-all-entries) | `clear`
[**Help**](#viewing-help) | `help`
[**Exit**](#exiting-the-program) | `exit`
