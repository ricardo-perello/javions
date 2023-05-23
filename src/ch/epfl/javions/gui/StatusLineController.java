package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {
    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    /**
     * Constructor for StatusLineController which is responsible for controlling
     * the status bar between the map and the table. Contains number of received messages
     * and the number of visible aircraft.
     */
    public StatusLineController() {
        pane = new BorderPane();
        pane.getStylesheets().add("status.css");
        this.aircraftCountProperty = new SimpleIntegerProperty();
        this.messageCountProperty = new SimpleLongProperty();

        //AIRCRAFT COUNT TEXT
        Text visibleAircraftCountText = new Text();
        visibleAircraftCountText.textProperty().bind(Bindings.createStringBinding(
                () -> "Visible aircraft: " + getAircraftCount(), aircraftCountProperty));
        pane.setLeft(visibleAircraftCountText);

        //MESSAGE COUNT TEXT
        Text visibleMessageCountText = new Text();
        visibleMessageCountText.textProperty().bind(Bindings.createStringBinding(
                () -> "Received messages: " + getMessageCount(), messageCountProperty));
        pane.setRight(visibleMessageCountText);

    }

    /**
     * public method that returns the pane of statusLineController.
     *
     * @return pane of statusLineController
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Sets a new AircraftCount.
     *
     * @param aircraftCount new aircraft count.
     */
    public void setAircraftCount(int aircraftCount) {
        this.aircraftCountProperty.set(aircraftCount);
    }

    /**
     * Sets a new messageCount.
     *
     * @param messageCount new message count.
     */
    public void setMessageCount(long messageCount) {
        this.messageCountProperty.set(messageCount);
    }

    /**
     * Gets the current AircraftCount.
     */
    public int getAircraftCount() {
        return aircraftCountProperty().get();
    }

    /**
     * Gets the current messageCount.
     */
    public long getMessageCount() {
        return messageCountProperty().get();
    }

    /**
     * public method that returns the property of the current aircraftCount
     * @return property of the current aircraftCount.
     */
    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    /**
     * public method that returns the property of the current messageCount
     * @return property of the current messageCount.
     */
    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}
