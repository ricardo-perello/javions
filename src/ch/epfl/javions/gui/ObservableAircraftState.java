package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ObservableAircraftState represents the observable state of an aircraft.
 * It provides properties and methods to access and
 * update various attributes of the aircraft, such as position, altitude, velocity, etc.
 *
 * @author Ricardo Perello Mas (357241)
 * @author Alejandro Meredith Romero (360864)
 */

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
    private final DoubleProperty altitudeProperty = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty velocityProperty = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty trackOrHeadingProperty = new SimpleDoubleProperty();

    //******************************* CONSTRUCTOR *******************************

    /**
     * ObservableAircraftState constructor
     *
     * @param icaoAddress  Icao Address of airplane
     * @param aircraftData Aircraft data of the airplane.
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {

        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;
        trajectory = FXCollections.observableArrayList();
        trajectoryProperty = FXCollections.unmodifiableObservableList(trajectory);

    }


    //******************************* GETTERS *******************************

    /**
     * getIcaoAddress
     *
     * @return Icao Address of airplane
     */
    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }

    /**
     * getAircraftData
     *
     * @return Aircraft Data of airplane
     */
    public AircraftData getAircraftData() {
        return aircraftData;
    }

    //******************************* LAST MESSAGE TIMESTAMP *******************************

    /**
     * setLastMessageTimeStampNs sets the last's message time stamp property.
     *
     * @param timeStampsNs long, value we will change to
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampsNs) {
        lastMessageTimeStampNsProperty.set(timeStampsNs);
    }

    /**
     * LastMessageTimeStampNsProperty method returns the property
     *
     * @return lastMessageTimeStampNsProperty property
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNsProperty;
    }

    /**
     * getLastMessageTimeStampNs method returns the value of lastMessageTimeStampNsProperty.
     *
     * @return the value of lastMessageTimeStampNsProperty
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNsProperty().get();
    }


    //******************************* CATEGORY *******************************

    /**
     * setCategory sets the category  property.
     *
     * @param category int value that we will change to.
     */
    @Override
    public void setCategory(int category) {
        categoryProperty.set(category);
    }

    /**
     * categoryProperty method returns the property.
     *
     * @return categoryProperty property.
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return categoryProperty;
    }

    /**
     * getCategory method returns the value of categoryProperty.
     *
     * @return the value of categoryProperty.
     */

    @SuppressWarnings("unused")
    public int getCategory() {
        return categoryProperty().get();
    }

//******************************* CALLSIGN *******************************

    /**
     * setCallSign sets the CallSign  property.
     *
     * @param callSign CallSign that we will change to.
     */
    @Override
    public void setCallSign(CallSign callSign) {
        callSignProperty.set(callSign);
    }

    /**
     * callSignProperty method returns the property.
     *
     * @return callSignProperty property.
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSignProperty;
    }

    /**
     * getCallSign method returns the value of callSignProperty.
     *
     * @return the value of callSignProperty.
     */
    public CallSign getCallSign() {
        return callSignProperty.get();
    }

//******************************* POSITION *******************************

    /**
     * setPosition sets the positionProperty and updates the trajectory record.
     *
     * @param position GeoPos that we will change to.
     */
    @Override
    public void setPosition(GeoPos position) {
        updateTrajectory(position, getAltitude(), true);
        positionProperty.set(position);
    }

    /**
     * positionProperty method returns the property.
     *
     * @return positionProperty property.
     */

    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return positionProperty;
    }

    /**
     * getGeoPos method returns the value of positionProperty.
     *
     * @return the value of positionProperty.
     */
    public GeoPos getPosition() {
        return positionProperty.get();
    }


    //******************************* ALTITUDE *******************************

    /**
     * setAltitude sets the altitudeProperty and updates the trajectory record.
     *
     * @param altitude double, value we will change to.
     */
    @Override
    public void setAltitude(double altitude) {
        updateTrajectory(getPosition(), altitude, false);
        altitudeProperty.set(altitude);
    }

    /**
     * altitudeProperty method returns the property.
     *
     * @return altitudeProperty property.
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitudeProperty;
    }

    /**
     * getAltitude method returns the value of altitudeProperty.
     *
     * @return the value of altitudeProperty.
     */
    public double getAltitude() {
        return altitudeProperty.getValue();
    }
//******************************* TRAJECTORY *******************************

    /**
     * trajectory property returns a list of the current and previous AirbornePos of the airplane.
     * This includes altitude and position.
     *
     * @return ObservableList of type AirbornePos
     */
    public ObservableList<AirbornePos> getTrajectory() {
        return trajectoryProperty;
    }

    /**
     * updateTrajectory method adds a new AirbornePos to the trajectory list when any of the following
     * conditions are met:
     * - if the list is empty,
     * - if the new position is not equal to the lsat position in the list,
     * - if the timestamp is the same as the last message's timestamp (removes last entry of the list
     * and then adds the new one)
     *
     * @param position position we are going to use in the new entry of the list.
     * @param altitude altitude we are going to use in the new entry of the list.
     * @param p        boolean, true if setPosition called this method, false if setAltitude called this method.
     */
    private void updateTrajectory(GeoPos position, double altitude, boolean p) {
        if (p && !(Double.isNaN(altitude))) {
            trajectory.add(new AirbornePos(position, altitude));
            trajectoryTimeStampNs = getLastMessageTimeStampNs();
        }
        if (!p && !(position == null)) {
            if (trajectory.isEmpty()) {
                trajectory.add(new AirbornePos(position, altitude));
                trajectoryTimeStampNs = getLastMessageTimeStampNs();
            } else if (getLastMessageTimeStampNs() == trajectoryTimeStampNs) {
                trajectory.remove(trajectory.size() - 1);
                trajectory.add(new AirbornePos(position, altitude));
                trajectoryTimeStampNs = getLastMessageTimeStampNs();
            }
        }
    }
//******************************* VELOCITY *******************************

    /**
     * setVelocity sets the velocityProperty.
     *
     * @param velocity double, value we will change to.
     */

    @Override
    public void setVelocity(double velocity) {
        velocityProperty.set(velocity);
    }

    /**
     * velocityProperty method returns the property.
     *
     * @return velocityProperty property.
     */

    public ReadOnlyDoubleProperty velocityProperty() {
        return velocityProperty;
    }

    /**
     * getVelocity method returns the value of velocityProperty.
     *
     * @return the value of velocityProperty.
     */

    public double getVelocity() {
        return velocityProperty.get();
    }


//******************************* TRACK OR HEADING *******************************

    /**
     * setTrackOrHeading sets the trackOrHeadingProperty.
     *
     * @param trackOrHeading double, value we will change to.
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        trackOrHeadingProperty.set(trackOrHeading);
    }

    /**
     * trackOrHeadingProperty method returns the property.
     *
     * @return trackOrHeadingProperty property.
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeadingProperty;
    }

    /**
     * getTrackOrHeading method returns the value of trackOrHeadingProperty.
     *
     * @return the value of trackOrHeadingProperty.
     */

    public double getTrackOrHeading() {
        return trackOrHeadingProperty.get();
    }

//******************************* AIRBORNE POS  *******************************

    /**
     * AirbornePos record that includes GeoPos position and its altitude
     *
     * @param position position
     * @param altitude altitude
     */
    public record AirbornePos(GeoPos position, double altitude) {
    }
}
