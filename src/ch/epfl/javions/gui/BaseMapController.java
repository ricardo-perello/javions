package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Math2;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final ObjectProperty<MapParameters> mapParametersProperty;
    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    GraphicsContext graphicsContext;


    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);
        mapParametersProperty = new SimpleObjectProperty<>(this.mapParameters);

        redrawNeeded = false;
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        setUpListeners();

        eventHandler();

    }

    public Pane pane() {
        return pane;
    }

    private void setUpListeners() {
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        canvas.heightProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        canvas.widthProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        mapParametersProperty.addListener((p, oldS, newS) -> redrawOnNextPulse());
    }


    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        //drawMap();
    }
    private void eventHandler() {
        addMouseDragging();
        addMouseClicking();
        addMouseScrolling();
    }
    private void addMouseDragging(){}

    private void addMouseClicking(){}

    private void addMouseScrolling(){

        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e ->{
            int zoomDelta =(int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);


            //int newZoomLevel = Math2.clamp();
        });


    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    public ReadOnlyObjectProperty<MapParameters> getMapParametersProperty(){
        return mapParametersProperty;
    }

    public void setMapParameters(MapParameters mapParameters) {
        mapParametersProperty.set(mapParameters);
    }


}
