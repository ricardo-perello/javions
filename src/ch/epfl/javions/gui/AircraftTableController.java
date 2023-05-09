package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.function.Consumer;

public final class AircraftTableController {
    private static final int PREFERRED_WIDTH_ICAO = 60;
    private static final int PREFERRED_WIDTH_DESCRIPTION = 70;
    private static final int PREFERRED_WIDTH_CALLSIGN = PREFERRED_WIDTH_DESCRIPTION;
    private static final int PREFERRED_WIDTH_REGISTRATION = 90;
    private static final int PREFERRED_WIDTH_MODEL = 230;
    private static final int PREFERRED_WIDTH_TYPE = 50;
    private final ObservableSet<ObservableAircraftState> aircraftStates;
    private final ObjectProperty<ObservableAircraftState> selectedPlane;
    private final TableView<ObservableAircraftState> table; //todo ns si es de este tipo

    private TableColumn<ObservableAircraftState, String> column_ICAO, column_CallSign,
                                                        column_Registration, column_Model,
                                                        column_Type, column_Description;

    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> selectedPlane){
        this.aircraftStates = aircraftStates;
        this.selectedPlane = selectedPlane;
        table = new TableView<>();
        table.styleProperty().set("table.css");
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
