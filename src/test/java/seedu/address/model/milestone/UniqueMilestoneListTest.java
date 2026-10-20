package seedu.address.model.milestone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.model.milestone.exceptions.DuplicateMilestoneException;
import seedu.address.model.milestone.exceptions.MilestoneNotFoundException;

public class UniqueMilestoneListTest {
    private static final Milestone FREE_DRINK = new Milestone("FREE500", "Free drink", 500);
    private static final Milestone EDITED_FREE_DRINK = new Milestone("FREE500", "Free drink and snack", 600);
    private static final Milestone FREE_DESSERT = new Milestone("DESSERT500", "Free dessert", 500);
    private static final Milestone VOUCHER = new Milestone("VOUCHER1000", "$10 voucher", 1000);

    private final UniqueMilestoneList milestones = new UniqueMilestoneList();

    @Test
    public void contains_nullMilestone_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> milestones.contains(null));
    }

    @Test
    public void contains_comparesCodes() {
        assertFalse(milestones.contains(FREE_DRINK));
        milestones.add(FREE_DRINK);
        assertTrue(milestones.contains(FREE_DRINK));
        assertTrue(milestones.contains(EDITED_FREE_DRINK));
        assertFalse(milestones.contains(FREE_DESSERT));
        assertFalse(milestones.contains(new Milestone("free500", "Free drink", 500)));
    }

    @Test
    public void add_nullMilestone_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> milestones.add(null));
        assertTrue(milestones.asUnmodifiableObservableList().isEmpty());
    }

    @Test
    public void add_uniqueMilestones_sortsByRequiredScoreThenCode() {
        milestones.add(VOUCHER);
        milestones.add(FREE_DRINK);
        milestones.add(FREE_DESSERT);
        assertEquals(List.of(FREE_DESSERT, FREE_DRINK, VOUCHER),
                milestones.asUnmodifiableObservableList());
    }

    @Test
    public void add_duplicateCode_throwsAndPreservesList() {
        milestones.add(FREE_DRINK);
        assertThrows(DuplicateMilestoneException.class, () -> milestones.add(FREE_DRINK));
        assertThrows(DuplicateMilestoneException.class, () -> milestones.add(EDITED_FREE_DRINK));
        assertEquals(List.of(FREE_DRINK), milestones.asUnmodifiableObservableList());
    }

    @Test
    public void setMilestone_nullArguments_throwsAndPreservesList() {
        milestones.add(FREE_DRINK);
        assertThrows(NullPointerException.class, () -> milestones.setMilestone(null, FREE_DESSERT));
        assertThrows(NullPointerException.class, () -> milestones.setMilestone(FREE_DRINK, null));
        assertEquals(List.of(FREE_DRINK), milestones.asUnmodifiableObservableList());
    }

    @Test
    public void setMilestone_missingOrUnequalTarget_throwsAndPreservesList() {
        milestones.add(FREE_DRINK);
        assertThrows(MilestoneNotFoundException.class, () ->
                milestones.setMilestone(FREE_DESSERT, FREE_DESSERT));
        assertThrows(MilestoneNotFoundException.class, () ->
                milestones.setMilestone(EDITED_FREE_DRINK, EDITED_FREE_DRINK));
        assertEquals(List.of(FREE_DRINK), milestones.asUnmodifiableObservableList());
    }

    @Test
    public void setMilestone_differentCode_throwsAndPreservesList() {
        String expectedMessage = UniqueMilestoneList.MESSAGE_CODE_CANNOT_BE_CHANGED;
        milestones.add(FREE_DRINK);
        assertThrows(IllegalArgumentException.class, expectedMessage, () ->
                milestones.setMilestone(FREE_DRINK, FREE_DESSERT));
        assertEquals(List.of(FREE_DRINK), milestones.asUnmodifiableObservableList());
    }

    @Test
    public void setMilestone_sameCode_updatesAndRestoresSortedOrder() {
        milestones.setMilestones(List.of(VOUCHER, FREE_DRINK, FREE_DESSERT));
        milestones.setMilestone(FREE_DRINK, EDITED_FREE_DRINK);
        assertEquals(List.of(FREE_DESSERT, EDITED_FREE_DRINK, VOUCHER),
                milestones.asUnmodifiableObservableList());
    }

    @Test
    public void remove_nullMilestone_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> milestones.remove(null));
    }

    @Test
    public void remove_missingOrUnequalMilestone_throwsAndPreservesList() {
        milestones.add(FREE_DRINK);
        assertThrows(MilestoneNotFoundException.class, () -> milestones.remove(FREE_DESSERT));
        assertThrows(MilestoneNotFoundException.class, () -> milestones.remove(EDITED_FREE_DRINK));
        assertEquals(List.of(FREE_DRINK), milestones.asUnmodifiableObservableList());
    }

    @Test
    public void remove_equalMilestone_removesOnlyTarget() {
        milestones.setMilestones(List.of(FREE_DRINK, FREE_DESSERT));
        milestones.remove(new Milestone("FREE500", "Free drink", 500));
        assertEquals(List.of(FREE_DESSERT), milestones.asUnmodifiableObservableList());
    }

    @Test
    public void setMilestones_nullListOrElement_throwsAndPreservesList() {
        milestones.add(FREE_DRINK);
        assertThrows(NullPointerException.class, () -> milestones.setMilestones((List<Milestone>) null));
        assertThrows(NullPointerException.class, () -> milestones.setMilestones((UniqueMilestoneList) null));
        assertThrows(NullPointerException.class, () -> milestones.setMilestones(Arrays.asList(FREE_DESSERT, null)));
        assertEquals(List.of(FREE_DRINK), milestones.asUnmodifiableObservableList());
    }

    @Test
    public void setMilestones_duplicateCodes_throwsAndPreservesList() {
        milestones.add(FREE_DESSERT);
        assertThrows(DuplicateMilestoneException.class, () ->
                milestones.setMilestones(List.of(FREE_DRINK, VOUCHER, EDITED_FREE_DRINK)));
        assertEquals(List.of(FREE_DESSERT), milestones.asUnmodifiableObservableList());
    }

    @Test
    public void setMilestones_validList_copiesSortsAndSupportsEmptyList() {
        milestones.add(FREE_DRINK);
        List<Milestone> replacement = new ArrayList<>(List.of(VOUCHER, FREE_DESSERT));
        milestones.setMilestones(replacement);
        replacement.clear();
        assertEquals(List.of(FREE_DESSERT, VOUCHER), milestones.asUnmodifiableObservableList());
        milestones.setMilestones(List.of());
        assertTrue(milestones.asUnmodifiableObservableList().isEmpty());
    }

    @Test
    public void setMilestones_uniqueList_copiesContents() {
        UniqueMilestoneList replacement = new UniqueMilestoneList();
        replacement.add(VOUCHER);
        milestones.add(FREE_DRINK);
        milestones.setMilestones(replacement);
        replacement.add(FREE_DESSERT);
        assertEquals(List.of(VOUCHER), milestones.asUnmodifiableObservableList());
    }

    @Test
    public void setMilestones_selfOrOwnView_preservesContents() {
        milestones.setMilestones(List.of(VOUCHER, FREE_DRINK));
        milestones.setMilestones(milestones);
        assertEquals(List.of(FREE_DRINK, VOUCHER), milestones.asUnmodifiableObservableList());
        milestones.setMilestones(milestones.asUnmodifiableObservableList());
        assertEquals(List.of(FREE_DRINK, VOUCHER), milestones.asUnmodifiableObservableList());
    }

    @Test
    public void asUnmodifiableObservableList_mutation_throwsAndPreservesList() {
        milestones.add(FREE_DRINK);
        ObservableList<Milestone> view = milestones.asUnmodifiableObservableList();
        assertThrows(UnsupportedOperationException.class, () -> view.add(FREE_DESSERT));
        assertThrows(UnsupportedOperationException.class, () -> view.set(0, FREE_DESSERT));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        assertThrows(UnsupportedOperationException.class, view::clear);
        assertEquals(List.of(FREE_DRINK), view);
    }

    @Test
    public void asUnmodifiableObservableList_changes_updatesViewAndNotifiesListeners() {
        ObservableList<Milestone> view = milestones.asUnmodifiableObservableList();
        List<List<Milestone>> observedContents = new ArrayList<>();
        view.addListener((ListChangeListener<Milestone>) change -> observedContents.add(new ArrayList<>(view)));
        milestones.add(FREE_DRINK);
        milestones.setMilestone(FREE_DRINK, EDITED_FREE_DRINK);
        milestones.remove(EDITED_FREE_DRINK);
        assertEquals(List.of(List.of(FREE_DRINK), List.of(EDITED_FREE_DRINK), List.of()), observedContents);
        assertTrue(view.isEmpty());
    }

    @Test
    public void iterator_usesSortedOrderAndRejectsRemoval() {
        milestones.setMilestones(List.of(VOUCHER, FREE_DRINK, FREE_DESSERT));
        Iterator<Milestone> iterator = milestones.iterator();
        assertEquals(FREE_DESSERT, iterator.next());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(FREE_DRINK, iterator.next());
        assertEquals(VOUCHER, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void equalsAndHashCode_compareCanonicalContents() {
        milestones.setMilestones(List.of(VOUCHER, FREE_DRINK));
        UniqueMilestoneList copy = new UniqueMilestoneList();
        copy.setMilestones(List.of(FREE_DRINK, VOUCHER));
        assertTrue(milestones.equals(milestones));
        assertTrue(milestones.equals(copy));
        assertEquals(milestones.hashCode(), copy.hashCode());
        assertFalse(milestones.equals(null));
        assertFalse(milestones.equals(List.of(FREE_DRINK, VOUCHER)));
        copy.setMilestones(List.of(FREE_DESSERT, VOUCHER));
        assertFalse(milestones.equals(copy));
        copy.setMilestones(List.of(EDITED_FREE_DRINK, VOUCHER));
        assertFalse(milestones.equals(copy));
    }

    @Test
    public void toStringMethod() {
        milestones.setMilestones(List.of(VOUCHER, FREE_DRINK));
        assertEquals(List.of(FREE_DRINK, VOUCHER).toString(), milestones.toString());
    }
}
