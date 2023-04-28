package ch.epfl.javions.gui;

import com.sun.javafx.scene.shape.SVGPathHelper;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import javax.swing.*;
import java.util.HashSet;

import static com.sun.javafx.css.StyleClassSet.getStyleClass;

public final class AircraftController {
    Scene scene;
    Pane pane;


    MapParameters parameters;
    ObservableSet<ObservableAircraftState> aircraftStates;
    ObservableAircraftState observableAircraftState;

    public AircraftController(MapParameters parameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> observableAircraftStateObjectProperty){

        this.parameters = parameters;
        this.aircraftStates = aircraftStates;
        this.observableAircraftState = observableAircraftStateObjectProperty.get();
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
        return new Group(setIcon(aircraftState));//, setLabel(aircraftState));
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
        //todo fix css file
        // icon.setStyle(getStyleClass());
        return icon;
    }

    public Pane pane(){
        return pane;
    }


}
