package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class MapParameters {
    private ObjectProperty<Integer> zoom;
    private ObjectProperty<Double> minX;
    private ObjectProperty<Double> minY;

    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument(zoom >= 6 && zoom <= 19 &&
                                    minX < Math.scalb(2, 8 + zoom) && minX >= 0 &&
                                    minY < Math.scalb(2, 8 + zoom) && minY >= 0);
        this.zoom = new SimpleObjectProperty<>(zoom);
        this.minX = new SimpleObjectProperty<>(minX);
        this.minY = new SimpleObjectProperty<>(minY);
    }

    public Integer getZoom(){
        return zoom.getValue();
    }

    public Double getMinX(){
        return minX.getValue();
    }

    public Double getMinY(){
        return minY.getValue();
    }

    public void scroll(double x, double y) {
        double newMinX = minX.getValue() + x;
        double newMinY = minY.getValue() + y;
        Preconditions.checkArgument(newMinX >= 0 && newMinX <Math.scalb(2, 8+ zoom.getValue()) &&
                                    newMinY >= 0 && newMinY < Math.scalb(2, 8+ zoom.getValue()));
        minX.set(minX.getValue() + x);
        minY.set(minY.getValue() + y);
    }

    public void changeZoomLevel(int zoomChange) {
        int newValueZoom = zoom.getValue() + zoomChange;
        Preconditions.checkArgument(newValueZoom >= 6 && newValueZoom <= 19);
        zoom.set(newValueZoom);
    }
}
