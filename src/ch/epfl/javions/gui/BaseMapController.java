package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Math2;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {

    private static final int TILE_SIZE = 256;
    private static final int MIN_ZOOM_LEVEL = 8;
    private static final int MAX_ZOOM_LEVEL = 19;
    private static final int MIN_TIME_BETWEEN_SCROLLS_MS = 200;

    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final ObjectProperty<MapParameters> mapParametersProperty;
    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private final ObjectProperty<Point2D> previousCoordsOnScreen;
    GraphicsContext graphicsContext;


    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);
        mapParametersProperty = new SimpleObjectProperty<>(this.mapParameters);
        previousCoordsOnScreen = new SimpleObjectProperty<>(new Point2D(0, 0));
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
        GraphicsContext gc = canvas.getGraphicsContext2D();
        MapParameters actualMP = mapParametersProperty.get();

        //Coordonnées du point en haut à gauche de la fenêtre.
        Point2D topLeft = actualMP.topLeft();
        //Coordonnées du point en bas à droite de la fenêtre.
        Point2D bottomRight = topLeft.add(canvas.getWidth(), canvas.getHeight());

        int zoomLevel = actualMP.getZoomValue();
        //Coordonnées des tuiles minimales et maximales à dessiner (le rectangle de tuiles depuis
        //la tuile de coordonnées (xMin, yMin) à la tuile (xMax, yMax)).
        int xMin = (int) topLeft.getX() / TILE_SIZE;
        int xMax = (int) bottomRight.getX() / TILE_SIZE;
        int yMin = (int) topLeft.getY() / TILE_SIZE;
        int yMax = (int) bottomRight.getY() / TILE_SIZE;

        //Position Y de destination du coin haut-gauche de la tuile à dessiner sur le canevas.
        int destinationY  = (int) -topLeft.getY() % TILE_SIZE;
        for (int y = yMin; y <= yMax; y++) {
            //Position X de destination du coin haut-gauche de la tuile à dessiner sur le canevas.
            int destinationX = (int) - topLeft.getX() % TILE_SIZE;
            for (int x = xMin; x <= xMax; x++) {
                try {
                    //Dessine la tuile actuelle, au niveau de zoom demandé, et à partir du pixel
                    //du bord du canevas, ce qui permet d'avoir des bouts de tuile, et non seulement
                    //des tuiles entières.
                    gc.drawImage(tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, x, y)),
                            destinationX, destinationY);
                } catch (IOException ignored) {} //Exception ignorée.

                //Incrémente les positions des valeurs X et Y de la longueur/largeur des tuiles.
                destinationX += TILE_SIZE;
            }
            destinationY += TILE_SIZE;
        }
    }


    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        //redrawOnNextPulse();
        drawMap();
    }
    private void eventHandler() {
        addMouseDragging();
        addMouseClicking();
        addMouseScrolling();
    }
    private void addMouseDragging(){
        //Prise de la coordonnée au début du clic.
        canvas.setOnMousePressed((e) -> previousCoordsOnScreen.set(new Point2D(e.getX(), e.getY())));

        //Calcul de la position actuelle de la carte affichée en fonction du déplacement.
        canvas.setOnMouseDragged((e) -> {

            double deltaX = e.getX() - previousCoordsOnScreen.get().getX(),
                    deltaY = e.getY() - previousCoordsOnScreen.get().getY();
            int zoomLevel = mapParametersProperty.get().getZoomValue();
            WebMercator pointWebMercator = mapParametersProperty.get().pointAt(0, 0);

            mapParametersProperty.set(mapParametersProperty.get().withMinXY(
                    pointWebMercator.xAtZoomLevel(zoomLevel) - deltaX,
                    pointWebMercator.yAtZoomLevel(zoomLevel) - deltaY));

            //Mise à jour de la coordonnée actuelle.
            previousCoordsOnScreen.set(new Point2D(e.getX(), e.getY()));
        });
    }

    private void addMouseClicking(){}

    private void addMouseScrolling(){

        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e ->{
            int zoomDelta =(int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + MIN_TIME_BETWEEN_SCROLLS_MS);


           // int newZoomLevel = Math2.clamp();
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
