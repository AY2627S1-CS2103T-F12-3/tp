package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;

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
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.milestone.Milestone;
import seedu.address.model.person.Person;

/** Displays equally spaced milestones on a scrollable timeline, with member progress when available. */
public class MilestoneTimeline extends ScrollPane {
    private static final Logger logger = LogsCenter.getLogger(MilestoneTimeline.class);
    private static final double LINE_X = 108;
    private static final double LINE_START_Y = 22;
    private static final double FIRST_MILESTONE_Y = 92;
    private static final double MILESTONE_SPACING = 87.2;
    private static final double END_PADDING = 64;
    private static final double REWARD_X = 135;
    private static final double MILESTONE_NUMBER_SIZE = 34;
    private static final double SCORE_INDICATOR_SHIFT_X = -6;
    private static final double SCORE_LABEL_WIDTH = 72;
    private static final double SCORE_ARROW_WIDTH = 8;
    private static final double SCORE_ARROW_HALF_HEIGHT = 5;

    private final Pane timeline = new Pane();
    /** Null when the timeline is shown without a selected member. */
    private final Person selectedPerson;

    /**
     * Creates a timeline showing the selected member's progress through the ordered milestones.
     *
     * @param person The member whose progress is shown.
     * @param milestones The ordered milestones to display.
     */
    public MilestoneTimeline(Person person, List<Milestone> milestones) {
        selectedPerson = requireNonNull(person);
        initializeTimeline(milestones);
    }

    /**
     * Creates a timeline of the ordered milestones without member progress.
     *
     * @param milestones The ordered milestones to display.
     */
    public MilestoneTimeline(List<Milestone> milestones) {
        selectedPerson = null;
        initializeTimeline(milestones);
    }

    /** Configures the timeline and displays either its milestones or its empty state. */
    private void initializeTimeline(List<Milestone> milestones) {
        configureScrolling();
        if (milestones.isEmpty()) {
            showEmptyMessage();
        } else {
            showMilestones(milestones);
        }
    }

    /** Makes the timeline scroll vertically within its available space. */
    private void configureScrolling() {
        getStyleClass().add("milestone-scroll");
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setPannable(true);
        setContent(timeline);
    }

    /** Centers the empty message when no milestones are available. */
    private void showEmptyMessage() {
        logger.fine("No milestones configured, unable to display milestones");
        Label emptyMessage = new Label("No milestones yet");
        emptyMessage.getStyleClass().add("milestone-empty");
        setFitToHeight(true);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setContent(new StackPane(emptyMessage));
    }

    /** Draws the timeline, milestone rows, and selected member's score when available. */
    private void showMilestones(List<Milestone> milestones) {
        double timelineHeight = calculateMilestoneY(milestones.size() - 1) + END_PADDING;
        timeline.setPrefHeight(timelineHeight);
        addTimelineLine(timelineHeight);
        addMilestoneRows(milestones);
        if (selectedPerson != null) {
            addScoreIndicator(selectedPerson.getScore().value, milestones);
        }
    }

    /** Adds a numbered row for each milestone in catalog order. */
    private void addMilestoneRows(List<Milestone> milestones) {
        for (int i = 0; i < milestones.size(); i++) {
            addMilestoneRow(milestones.get(i), i);
        }
    }

    /** Adds the vertical line, its starting dot, and its ending arrow. */
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

    /** Adds one milestone's number, reward, and status at its timeline position. */
    private void addMilestoneRow(Milestone milestone, int index) {
        double y = calculateMilestoneY(index);
        timeline.getChildren().addAll(createMilestoneNumber(index, y),
                createMilestoneDetails(milestone, y));
    }

    /** Creates a numbered marker centered on the timeline line at {@code y}. */
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

    /** Creates the reward title and status beside a milestone marker. */
    private VBox createMilestoneDetails(Milestone milestone, double y) {
        TextFlow title = createMilestoneTitle(milestone);
        Label status = createMilestoneStatus(milestone, title);
        VBox details = new VBox(4, title, status);
        details.setLayoutX(REWARD_X);
        details.setLayoutY(y - 20);
        return details;
    }

    /** Creates a wrapping reward title containing its description. */
    private TextFlow createMilestoneTitle(Milestone milestone) {
        Text description = new Text(milestone.getRewardDescription());
        description.getStyleClass().add("milestone-reward");
        TextFlow title = new TextFlow(description);
        title.prefWidthProperty().bind(timeline.widthProperty().subtract(REWARD_X + 12));
        return title;
    }

