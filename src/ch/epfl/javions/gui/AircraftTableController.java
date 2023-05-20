package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.text.NumberFormat;
import java.util.function.Consumer;

import static java.lang.Double.compare;

public final class AircraftTableController {
    private static final int PREFERRED_WIDTH_ICAO = 60;
    private static final int PREFERRED_WIDTH_DESCRIPTION = 70;
    private static final int PREFERRED_WIDTH_CALLSIGN = PREFERRED_WIDTH_DESCRIPTION;
    private static final int PREFERRED_WIDTH_REGISTRATION = 90;
    private static final int PREFERRED_WIDTH_MODEL = 230;
    private static final int PREFERRED_WIDTH_TYPE = 50;
    private static final int PREFERRED_WIDTH_NUMERIC = 85;
    private Consumer<ObservableAircraftState> consumer;
    private final ObservableSet<ObservableAircraftState> aircraftStates;
    private final TableView<ObservableAircraftState> table;
    private static NumberFormat numberFormatPosition;
    private static NumberFormat numberFormat;
    private final Pane pane;

    /**
     * Constructor for AircraftTableController which is responsible for creating the table showing the
     * information of all the visible aircraft.
     *
     * @param aircraftStates Observable set of all the aircraft states .
     * @param selected       AircraftState of the selected aircraft.
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> selected) {
        this.aircraftStates = aircraftStates;
        table = new TableView<>();
        this.pane = new Pane(table);
        table.getStylesheets().add("table.css");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        table.setTableMenuButtonVisible(true);
        pane.widthProperty().addListener((observable, oldValue, newValue) -> table.setPrefWidth((Double) newValue));
        pane.heightProperty().addListener((observable, oldValue, newValue) -> table.setPrefHeight((Double) newValue));
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                if (table.getSelectionModel().getSelectedItem() != null) {
                    consumer.accept(table.getSelectionModel().getSelectedItem());
                }

            }
        });

        selected.addListener((observable, oldValue, newValue) -> {
            if (newValue != table.getSelectionModel().getSelectedItem()) {
                table.getSelectionModel().select(newValue);
                table.scrollTo(table.getSelectionModel().getSelectedIndex());

            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != selected.get()) {
                selected.set(newValue);

            }
        });

        initializeNumberFormatters();
        createColumns();
        addRows();
        addListenerToSet();

    }

    /**
     * private method that adds a listener to be notified when the set of aircraft states changes.
     */
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

    /**
     * private method that initializes 2 number formatters that will be used
     * in the numeric columns.
     */
    private void initializeNumberFormatters() {
        numberFormatPosition = NumberFormat.getInstance();
        numberFormatPosition.setMinimumFractionDigits(4);
        numberFormatPosition.setMaximumFractionDigits(4);


        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
    }

    /**
     * this private method adds all the aircraft in aircraftStates to the table
     */
    private void addRows() {
        for (ObservableAircraftState aircraftState : aircraftStates) {
            table.getItems().add(aircraftState);
        }
    }

