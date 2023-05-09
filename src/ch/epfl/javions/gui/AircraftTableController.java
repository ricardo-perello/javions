package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
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
        createColumns();
        setColumnsValue();
        setPreferredWidths();

        table.getColumns().addAll(column_ICAO, column_CallSign, column_Registration,column_Model,column_Type,column_Description);
        addRows();
    }

    private void addRows() {
        for (ObservableAircraftState aircraftState : aircraftStates) {
            table.getItems().add(aircraftState);
        }
    }

    private void setPreferredWidths() {
        column_ICAO.setPrefWidth(PREFERRED_WIDTH_ICAO);
        column_CallSign.setPrefWidth(PREFERRED_WIDTH_CALLSIGN);
        column_Registration.setPrefWidth(PREFERRED_WIDTH_REGISTRATION);
        column_Model.setPrefWidth(PREFERRED_WIDTH_MODEL);
        column_Type.setPrefWidth(PREFERRED_WIDTH_TYPE);
        column_Description.setPrefWidth(PREFERRED_WIDTH_DESCRIPTION);
    }

    private void setColumnsValue(){
        column_ICAO.setCellValueFactory(newRow ->
                new ReadOnlyObjectWrapper<>(newRow.getValue().getIcaoAddress().string()));

        column_CallSign.setCellValueFactory(newRow -> newRow.getValue().callSignProperty().map(CallSign::string));

        column_Registration.setCellValueFactory(newRow ->
                new ReadOnlyObjectWrapper<String>(newRow.getValue()
                        .getAircraftData().registration().string()));

        column_Model.setCellValueFactory(newRow ->
                new ReadOnlyObjectWrapper<>(newRow.getValue().getAircraftData().model()));

        column_Type.setCellValueFactory(newRow ->
                new ReadOnlyObjectWrapper<>(newRow.getValue().getAircraftData().typeDesignator().string()));

        column_Description.setCellValueFactory(newRow -> new ReadOnlyObjectWrapper<>(newRow.getValue().getAircraftData().description().string()));
    }

    private void createColumns() {
        column_ICAO = new TableColumn<>();
        column_CallSign = new TableColumn<>();
        column_Registration = new TableColumn<>();
        column_Model = new TableColumn<>();
        column_Type = new TableColumn<>();
        column_Description = new TableColumn<>();
    }

    public void pane(){
        //todo por HACER
    }

    public void setOnDoubleClick (Consumer<ObservableAircraftState> consumer){
        consumer.accept(selectedPlane.get());
    }
}
