package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Person;
import seedu.address.model.person.Score;
import seedu.address.model.reward.RedeemedMilestoneRewardHistory;
import seedu.address.testutil.PersonBuilder;

class MilestoneTimelineTest {
    private static final Milestone FIRST = new Milestone("FIRST", "First reward", 100);
    private static final Milestone SECOND = new Milestone("SECOND", "Second reward", 200);
    private static final Milestone THIRD = new Milestone("THIRD", "Third reward", 300);
    private static final List<Milestone> MILESTONES = List.of(FIRST, SECOND, THIRD);

    @BeforeAll
    static void initializeJavaFx() {
        JavaFxTestUtil.initializeJavaFx();
    }

    @Test
    void constructor_noMilestones_showsEmptyMessage() {
        JavaFxTestUtil.runOnFxThread(() -> {
            MilestoneTimeline timeline = new MilestoneTimeline(List.of());
            StackPane content = (StackPane) timeline.getContent();

            assertEquals("No milestones yet", ((Label) content.getChildren().get(0)).getText());
        });
    }

    @Test
    void constructor_noPerson_showsRequiredPointsWithoutScoreIndicator() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = (Pane) new MilestoneTimeline(MILESTONES).getContent();

            assertEquals(List.of("Points needed: 100", "Points needed: 200", "Points needed: 300"),
                    getStatusTexts(content));
            assertFalse(containsStyleClass(content, "milestone-score-pointer"));
        });
    }

    @Test
    void constructor_redeemedAndUnredeemedMilestones_showsCorrectStatuses() {
        JavaFxTestUtil.runOnFxThread(() -> {
            RedeemedMilestoneRewardHistory history = new RedeemedMilestoneRewardHistory()
                    .addRedeemedMilestoneReward(FIRST);
            Person person = new PersonBuilder().withScore(new Score(250))
                    .withRedeemedMilestoneRewardHistory(history).build();
            Pane content = (Pane) new MilestoneTimeline(person, MILESTONES).getContent();

            assertEquals(List.of("Redeemed", "Unredeemed", "50 more points needed"), getStatusTexts(content));
        });
    }

    @Test
    void scoreBelowFirstMilestone_placesIndicatorBeforeFirstMarker() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = createTimelineForScore(50);

            assertTrue(getScoreY(content) < getMarkerCenterY(content, 1));
        });
    }

    @Test
    void scoreBetweenMilestones_placesIndicatorHalfwayBetweenMarkers() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = createTimelineForScore(150);
            double firstY = getMarkerCenterY(content, 1);
            double secondY = getMarkerCenterY(content, 2);

            assertEquals((firstY + secondY) / 2, getScoreY(content), 0.001);
        });
    }

    @Test
    void scoreAtMilestone_alignsIndicatorAndKeepsArrowOutsideMarker() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = createTimelineForScore(200);
            Label marker = getMarker(content, 2);
            Polygon arrow = (Polygon) getNodeWithStyleClass(content, "milestone-score-arrow");

            assertEquals(getMarkerCenterY(content, 2), getScoreY(content), 0.001);
            assertTrue(arrow.getPoints().get(2) < marker.getLayoutX());
        });
    }

    @Test
    void scoreAboveLastMilestone_placesIndicatorAfterLastMarker() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = createTimelineForScore(400);

            assertTrue(getScoreY(content) > getMarkerCenterY(content, 3));
        });
    }

    @Test
    void scoreAtMaximum_fitsNineDigitsInScoreLabel() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = createTimelineForScore(Score.MAX_SCORE);
            Label scoreLabel = (Label) getNodeWithStyleClass(content, "milestone-score");
            Text digits = new Text(Integer.toString(Score.MAX_SCORE));
            digits.setFont(scoreLabel.getFont());

            double requiredWidth = digits.getLayoutBounds().getWidth()
                    + scoreLabel.getPadding().getLeft() + scoreLabel.getPadding().getRight();
            assertTrue(scoreLabel.getPrefWidth() >= requiredWidth);
        });
    }

    private static Pane createTimelineForScore(int score) {
        Person person = new PersonBuilder().withScore(new Score(score)).build();
        return (Pane) new MilestoneTimeline(person, MILESTONES).getContent();
    }

    private static List<String> getStatusTexts(Pane content) {
        return content.getChildren().stream()
                .filter(VBox.class::isInstance)
                .map(VBox.class::cast)
                .map(details -> ((Label) details.getChildren().get(1)).getText())
                .toList();
    }

    private static double getScoreY(Pane content) {
        return ((Line) getNodeWithStyleClass(content, "milestone-score-pointer")).getStartY();
    }

    private static double getMarkerCenterY(Pane content, int number) {
        Label marker = getMarker(content, number);
        return marker.getLayoutY() + marker.getPrefHeight() / 2;
    }

    private static Label getMarker(Pane content, int number) {
        return content.getChildren().stream()
                .filter(node -> node.getStyleClass().contains("milestone-number"))
                .map(Label.class::cast)
                .filter(marker -> marker.getText().equals(Integer.toString(number)))
                .findFirst().orElseThrow();
    }

    private static boolean containsStyleClass(Pane content, String styleClass) {
        return content.getChildren().stream().anyMatch(node -> node.getStyleClass().contains(styleClass));
    }

    private static Node getNodeWithStyleClass(Pane content, String styleClass) {
        return content.getChildren().stream()
                .filter(node -> node.getStyleClass().contains(styleClass))
                .findFirst().orElseThrow();
    }

}
