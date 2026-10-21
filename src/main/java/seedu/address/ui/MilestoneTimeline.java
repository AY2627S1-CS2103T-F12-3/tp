package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Person;

/** Displays equally spaced milestones on a scrollable timeline, with member progress when available. */
public class MilestoneTimeline extends ScrollPane {
    private static final double LINE_X = 94;
    private static final double LINE_START_Y = 22;
    private static final double FIRST_MILESTONE_Y = 92;
    private static final double MILESTONE_SPACING = 79.2;
    private static final double END_PADDING = 100;
    private static final double REWARD_X = 110;
    private static final double MILESTONE_NUMBER_SIZE = 42;
    private static final double SCORE_INDICATOR_SHIFT_X = -6;

    private final Pane timeline = new Pane();
    /** Null when the timeline is shown without a selected member. */
    private final Person selectedPerson;

    /** Creates a timeline showing the selected member's progress through the ordered milestones. */
    public MilestoneTimeline(Person person, List<Milestone> milestones) {
        selectedPerson = requireNonNull(person);
        initializeTimeline(milestones);
    }

    /** Creates a timeline of the ordered milestones without member progress. */
    public MilestoneTimeline(List<Milestone> milestones) {
        selectedPerson = null;
        initializeTimeline(milestones);
    }

    private void initializeTimeline(List<Milestone> milestones) {
        configureScrolling();
        if (milestones.isEmpty()) {
            showEmptyMessage();
        } else {
            showMilestones(milestones);
        }
    }

    private void configureScrolling() {
        getStyleClass().add("milestone-scroll");
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setPannable(true);
        setContent(timeline);
    }

    private void showEmptyMessage() {
        Label emptyMessage = new Label("No milestones yet");
        emptyMessage.getStyleClass().add("milestone-empty");
        setFitToHeight(true);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setContent(new StackPane(emptyMessage));
    }

    private void showMilestones(List<Milestone> milestones) {
        double timelineHeight = calculateMilestoneY(milestones.size() - 1) + END_PADDING;
        timeline.setPrefHeight(timelineHeight);
        addTimelineLine(timelineHeight);
        addMilestoneRows(milestones);
        if (selectedPerson != null) {
            addScoreIndicator(selectedPerson.getScore().value, milestones);
        }
    }

    private void addMilestoneRows(List<Milestone> milestones) {
        for (int i = 0; i < milestones.size(); i++) {
            addMilestoneRow(milestones.get(i), i);
        }
    }

    private void addTimelineLine(double height) {
        Line line = new Line(LINE_X, LINE_START_Y, LINE_X, height - 20);
        line.getStyleClass().add("milestone-line");
        Circle start = new Circle(LINE_X, LINE_START_Y, 6);
        start.getStyleClass().add("milestone-endpoint");
        Polygon arrow = new Polygon(LINE_X - 8, height - 28, LINE_X + 8, height - 28,
                LINE_X, height - 12);
        arrow.getStyleClass().add("milestone-arrow");
        timeline.getChildren().addAll(line, start, arrow);
    }

    private void addMilestoneRow(Milestone milestone, int index) {
        double y = calculateMilestoneY(index);
        timeline.getChildren().addAll(createMilestoneNumber(index, y),
                createMilestoneDetails(milestone, y));
    }

    private Label createMilestoneNumber(int index, double y) {
        Label number = new Label(Integer.toString(index + 1));
        number.getStyleClass().add("milestone-number");
        number.setAlignment(Pos.CENTER);
        number.setMinSize(MILESTONE_NUMBER_SIZE, MILESTONE_NUMBER_SIZE);
        number.setPrefSize(MILESTONE_NUMBER_SIZE, MILESTONE_NUMBER_SIZE);
        number.setLayoutX(LINE_X - MILESTONE_NUMBER_SIZE / 2);
        number.setLayoutY(y - MILESTONE_NUMBER_SIZE / 2);
        return number;
    }

    private VBox createMilestoneDetails(Milestone milestone, double y) {
        TextFlow title = createMilestoneTitle(milestone);
        Label status = createMilestoneStatus(milestone, title);
        VBox details = new VBox(4, title, status);
        details.setLayoutX(REWARD_X);
        details.setLayoutY(y - 23);
        return details;
    }

