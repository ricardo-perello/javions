package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

public final class MapParameters {

    private static final int MAX_ZOOM = 19;
    private static final int MIN_ZOOM = 6;

    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;

    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument(zoom >= MIN_ZOOM && zoom <= MAX_ZOOM); //&&
                                    //minX < Math.scalb(2, 8 + zoom) && minX >= 0 &&
                                    //minY < Math.scalb(2, 8 + zoom) && minY >= 0);
        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);
    }

    public Integer getZoomValue(){
        return zoom.getValue();
    }

    public Double getMinXValue(){
        return minX.getValue();
    }

    public Double getMinYValue(){
        return minY.getValue();
    }

    public ReadOnlyIntegerProperty getZoomProperty(){
        return IntegerProperty.readOnlyIntegerProperty(zoom);
    }

    public ReadOnlyDoubleProperty getMinXProperty(){
        return DoubleProperty.readOnlyDoubleProperty(minX);
    }

    public ReadOnlyDoubleProperty getMinYProperty(){
        return DoubleProperty.readOnlyDoubleProperty(minY);
    }

    public void scroll(double x, double y) {
        double newMinX = minX.getValue() + x;
        double newMinY = minY.getValue() + y;
        /*Preconditions.checkArgument(newMinX >= 0 && newMinX <Math.scalb(2, 8+ zoom.getValue()) &&
                                    newMinY >= 0 && newMinY < Math.scalb(2, 8+ zoom.getValue()));*/
        minX.set(newMinX);
        minY.set(newMinY);
    }

    public void changeZoomLevel(int zoomChange) {
        int newValueZoom = zoom.getValue() + zoomChange;
        Preconditions.checkArgument(newValueZoom >= MIN_ZOOM && newValueZoom <= MAX_ZOOM);
        //todo mirar con tota si esta bien
        minX.set(minX.getValue() * Math.scalb(2, zoomChange));
        minY.set(minY.getValue() * Math.scalb(2, zoomChange));
        //todo adapter les valeurs de minX et minY
        zoom.set(newValueZoom);
    }
}
