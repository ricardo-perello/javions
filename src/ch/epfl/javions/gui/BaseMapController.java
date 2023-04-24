package ch.epfl.javions.gui;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {
    TileManager tileManager;
    MapParameters mapParameters;
    Canvas canvas = new Canvas();
    Pane pane = new Pane();
    GraphicsContext graphicsContext;
    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        canvas.widthProperty().bind(pane.widthProperty());
        graphicsContext = canvas.getGraphicsContext2D();
    }


    public Node pane() throws IOException {
        return null;
    }
}