    /**
     * this private method creates all the columns of the table.
     * ICAO, CALLSIGN, REGISTRATION, MODEL, TYPE, DESCRIPTION.
     * LONGITUDE, LATITUDE, ALTITUDE, VELOCITY, HEADING.
     */
    private void createColumns() {
        //****************************************** ICAO ******************************************
        TableColumn<ObservableAircraftState, String> column_ICAO = new TableColumn<>();
        column_ICAO.setPrefWidth(PREFERRED_WIDTH_ICAO);
        column_ICAO.setText("ICAO");
        column_ICAO.setCellValueFactory(newRow ->
                new ReadOnlyObjectWrapper<>(newRow.getValue().getIcaoAddress().string()));
        table.getColumns().add(column_ICAO);

        //****************************************** CALLSIGN ******************************************

        TableColumn<ObservableAircraftState, String> column_CallSign = new TableColumn<>();
        column_CallSign.setPrefWidth(PREFERRED_WIDTH_CALLSIGN);
        column_CallSign.setText("Callsign");
        column_CallSign.setCellValueFactory(newRow ->
                newRow.getValue().callSignProperty().map(CallSign::string));
        table.getColumns().add(column_CallSign);

        //****************************************** REGISTRATION ******************************************

        TableColumn<ObservableAircraftState, String> column_Registration = new TableColumn<>();
        column_Registration.setPrefWidth(PREFERRED_WIDTH_REGISTRATION);
        column_Registration.setText("Registration");
        column_Registration.setCellValueFactory(newRow -> {
            if (newRow.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            } else {
                return new ReadOnlyObjectWrapper<>(newRow.getValue().getAircraftData().registration().string());
            }
        });

        table.getColumns().add(column_Registration);

        //****************************************** MODEL ******************************************

        TableColumn<ObservableAircraftState, String> column_Model = new TableColumn<>();
        column_Model.setPrefWidth(PREFERRED_WIDTH_MODEL);
        column_Model.setText("Model");
        column_Model.setCellValueFactory(newRow -> {
            if (newRow.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            } else {
                return new ReadOnlyObjectWrapper<>(newRow.getValue().getAircraftData().model());
            }
        });
        table.getColumns().add(column_Model);

        //****************************************** TYPE ******************************************

        TableColumn<ObservableAircraftState, String> column_Type = new TableColumn<>();
        column_Type.setPrefWidth(PREFERRED_WIDTH_TYPE);
        column_Type.setText("Type");
        column_Type.setCellValueFactory(newRow -> {
            if (newRow.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            } else {
                return new ReadOnlyObjectWrapper<>(newRow.getValue().getAircraftData().typeDesignator().string());
            }
        });

        table.getColumns().add(column_Type);

        //****************************************** DESCRIPTION ******************************************

        TableColumn<ObservableAircraftState, String> column_Description = new TableColumn<>();
        column_Description.setPrefWidth(PREFERRED_WIDTH_DESCRIPTION);
        column_Description.setText("Description");
        column_Description.setCellValueFactory(newRow -> {
            if (newRow.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            } else {
                return new ReadOnlyObjectWrapper<>(newRow.getValue().getAircraftData().description().string());
            }
        });

        table.getColumns().add(column_Description);

        //**********************************************************************************************
        //****************************************** NUMERIC  ******************************************
        //**********************************************************************************************


        //****************************************** LONGITUDE ******************************************

        TableColumn<ObservableAircraftState, String> column_Longitude = new TableColumn<>();
        column_Longitude.setPrefWidth(PREFERRED_WIDTH_NUMERIC);
        column_Longitude.setText("Longitude (º)");
        column_Longitude.getStyleClass().add("numeric");
        column_Longitude.setCellValueFactory(newRow ->
                newRow.getValue().positionProperty().map(pos ->
                {
                    String s = numberFormatPosition.format(
                            Units.convertTo((pos.longitude()),
                                    Units.Angle.DEGREE));
                    return s.replace(",", ".");
                }));

        column_Longitude.setComparator(AircraftTableController::parserComparator);
        table.getColumns().add(column_Longitude);

        //****************************************** LATITUDE ******************************************

        TableColumn<ObservableAircraftState, String> column_Latitude = new TableColumn<>();
        column_Latitude.setPrefWidth(PREFERRED_WIDTH_NUMERIC);
        column_Latitude.setText("Latitude (º)");
        column_Latitude.getStyleClass().add("numeric");
        column_Latitude.setCellValueFactory(newRow ->
                newRow.getValue().positionProperty().map(pos ->
                {
                    String s = numberFormatPosition.format(
                            Units.convertTo((pos.latitude()),
                                    Units.Angle.DEGREE));
                    return s.replace(",", ".");
                }));

        column_Latitude.setComparator(AircraftTableController::parserComparator);
        table.getColumns().add(column_Latitude);

        //****************************************** ALTITUDE ******************************************

        TableColumn<ObservableAircraftState, String> column_Altitude = new TableColumn<>();
        column_Altitude.setPrefWidth(PREFERRED_WIDTH_NUMERIC);
        column_Altitude.setText("Altitude (m)");
        column_Altitude.getStyleClass().add("numeric");
        column_Altitude.setCellValueFactory(newRow ->
                newRow.getValue().altitudeProperty().map(
                        alt ->
                        {
                            String s = numberFormat.format(alt.doubleValue());
                            return s.replace(".", "");
                        }));
        column_Altitude.setComparator(AircraftTableController::parserComparator);
        table.getColumns().add(column_Altitude);


        //****************************************** VELOCITY ******************************************

        TableColumn<ObservableAircraftState, String> column_Velocity = new TableColumn<>();
        column_Velocity.setPrefWidth(PREFERRED_WIDTH_NUMERIC);
        column_Velocity.setText("Velocity (km/h)");
        column_Velocity.getStyleClass().add("numeric");
        column_Velocity.setCellValueFactory(newRow ->
                newRow.getValue().velocityProperty().map(
                        vel -> numberFormat.format(
                                Units.convertTo(vel.doubleValue(),
                                        Units.Speed.KILOMETER_PER_HOUR))));
        column_Velocity.setComparator(AircraftTableController::parserComparator);
        table.getColumns().add(column_Velocity);

        //****************************************** HEADING ******************************************

        TableColumn<ObservableAircraftState, String> column_Heading = new TableColumn<>();
        column_Heading.setPrefWidth(PREFERRED_WIDTH_NUMERIC);
        column_Heading.setText("Heading (°)");
        column_Heading.getStyleClass().add("numeric");
        column_Heading.setVisible(false);
        column_Heading.setCellValueFactory(newRow ->
                newRow.getValue().trackOrHeadingProperty().map(
                        hea -> numberFormat.format(
                                Units.convertTo(hea.doubleValue(),
                                        Units.Angle.DEGREE))));
        column_Heading.setComparator(AircraftTableController::parserComparator);
        table.getColumns().add(column_Heading);
    }

    /**
     * this private static method parses both entries from strings to doubles
     * and then uses the default compare method between them.
     *
     * @param o1 first string to compare
     * @param o2 second string to compare
     * @return compare both doubles and returns 0 if the entries are numerically equal,
     * positive if o1 is more than o2
     * negative if o1 is less than o2.
     */
    private static int parserComparator(String o1, String o2) {
        double v1 = Double.parseDouble(o1);
        double v2 = Double.parseDouble(o2);
        return compare(v1, v2);
    }

    /**
     * method that returns the pane of the table.
     *
     * @return the pane of the table.
     */
    public Pane pane() {
        return pane;
    }

    /**
     * public sets a consumer which is responsible for centering the basemap on a row that has
     * double-clicked.
     *
     * @param consumer lambda consumer which centers the basemap on the plane that is double-clicked.
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer) {
        this.consumer = consumer;
    }
}
