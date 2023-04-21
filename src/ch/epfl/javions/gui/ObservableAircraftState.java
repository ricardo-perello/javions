package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ObservableAircraftState implements AircraftStateSetter {

    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;
    private LongProperty lastMessageTimeStampNsProperty;
    private IntegerProperty categoryProperty;
    private ObjectProperty<CallSign> callSignProperty;
    private ObjectProperty<GeoPos> positionProperty;
    private final ObservableList<AirbornePos> trajectory;
    private final ObservableList<AirbornePos> trajectoryProperty;
    private DoubleProperty altitudeProperty;
    private DoubleProperty velocityProperty;
    private DoubleProperty trackOrHeadingProperty;

    //******************************* CONSTRUCTOR *******************************

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;
        trajectory = FXCollections.observableArrayList();
        trajectoryProperty = FXCollections.unmodifiableObservableList(trajectory);
    }

    //******************************* GETTERS *******************************

    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }

    public AircraftData getAircraftData() {
        return aircraftData;
    }

    //******************************* LAST MESSAGE TIMESTAMP *******************************
    @Override
    public void setLastMessageTimeStampNs(long timeStampsNs) {
        lastMessageTimeStampNsProperty.set(timeStampsNs);
    }

    public ReadOnlyLongProperty LastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNsProperty;
    }

    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNsProperty.get();
    }


    //******************************* CATEGORY *******************************

    @Override
    public void setCategory(int category) {
        categoryProperty.set(category);
    }

    public ReadOnlyIntegerProperty categoryProperty() {
        return categoryProperty;
    }

    public int getCategory() {
        return categoryProperty.get();
    }

//******************************* CALLSIGN *******************************

    @Override
    public void setCallSign(CallSign callSign) {
        callSignProperty.set(callSign);
    }

    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSignProperty;
    }

    public CallSign getCallSign() {
        return callSignProperty.get();
    }

//******************************* POSITION *******************************

    @Override
    public void setPosition(GeoPos position) {
        positionProperty.set(position);
    }

    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return positionProperty;
    }

    public GeoPos getGeoPos() {
        return positionProperty.get();
    }

    //******************************* TRAJECTORY *******************************

    public ObservableList<AirbornePos> trajectory() {
        return trajectoryProperty;
    }

    //******************************* ALTITUDE *******************************

    @Override
    public void setAltitude(double altitude) {
        altitudeProperty.set(altitude);
    }

    public ReadOnlyDoubleProperty altitude() {
        return altitudeProperty;
    }

    public double getAltitude() {
        return altitudeProperty.get();
    }

//******************************* VELOCITY *******************************


    @Override
    public void setVelocity(double velocity) {
        velocityProperty.set(velocity);
    }

    public ReadOnlyDoubleProperty velocityProperty() {
        return velocityProperty;
    }

    public double getVelocity() {
        return velocityProperty.get();
    }


//******************************* TRACK OR HEADING *******************************

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        trackOrHeadingProperty.set(trackOrHeading);
    }

    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeadingProperty;
    }

    public double getTrackOrHeading() {
        return trackOrHeadingProperty.get();
    }

//******************************* AIRBORNE POS  *******************************


    public record AirbornePos(GeoPos geoPos, double altitude) {

    }
}
