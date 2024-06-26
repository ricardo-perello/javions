package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


/**
 * StatusLineController class is responsible for controlling the status bar between the map and the table.
 * It displays the number of received messages and the number of visible aircraft.
 *
 * @author Ricardo Perello Mas (357241)
 * @author Alejandro Meredith Romero (360864)
 */

public final class StatusLineController {
    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;
    private static final String VISIBLE_AIRCRAFT = "Aéronefs visibles : ";
    private static final String RECEIVED_MESSAGES = "Messages reçus : ";


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
                () -> VISIBLE_AIRCRAFT + aircraftCountProperty.getValue(), aircraftCountProperty));
        pane.setLeft(visibleAircraftCountText);

        //MESSAGE COUNT TEXT
        Text visibleMessageCountText = new Text();
        visibleMessageCountText.textProperty().bind(Bindings.createStringBinding(
                () -> RECEIVED_MESSAGES + messageCountProperty.getValue(), messageCountProperty));
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
     * public method that returns the property of the current aircraftCount
     *
     * @return property of the current aircraftCount.
     */
    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    /**
     * public method that returns the property of the current messageCount
     *
     * @return property of the current messageCount.
     */
    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}
