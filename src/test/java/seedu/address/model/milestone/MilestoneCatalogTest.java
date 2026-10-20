package seedu.address.model.milestone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.milestone.exceptions.DuplicateMilestoneException;

public class MilestoneCatalogTest {
    private static final Milestone FREE_DRINK = new Milestone("FREE500", "Free drink", 500);
    private static final Milestone EDITED_FREE_DRINK = new Milestone("FREE500", "Free drink and snack", 600);
    private static final Milestone FREE_DESSERT = new Milestone("DESSERT500", "Free dessert", 500);
    private static final Milestone VOUCHER = new Milestone("VOUCHER1000", "$10 voucher", 1000);

    private final MilestoneCatalog catalog = new MilestoneCatalog();

    @Test
    public void constructor_emptyCatalog_hasNoMilestones() {
        assertTrue(catalog.getMilestoneList().isEmpty());
    }

    @Test
    public void constructor_readOnlyCatalog_copiesContentsIndependently() {
        catalog.addMilestone(FREE_DRINK);
        MilestoneCatalog copy = new MilestoneCatalog(catalog);

        catalog.setMilestone(FREE_DRINK, EDITED_FREE_DRINK);
        assertEquals(List.of(FREE_DRINK), copy.getMilestoneList());
        copy.addMilestone(FREE_DESSERT);
        assertEquals(List.of(EDITED_FREE_DRINK), catalog.getMilestoneList());
        assertEquals(List.of(FREE_DESSERT, FREE_DRINK), copy.getMilestoneList());
    }

    @Test
    public void constructorAndResetData_nullCatalog_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MilestoneCatalog(null));
        catalog.addMilestone(FREE_DRINK);
        assertThrows(NullPointerException.class, () -> catalog.resetData(null));
        assertEquals(List.of(FREE_DRINK), catalog.getMilestoneList());
    }

    @Test
    public void resetData_validCatalog_copiesContentsAndUpdatesExistingView() {
        catalog.addMilestone(FREE_DRINK);
        ObservableList<Milestone> view = catalog.getMilestoneList();
        MilestoneCatalog replacement = new MilestoneCatalog();
        replacement.addMilestone(FREE_DESSERT);

        catalog.resetData(replacement);
        replacement.removeMilestone(FREE_DESSERT);

        assertEquals(List.of(FREE_DESSERT), view);
        catalog.resetData(catalog);
        assertEquals(List.of(FREE_DESSERT), view);
    }

    @Test
    public void resetData_duplicateCodes_throwsAndPreservesCatalog() {
        catalog.addMilestone(FREE_DESSERT);
        ReadOnlyMilestoneCatalog invalidCatalog = () -> FXCollections.unmodifiableObservableList(
                FXCollections.observableArrayList(FREE_DRINK, EDITED_FREE_DRINK));

        assertThrows(DuplicateMilestoneException.class, () -> catalog.resetData(invalidCatalog));
        assertEquals(List.of(FREE_DESSERT), catalog.getMilestoneList());
    }

    @Test
    public void resetData_nullListOrElement_throwsAndPreservesCatalog() {
        catalog.addMilestone(FREE_DESSERT);
        ReadOnlyMilestoneCatalog nullListCatalog = () -> null;
        ReadOnlyMilestoneCatalog nullMilestoneCatalog = () -> FXCollections.unmodifiableObservableList(
                FXCollections.observableArrayList(Arrays.asList(FREE_DRINK, null)));

        assertThrows(NullPointerException.class, () -> catalog.resetData(nullListCatalog));
        assertThrows(NullPointerException.class, () -> catalog.resetData(nullMilestoneCatalog));
        assertEquals(List.of(FREE_DESSERT), catalog.getMilestoneList());
    }

    @Test
    public void setMilestones_validList_copiesSortsAndSupportsEmptyList() {
        catalog.addMilestone(FREE_DRINK);
        List<Milestone> replacement = new ArrayList<>(List.of(VOUCHER, FREE_DESSERT));
        catalog.setMilestones(replacement);
        replacement.clear();

        assertEquals(List.of(FREE_DESSERT, VOUCHER), catalog.getMilestoneList());
        catalog.setMilestones(List.of());
        assertTrue(catalog.getMilestoneList().isEmpty());
    }

    @Test
    public void hasMilestone_comparesCodesAndRejectsNull() {
        assertFalse(catalog.hasMilestone(FREE_DRINK));
        catalog.addMilestone(FREE_DRINK);
        assertTrue(catalog.hasMilestone(EDITED_FREE_DRINK));
        assertTrue(catalog.hasMilestone(new Milestone("free500", "Different reward", 700)));
        assertFalse(catalog.hasMilestone(FREE_DESSERT));
        assertThrows(NullPointerException.class, () -> catalog.hasMilestone(null));
    }

    @Test
    public void milestoneOperations_updateCatalogAndRejectDuplicates() {
        catalog.addMilestone(FREE_DRINK);
        catalog.addMilestone(FREE_DESSERT);
        assertThrows(DuplicateMilestoneException.class, () -> catalog.addMilestone(EDITED_FREE_DRINK));

        catalog.setMilestone(FREE_DRINK, EDITED_FREE_DRINK);
        assertEquals(List.of(FREE_DESSERT, EDITED_FREE_DRINK), catalog.getMilestoneList());
        catalog.removeMilestone(FREE_DESSERT);
        assertEquals(List.of(EDITED_FREE_DRINK), catalog.getMilestoneList());
    }

    @Test
    public void getMilestoneList_readOnlyCatalogView_rejectsMutationAndReflectsUpdates() {
        ReadOnlyMilestoneCatalog readOnlyCatalog = catalog;
        ObservableList<Milestone> view = readOnlyCatalog.getMilestoneList();
        catalog.addMilestone(FREE_DRINK);

        assertEquals(List.of(FREE_DRINK), view);
        assertThrows(UnsupportedOperationException.class, () -> view.add(FREE_DESSERT));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        assertEquals(List.of(FREE_DRINK), catalog.getMilestoneList());
    }

    @Test
    public void equalsAndHashCode_compareCanonicalContents() {
        catalog.setMilestones(List.of(VOUCHER, FREE_DRINK));
        MilestoneCatalog copy = new MilestoneCatalog();
        copy.setMilestones(List.of(FREE_DRINK, VOUCHER));

        assertTrue(catalog.equals(catalog));
        assertTrue(catalog.equals(copy));
        assertEquals(catalog.hashCode(), copy.hashCode());
        assertFalse(catalog.equals(null));
        assertFalse(catalog.equals(List.of(FREE_DRINK, VOUCHER)));
        copy.setMilestones(List.of(FREE_DESSERT, VOUCHER));
        assertFalse(catalog.equals(copy));
    }

    @Test
    public void toStringMethod() {
        catalog.setMilestones(List.of(VOUCHER, FREE_DRINK));
        String expected = MilestoneCatalog.class.getCanonicalName()
                + "{milestones=" + List.of(FREE_DRINK, VOUCHER) + "}";
        assertEquals(expected, catalog.toString());
    }
}
