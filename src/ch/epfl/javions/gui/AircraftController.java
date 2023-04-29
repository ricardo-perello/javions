package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

public final class AircraftController {
    Scene scene;
    Pane pane = new Pane();


    MapParameters mapParameters;
    ObservableSet<ObservableAircraftState> aircraftStates;
    ObservableAircraftState observableAircraftState;
    SimpleIntegerProperty zoomProperty = new SimpleIntegerProperty();
    SimpleDoubleProperty minXProperty = new SimpleDoubleProperty();
    SimpleDoubleProperty minYProperty = new SimpleDoubleProperty();



    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> observableAircraftStateObjectProperty){

        this.mapParameters = mapParameters;
        this.aircraftStates = aircraftStates;
        this.observableAircraftState = observableAircraftStateObjectProperty.get();
        zoomProperty.bind(mapParameters.zoomProperty());
        minXProperty.bind(mapParameters.minXProperty());
        minYProperty.bind(mapParameters.minYProperty());
        addAnnotatedGroups();
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> { if(change.wasAdded()){
                   addAnnotated(change.getElementAdded());
                }
                else{
                    pane.getChildren().remove(pane.lookup("#"+
                            change.getElementRemoved().getIcaoAddress().toString()));
                }
                });

        pane.setPickOnBounds(false);
        scene = new Scene(pane);
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

    private void setLinks() {

    }

    private Group setAircraftInfo(ObservableAircraftState aircraftState) {
        Group aircraftInfo = new Group(setIcon(aircraftState));//, setLabel(aircraftState));
        SimpleObjectProperty<GeoPos> aircraftPositionProperty = new SimpleObjectProperty<>();
        aircraftPositionProperty.bind(aircraftState.positionProperty());

        aircraftInfo.layoutXProperty().bind(xOnScreen(aircraftPositionProperty));
        aircraftInfo.layoutYProperty().bind(yOnScreen(aircraftPositionProperty));
        return aircraftInfo;
    }

    private ReadOnlyDoubleProperty xOnScreen(SimpleObjectProperty<GeoPos> aircraftPositionProperty) {
        double x = WebMercator.x(zoomProperty.get(), aircraftPositionProperty.getValue().longitude())
                - minXProperty.get();
        return new SimpleDoubleProperty(x) ;
    }
    private ReadOnlyDoubleProperty yOnScreen(SimpleObjectProperty<GeoPos> aircraftPositionProperty) {
        double y = WebMercator.y(zoomProperty.get(), aircraftPositionProperty.getValue().latitude())
                - minYProperty.get();
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

    private SVGPath setIcon(ObservableAircraftState aircraftState) {
        AircraftIcon aircraftIcon = AircraftIcon.iconFor(aircraftState.getAircraftData().typeDesignator(),
                aircraftState.getAircraftData().description(),
                aircraftState.getCategory(),
                aircraftState.getAircraftData().wakeTurbulenceCategory() );
        SVGPath icon = new SVGPath();
        icon.setContent(aircraftIcon.svgPath());
        SimpleDoubleProperty xOnScreenProperty = new SimpleDoubleProperty();



        SimpleDoubleProperty yOnScreen = new SimpleDoubleProperty();
        icon.layoutXProperty().bind(xOnScreenProperty);
        icon.layoutYProperty().bind(yOnScreen);
        //todo fix css file
        // icon.setStyle(getStyleClass());
        return icon;
    }

    public Pane pane(){
        return pane;
    }


}
