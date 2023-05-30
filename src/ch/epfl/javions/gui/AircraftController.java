package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import static ch.epfl.javions.Units.Angle.DEGREE;

/**
 * The `AircraftController` class manages all the visible aircraft on a map.
 * It creates and manages groups of aircraft, including their icons, labels, and trajectories.
 *
 * @author Ricardo Perello Mas ()
 * @author Alejandro Meredith Romero (360864)
 */

public final class AircraftController {

    private static final int ZOOM_VISIBILITY_THRESHOLD = 11;
    private final Pane pane = new Pane();
    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> aircraftStates;
    private final ObjectProperty<ObservableAircraftState> selected;
    private final IntegerProperty zoomProperty;
    private final DoubleProperty minXProperty;
    private final DoubleProperty minYProperty;
    private final ColorRamp colorRamp;

    /**
     * Constructor for AircraftController, creates instance of AircraftController
     * which manages all the visible aircraft.
     *
     * @param mapParameters  instance of mapParameters.
     * @param aircraftStates instance of aircraftStates.
     * @param selected       selected aircraft, label and trajectory will show.
     */
    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> selected) {

        this.mapParameters = mapParameters;
        this.aircraftStates = aircraftStates;
        this.selected = selected;
        minXProperty = (DoubleProperty) mapParameters.minXProperty();
        minYProperty = (DoubleProperty) mapParameters.minYProperty();
        zoomProperty = (IntegerProperty) mapParameters.zoomProperty();
        colorRamp = ColorRamp.PLASMA;
        addAnnotatedGroups();
        pane.setPickOnBounds(false);
        pane.getStylesheets().add("aircraft.css");
        addListenerToSet();
        eventHandlers();
    }

    /**
     * private method that allows to create a new annotated group for every plane
     */
    private void addAnnotatedGroups() {
        for (ObservableAircraftState aircraftState : aircraftStates) {
            addAnnotated(aircraftState);
        }
    }

    /**
     * private method that allows to create a new annotated group for a specific plane
     *
     * @param aircraftState ObservableAircraftState of the plane we wish to create a new annotated group
     */
    private void addAnnotated(ObservableAircraftState aircraftState) {
        Group aircraftInfo = setAircraftInfo(aircraftState);
        Group trajectory = setTrajectory(aircraftState);
        Group annotated = new Group(trajectory, aircraftInfo);
        annotated.setId(aircraftState.getIcaoAddress().toString());
        annotated.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());
        pane.getChildren().add(annotated);
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Trajectory~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * private method that allows to create a group for the trajectory
     *
     * @param aircraftState, ObservableAircraftState, the plane we wish to create its trajectory
     * @return a Group with the trajectory
     */
    private Group setTrajectory(ObservableAircraftState aircraftState) {
        Group trajectory = new Group();
        trajectory.getStyleClass().add("trajectory");
        trajectory.setVisible(false);
        //if trajectory changes, it gets updated
        aircraftState.getTrajectory().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>)
                observable -> updateTrajectory(aircraftState, trajectory));
        //if change in the zoom level we calculate the trajectory again
        zoomProperty.addListener((observable -> updateTrajectory(aircraftState, trajectory)));
        //if the plane that is selected changes we update the trajectory
        selected.addListener((observable, oldValue, newValue) -> {
            trajectory.setVisible(newValue == aircraftState);
            updateTrajectory(aircraftState, trajectory);
        });
        return trajectory;
    }

    /**
     * private method that allows to update the trajectory of a given plane
     *
     * @param aircraftState, ObservableAircraftState, plane we wish update the trajectory
     * @param trajectory,    Group, the trajectory that needs updating
     */
    private void updateTrajectory(ObservableAircraftState aircraftState, Group trajectory) {
        trajectory.getChildren().clear();
        if (trajectory.isVisible()) {
            for (int i = 1; i < aircraftState.getTrajectory().size(); i++) {

                GeoPos start = aircraftState.getTrajectory().get(i - 1).position();
                GeoPos end = aircraftState.getTrajectory().get(i).position();
                //creating a line between the two positions
                Line line = new Line(WebMercator.x(zoomProperty.get(), start.longitude()),
                        WebMercator.y(zoomProperty.get(), start.latitude()),
                        WebMercator.x(zoomProperty.get(), end.longitude()),
                        WebMercator.y(zoomProperty.get(), end.latitude()));
                //coloring in the trajectory
                colorTrajectory(aircraftState.getTrajectory().get(i - 1).altitude(),
                        aircraftState.getTrajectory().get(i).altitude(), line);
                line.layoutXProperty().bind(mapParameters.minXProperty().negate());
                line.layoutYProperty().bind(mapParameters.minYProperty().negate());
                trajectory.getChildren().add(line);
            }
        }
    }

    /**
     * private method that allows to color in the trajectory depending on the altitude
     *
     * @param altitude1, double altitude of the beginning of the line we wish to color in
     * @param altitude2  double, altitude of the end of the line we wish to color in
     * @param line,      the line we want to color
     */
    private void colorTrajectory(double altitude1, double altitude2, Line line) {
        if (altitude1 == altitude2) {
            line.setStroke(ColorRamp.PLASMA.at(altitude1));
        } else {
            line.setStroke(new LinearGradient(0, 0, 1, 0,
                    true, CycleMethod.NO_CYCLE,
                    new Stop(0, ColorRamp.PLASMA.at(altitude1)),
                    new Stop(1, ColorRamp.PLASMA.at(altitude2))));
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Aircraft info ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * private method that creates and sets the icon and label for an airplane group
     *
     * @param aircraftState aircraftState of the plane we want to create icon and label for.
     * @return group containing the icon and label.
     */
    private Group setAircraftInfo(ObservableAircraftState aircraftState) {
        SVGPath icon = setIcon(aircraftState);
        Group label = setLabel(aircraftState);
        Group aircraftInfo = new Group(icon, label);
        repositionAircraft(aircraftState, aircraftInfo);

        minXProperty.addListener((observable, oldValue, newValue) -> repositionAircraft(aircraftState, aircraftInfo));
        minYProperty.addListener((observable, oldValue, newValue) -> repositionAircraft(aircraftState, aircraftInfo));
        aircraftState.positionProperty().addListener((observable, oldValue, newValue) ->
                repositionAircraft(aircraftState, aircraftInfo));
        aircraftState.altitudeProperty().addListener((observable) -> setLabel(aircraftState));
        aircraftState.velocityProperty().addListener((observable -> setLabel(aircraftState)));
        mapParameters.zoomProperty().addListener((observable -> changeVisibility(label, aircraftState)));
        selected.addListener((observable -> changeVisibility(label, aircraftState)));

        return aircraftInfo;
    }

    /**
     * private method that creates the icon of the aircraft.
     *
     * @param aircraftState aircraftState of the plane we want to create icon.
     * @return the icon of the aircraft.
     */
    private SVGPath setIcon(ObservableAircraftState aircraftState) {

        ObservableValue<AircraftIcon> aircraftIcon = aircraftState.categoryProperty().map(category -> {
            if (aircraftState.getAircraftData() != null) {
                return AircraftIcon.iconFor(
                        aircraftState.getAircraftData().typeDesignator() != null ?
                                aircraftState.getAircraftData().typeDesignator() : new AircraftTypeDesignator(""),
                        aircraftState.getAircraftData().description() != null ?
                                aircraftState.getAircraftData().description() : new AircraftDescription(""),
                        category.intValue(),
                        aircraftState.getAircraftData().wakeTurbulenceCategory() != null ?
                                aircraftState.getAircraftData().wakeTurbulenceCategory() :
                                WakeTurbulenceCategory.of(""));
            } else {
                return AircraftIcon.iconFor(new AircraftTypeDesignator(""),
                        new AircraftDescription(""), category.intValue(), WakeTurbulenceCategory.of(""));
            }
        });


        SVGPath icon = new SVGPath();
        icon.contentProperty().bind(aircraftIcon.map(AircraftIcon::svgPath));
        altitudeColorFill(icon, aircraftState);
        if (aircraftIcon.getValue().canRotate()) {
            setIconRotation(icon, aircraftState);
        }
        aircraftState.trackOrHeadingProperty().addListener((observable, oldValue, newValue) -> {
            if (aircraftIcon.getValue().canRotate()) {
                setIconRotation(icon, aircraftState);
            }
        });
        aircraftState.altitudeProperty().addListener((observable, oldValue, newValue) ->
                altitudeColorFill(icon, aircraftState));

        icon.getStyleClass().add("aircraft");
        return icon;
    }

    /**
     * private method that sets the filling color of the icon depending on the altitude of the aircraft.
     *
     * @param icon          icon of the aircraft.
     * @param aircraftState aircraftState of the plane we want to set icon colour for.
     */
    private void altitudeColorFill(SVGPath icon, ObservableAircraftState aircraftState) {
        icon.setFill(colorRamp.at(aircraftState.getAltitude()));
    }

    /**
     * private method that sets the rotation of the icon depending on the heading of the aircraft.
     *
     * @param icon          icon of the aircraft.
     * @param aircraftState aircraftState of the plane we want to set rotation for.
     */
    private void setIconRotation(SVGPath icon, ObservableAircraftState aircraftState) {
        icon.setRotate(Units.convertTo(aircraftState.getTrackOrHeading(), DEGREE));
    }

    /**
     * private method that repositions an aircraft on the map, which is called when user moves the map
     * or when the aircraft position changes.
     *
     * @param aircraftState aircraftState of the aircraft we want to reposition.
     * @param aircraftInfo  group containing the icon and label of the aircraft to reposition.
     */
    private void repositionAircraft(ObservableAircraftState aircraftState, Group aircraftInfo) {
        aircraftInfo.setLayoutX(xOnScreen(aircraftState.getPosition()));
        aircraftInfo.setLayoutY(yOnScreen(aircraftState.getPosition()));
    }

    /**
     * X projection on the WebMercator map depending on the mapParameters and the position of the aircraft.
     *
     * @param aircraftPositionProperty property of the aircraft position.
     * @return the x projection on the WebMercator map.
     */
    private Double xOnScreen(GeoPos aircraftPositionProperty) {
        return WebMercator.x(zoomProperty.get(), aircraftPositionProperty.longitude())
                - minXProperty.get();
    }

    /**
     * Y projection on the WebMercator map depending on the mapParameters and the position of the aircraft.
     *
     * @param aircraftPositionProperty property of the aircraft position.
     * @return the y projection on the WebMercator map.
     */
    private Double yOnScreen(GeoPos aircraftPositionProperty) {
        return WebMercator.y(zoomProperty.get(), aircraftPositionProperty.latitude())
                - minYProperty.get();
    }

    /**
     * private method that sets the creates and sets the label for an aircraft.
     *
     * @param aircraftState aircraftState of the plane we want to create label for.
     * @return group containing the label.
     */
    private Group setLabel(ObservableAircraftState aircraftState) {
        Text t1 = new Text();

        t1.textProperty().bind(Bindings.createStringBinding(() ->
                        aircraftState.getAircraftData() != null ?
                                aircraftState.getAircraftData().registration().string() :
                                (aircraftState.getCallSign() != null ?
                                        aircraftState.getCallSign().string() :
                                        aircraftState.getIcaoAddress().string()),
                aircraftState.callSignProperty()));

        Text t2 = new Text();
        t2.textProperty().bind(Bindings.createStringBinding(() -> {

            String velocityString = !Double.isNaN(aircraftState.velocityProperty().get()) ?
                    String.format("%.0f", Units.convertTo(aircraftState.getVelocity(), Units.Speed.KILOMETER_PER_HOUR))
                    : "?";

            //we do not check if altitude is NaN since we would not accept it
            String altitudeString = String.format("%.0f", aircraftState.getAltitude());

            return String.format("\n" + velocityString + "\u2002km/h,\u2002" + altitudeString + "\u2002m");
        }, aircraftState.velocityProperty(), aircraftState.altitudeProperty()));


        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(t2.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rectangle.heightProperty().bind(t2.layoutBoundsProperty().map(b -> b.getHeight() + 4));
        Group label = new Group(rectangle, t1, t2);
        label.getStyleClass().add("label");
        changeVisibility(label, aircraftState);
        selected.addListener((observable, oldValue, newValue) -> changeVisibility(label, aircraftState));

        return label;
    }

    /**
     * private method that changes the visibility of the label depending on the zoom level of the map and
     * the selected aircraft.
     *
     * @param label         label of an aircraft.
     * @param aircraftState aircraftState of the plane we update the visibility of.
     */
    private void changeVisibility(Group label, ObservableAircraftState aircraftState) {
        if (selected.get() != null) {
            label.setVisible(mapParameters.zoomProperty().get() >= ZOOM_VISIBILITY_THRESHOLD
                    || selected.get().equals(aircraftState));
        } else {
            label.setVisible(mapParameters.zoomProperty().get() >= ZOOM_VISIBILITY_THRESHOLD);
        }
    }


    /**
     * private method that adds a listener to the set of observable aircraft states that
     * adds/removes the group of a plane that has been added/removed from the set.
     */
    private void addListenerToSet() {
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        addAnnotated(change.getElementAdded());
                    } else {
                        pane.getChildren().remove(pane.lookup("#" +
                                change.getElementRemoved().getIcaoAddress().toString()));
                    }
                }
        );
    }

    /**
     * private method that adds the handler for a mouse click which turns the clicked aircraft into the "selected".
     */
    private void eventHandlers() {
        pane.setOnMouseClicked(mouseEvent -> {
            Node clicked = ((Node) mouseEvent.getTarget()).getParent().getParent();
            for (ObservableAircraftState state : aircraftStates) {
                if (pane.lookup("#" + state.getIcaoAddress().toString()).equals(clicked)) {
                    selected.set(state);
                    break;
                }
            }
        });
    }

    /**
     * Pane method which returns the pane of the AircraftController.
     *
     * @return Pane of the aircraft controller.
     */
    public Pane pane() {
        return pane;
    }


}
