package ch.epfl.javions.gui;

import com.sun.javafx.scene.shape.SVGPathHelper;
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

    /*public  pane(){
        return pane;
    }*/


}
