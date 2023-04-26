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
    private long trajectoryTimeStampNs;
    private final LongProperty lastMessageTimeStampNsProperty = new SimpleLongProperty();
    private final IntegerProperty categoryProperty = new SimpleIntegerProperty();
    private final ObjectProperty<CallSign> callSignProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<GeoPos> positionProperty = new SimpleObjectProperty<>();
    private final ObservableList<AirbornePos> trajectory;
    private final ObservableList<AirbornePos> trajectoryProperty;
    private final DoubleProperty altitudeProperty = new SimpleDoubleProperty();
    private final DoubleProperty velocityProperty = new SimpleDoubleProperty();
    private final DoubleProperty trackOrHeadingProperty = new SimpleDoubleProperty();

    //******************************* CONSTRUCTOR *******************************
// TODO: 23/4/23 add comments
    //todo poner en readOnly
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
        updateTrajectory(position, getAltitude());
        positionProperty.set(position);
    }

    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return positionProperty;
    }

    public GeoPos getGeoPos() {
        return positionProperty.get();
    }


    //******************************* ALTITUDE *******************************

    @Override
    public void setAltitude(double altitude) {
        updateTrajectory(getGeoPos(), altitude);
        altitudeProperty.set(altitude);
    }

    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitudeProperty;
    }

    public double getAltitude() {
        return altitudeProperty.get();
    }
//******************************* TRAJECTORY *******************************

    public ObservableList<AirbornePos> trajectory() {
        return trajectoryProperty;
    }

    private void updateTrajectory(GeoPos position, double altitude) {
        if ((Double.isNaN(altitude)) || (position == null)) return;
        if (trajectory.isEmpty()) {
            trajectory.add(new AirbornePos(position, altitude));
        } else if (!position.equals(getGeoPos())) {
            trajectory.add(new AirbornePos(position, altitude));
        } else if (getLastMessageTimeStampNs() == trajectoryTimeStampNs) {
            trajectory.remove(trajectory.size() - 1);
            trajectory.add(new AirbornePos(position, altitude));
            trajectoryTimeStampNs = getLastMessageTimeStampNs();
        }
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
