package seedu.address.ui;

import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import seedu.address.model.person.Person;

/** Displays the selected person's details and score beside their recent orders. */
public class ProfilePanel extends UiPart<Region> {
    private static final String FXML = "ProfilePanel.fxml";
    private static final int MAX_SCALE_SCORE = 1000;
    private static final int SCORE_TICK_INTERVAL = 200;
    private static final double SCALE_LINE_X = 52;
    private static final double SCALE_TOP_FRACTION = 0.05;
    private static final double SCALE_RANGE_FRACTION = 0.9;

    @FXML
    private Label name;

    @FXML
    private Label phone;

    @FXML
    private Label index;

    @FXML
    private Pane scoreScale;

    @FXML
    private StackPane recentOrdersPlaceholder;

    /**
     * Creates a profile panel for {@code person}.
     *
     * @param person The person to display.
     * @param displayedIndex The person's one-based index in the displayed list, or zero if absent.
     */
    public ProfilePanel(Person person, int displayedIndex) {
        super(FXML);
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        index.setText(displayedIndex == 0 ? "Not in current list" : "List index: " + displayedIndex);
        fillScoreScale(person.getScore().value);
        recentOrdersPlaceholder.getChildren().add(new RecentOrdersPanel(person.getOrderHistory()).getRoot());
    }

    /** Adds the scale line, its labeled ticks, and the current score indicator. */
    private void fillScoreScale(int currentScore) {
        addScaleLine();
        for (int tickScore = 0; tickScore <= MAX_SCALE_SCORE; tickScore += SCORE_TICK_INTERVAL) {
            addScaleTick(tickScore);
        }
        addCurrentScore(currentScore);
    }

    /** Adds a vertical line that resizes with the score scale. */
    private void addScaleLine() {
        Line scaleLine = new Line(SCALE_LINE_X, 0, SCALE_LINE_X, 0);
        scaleLine.endYProperty().bind(scoreScale.heightProperty());
        scaleLine.getStyleClass().add("profile-scale-line");
        scoreScale.getChildren().add(scaleLine);
    }

    /** Adds the current score marker and label, capping the marker at the top of the scale. */
    private void addCurrentScore(int currentScore) {
        DoubleBinding markerY = scorePosition(Math.min(currentScore, MAX_SCALE_SCORE));
        Circle marker = new Circle(SCALE_LINE_X, 0, 5);
        marker.centerYProperty().bind(markerY);
        marker.getStyleClass().add("profile-score-marker");
        scoreScale.getChildren().addAll(marker, createCurrentScoreLabel(currentScore, markerY));
    }

    /** Creates a label beside the current score marker that can wrap as the panel narrows. */
    private Label createCurrentScoreLabel(int currentScore, DoubleBinding markerY) {
        Label scoreLabel = new Label("Current score: " + currentScore);
        scoreLabel.getStyleClass().add("profile-score");
        scoreLabel.setWrapText(true);
        scoreLabel.setLayoutX(SCALE_LINE_X + 14);
        scoreLabel.prefWidthProperty().bind(scoreScale.widthProperty().subtract(SCALE_LINE_X + 14));
        centerLabelAt(scoreLabel, markerY);
        return scoreLabel;
    }

    /** Adds a tick mark and its value to the scale. */
    private void addScaleTick(int tickScore) {
        DoubleBinding tickY = scorePosition(tickScore);
        scoreScale.getChildren().addAll(createTickMark(tickY), createTickLabel(tickScore, tickY));
    }

    /** Creates a tick mark whose vertical position follows the scale. */
    private Line createTickMark(DoubleBinding tickY) {
        Line tick = new Line(SCALE_LINE_X - 8, 0, SCALE_LINE_X, 0);
        tick.startYProperty().bind(tickY);
        tick.endYProperty().bind(tickY);
        tick.getStyleClass().add("profile-scale-tick");
        return tick;
    }

    /** Creates a score label to the left of a tick mark. */
    private Label createTickLabel(int tickScore, DoubleBinding tickY) {
        Label tickLabel = new Label(Integer.toString(tickScore));
        tickLabel.getStyleClass().add("profile-scale-label");
        tickLabel.setPrefWidth(SCALE_LINE_X - 14);
        tickLabel.setAlignment(Pos.CENTER_RIGHT);
        centerLabelAt(tickLabel, tickY);
        return tickLabel;
    }

    /** Keeps a label vertically centered on a position as the scale resizes. */
    private void centerLabelAt(Label label, DoubleBinding positionY) {
        label.layoutYProperty().bind(positionY.subtract(label.heightProperty().divide(2)));
    }

    /** Returns a score's vertical position, leaving space beyond both ends of the scale. */
    private DoubleBinding scorePosition(int scoreValue) {
        double fractionFromTop = 1 - SCALE_TOP_FRACTION
                - (double) scoreValue / MAX_SCALE_SCORE * SCALE_RANGE_FRACTION;
        return scoreScale.heightProperty().multiply(fractionFromTop);
    }
}
