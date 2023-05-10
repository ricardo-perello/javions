package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.text.NumberFormat;
import java.util.function.Consumer;

public final class AircraftTableController {
    private static final int PREFERRED_WIDTH_ICAO = 60;
    private static final int PREFERRED_WIDTH_DESCRIPTION = 70;
    private static final int PREFERRED_WIDTH_CALLSIGN = PREFERRED_WIDTH_DESCRIPTION;
    private static final int PREFERRED_WIDTH_REGISTRATION = 90;
    private static final int PREFERRED_WIDTH_MODEL = 230;
    private static final int PREFERRED_WIDTH_TYPE = 50;
    private static final int PREFERRED_WIDTH_NUMERIC = 85;
    private final ObservableSet<ObservableAircraftState> aircraftStates;
    private final ObjectProperty<ObservableAircraftState> selected;
    private final TableView<ObservableAircraftState> table;
    private static NumberFormat numberFormatPosition;
    private static NumberFormat numberFormat;
    private final Pane pane;

    private TableColumn<ObservableAircraftState, String> column_ICAO, column_CallSign,
            column_Registration, column_Model,
            column_Type, column_Description,
            column_Longitude, column_Latitude,
            column_Altitude, column_Velocity,
            column_Heading;

    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> selected) {
        this.aircraftStates = aircraftStates;
        this.selected = selected;
        table = new TableView<>();
        this.pane = new Pane(table);
        table.getStylesheets().add("table.css");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        table.setTableMenuButtonVisible(true);

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                setOnDoubleClick(selected::set);
            }
        });

        selected.addListener((observable, oldValue, newValue) -> {
            if(newValue != table.getSelectionModel().getSelectedItem()){
                table.getSelectionModel().select(newValue);
                table.scrollTo(table.getSelectionModel().getSelectedIndex());
                System.out.println("new table selection: " + newValue.getIcaoAddress().string());
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != selected.get()){
                selected.set(newValue);
                System.out.println("new selected: " + newValue.getIcaoAddress().string());
            }
        });

        setNumberFormatters();
        createColumns();
        addRows();
        addListenerToSet();

    }

    private void addListenerToSet() {
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        table.getItems().add(change.getElementAdded());
                        table.sort();
                    } else {
                        table.getItems().remove(change.getElementRemoved());
                        // TODO: 9/5/23 remove all listeners of the removed element
                    }
                });
    }

    private void setNumberFormatters() {
        numberFormatPosition = NumberFormat.getInstance();
        numberFormatPosition.setMinimumFractionDigits(4);
        numberFormatPosition.setMaximumFractionDigits(4);

        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
    }

    private void addRows() {
        for (ObservableAircraftState aircraftState : aircraftStates) {
            table.getItems().add(aircraftState);
        }
    }

    private void createColumns() {
        //****************************************** ICAO ******************************************
        column_ICAO = new TableColumn<>();
        column_ICAO.setPrefWidth(PREFERRED_WIDTH_ICAO);
        column_ICAO.setText("ICAO");
        column_ICAO.setCellValueFactory(newRow ->
                new ReadOnlyObjectWrapper<>(newRow.getValue().getIcaoAddress().string()));
        table.getColumns().add(column_ICAO);

        //****************************************** CALLSIGN ******************************************

        column_CallSign = new TableColumn<>();
        column_CallSign.setPrefWidth(PREFERRED_WIDTH_CALLSIGN);
        column_CallSign.setText("Callsign");
        column_CallSign.setCellValueFactory(newRow ->
                newRow.getValue().callSignProperty().map(CallSign::string));
        table.getColumns().add(column_CallSign);

        //****************************************** REGISTRATION ******************************************

        column_Registration = new TableColumn<>();
        column_Registration.setPrefWidth(PREFERRED_WIDTH_REGISTRATION);
        column_Registration.setText("Registration");
        column_Registration.setCellValueFactory(newRow ->
                new ReadOnlyObjectWrapper<String>(newRow.getValue()
                        .getAircraftData().registration().string()));
        table.getColumns().add(column_Registration);

        //****************************************** MODEL ******************************************

        column_Model = new TableColumn<>();
        column_Model.setPrefWidth(PREFERRED_WIDTH_MODEL);
        column_Model.setText("Model");
        column_Model.setCellValueFactory(newRow ->
                new ReadOnlyObjectWrapper<>(newRow.getValue().getAircraftData().model()));
        table.getColumns().add(column_Model);

        //****************************************** TYPE ******************************************

        column_Type = new TableColumn<>();
        column_Type.setPrefWidth(PREFERRED_WIDTH_TYPE);
        column_Type.setText("Type");
        column_Type.setCellValueFactory(newRow ->
                new ReadOnlyObjectWrapper<>(newRow.getValue().getAircraftData()
                        .typeDesignator().string()));
        table.getColumns().add(column_Type);

        //****************************************** DESCRIPTION ******************************************

        column_Description = new TableColumn<>();
        column_Description.setPrefWidth(PREFERRED_WIDTH_DESCRIPTION);
        column_Description.setText("Description");
        column_Description.setCellValueFactory(newRow ->
                new ReadOnlyObjectWrapper<>(newRow.getValue().getAircraftData().description().string()));
        table.getColumns().add(column_Description);

        //**********************************************************************************************
        //****************************************** NUMERIC  ******************************************
        //**********************************************************************************************


        //****************************************** LONGITUDE ******************************************

        column_Longitude = new TableColumn<>();
        column_Longitude.setPrefWidth(PREFERRED_WIDTH_NUMERIC);
        column_Longitude.setText("Longitude (º)");
        column_Longitude.getStyleClass().add("numeric");
        column_Longitude.setCellValueFactory(newRow ->
                newRow.getValue().positionProperty().map(pos ->
                        numberFormatPosition.format(
                        Units.convertTo((pos.longitude()),
                        Units.Angle.DEGREE))));
        table.getColumns().add(column_Longitude);

        //****************************************** LATITUDE ******************************************

        column_Latitude = new TableColumn<>();
        column_Latitude.setPrefWidth(PREFERRED_WIDTH_NUMERIC);
        column_Latitude.setText("Latitude (º)");
        column_Latitude.getStyleClass().add("numeric");
        column_Latitude.setCellValueFactory(newRow ->
                newRow.getValue().positionProperty().map(pos ->
                        numberFormatPosition.format(
                        Units.convertTo((pos.latitude()),
                        Units.Angle.DEGREE))));

        table.getColumns().add(column_Latitude);

        //****************************************** ALTITUDE ******************************************

        column_Altitude = new TableColumn<>();
        column_Altitude.setPrefWidth(PREFERRED_WIDTH_NUMERIC);
        column_Altitude.setText("Altitude (m)");
        column_Altitude.getStyleClass().add("numeric");
        column_Altitude.setCellValueFactory(newRow ->
                newRow.getValue().altitudeProperty().map(
                        alt -> numberFormat.format(alt.doubleValue())));
        table.getColumns().add(column_Altitude);


        //****************************************** VELOCITY ******************************************

        column_Velocity = new TableColumn<>();
        column_Velocity.setPrefWidth(PREFERRED_WIDTH_NUMERIC);
        column_Velocity.setText("Velocity (km/h)");
        column_Velocity.getStyleClass().add("numeric");
        column_Velocity.setCellValueFactory(newRow ->
                newRow.getValue().velocityProperty().map(
                        vel -> numberFormat.format(
                                Units.convertTo(vel.doubleValue(),
                                Units.Speed.KILOMETER_PER_HOUR))));
        table.getColumns().add(column_Velocity);

        //****************************************** HEADING ******************************************

        column_Heading = new TableColumn<>();
        column_Heading.setPrefWidth(PREFERRED_WIDTH_NUMERIC);
        column_Heading.setText("Heading (°)");
        column_Heading.getStyleClass().add("numeric");
        column_Heading.setCellValueFactory(newRow ->
                newRow.getValue().trackOrHeadingProperty().map(
                        hea -> numberFormat.format(
                                Units.convertTo(hea.doubleValue(),
                                        Units.Angle.DEGREE))));
        table.getColumns().add(column_Heading);
    }

    public Node pane() {
       return pane;
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer) {

        consumer.accept(table.getSelectionModel().getSelectedItem());
        //BaseMapController.centerOn(selected.get().getPosition());
        System.out.println(selected.get().getIcaoAddress().string());
    }
}