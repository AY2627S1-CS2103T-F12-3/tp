package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
            Label emptyMessage = (Label) content.getChildren().get(0);

            // EP: empty milestone catalog
            assertEquals("No milestones yet", emptyMessage.getText()); // Boundary value: zero milestones
        });
    }

    @Test
    void constructor_noPerson_showsRequiredPointsWithoutScoreIndicator() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = (Pane) new MilestoneTimeline(MILESTONES).getContent();

            // EP: milestones present with no selected person
            assertEquals(List.of("Points needed: 100", "Points needed: 200", "Points needed: 300"),
                    getStatusTexts(content));
            assertFalse(containsStyleClass(content, "milestone-score-pointer"));
        });
    }

    @Test
    void constructor_oneMilestone_showsSingleMarkerAndRequiredPoints() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = (Pane) new MilestoneTimeline(List.of(FIRST)).getContent();

            // EP: smallest nonempty milestone catalog
            assertEquals("1", getMarker(content, 1).getText()); // Boundary value: one milestone
            assertEquals(List.of("Points needed: 100"), getStatusTexts(content));
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

            // EP: redeemed, eligible but unredeemed, and below required score
            assertEquals(List.of("Redeemed", "Unredeemed", "50 more points needed"), getStatusTexts(content));
        });
    }

    @Test
    void scoreBelowFirstMilestone_placesIndicatorBeforeFirstMarker() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = createTimelineForScore(50);

            // EP: score below the first threshold
            assertTrue(getScoreY(content) < getMarkerCenterY(content, 1));
        });
    }

    @Test
    void scoreBetweenMilestones_placesIndicatorHalfwayBetweenMarkers() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = createTimelineForScore(150);
            double firstY = getMarkerCenterY(content, 1);
            double secondY = getMarkerCenterY(content, 2);

            // EP: score strictly between adjacent thresholds
            assertEquals((firstY + secondY) / 2, getScoreY(content), 0.001);
        });
    }

    @Test
    void scoreAtMilestone_alignsIndicatorAndKeepsArrowOutsideMarker() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = createTimelineForScore(200);
            Label marker = getMarker(content, 2);
            Polygon arrow = (Polygon) getNodeWithStyleClass(content, "milestone-score-arrow");

            // EP: score equals an intermediate threshold
            assertEquals(getMarkerCenterY(content, 2), getScoreY(content), 0.001); // Boundary value: 200
            assertTrue(arrow.getPoints().get(2) < marker.getLayoutX());
        });
    }

    @Test
    void scoreAboveLastMilestone_placesIndicatorAfterLastMarker() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane content = createTimelineForScore(400);

            // EP: score above the final threshold
            assertTrue(getScoreY(content) > getMarkerCenterY(content, 3));
        });
    }

    @Test
    void scoreAroundFirstAndLastMilestones_placesIndicatorAcrossThresholds() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane belowFirst = createTimelineForScore(99);
            Pane atFirst = createTimelineForScore(100);
            Pane aboveFirst = createTimelineForScore(101);
            Pane belowLast = createTimelineForScore(299);
            Pane atLast = createTimelineForScore(300);
            Pane aboveLast = createTimelineForScore(301);

            // EP: score below, at, or above the first threshold
            assertTrue(getScoreY(belowFirst) < getMarkerCenterY(belowFirst, 1)); // Boundary value: 99
            assertEquals(getMarkerCenterY(atFirst, 1), getScoreY(atFirst), 0.001); // Boundary value: 100
            assertTrue(getScoreY(aboveFirst) > getMarkerCenterY(aboveFirst, 1)); // Boundary value: 101

            // EP: score below, at, or above the final threshold
            assertTrue(getScoreY(belowLast) < getMarkerCenterY(belowLast, 3)); // Boundary value: 299
            assertEquals(getMarkerCenterY(atLast, 3), getScoreY(atLast), 0.001); // Boundary value: 300
            assertTrue(getScoreY(aboveLast) > getMarkerCenterY(aboveLast, 3)); // Boundary value: 301
        });
    }

    @Test
    void scoreAroundIntermediateMilestone_movesAcrossMarker() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Pane below = createTimelineForScore(199);
            Pane at = createTimelineForScore(200);
            Pane above = createTimelineForScore(201);

            // EP: score below, at, or above an intermediate threshold
            assertTrue(getScoreY(below) < getMarkerCenterY(below, 2)); // Boundary value: 199
            assertEquals(getMarkerCenterY(at, 2), getScoreY(at), 0.001); // Boundary value: 200
            assertTrue(getScoreY(above) > getMarkerCenterY(above, 2)); // Boundary value: 201
        });
    }

    @Test
    void scoreAroundRequiredPoints_changesStatusAtThreshold() {
        JavaFxTestUtil.runOnFxThread(() -> {
            // EP: score below the required points
            assertEquals("1 more points needed",
                    getStatusTexts(createTimelineForScore(99)).get(0)); // Boundary value: 99

            // EP: score meets or exceeds the required points without redemption
            assertEquals("Unredeemed",
                    getStatusTexts(createTimelineForScore(100)).get(0)); // Boundary value: 100
            assertEquals("Unredeemed",
                    getStatusTexts(createTimelineForScore(101)).get(0)); // Boundary value: 101
        });
    }

    @Test
    void longRewards_resize_keepsFullTextAndRowsSeparate() {
        JavaFxTestUtil.runOnFxThread(() -> {
            String reward = "A long reward description ".repeat(40);
            Milestone first = new Milestone("FIRST", reward, 100);
            Milestone last = new Milestone("LAST", "LongUnbrokenReward".repeat(40), 200);
            MilestoneTimeline timeline = new MilestoneTimeline(List.of(first, last));
            StackPane root = new StackPane(timeline);
            new Scene(root, 500, 300);
            root.getStylesheets().add(getClass().getResource("/view/Extensions.css").toExternalForm());
            root.applyCss();
            root.layout();
            Pane content = (Pane) timeline.getContent();
            List<VBox> rows = content.getChildren().stream().filter(VBox.class::isInstance)
                    .map(VBox.class::cast).toList();
            double wideSecondY = getMarkerCenterY(content, 2);

            root.resize(300, 300);
            root.layout();
            content.layout();

            TextFlow title = (TextFlow) rows.get(0).getChildren().get(0);
            // EP: long wrapped and unbroken reward text in a narrow viewport
            assertEquals(reward, ((Text) title.getChildren().get(0)).getText());
            assertTrue(getMarkerCenterY(content, 2) > wideSecondY);
            assertTrue(rows.get(0).getLayoutY() + rows.get(0).getHeight() < rows.get(1).getLayoutY());
            assertTrue(content.getPrefHeight() > rows.get(1).getLayoutY() + rows.get(1).getHeight());
            assertTrue(title.getHeight() >= title.prefHeight(title.getWidth()) - 1);

            root.resize(500, 300);
            root.layout();
            // EP: reward layout returns to its wide-viewport position
            assertEquals(wideSecondY, getMarkerCenterY(content, 2), 1);
        });
    }

    @Test
    void longReward_scoreBetweenMilestones_followsExpandedSpacing() {
        JavaFxTestUtil.runOnFxThread(() -> {
            Milestone first = new Milestone("FIRST", "Long reward ".repeat(40), 100);
            Person person = new PersonBuilder().withScore(new Score(150)).build();
            MilestoneTimeline timeline = new MilestoneTimeline(person, List.of(first, SECOND));
            StackPane root = new StackPane(timeline);
            new Scene(root, 350, 300);
            root.getStylesheets().add(getClass().getResource("/view/Extensions.css").toExternalForm());
            root.applyCss();
            root.layout();
            Pane content = (Pane) timeline.getContent();

            // EP: score between thresholds when a long reward expands row spacing
            assertTrue(getMarkerCenterY(content, 2) - getMarkerCenterY(content, 1) > 87.2);
            assertEquals((getMarkerCenterY(content, 1) + getMarkerCenterY(content, 2)) / 2,
                    getScoreY(content), 0.001);
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
