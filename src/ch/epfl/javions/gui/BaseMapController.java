package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * BaseMapController is a controller class for a map-based graphical user interface.
 * It manages the rendering of map tiles and handles user interactions such as scrolling and zooming.
 *
 * @author Ricardo Perello Mas (357241)
 * @author Alejandro Meredith Romero (360864)
 */

public final class BaseMapController {
    private static final int TILE_SIZE = 256;
    private static final int MIN_TIME_BETWEEN_SCROLLS_MS = 200;

    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final ObjectProperty<MapParameters> mapParametersProperty;
    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private final ObjectProperty<Point2D> previousMouseCoordsOnScreen;

    /**
     * BaseMapController constructor initialises tileManager, mapParameters, mapParametersProperty,
     * canvas, pane, previousMouseCoordsOnScreen, redrawNeeded. It also binds the dimensions of the
     * canvas to the dimensions of the pane. It also sets up the listeners and sets up the event handlers.
     *
     * @param tileManager   tileManager that is in charge of finding the images for the tiles.
     * @param mapParameters mapParameters of the viewed map.
     */
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

    /**
     * Pane simply returns the pane.
     *
     * @return pane.
     */
    public Pane pane() {
        return pane;
    }

    /**
     * setUpListeners adds the listeners needed to resize the window and move around the map.
     */
    private void setUpListeners() {
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        canvas.heightProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        canvas.widthProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        mapParametersProperty.addListener((p, oldS, newS) -> redrawOnNextPulse());
    }

    /**
     * drawMap method uses the graphicsContext.drawImage method to draw all the tiles from the map.
     * These images are found using the tile manager and using a double for loop to find the image
     * of the tile (x,y).
     */
    private void renderMap() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        MapParameters currentMapParameters = mapParametersProperty.get();


        Point2D origin = findTopLeft(currentMapParameters);

        Point2D end = origin.add(canvas.getWidth(), canvas.getHeight());

        int zoomLevel = currentMapParameters.getZoom();

        int xMinTile = (int) origin.getX() / TILE_SIZE;
        int xMaxTile = (int) end.getX() / TILE_SIZE;
        int yMinTile = (int) origin.getY() / TILE_SIZE;
        int yMaxTile = (int) end.getY() / TILE_SIZE;

        int destY = (int) -origin.getY() % TILE_SIZE;
        for (int y = yMinTile; y <= yMaxTile; y++) {

            int destX = (int) -origin.getX() % TILE_SIZE;
            for (int x = xMinTile; x <= xMaxTile; x++) {
                try {
                    graphicsContext.drawImage(tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, x, y)),
                            destX, destY);

                } catch (IOException ignored) {
                }

                destX += TILE_SIZE;
            }
            destY += TILE_SIZE;
        }
    }

    /**
     * findTopLeft returns a 2D point of the position of the top left corner of the map.
     *
     * @param currentMapParameters map parameters at the moment the method is called.
     * @return Point2D of the position of the top left corner of the map.
     */
    private static Point2D findTopLeft(MapParameters currentMapParameters) {
        return new Point2D(currentMapParameters.getMinX(), currentMapParameters.getMinY());
    }

    /**
     * redrawIfNeeded is a method that draws the new map when the map changes and needs to be redrawn.
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        redrawOnNextPulse();
        renderMap();
    }

    /**
     * redrawOnNextPulse method is called when the map changes and needs to be redrawn. It will activate
     * the method redrawIfNeeded, and it requests a new pulse.
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * EventHandler installs the event handlers on the panel that contains the map.
     */
    private void eventHandler() {
        addEventMouseDragging();
        addEventMouseScrolling();
    }

    /**
     * AddEventMouseDragging method calls the scroll method to pan the map by the same displacement as the
     * mouse was dragged.
     */
    private void addEventMouseDragging() {

        canvas.setOnMousePressed((e) -> previousMouseCoordsOnScreen.set(new Point2D(e.getX(), e.getY())));

        canvas.setOnMouseDragged((e) -> {

            double deltaX = previousMouseCoordsOnScreen.getValue().getX() - e.getX(),
                    deltaY = previousMouseCoordsOnScreen.getValue().getY() - e.getY();
            mapParameters.scroll(deltaX, deltaY);
            previousMouseCoordsOnScreen.set(new Point2D(e.getX(), e.getY()));
        });
    }

    /**
     * addEventMouseScrolling method is used to change the zoom level of the map. When the mouse wheel is
     * scrolled, the map will zoom in/out with the mouse pointer as perspective center.
     */
    private void addEventMouseScrolling() {
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


    /**
     * centerOn changes the map parameters so that the center of the map is the GeoPos passed
     * as parameter.
     *
     * @param geoPos new center of the map.
     */
    public void centerOn(GeoPos geoPos) {
        double x = WebMercator.x(mapParameters.getZoom(), geoPos.longitude())
                - mapParameters.getMinX() - pane.getWidth() / 2;
        double y = WebMercator.y(mapParameters.getZoom(), geoPos.latitude())
                - mapParameters.getMinY() - pane.getHeight() / 2;

        mapParameters.scroll(x, y);
    }
}
