package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

import static ch.epfl.javions.gui.BaseMapController.MAX_ZOOM_LEVEL;
import static ch.epfl.javions.gui.BaseMapController.MIN_ZOOM_LEVEL;

public final class MapParameters {

    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;

    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument(zoom >= MIN_ZOOM_LEVEL
                && zoom <= MAX_ZOOM_LEVEL);
        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);
    }

    public int getZoomValue(){
        return zoom.getValue();
    }

    public double getMinXValue(){
        return minX.getValue();
    }

    public double getMinYValue(){
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
        minX.set(newMinX);
        minY.set(newMinY);
    }

    public void changeZoomLevel(int zoomChange) {
        //todo mirar con tota si esta bien
        //todo 11<n == 2^n
         zoomChange = zoomChange > 0 ? 1 : -1;
        int newZoomLevel = Math2.clamp(MIN_ZOOM_LEVEL, getZoomValue()
                + (zoomChange), MAX_ZOOM_LEVEL);
        if(zoom.getValue() != newZoomLevel) {
            minX.set(minX.getValue() * Math.scalb(1, zoomChange));
            minY.set(minY.getValue() * Math.scalb(1, zoomChange));
            zoom.set(newZoomLevel);
        }


    }

}