    private TextFlow createMilestoneTitle(Milestone milestone) {
        Text description = new Text(milestone.getRewardDescription());
        description.getStyleClass().add("milestone-reward");
        Text code = new Text(" [" + milestone.getCode() + "]");
        code.getStyleClass().add("milestone-code");
        TextFlow title = new TextFlow(description, code);
        title.prefWidthProperty().bind(timeline.widthProperty().subtract(REWARD_X + 12));
        return title;
    }

    private Label createMilestoneStatus(Milestone milestone, TextFlow title) {
        Label status = new Label(determineStatusText(milestone));
        status.getStyleClass().add("milestone-status");
        status.setWrapText(true);
        status.prefWidthProperty().bind(title.prefWidthProperty());
        return status;
    }

    private String determineStatusText(Milestone milestone) {
        if (selectedPerson == null) {
            return "Points needed: " + milestone.getRequiredScore();
        }
        if (selectedPerson.getRedeemedMilestoneRewardHistory().contains(milestone)) {
            return "Redeemed";
        }
        long scoreAway = (long) milestone.getRequiredScore() - selectedPerson.getScore().value;
        return scoreAway <= 0 ? "Unredeemed" : scoreAway + " more points needed";
    }

    private void addScoreIndicator(int score, List<Milestone> milestones) {
        double y = calculateScoreY(score, milestones);
        timeline.getChildren().addAll(createScoreLabel(score, y), createScorePointer(y), createScoreArrow(y));
    }

    private Label createScoreLabel(int score, double y) {
        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.getStyleClass().add("milestone-score");
        scoreLabel.setAlignment(Pos.CENTER_RIGHT);
        scoreLabel.layoutYProperty().bind(scoreLabel.heightProperty().multiply(-0.5).add(y));
        scoreLabel.setPrefWidth(LINE_X - 28);
        scoreLabel.setTranslateX(SCORE_INDICATOR_SHIFT_X);
        return scoreLabel;
    }

    private Line createScorePointer(double y) {
        Line pointer = new Line(LINE_X - 24, y, LINE_X - 5, y);
        pointer.getStyleClass().add("milestone-score-pointer");
        pointer.setTranslateX(SCORE_INDICATOR_SHIFT_X);
        return pointer;
    }

    private Polygon createScoreArrow(double y) {
        Polygon arrow = new Polygon(LINE_X - 12, y - 5, LINE_X - 4, y,
                LINE_X - 12, y + 5);
        arrow.getStyleClass().add("milestone-score-arrow");
        arrow.setTranslateX(SCORE_INDICATOR_SHIFT_X);
        return arrow;
    }

    private double calculateScoreY(int score, List<Milestone> milestones) {
        if (score < milestones.get(0).getRequiredScore()) {
            return FIRST_MILESTONE_Y - MILESTONE_SPACING / 3;
        }
        for (int i = 1; i < milestones.size(); i++) {
            if (score <= milestones.get(i).getRequiredScore()) {
                return interpolateScoreY(score, milestones.get(i - 1), milestones.get(i), i);
            }
        }
        int lastIndex = milestones.size() - 1;
        if (score == milestones.get(lastIndex).getRequiredScore()) {
            return calculateMilestoneY(lastIndex);
        }
        return calculateMilestoneY(lastIndex) + MILESTONE_SPACING / 2;
    }

    private double interpolateScoreY(int score, Milestone previous, Milestone next, int nextIndex) {
        long scoreRange = (long) next.getRequiredScore() - previous.getRequiredScore();
        if (scoreRange == 0) {
            return calculateMilestoneY(nextIndex);
        }
        double progress = (double) ((long) score - previous.getRequiredScore()) / scoreRange;
        return calculateMilestoneY(nextIndex - 1) + progress * MILESTONE_SPACING;
    }

    private double calculateMilestoneY(int index) {
        return FIRST_MILESTONE_Y + index * MILESTONE_SPACING;
    }
}
