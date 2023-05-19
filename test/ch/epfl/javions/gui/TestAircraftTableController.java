package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javions.gui.AircraftStateManagerTest.getDatabase;

public  final  class TestAircraftTableController extends Application {
    public  static  void  main (String[] args) { launch(args); }



    @Override
    public  void  start (Stage primaryStage)  throws Exception {
        Path tileCache  = Path.of( "tile-cache" );
        TileManager  tm  =
                new  TileManager (tileCache, "tile.openstreetmap.org" );
        MapParameters  mp  =
                new  MapParameters ( 17 , 17_389_327 , 11_867_430 );
        //BorderPane root  =  new BorderPane (bmc.pane());
        //primaryStage.setScene( new Scene(root));

        // … to complete (see TestBaseMapController)
        //BaseMapController  bmc  = …;

        // Create database
        URL dbUrl  = getClass().getResource( "/aircraft.zip" );
        assert dbUrl != null ;
        String  f;
        f = Path.of(dbUrl.toURI()).toString();
        var  db  =  new AircraftDatabase(f);

        AircraftStateManager  asm  =  new  AircraftStateManager (db);
        ObjectProperty<ObservableAircraftState> sap= new SimpleObjectProperty<>();
        AircraftTableController  atc  = new  AircraftTableController (asm.states(), sap);
        var  root  =  new Pane(atc.pane());
        primaryStage.setScene( new Scene(root));
        primaryStage.show();

        var  mi  = readAllMessages( "messages_20230318_0915.bin" ).iterator();
        primaryStage.show();

        // Aircraft animation
        new  AnimationTimer() {
            @Override
            public  void  handle (long now) {
                try {
                    for ( int  i  =  0 ; i < 10 ; i += 1 ) {
                        Message m  = MessageParser.parse(mi.next( ));
                        if (m != null ) asm.updateWithMessage(m);
                        if (i == 9) asm.purge();
                    }
                } catch (IOException e) {
                    throw  new UncheckedIOException(e);
                }
            }
        }.start();
    }
    static List<RawMessage> readAllMessages (String fileName) throws IOException { /* … to do */
        AircraftStateManager manager = new AircraftStateManager(getDatabase());
        long startTime = System.nanoTime();
        ArrayList<RawMessage> rm = new ArrayList<>();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/"+fileName)))) {
            byte[] bytes = new byte[RawMessage.LENGTH];

            int length = s.available();

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