package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;

import javax.swing.text.Element;
import javax.swing.text.TabableView;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.function.Consumer;

public final class AircraftTableController {
    private ObservableSet<ObservableAircraftState> aircraftStates;
    private ObjectProperty<ObservableAircraftState> selectedPlane;
    private TableView<ObservableAircraftState> table; //todo ns si es de este tipo

    private TableColumn<ObservableAircraftState, String> column_O


    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> selectedPlane){
        this.aircraftStates = aircraftStates;
        this.selectedPlane = selectedPlane;
        table = new TableView<>();
        table.styleProperty().set("resources/table.css");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        table.setTableMenuButtonVisible(true);
        TableColumn<ObservableAircraftState, String> columnString = new TableColumn<>();

    }

    public void pane(){
        //todo por HACER
    }

    public void setOnDoubleClick (Consumer<ObservableAircraftState> consumer){
        consumer.accept(selectedPlane.get());
    }
}
