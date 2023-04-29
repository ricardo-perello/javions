package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.concurrent.Callable;

import static javafx.beans.binding.Bindings.createDoubleBinding;

public final class AircraftController {
    Scene scene;
    Pane pane = new Pane();


    MapParameters mapParameters;
    ObservableSet<ObservableAircraftState> aircraftStates;
    ObservableAircraftState observableAircraftState;
    int zoom;
    double minX;
    double minY;



    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> observableAircraftStateObjectProperty){


        this.mapParameters = mapParameters;
        this.aircraftStates = aircraftStates;
        this.observableAircraftState = observableAircraftStateObjectProperty.get();
        addAnnotatedGroups();

        pane.setPickOnBounds(false);
        scene = new Scene(pane);
        addListeners();

    }

    private void addListeners() {
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
        pane.getChildren().add(annotated);
    }

    private Group setTrajectory(ObservableAircraftState aircraftState) {
        return  null;
    }


    private Group setAircraftInfo(ObservableAircraftState aircraftState) {
        Group aircraftInfo = new Group(setIcon(aircraftState));//, setLabel(aircraftState));
        SimpleObjectProperty<GeoPos> aircraftPositionProperty = new SimpleObjectProperty<>();
        aircraftPositionProperty.bind(aircraftState.positionProperty());
        mapParameters.minXProperty().addListener((observable, oldValue, newValue) ->
            aircraftInfo.setLayoutX(xOnScreen(aircraftPositionProperty).doubleValue()));

        mapParameters.minYProperty().addListener((observable, oldValue, newValue) ->{
                aircraftInfo.setLayoutY(yOnScreen(aircraftPositionProperty).doubleValue());
            System.out.println(newValue);
                });

        /*
        aircraftInfo.layoutXProperty().bind(xOnScreen(aircraftPositionProperty));
        aircraftInfo.layoutYProperty().bind(yOnScreen(aircraftPositionProperty));

         */
        return aircraftInfo;
    }

    private SVGPath setIcon(ObservableAircraftState aircraftState) {
        AircraftIcon aircraftIcon = AircraftIcon.iconFor(aircraftState.getAircraftData().typeDesignator(),
                aircraftState.getAircraftData().description(),
                aircraftState.getCategory(),
                aircraftState.getAircraftData().wakeTurbulenceCategory() );
        SVGPath icon = new SVGPath();
        icon.setContent(aircraftIcon.svgPath());
        //todo fix css file
        // icon.setStyle(getStyleClass());
        return icon;
    }

    private ReadOnlyDoubleProperty xOnScreen(SimpleObjectProperty<GeoPos> aircraftPositionProperty) {
        double x = WebMercator.x(zoom, aircraftPositionProperty.getValue().longitude())
                - mapParameters.minXProperty().get();
        return new SimpleDoubleProperty(x) ;
    }

    private ReadOnlyDoubleProperty yOnScreen(SimpleObjectProperty<GeoPos> aircraftPositionProperty) {
        double y = WebMercator.y(zoom, aircraftPositionProperty.getValue().latitude())
                - mapParameters.minYProperty().get();
        return new SimpleDoubleProperty(y) ;
    }


    /*private Group setLabel(ObservableAircraftState aircraftState) {
        StringBuilder s
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


    }*/

    public Pane pane(){
        return pane;
    }


}
