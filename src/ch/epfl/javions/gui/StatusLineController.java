package ch.epfl.javions.gui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public final class StatusLineController {
    private final Pane pane;

    public StatusLineController(){
        this.pane = new BorderPane();
        this.pane.getStyleClass().add("status");


    }
    public Node pane(){
        return pane;
    }
}