    /** Creates a status label that wraps to the same width as the title. */
    private Label createMilestoneStatus(Milestone milestone, TextFlow title) {
        Label status = new Label(determineStatusText(milestone));
        status.getStyleClass().add("milestone-status");
        status.setWrapText(true);
        status.prefWidthProperty().bind(title.prefWidthProperty());
        return status;
    }

    /** Returns the milestone's required points or the selected member's redemption status. */
    private String determineStatusText(Milestone milestone) {
        if (selectedPerson == null) {
            return "Points needed: " + milestone.getRequiredScore();
        }
        if (selectedPerson.hasRedeemed(milestone)) {
            return "Redeemed";
        }
        long scoreAway = (long) milestone.getRequiredScore() - selectedPerson.getScore().value;
        return scoreAway <= 0 ? "Unredeemed" : scoreAway + " more points needed";
    }

    /** Adds the selected member's score label and pointer beside the timeline. */
    private void addScoreIndicator(int score, List<Milestone> milestones) {
        double y = calculateScoreY(score, milestones);
        double arrowTipX = calculateScoreArrowTipX(y, milestones);
        timeline.getChildren().addAll(createScoreLabel(score, y),
                createScorePointer(y, arrowTipX), createScoreArrow(y, arrowTipX));
    }

    /** Creates a wrapping score label inside the timeline's left edge, centered at {@code y}. */
    private Label createScoreLabel(int score, double y) {
        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.getStyleClass().add("milestone-score");
        scoreLabel.setAlignment(Pos.CENTER_RIGHT);
        scoreLabel.setTextAlignment(TextAlignment.RIGHT);
        scoreLabel.setWrapText(true);
        scoreLabel.layoutYProperty().bind(scoreLabel.heightProperty().multiply(-0.5).add(y));
        scoreLabel.setPrefWidth(SCORE_LABEL_WIDTH);
        return scoreLabel;
    }

    /** Creates the horizontal line connecting the score label to its arrow. */
    private Line createScorePointer(double y, double arrowTipX) {
        Line pointer = new Line(SCORE_LABEL_WIDTH, y, arrowTipX - 1, y);
        pointer.getStyleClass().add("milestone-score-pointer");
        return pointer;
    }

    /** Creates the arrow pointing from the score label toward the timeline. */
    private Polygon createScoreArrow(double y, double arrowTipX) {
        Polygon arrow = new Polygon(arrowTipX - SCORE_ARROW_WIDTH, y - SCORE_ARROW_HALF_HEIGHT,
                arrowTipX, y, arrowTipX - SCORE_ARROW_WIDTH, y + SCORE_ARROW_HALF_HEIGHT);
        arrow.getStyleClass().add("milestone-score-arrow");
        return arrow;
    }

    /** Moves the arrow tip outside a numbered marker when the score is near that marker. */
    private double calculateScoreArrowTipX(double y, List<Milestone> milestones) {
        double markerRadius = MILESTONE_NUMBER_SIZE / 2;
        for (int i = 0; i < milestones.size(); i++) {
            if (Math.abs(y - calculateMilestoneY(i)) <= markerRadius + SCORE_ARROW_HALF_HEIGHT) {
                return LINE_X - markerRadius - 1;
            }
        }
        return LINE_X - 4 + SCORE_INDICATOR_SHIFT_X;
    }

    /** Returns the score's vertical position relative to the milestone thresholds. */
    private double calculateScoreY(int score, List<Milestone> milestones) {
        assert !milestones.isEmpty() : "Score position requires at least one milestone";
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

    /** Interpolates the score's vertical position between two milestone thresholds. */
    private double interpolateScoreY(int score, Milestone previous, Milestone next, int nextIndex) {
        assert nextIndex > 0 : "The next milestone must have a preceding milestone";
        assert previous.getRequiredScore() <= score && score <= next.getRequiredScore()
                : "Score must fall between the ordered milestone thresholds";
        long scoreRange = (long) next.getRequiredScore() - previous.getRequiredScore();
        if (scoreRange == 0) {
            return calculateMilestoneY(nextIndex);
        }
        double progress = (double) ((long) score - previous.getRequiredScore()) / scoreRange;
        return calculateMilestoneY(nextIndex - 1) + progress * MILESTONE_SPACING;
    }

    /** Returns the center position for the milestone at {@code index}. */
    private double calculateMilestoneY(int index) {
        return FIRST_MILESTONE_Y + index * MILESTONE_SPACING;
    }
}
