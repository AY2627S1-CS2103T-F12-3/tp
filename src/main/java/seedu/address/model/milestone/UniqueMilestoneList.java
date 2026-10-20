package seedu.address.model.milestone;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.milestone.exceptions.DuplicateMilestoneException;
import seedu.address.model.milestone.exceptions.MilestoneNotFoundException;

/**
 * Stores milestones by ascending required score and code, enforcing unique case-insensitive codes and disallowing
 * nulls. Duplicate checks use {@link Milestone#isSameMilestone(Milestone)}. Replacement targets and removal use
 * {@link Milestone#equals(Object)} so that a milestone with exactly matching fields is selected.
 */
public class UniqueMilestoneList implements Iterable<Milestone> {
    public static final String MESSAGE_CODE_CANNOT_BE_CHANGED = "Milestone codes cannot be changed.";

    private static final Comparator<Milestone> MILESTONE_COMPARATOR =
            Comparator.comparingInt(Milestone::getRequiredScore)
                    .thenComparing(Milestone::getCode);

    private final ObservableList<Milestone> internalList = FXCollections.observableArrayList();
    private final ObservableList<Milestone> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if a milestone with the same case-insensitive code as {@code toCheck} exists.
     */
    public boolean contains(Milestone toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameMilestone);
    }

    /**
     * Adds a milestone in ascending order of required score and code.
     *
     * @throws DuplicateMilestoneException if its code already exists.
     */
    public void add(Milestone toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateMilestoneException();
        }

        List<Milestone> replacement = new ArrayList<>(internalList);
        replacement.add(toAdd);
        replaceWithSorted(replacement);
    }

    /**
     * Replaces {@code target} with {@code editedMilestone} and restores the list's sorted order.
     * Both milestones must have the same case-insensitive code because milestone identities cannot be edited.
     *
     * @throws MilestoneNotFoundException if no milestone equals {@code target}.
     * @throws IllegalArgumentException if the edited milestone has a different code.
     */
    public void setMilestone(Milestone target, Milestone editedMilestone) {
        requireAllNonNull(target, editedMilestone);
        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new MilestoneNotFoundException();
        }
        if (!target.isSameMilestone(editedMilestone)) {
            throw new IllegalArgumentException(MESSAGE_CODE_CANNOT_BE_CHANGED);
        }

        List<Milestone> replacement = new ArrayList<>(internalList);
        replacement.set(index, editedMilestone);
        replaceWithSorted(replacement);
    }

    /**
     * Removes the milestone equal to {@code toRemove}.
     *
     * @throws MilestoneNotFoundException if no milestone equals {@code toRemove}.
     */
    public void remove(Milestone toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new MilestoneNotFoundException();
        }
    }

    /**
     * Copies the contents of {@code replacement} into this list in sorted order.
     */
    public void setMilestones(UniqueMilestoneList replacement) {
        requireNonNull(replacement);
        setMilestones(replacement.internalList);
    }

    /**
     * Replaces the contents with a sorted copy of {@code milestones}.
     * Invalid input leaves the existing contents unchanged.
     *
     * @throws NullPointerException if the list or a milestone is null.
     * @throws DuplicateMilestoneException if two milestones have the same case-insensitive code.
     */
    public void setMilestones(List<Milestone> milestones) {
        requireNonNull(milestones);
        List<Milestone> replacement = new ArrayList<>(milestones);
        requireAllNonNull(replacement);
        if (!milestonesAreUnique(replacement)) {
            throw new DuplicateMilestoneException();
        }

        replaceWithSorted(replacement);
    }

    /**
     * Returns an unmodifiable observable view that reflects subsequent changes to this list.
     */
    public ObservableList<Milestone> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    /**
     * Returns an iterator over the milestones in sorted order. Removal through the iterator is unsupported.
     */
    @Override
    public Iterator<Milestone> iterator() {
        return internalUnmodifiableList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UniqueMilestoneList otherMilestoneList)) {
            return false;
        }
        return internalList.equals(otherMilestoneList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    private void replaceWithSorted(List<Milestone> milestones) {
        milestones.sort(MILESTONE_COMPARATOR);
        internalList.setAll(milestones);
    }

    private boolean milestonesAreUnique(List<Milestone> milestones) {
        for (int i = 0; i < milestones.size() - 1; i++) {
            for (int j = i + 1; j < milestones.size(); j++) {
                if (milestones.get(i).isSameMilestone(milestones.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
