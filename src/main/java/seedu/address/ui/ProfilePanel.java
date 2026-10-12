package seedu.address.ui;

import java.util.List;

import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import seedu.address.model.order.Order;
import seedu.address.model.person.Person;

/** Displays the selected person's details and recent orders. */
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
    private VBox orders;

    /** Creates a profile panel for {@code person}, using zero when it is not in the displayed list. */
    public ProfilePanel(Person person, int displayedIndex) {
        super(FXML);
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        index.setText(displayedIndex == 0 ? "Not in current list" : "List index: " + displayedIndex);
        fillScoreScale(person.getScore().value);

        List<Order> recentOrders = person.getOrderHistory().getOrders();
        if (recentOrders.isEmpty()) {
            Label emptyOrders = new Label("No orders yet");
            emptyOrders.getStyleClass().add("profile-empty-orders");
            orders.getChildren().add(emptyOrders);
        } else {
            recentOrders.stream()
                    .map(this::createOrderRow)
                    .forEach(orders.getChildren()::add);
        }
    }

    private void fillScoreScale(int currentScore) {
        Line scaleLine = new Line(SCALE_LINE_X, 0, SCALE_LINE_X, 0);
        scaleLine.endYProperty().bind(scoreScale.heightProperty());
        scaleLine.getStyleClass().add("profile-scale-line");
        scoreScale.getChildren().add(scaleLine);

        for (int tickScore = 0; tickScore <= MAX_SCALE_SCORE; tickScore += SCORE_TICK_INTERVAL) {
            addScaleTick(tickScore);
        }

        int markerScore = Math.min(currentScore, MAX_SCALE_SCORE);
        DoubleBinding markerY = scorePosition(markerScore);
        Circle marker = new Circle(SCALE_LINE_X, 0, 5);
        marker.centerYProperty().bind(markerY);
        marker.getStyleClass().add("profile-score-marker");

        Label scoreLabel = new Label("Current score: " + currentScore);
        scoreLabel.getStyleClass().add("profile-score");
        scoreLabel.setWrapText(true);
        scoreLabel.setLayoutX(SCALE_LINE_X + 14);
        scoreLabel.prefWidthProperty().bind(scoreScale.widthProperty().subtract(SCALE_LINE_X + 14));
        scoreLabel.layoutYProperty().bind(markerY.subtract(scoreLabel.heightProperty().divide(2)));
        scoreScale.getChildren().addAll(marker, scoreLabel);
    }

    private void addScaleTick(int tickScore) {
        DoubleBinding tickY = scorePosition(tickScore);
        Line tick = new Line(SCALE_LINE_X - 8, 0, SCALE_LINE_X, 0);
        tick.startYProperty().bind(tickY);
        tick.endYProperty().bind(tickY);
        tick.getStyleClass().add("profile-scale-tick");

        Label tickLabel = new Label(Integer.toString(tickScore));
        tickLabel.getStyleClass().add("profile-scale-label");
        tickLabel.setPrefWidth(SCALE_LINE_X - 14);
        tickLabel.setAlignment(Pos.CENTER_RIGHT);
        tickLabel.layoutYProperty().bind(tickY.subtract(tickLabel.heightProperty().divide(2)));
        scoreScale.getChildren().addAll(tick, tickLabel);
    }

    private DoubleBinding scorePosition(int scoreValue) {
        double fractionFromTop = 1 - SCALE_TOP_FRACTION
                - (double) scoreValue / MAX_SCALE_SCORE * SCALE_RANGE_FRACTION;
        return scoreScale.heightProperty().multiply(fractionFromTop);
    }

    private HBox createOrderRow(Order order) {
        Label itemName = new Label(order.itemName);
        itemName.setWrapText(true);
        itemName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(itemName, Priority.ALWAYS);

        Label quantity = new Label(Integer.toString(order.quantity));
        quantity.getStyleClass().add("profile-order-quantity");

        HBox row = new HBox(itemName, quantity);
        row.getStyleClass().add("profile-order-row");
        return row;
    }
}
