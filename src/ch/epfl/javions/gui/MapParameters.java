package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;


public final class MapParameters {

    //the IntegerProperty used to keep the zoom level
    private final IntegerProperty zoom;

    //DoubleProperty that stores the x coordinate of the top left corner of the window
    private final DoubleProperty minX;

    //DoubleProperty that stores the y coordinate of the top left corner of the window

    private final DoubleProperty minY;


    public static final int MIN_ZOOM_LEVEL = 6;
    public static final int MAX_ZOOM_LEVEL = 19;

    /**
     * constructor for MapParameters
     * @param zoom int, initial value for zoom level
     * @param minX double, initial value for minX
     * @param minY double, initial value for minY
     */
    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument(zoom >= MIN_ZOOM_LEVEL
                && zoom <= MAX_ZOOM_LEVEL);
        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);
    }

    /**
     * public method to get the zoom
     * @return int, the value of the IntegerProperty zoom
     */
    public int getZoom() {
        return zoom.getValue();
    }

    /**
     * public method to get the minX
     * @return double, the value stored in the DoubleProperty minX
     */
    public double getMinX() {
        return minX.getValue();
    }

    /**
     * public method to get minY
     * @return double, the values stored in the DoubleProperty minY
     */
    public double getMinY() {
        return minY.getValue();
    }

    /**
     * public method to get zoom
     * @return ReadOnlyIntegerProperty, the IntegerProperty zoom
     */
    public ReadOnlyIntegerProperty zoomProperty() {
        return IntegerProperty.readOnlyIntegerProperty(zoom);
    }

    /**
     * public method to get minX
     * @return ReadOnlyDoubleProperty, the DoubleProperty minX
     */
    public ReadOnlyDoubleProperty minXProperty() {
        return DoubleProperty.readOnlyDoubleProperty(minX);
    }

    /**
     * public method to get minY
     * @return ReadOnlyDoubleProperty, the DoubleProperty minY
     */
    public ReadOnlyDoubleProperty minYProperty() {
        return DoubleProperty.readOnlyDoubleProperty(minY);
    }

    /**
     * public method that allows to change the position of the top left corner
     * @param x, double, the distance the corner has to be shifted in the x-axis
     * @param y, double, the distance the corner has to be shifted in the y-axis
     */
    public void scroll(double x, double y) {
        double newMinX = minX.getValue() + x;
        double newMinY = minY.getValue() + y;
        minX.set(newMinX);
        minY.set(newMinY);
    }

    /**
     * public method that allows to change the zoom level
     * @param zoomChange, the amount of we need to change the zoom by (can be negative, positive)
     */
    public void changeZoomLevel(int zoomChange) {
        //changes the value of zoom to -1 or 1
        //we do not check if it equals to 0 since we exclude that situation in addMouseScrolling from BaseMapController
        zoomChange = zoomChange > 0 ? 1 : -1;
        // we make sure that it is between 6 (included) and 19 (included)
        int newZoomLevel = Math2.clamp(MIN_ZOOM_LEVEL, getZoom()
                + (zoomChange), MAX_ZOOM_LEVEL);
        //we adjust the coordinates of the top left corner using adjustCoordinates
        if (zoom.getValue() != newZoomLevel) {
            adjustCoordinates(zoomChange);
            zoom.set(newZoomLevel);
        }
    }

    /**
     * private method that allows to adjust the position of the top left corner using the zoom level
     * @param zoomChange, int the amount of change in the zoom level
     */
    private void adjustCoordinates(int zoomChange) {
        //we use the facts that 1<<n == 2^n and a * 2^-n <=> a/(2^n)
        if (zoomChange < 0) {
            minX.set(minX.getValue() / (1 << -zoomChange));
            minY.set(minY.getValue() / (1 << -zoomChange));
        } else {
            minX.set(minX.getValue() * (1 << zoomChange));
            minY.set(minY.getValue() * (1 << zoomChange));
        }
    }

}
