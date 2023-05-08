package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point2D;
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
import static ch.epfl.javions.gui.AircraftIcon.iconFor;

public final class AircraftController {

    private final Pane pane = new Pane();
    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> aircraftStates;
    private final ObjectProperty<ObservableAircraftState> selected;
    private final IntegerProperty zoomProperty;
    private final DoubleProperty minXProperty;
    private final DoubleProperty minYProperty;
    private final ColorRamp colorRamp;


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

    private void addAnnotatedGroups() {
        for (ObservableAircraftState aircraftState : aircraftStates) {
            addAnnotated(aircraftState);
        }
    }

    private void addAnnotated(ObservableAircraftState aircraftState) {
        Group aircraftInfo = setAircraftInfo(aircraftState);
        Group trajectory = setTrajectory(aircraftState);
        Group annotated = new Group(trajectory, aircraftInfo);
        annotated.setId(aircraftState.getIcaoAddress().toString());
        annotated.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());
        pane.getChildren().add(annotated);
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Trajectory~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private Group setTrajectory(ObservableAircraftState aircraftState) {
        Group trajectory = new Group();
        trajectory.getStyleClass().add("trajectory");
        trajectory.setVisible(false);

        aircraftState.getTrajectory().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>)
                observable -> updateTrajectory(aircraftState, trajectory));

        zoomProperty.addListener((observable -> updateTrajectory(aircraftState, trajectory)));

        selected.addListener((observable, oldValue, newValue) -> trajectory.setVisible(newValue == aircraftState));

        return trajectory;
    }

    private void updateTrajectory(ObservableAircraftState aircraftState, Group trajectory) {

        if (trajectory.isVisible()) {
            trajectory.getChildren().clear();
            //ObservableList<ObservableAircraftState.AirbornePos> trajectoryPlane = aircraftState.getTrajectory();
            for (int i = 1; i < aircraftState.getTrajectory().size(); i++) {
                GeoPos start = aircraftState.getTrajectory().get(i - 1).position();
                GeoPos end = aircraftState.getTrajectory().get(i).position();

                Line line = new Line(WebMercator.x(zoomProperty.get(), start.longitude()),
                        WebMercator.y(zoomProperty.get(), start.latitude()),
                        WebMercator.x(zoomProperty.get(), end.longitude()),
                        WebMercator.y(zoomProperty.get(), end.latitude()));
                colorTrajectory(aircraftState.getTrajectory().get(i - 1).altitude(), aircraftState.getTrajectory().get(i).altitude(), line);
                line.layoutXProperty().bind(mapParameters.minXProperty().negate());
                line.layoutYProperty().bind(mapParameters.minYProperty().negate());
                trajectory.getChildren().add(line);
            }
        }
    }

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
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private Group setAircraftInfo(ObservableAircraftState aircraftState) {
        SVGPath icon = setIcon(aircraftState);
        Group label = setLabel(aircraftState);
        Group aircraftInfo = new Group(icon, label);
        repositionAircraft(aircraftState, aircraftInfo);

        minXProperty.addListener((observable, oldValue, newValue) -> repositionAircraft(aircraftState, aircraftInfo));
        minYProperty.addListener((observable, oldValue, newValue) -> repositionAircraft(aircraftState, aircraftInfo));
        aircraftState.positionProperty().addListener((observable, oldValue, newValue) -> repositionAircraft(aircraftState, aircraftInfo));
        aircraftState.altitudeProperty().addListener((observable) -> setLabel(aircraftState));
        aircraftState.velocityProperty().addListener((observable -> setLabel(aircraftState)));
        mapParameters.zoomProperty().addListener((observable -> changeVisibility(label, aircraftState)));
        selected.addListener((observable -> changeVisibility(label, aircraftState)));

        return aircraftInfo;
    }

    private SVGPath setIcon(ObservableAircraftState aircraftState) {
//todo hay q mirar si esto puede ser null
        AircraftIcon aircraftIcon = iconFor(aircraftState.getAircraftData().typeDesignator(),
                aircraftState.getAircraftData().description(),
                aircraftState.getCategory(),
                aircraftState.getAircraftData().wakeTurbulenceCategory());

        SVGPath icon = new SVGPath();
        icon.setContent(aircraftIcon.svgPath());
        altitudeColorFill(icon, aircraftState);
        aircraftState.trackOrHeadingProperty().addListener((observable, oldValue, newValue) -> {
            if (aircraftIcon.canRotate()) {
                setIconRotation(icon, aircraftState);
            }
        });
        aircraftState.altitudeProperty().addListener((observable, oldValue, newValue) ->
                altitudeColorFill(icon, aircraftState));

        icon.getStyleClass().add("aircraft");
        return icon;
    }

    private void altitudeColorFill(SVGPath icon, ObservableAircraftState aircraftState) {
        icon.setFill(colorRamp.at(aircraftState.getAltitude()));
    }

    private void setIconRotation(SVGPath icon, ObservableAircraftState aircraftState) {
        icon.setRotate(Units.convertTo(aircraftState.getTrackOrHeading(), DEGREE));
    }


    private void repositionAircraft(ObservableAircraftState aircraftState, Group aircraftInfo) {
        SimpleObjectProperty<GeoPos> aircraftPositionProperty = new SimpleObjectProperty<>();
        aircraftPositionProperty.bind(aircraftState.positionProperty());
        aircraftInfo.setLayoutX(xOnScreen(aircraftPositionProperty.getValue()).doubleValue());
        aircraftInfo.setLayoutY(yOnScreen(aircraftPositionProperty.getValue()).doubleValue());

    }

    private ReadOnlyDoubleProperty xOnScreen(GeoPos aircraftPositionProperty) {
        double x = WebMercator.x(zoomProperty.get(), aircraftPositionProperty.longitude())
                - minXProperty.get();
        return new SimpleDoubleProperty(x);
    }

    private ReadOnlyDoubleProperty yOnScreen(GeoPos aircraftPositionProperty) {
        double y = WebMercator.y(zoomProperty.get(), aircraftPositionProperty.latitude())
                - minYProperty.get();
        return new SimpleDoubleProperty(y);
    }


    private Group setLabel(ObservableAircraftState aircraftState) {

        Text t1 = new Text();
        if (aircraftState.getAircraftData().registration() != null) {
            t1.textProperty().bind(Bindings.createStringBinding(
                    () -> aircraftState.getAircraftData().registration().string()));
        } else if (aircraftState.getCallSign() != null) {
            t1.textProperty().bind(Bindings.createStringBinding(
                    () -> aircraftState.getCallSign().string()));
        } else {
            t1.textProperty().bind(Bindings.createStringBinding(
                    () -> aircraftState.getIcaoAddress().string()));
        }

        Text t2 = new Text();

        if (!Double.isNaN(aircraftState.velocityProperty().get()) &&
                !Double.isNaN(aircraftState.altitudeProperty().get())) {
            t2.textProperty().bind(Bindings.createStringBinding(() ->
                            String.format("\n%.0f\u2002km/h,\u2002%.0f\u2002m",
                                    Units.convertTo(aircraftState.getVelocity(), Units.Speed.KILOMETER_PER_HOUR),
                                    aircraftState.getAltitude()),
                    aircraftState.velocityProperty(),
                    aircraftState.altitudeProperty()));
        } else if (!Double.isNaN(aircraftState.velocityProperty().get()) &&
                Double.isNaN(aircraftState.altitudeProperty().get())) {
            t2.textProperty().bind(Bindings.createStringBinding(() ->
                    String.format("\n%.0f\u2002km/h,\u2002?\u2002m",
                            Units.convertTo(aircraftState.getVelocity(), Units.Speed.KILOMETER_PER_HOUR),
                            aircraftState.velocityProperty())));
        } else if (Double.isNaN(aircraftState.velocityProperty().get()) &&
                !Double.isNaN(aircraftState.altitudeProperty().get())) {
            t2.textProperty().bind(Bindings.createStringBinding(() ->
                            String.format("\n?\u2002km/h,\u2002%.0f\u2002m",
                            aircraftState.getAltitude()),
                            aircraftState.altitudeProperty()
            ));
        } else {
            t2.textProperty().bind(Bindings.createStringBinding(() ->
                    "\n?\u2002km/h,\u2002?\u2002m"));        }

        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(t2.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rectangle.heightProperty().bind(t2.layoutBoundsProperty().map(b -> b.getHeight() + 4));
        Group label = new Group(rectangle, t1, t2);
        label.getStyleClass().add("label");
        changeVisibility(label, aircraftState);
        selected.addListener((observable, oldValue, newValue) -> changeVisibility(label, aircraftState));


        return label;
    }

    private void changeVisibility(Group label, ObservableAircraftState aircraftState) {
        if (selected.get() != null) {
            label.setVisible(mapParameters.zoomProperty().get() >= 11 || selected.get().equals(aircraftState));
        } else {
            label.setVisible(mapParameters.zoomProperty().get() >= 11);
        }
        //todo hacer q se pueda salir del selected
    }



    private void addListenerToSet() {
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        addAnnotated(change.getElementAdded());
                    } else {
                        pane.getChildren().remove(pane.lookup("#" +
                                change.getElementRemoved().getIcaoAddress().toString()));
                    }
                });
    }

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


    public Pane pane() {
        return pane;
    }


}
