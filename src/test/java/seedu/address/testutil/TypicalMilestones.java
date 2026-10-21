package seedu.address.testutil;

import seedu.address.model.milestone.Milestone;

/**
 * A utility class containing milestone objects to be used in tests.
 */
public class TypicalMilestones {
    public static final Milestone FREE_DRINK = new Milestone("FREE500", "Free drink", 500);
    public static final Milestone EDITED_FREE_DRINK =
            new Milestone("FREE500", "Free drink and snack", 600);
    public static final Milestone FREE_DESSERT = new Milestone("DESSERT500", "Free dessert", 500);
    public static final Milestone VOUCHER = new Milestone("VOUCHER1000", "$10 voucher", 1000);
}
