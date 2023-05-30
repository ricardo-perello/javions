/**
 * The main class of the Javions application.
 * It extends the JavaFX Application class and provides the entry point for launching the application.
 * Javions is a program that visualizes aircraft data on a map.
 * The application initializes the user interface components, such as the map, aircraft database, and controllers,
 * and starts an animation timer to handle incoming messages and update the state of the aircraft.
 *
 * @author Ricardo Perelló Mas
 * @author Alejandro Meredith Romero
 * @version 1.0
 * @since 2023-05-20
 */
package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * The main class of the Javions application.
 *
 * @author Ricardo Perello Mas ()
 * @author Alejandro Meredith Romero (360864)
 */
public final class Main extends Application {
    private static final int INITIAL_ZOOM = 8;
    private static final int INITIAL_MIN_X = 33_530;
    private static final int INITIAL_MIN_Y = 23_070;
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final double NANO_TO_MILLI = 1e-6;
    private static final double ONE_SECOND_IN_NANO = 1e9;
    private static final String TILE_REPOSITORY = "tile-cache";
    private static final String SERVER_NAME = "tile.openstreetmap.org";
    private static final String DATABASE_ZIP = "/aircraft.zip";
    private static final String APPLICATION_NAME = "Javions";
    private static double lastPurgeTimeStamp = ONE_SECOND_IN_NANO;
    private ConcurrentLinkedQueue<RawMessage> rawMessageQueue;

    /**
     * The entry point of the Javions application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the Javions application.
     *
     * @param primaryStage the primary stage for the application
     * @throws Exception if an error occurs during application startup
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        long startTime = System.nanoTime();
        Path tileCache = Path.of(TILE_REPOSITORY);
        TileManager tm =
                new TileManager(tileCache, SERVER_NAME);
        MapParameters mp =
                new MapParameters(INITIAL_ZOOM, INITIAL_MIN_X, INITIAL_MIN_Y);
        BaseMapController bmc = new BaseMapController(tm, mp);

        // Create database
        URL u = getClass().getResource(DATABASE_ZIP);
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());

        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        StatusLineController slc = new StatusLineController();


        StackPane stackPane = new StackPane(bmc.pane(), ac.pane());
        BorderPane borderPane = new BorderPane(atc.pane(), slc.pane(), null, null, null);
        SplitPane root = new SplitPane(stackPane, borderPane);
        root.setOrientation(Orientation.VERTICAL);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(APPLICATION_NAME);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.show();

        rawMessageQueue = new ConcurrentLinkedQueue<>();
        atc.setOnDoubleClick(selected -> bmc.centerOn(selected.getPosition()));

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    if (rawMessageQueue.peek() != null) {
                        Message m = MessageParser.parse(rawMessageQueue.poll());
                        if (m != null) {
                            asm.updateWithMessage(m);
                            slc.setAircraftCount(asm.states().size());
                            slc.setMessageCount(slc.getMessageCount() + 1);
                        }
                    }
                    long nanoTime = System.nanoTime();
                    if (nanoTime - lastPurgeTimeStamp >= ONE_SECOND_IN_NANO) {
                        lastPurgeTimeStamp = nanoTime;
                        asm.purge();
                    }

                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }

            }
        }.start();

        Thread threadMessage = new Thread(() -> {
            try {
                //read as a file
                if (!getParameters().getRaw().isEmpty()) {
                    for (RawMessage message : readAllMessages(getParameters().getRaw().get(0))) {
                        if (message.timeStampNs() > System.nanoTime() - startTime) {
                            Thread.sleep((long) ((message.timeStampNs() - (System.nanoTime() - startTime))
                                    * NANO_TO_MILLI));
                        }
                        rawMessageQueue.add(message);
                    }
                } else {//read with System.in
                    AdsbDemodulator mi = new AdsbDemodulator(System.in);
                    RawMessage message = mi.nextMessage();
                    while (message != null) {
                        rawMessageQueue.add(message);
                        message = mi.nextMessage();
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        threadMessage.setDaemon(true);
        threadMessage.start();
    }

    /**
     * Reads all raw messages from the specified file and returns them as a list of RawMessage objects.
     *
     * @param fileName the name of the file to read the raw messages from
     * @return a list of RawMessage objects read from the file
     * @throws IOException if an I/O error occurs while reading the file
     */
    static List<RawMessage> readAllMessages(String fileName) throws IOException {
        ArrayList<RawMessage> rm = new ArrayList<>();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)))) {
            byte[] bytes = new byte[RawMessage.LENGTH];

            while (s.available() > 0) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);

                RawMessage rawM = RawMessage.of(timeStampNs, bytes);
                if (bytesRead == RawMessage.LENGTH && rawM != null) rm.add(rawM);

            }
        } catch (EOFException e) { /* nothing to do */ } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return rm;
    }
}



