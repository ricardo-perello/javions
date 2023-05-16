package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public final class StatusLineController {
    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    public StatusLineController() {
        pane = new BorderPane();
        pane.getStylesheets().add("status.css");
        this.aircraftCountProperty = new SimpleIntegerProperty();
        this.messageCountProperty = new SimpleLongProperty();

        //AIRCRAFT COUNT TEXT
        Text visibleAircraftCountText = new Text();
        visibleAircraftCountText.textProperty().bind(Bindings.createStringBinding(
                () -> "Visible aircraft: " + aircraftCountProperty.get(), aircraftCountProperty));
        pane.setLeft(visibleAircraftCountText);

        //MESSAGE COUNT TEXT
        Text visibleMessageCountText = new Text();
        visibleMessageCountText.textProperty().bind(Bindings.createStringBinding(
                () -> "Received messages: " + messageCountProperty.get(), messageCountProperty));
        pane.setRight(visibleMessageCountText);

    }

    public Node pane() {
        return pane;
    }

    public void setAircraftCount(int aircraftCount) {
        this.aircraftCountProperty.set(aircraftCount);
    }

    public void setMessageCount(long messageCount) {
        this.messageCountProperty.set(messageCount);
    }

    public int getAircraftCount() {
        return aircraftCountProperty.get();
    }

    public long getMessageCount() {
        return messageCountProperty.get();
    }

    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}
