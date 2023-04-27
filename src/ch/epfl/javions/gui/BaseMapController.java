package ch.epfl.javions.gui;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {
    // TODO: 26/4/23 comments
    // TODO: 26/4/23 create centerOn method
    private static final int TILE_SIZE = 256;
    static final int MIN_ZOOM_LEVEL = 6;
    static final int MAX_ZOOM_LEVEL = 19;
    private static final int MIN_TIME_BETWEEN_SCROLLS_MS = 200;

    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final ObjectProperty<MapParameters> mapParametersProperty;
    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private final ObjectProperty<Point2D> previousMouseCoordsOnScreen;
    GraphicsContext graphicsContext;


    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);
        mapParametersProperty = new SimpleObjectProperty<>(this.mapParameters);
        previousMouseCoordsOnScreen = new SimpleObjectProperty<>(new Point2D(0, 0));
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

    private void drawMap() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        MapParameters actualMapParameters = mapParametersProperty.get();

        //finds coordinates of top left corner of map
        Point2D topLeft = topLeft(actualMapParameters);
        //Coordinates of bottom right corner of map
        Point2D bottomRight = topLeft.add(canvas.getWidth(), canvas.getHeight());

        int zoomLevel = actualMapParameters.getZoomValue();
        //Coordonnées des tuiles minimales et maximales à dessiner (le rectangle de tuiles depuis
        //la tuile de coordonnées (xMin, yMin) à la tuile (xMax, yMax)).
        int xMin = (int) topLeft.getX() / TILE_SIZE;
        int xMax = (int) bottomRight.getX() / TILE_SIZE;
        int yMin = (int) topLeft.getY() / TILE_SIZE;
        int yMax = (int) bottomRight.getY() / TILE_SIZE;

        //Position Y de destination du coin haut-gauche de la tuile à dessiner sur le canevas.
        int destinationY = (int) -topLeft.getY() % TILE_SIZE;
        for (int y = yMin; y <= yMax; y++) {
            //Position X de destination du coin haut-gauche de la tuile à dessiner sur le canevas.
            int destinationX = (int) -topLeft.getX() % TILE_SIZE;
            for (int x = xMin; x <= xMax; x++) {
                try {
                    //Dessine la tuile actuelle, au niveau de zoom demandé, et à partir du pixel
                    //du bord du canevas, ce qui permet d'avoir des bouts de tuile, et non seulement
                    //des tuiles entières.


                    graphicsContext.drawImage(tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, x, y)),
                            destinationX, destinationY);

                } catch (IOException ignored) {
                } //Exception ignorée.

                //Incrémente les positions des valeurs X et Y de la longueur/largeur des tuiles.
                destinationX += TILE_SIZE;
            }
            destinationY += TILE_SIZE;
        }
    }

    private Point2D topLeft(MapParameters actualMapParameters) {
        return new Point2D(actualMapParameters.getMinXValue(), actualMapParameters.getMinYValue());
    }


    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        redrawOnNextPulse();
        drawMap();
    }

    private void eventHandler() {
        addMouseDragging();
        addMouseClicking();
        addMouseScrolling();
    }

    private void addMouseDragging() {

        canvas.setOnMousePressed((e) -> previousMouseCoordsOnScreen.set(new Point2D(e.getX(), e.getY())));

        canvas.setOnMouseDragged((e) -> {

            double deltaX = previousMouseCoordsOnScreen.getValue().getX() - e.getX(),
                    deltaY = previousMouseCoordsOnScreen.getValue().getY() - e.getY();
            mapParameters.scroll(deltaX, deltaY);
            previousMouseCoordsOnScreen.set(new Point2D(e.getX(), e.getY()));
        });
    }

    private void addMouseClicking() {
    }

    private void addMouseScrolling() {
        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            double zoomDelta = e.getDeltaY();
            long currentTime = System.currentTimeMillis();
            if ((zoomDelta == 0) || (currentTime < minScrollTime.get())) return;
            minScrollTime.set(currentTime + MIN_TIME_BETWEEN_SCROLLS_MS);

            mapParameters.scroll(e.getX(), e.getY());
            mapParameters.changeZoomLevel((int) zoomDelta);
            mapParameters.scroll(-e.getX(), -e.getY());
        });

    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    public ReadOnlyObjectProperty<MapParameters> getMapParametersProperty() {
        return mapParametersProperty;
    }

    public void setMapParameters(MapParameters mapParameters) {
        mapParametersProperty.set(mapParameters);
    }


}
