package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftRegistration;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.util.Iterator;

import static ch.epfl.javions.Units.Angle.DEGREE;

import static javafx.beans.binding.Bindings.negate;

public final class AircraftController {
    Scene scene;
    Pane pane = new Pane();


    MapParameters mapParameters;
    ObservableSet<ObservableAircraftState> aircraftStates;
    ObjectProperty<ObservableAircraftState> selected;
    IntegerProperty zoomProperty;
    DoubleProperty minXProperty;
    DoubleProperty minYProperty;
    ColorRamp colorRamp;




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
        scene = new Scene(pane);
        pane.getStylesheets().add("aircraft.css");
        addListenerToSet();
        eventHandlers();
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
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Node clicked = ((Node) mouseEvent.getTarget()).getParent().getParent();
                if (pane.getChildren().contains(clicked)){
                    for (ObservableAircraftState state : aircraftStates) {
                        if (pane.lookup("#" + state.getIcaoAddress().toString()).equals(clicked)) {
                            selected.set(state);
                        }
                    }
                }
            }
        });
    }

    private void addAnnotatedGroups() {
        for (ObservableAircraftState aircraftState : aircraftStates) {
            addAnnotated(aircraftState);
        }
    }

    private void addAnnotated(ObservableAircraftState aircraftState) {
        Group aircraftInfo = setAircraftInfo(aircraftState);
        Group trajectory = setTrajectory(aircraftState);
        Group annotated = new Group(aircraftInfo);//, trajectory);
        annotated.setId(aircraftState.getIcaoAddress().toString());
        annotated.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());
        pane.getChildren().add(annotated);
    }

    private Group setTrajectory(ObservableAircraftState aircraftState) {
        Group trajectory;
        ObservableList<ObservableAircraftState.AirbornePos> trajectoryList = aircraftState.getTrajectory();
        GeoPos startingPos = trajectoryList.get(0).geoPos();
        Path path = new Path();
        path.getElements().add(new MoveTo(startingPos.latitude(), startingPos.longitude()));
        for (ObservableAircraftState.AirbornePos airbornePos : trajectoryList) {
            double xEnd = airbornePos.geoPos().latitude();
            double yEnd = airbornePos.geoPos().longitude();
            path.getElements().add(new LineTo(xEnd, yEnd));
        }
        trajectoryList.addListener((ListChangeListener<? super ObservableAircraftState.AirbornePos>) observable -> {
            if (aircraftState.getTrajectory().size() > 1) {
                double xStart = trajectoryList.get(trajectoryList.size()-1).geoPos().latitude();
                double yStart = trajectoryList.get(trajectoryList.size()-1).geoPos().longitude();
                path.getElements().add(new LineTo(xStart, yStart));
            }
        });

        return  null;
    }

    private Group setAircraftInfo(ObservableAircraftState aircraftState) {
        SVGPath icon = setIcon(aircraftState);
        Group label = setLabel(aircraftState);
        Group aircraftInfo = new Group(icon, label);
        repositionAircraft(aircraftState, aircraftInfo);

        minXProperty.addListener((observable, oldValue, newValue) -> {
            repositionAircraft(aircraftState, aircraftInfo);
        });
        minYProperty.addListener((observable, oldValue, newValue) -> {
            repositionAircraft(aircraftState, aircraftInfo);
        });
        aircraftState.positionProperty().addListener((observable, oldValue, newValue) -> {
            repositionAircraft(aircraftState, aircraftInfo);
        });
        aircraftState.altitudeProperty().addListener((observable)-> setLabel(aircraftState));
        aircraftState.velocityProperty().addListener((observable -> setLabel(aircraftState)));
        mapParameters.zoomProperty().addListener((observable -> changeVisibility(label)));

        return aircraftInfo;
    }

    private SVGPath setIcon(ObservableAircraftState aircraftState) {
//todo hay q mirar si esto puede ser null
        AircraftIcon aircraftIcon = AircraftIcon.iconFor(aircraftState.getAircraftData().typeDesignator(),
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
        aircraftInfo.setLayoutX(xOnScreen(aircraftPositionProperty).doubleValue());
        aircraftInfo.setLayoutY(yOnScreen(aircraftPositionProperty).doubleValue());

    }

    private void repositionTrajectoryPath(ObservableAircraftState aircraftState, Group trajectory) {
        SimpleObjectProperty<GeoPos> aircraftPositionProperty = new SimpleObjectProperty<>();
        aircraftPositionProperty.bind(aircraftState.positionProperty());
        trajectory.setLayoutX(xOnScreen(aircraftPositionProperty).doubleValue());
        trajectory.setLayoutY(yOnScreen(aircraftPositionProperty).doubleValue());

    }

    private ReadOnlyDoubleProperty xOnScreen(SimpleObjectProperty<GeoPos> aircraftPositionProperty) {
        double x = WebMercator.x(zoomProperty.get(), aircraftPositionProperty.getValue().longitude())
                - minXProperty.get();
        return new SimpleDoubleProperty(x);
    }

    private ReadOnlyDoubleProperty yOnScreen(SimpleObjectProperty<GeoPos> aircraftPositionProperty) {
        double y = WebMercator.y(zoomProperty.get(), aircraftPositionProperty.getValue().latitude())
                - minYProperty.get();
        return new SimpleDoubleProperty(y);
    }


    private Group setLabel(ObservableAircraftState aircraftState) {

        Text t1 = new Text();
        if (aircraftState.getAircraftData().registration() != null){
            t1.textProperty().bind(Bindings.createStringBinding(
                    ()->aircraftState.getAircraftData().registration().string()));
        } else if (aircraftState.getCallSign() != null) {
            t1.textProperty().bind(Bindings.createStringBinding(
                    ()->aircraftState.getCallSign().string()));
        }else{
            t1.textProperty().bind(Bindings.createStringBinding(
                    ()->aircraftState.getIcaoAddress().string()));
        }

        Text t2 = new Text();

        if ( !Double.isNaN(aircraftState.velocityProperty().get()) &&
                !Double.isNaN(aircraftState.altitudeProperty().get())){
            t2.textProperty().bind(Bindings.createStringBinding(()->
                String.format("\n%.0f\u2002km/h,\u2002%.0f\u2002m",
                    Units.convertTo(aircraftState.getVelocity(),Units.Speed.KILOMETER_PER_HOUR),
                    aircraftState.getAltitude()),
                    aircraftState.velocityProperty(),
                    aircraftState.altitudeProperty()));
        }else if (!Double.isNaN(aircraftState.velocityProperty().get()) &&
                Double.isNaN(aircraftState.altitudeProperty().get())){
            t2.textProperty().bind(Bindings.createStringBinding(()->
                String.format("\n%.0f\u2002km/h,\u2002%?\u2002m",
                    Units.convertTo(aircraftState.getVelocity(),Units.Speed.KILOMETER_PER_HOUR),
                    aircraftState.velocityProperty())));
        }else if (Double.isNaN(aircraftState.velocityProperty().get()) &&
                !Double.isNaN(aircraftState.altitudeProperty().get())){
            t2.textProperty().bind(Bindings.createStringBinding(()->
                    String.format("\n%?\u2002km/h,\u2002%.0f\u2002m",
                    aircraftState.getAltitude()),
                    aircraftState.altitudeProperty()
            ));
        }else{
            t2.textProperty().bind(Bindings.createStringBinding(()->
                    String.format("\n%?\u2002km/h,\u2002%?\u2002m")));
        }

        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(t2.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rectangle.heightProperty().bind(t2.layoutBoundsProperty().map(b -> b.getHeight() + 4));
        Group label = new Group(rectangle, t1, t2);
        label.getStyleClass().add("label");
        changeVisibility(label);


        return label;
    }

    private boolean requireNonNull(Double v) {
        if (v != null){
            return  true;
        }
        return false;
    }

    private void changeVisibility(Group label){
        label.setVisible(mapParameters.zoomProperty().get()>= 11);
    }


    public Pane pane() {
        return pane;
    }


}
