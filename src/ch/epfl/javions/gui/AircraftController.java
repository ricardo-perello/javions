package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftRegistration;
import javafx.beans.property.*;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import static ch.epfl.javions.Units.Angle.DEGREE;
import static javafx.beans.binding.Bindings.negate;

public final class AircraftController {
    Scene scene;
    Pane pane = new Pane();


    MapParameters mapParameters;
    ObservableSet<ObservableAircraftState> aircraftStates;
    ObservableAircraftState observableAircraftState;
    IntegerProperty zoomProperty;

    DoubleProperty minXProperty;
    DoubleProperty minYProperty;




    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> observableAircraftStateObjectProperty){


        this.mapParameters = mapParameters;
        this.aircraftStates = aircraftStates;
        this.observableAircraftState = observableAircraftStateObjectProperty.get();
        /*minXProperty.bind(mapParameters.minXProperty());
        minYProperty.bind(mapParameters.minYProperty());*/
        minXProperty = (DoubleProperty) mapParameters.minXProperty();
        minYProperty = (DoubleProperty) mapParameters.minYProperty();
        //zoomProperty.bind(mapParameters.zoomProperty());
        zoomProperty = (IntegerProperty) mapParameters.zoomProperty();
        addAnnotatedGroups();

        pane.setPickOnBounds(false);
        scene = new Scene(pane);
        pane.getStylesheets().add("aircraft.css");
        addListenerToSet();
    }

    private void addListenerToSet() {
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> { if(change.wasAdded()){
                    addAnnotated(change.getElementAdded());
                }
                else{
                    pane.getChildren().remove(pane.lookup("#"+
                            change.getElementRemoved().getIcaoAddress().toString()));
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
        annotated.viewOrderProperty().bind(negate(aircraftState.altitudeProperty()));
        pane.getChildren().add(annotated);
    }

    private Group setTrajectory(ObservableAircraftState aircraftState) {
        //trajectory.getStyleClass().add("trajectory");

        return  null;
    }

    private Group setAircraftInfo(ObservableAircraftState aircraftState) {
        SVGPath icon = setIcon(aircraftState);
        Group aircraftInfo = new Group(icon);//, setLabel(aircraftState));

        minXProperty.addListener((observable, oldValue, newValue) -> {
            repositionAircraft(aircraftState, aircraftInfo);
        });
        minYProperty.addListener((observable, oldValue, newValue) ->{
            repositionAircraft(aircraftState, aircraftInfo);
        });
        aircraftState.positionProperty().addListener((observable, oldValue, newValue) -> {
            repositionAircraft(aircraftState, aircraftInfo);
        });


        //aircraftInfo.layoutXProperty().bind(xOnScreen(aircraftPositionProperty));
        //aircraftInfo.layoutYProperty().bind(yOnScreen(aircraftPositionProperty));


        return aircraftInfo;
    }
    private SVGPath setIcon(ObservableAircraftState aircraftState) {

        AircraftIcon aircraftIcon = AircraftIcon.iconFor(aircraftState.getAircraftData().typeDesignator(),
                aircraftState.getAircraftData().description(),
                aircraftState.getCategory(),
                aircraftState.getAircraftData().wakeTurbulenceCategory() );

        SVGPath icon = new SVGPath();
        icon.setContent(aircraftIcon.svgPath());
        aircraftState.trackOrHeadingProperty().addListener((observable, oldValue, newValue) -> {
            if (aircraftIcon.canRotate()){
            setIconRotation(icon, aircraftState);
            }
        });
        icon.getStyleClass().add("aircraft");
        return icon;
    }

    private void setIconRotation(SVGPath icon, ObservableAircraftState aircraftState) {
        icon.setRotate(Units.convertTo(aircraftState.getTrackOrHeading(), DEGREE));
        System.out.println(Units.convertTo(aircraftState.getTrackOrHeading(), DEGREE));

    }

    private void repositionAircraft(ObservableAircraftState aircraftState, Group aircraftInfo){
        SimpleObjectProperty<GeoPos> aircraftPositionProperty = new SimpleObjectProperty<>();
        aircraftPositionProperty.bind(aircraftState.positionProperty());
        aircraftInfo.setLayoutX(xOnScreen(aircraftPositionProperty).doubleValue());
        aircraftInfo.setLayoutY(yOnScreen(aircraftPositionProperty).doubleValue());

    }

    private ReadOnlyDoubleProperty xOnScreen(SimpleObjectProperty<GeoPos> aircraftPositionProperty) {
        double x = WebMercator.x(zoomProperty.get(), aircraftPositionProperty.getValue().longitude())
                - minXProperty.get();
        return new SimpleDoubleProperty(x) ;
    }

    private ReadOnlyDoubleProperty yOnScreen(SimpleObjectProperty<GeoPos> aircraftPositionProperty) {
        double y = WebMercator.y(zoomProperty.get(), aircraftPositionProperty.getValue().latitude())
                - minYProperty.get();
        return new SimpleDoubleProperty(y);
    }


    private Group setLabel(ObservableAircraftState aircraftState) {

        Text t1 = new Text();
        AircraftRegistration registration = aircraftState.getAircraftData().registration();
        if(aircraftState.getAircraftData().registration() != null){
            t1.textProperty().setValue(registration.string());
        }else{
            CallSign callSign = aircraftState.getCallSign();
            if (callSign != null){
                t1.textProperty().setValue(callSign.string());
            }
            else{
                t1.textProperty().setValue(aircraftState.getIcaoAddress().string());
            }
        }


















        StringBuilder s;
        String text1 = " ";

        if(aircraftState.getAircraftData().registration() != null){
            text1 += aircraftState.getAircraftData().registration().toString();
        } else if (aircraftState.getCallSign() != null) {
            text1 += aircraftState.getCallSign().toString();
        }else{
            text1 += aircraftState.getIcaoAddress();
        }

        String text2 = " ";

        if (Double.isNaN(aircraftState.getVelocity())){
            text2 += "?";
        }else {
            text2 += Double.toString(aircraftState.getVelocity());
        }

        text2 += " m/s ";

        if (Double.isNaN(aircraftState.getAltitude())){
            text2 += "?";
        }
        else{
            text2 += Double.toString(aircraftState.getAltitude());
        }

        text2 += " m";


        Text text = new Text();
        text.setText(text1 + text2);
        label.getStyleClass().add("label");


    }

    public Pane pane(){
        return pane;
    }


}
