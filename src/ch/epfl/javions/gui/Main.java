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
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Main extends Application {
    private static final int INITIAL_ZOOM = 8;
    private static final int INITIAL_MIN_X = 33_530;
    private static final int INITIAL_MIN_Y = 23_070;
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final double NANO_TO_MILLI = 1e-6;
    private ConcurrentLinkedQueue<RawMessage> rawMessageQueue;
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        long startTime = System.nanoTime();
        Path tileCache = Path.of("tile-cache");
        TileManager tm =
                new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(INITIAL_ZOOM, INITIAL_MIN_X, INITIAL_MIN_Y);
        BaseMapController bmc = new BaseMapController(tm, mp);

        // Create database
        URL u = getClass().getResource("/aircraft.zip");
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());

        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        StatusLineController slc = new StatusLineController();



        var stackPane = new StackPane(bmc.pane(), ac.pane());
        var borderPane = new BorderPane(atc.pane(), slc.pane(), null, null, null);
        var root = new SplitPane(stackPane, borderPane);
        root.setOrientation(Orientation.VERTICAL);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Javions");
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.show();

        rawMessageQueue = new ConcurrentLinkedQueue<>();
        atc.setOnDoubleClick(selected ->bmc.centerOn(selected.getPosition()));

        new  AnimationTimer() {
            @Override
            public  void  handle (long now) {
                try {
                        if (rawMessageQueue.peek() != null){
                            Message m  = MessageParser.parse(rawMessageQueue.poll());
                            if (m != null ) {
                                asm.updateWithMessage(m);
                                slc.setAircraftCount(asm.states().size());
                                slc.setMessageCount(slc.getMessageCount() + 1);

                            }
                        }
                        if (System.nanoTime() % 1e6 == 1000) asm.purge();

                } catch (IOException e) {
                    throw  new UncheckedIOException(e);
                }

            }
        }.start();
                //read as a file
        Thread threadMessage = new Thread(() -> {
            try {
                if (!getParameters().getRaw().isEmpty()) {
                    for (RawMessage message : readAllMessages(getParameters().getRaw().get(0))) {
                        if (message.timeStampNs() > System.nanoTime() - startTime) {
                            Thread.sleep((long) ((message.timeStampNs() - (System.nanoTime() - startTime)) * NANO_TO_MILLI));
                        }
                        rawMessageQueue.add(message);
                    }
                }else{
                    AdsbDemodulator mi = new AdsbDemodulator(System.in);
                    RawMessage message = mi.nextMessage();
                    while(message != null){
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

    static List<RawMessage> readAllMessages (String fileName) throws IOException {
        ArrayList<RawMessage> rm = new ArrayList<>();
        //todo quitar este startTime
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)))) {
            byte[] bytes = new byte[RawMessage.LENGTH];

            while (s.available() > 0) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);

                rm.add(new RawMessage(timeStampNs, message));
            }
        }catch (EOFException e) { /* nothing to do */ } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return rm;
    }
}



