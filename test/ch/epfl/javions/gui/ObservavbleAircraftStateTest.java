package ch.epfl.javions.gui;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.AircraftStateManager;
import ch.epfl.javions.gui.ObservableAircraftState;
import javafx.collections.ObservableSet;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URLDecoder;
import java.util.Comparator;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.jupiter.api.Assertions.*;
class AircraftStateManagerTest {
    private static AircraftDatabase getDatabase() {
        // Try to get the database from the resources
        var aircraftResourceUrl = AircraftStateManagerTest.class.getResource("/aircraft.zip");
        if (aircraftResourceUrl != null)
            return new AircraftDatabase(URLDecoder.decode(aircraftResourceUrl.getFile(), UTF_8));

        // Try to get the database from the JAVIONS_AIRCRAFT_DATABASE environment variable
        // (only meant to simplify testing of several projects with a single database)
        var aircraftFileName = System.getenv("JAVIONS_AIRCRAFT_DATABASE");
        if (aircraftFileName != null)
            return new AircraftDatabase(aircraftFileName);

        throw new Error("Could not find aircraft database");
    }

    @Test
    public void TestIDUnderstand(){
        AircraftStateManager manager = new AircraftStateManager(getDatabase());
        long startTime = System.nanoTime();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/messages_20230318_0915.bin")))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            System.out.println("OACI    Indicatif      Immat.  Modèle                        Longitude   Latitude   Alt.  Vit.\n" +
                    "――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
            int cont = 0;
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);

                Message message1 = MessageParser.parse(new RawMessage(timeStampNs,message));

                if(message1 !=null){
                    /*if(message1.timeStampNs() < (System.nanoTime() - startTime)){
                        Thread.sleep((long) (((System.nanoTime() - startTime) - message1.timeStampNs())/(9E6)));
                    }*/
                    manager.updateWithMessage(message1);
                    cont++;
                    manager.purge();
                }

                ObservableSet<ObservableAircraftState> states = manager.states();
                String CSI = "\u001B[";
                String CLEAR_SCREEN = CSI + "2J";
                Thread.sleep(1);

                System.out.print(CLEAR_SCREEN);
                System.out.print(CSI + ";H");
                //System.out.println("OACI    Indicatif      Immat.  Modèle                        Longitude   Latitude   Alt.  Vit.");

                for (ObservableAircraftState state: states) {
                    System.out.printf("%5s %10s %10s %32s  %f6 %6f %5f %5f %1c \n",
                            state.getIcaoAddress().string(),
                            Objects.isNull(state.callSignProperty().get())?" ":state.callSignProperty().get().string(),
                            state.getAircraftData().registration().string(),
                            state.getAircraftData().model(),
                            Units.convertTo(state.positionProperty().get().longitude(), Units.Angle.DEGREE),
                            Units.convertTo(state.positionProperty().get().latitude(),Units.Angle.DEGREE),
                            state.altitudeProperty().get(),
                            Units.convertTo(state.velocityProperty().get(),Units.Speed.KILOMETER_PER_HOUR),
                            findArrow(Units.convertTo(state.trackOrHeadingProperty().get(),Units.Angle.DEGREE)));
                }
            }
        } catch (EOFException e) { /* nothing to do */ } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static class AddressComparator
            implements Comparator<ObservableAircraftState> {
        @Override
        public int compare(ObservableAircraftState o1,
                           ObservableAircraftState o2) {
            String s1 = o1.getIcaoAddress().string();
            String s2 = o2.getIcaoAddress().string();
            return s1.compareTo(s2);
        }
    }
    private static char findArrow(double trackOrHeading){
        if( (0 <= trackOrHeading && trackOrHeading <= 22.5) || (337.5 <= trackOrHeading && trackOrHeading <= 360)){
            return '↑';
        }
        if( 22.5 < trackOrHeading && trackOrHeading <= 67.5){
            return '↗';
        }
        if( 67.5 < trackOrHeading && trackOrHeading <= 112.5){
            return '→';
        }
        if( 112.5 < trackOrHeading && trackOrHeading <= 157.5){
            return '↘';
        }
        if( 157.5 < trackOrHeading && trackOrHeading <= 202.5){
            return '↓';
        }
        if( 202.5 < trackOrHeading && trackOrHeading <= 247.5){
            return '↙';
        }
        if( 247.5 < trackOrHeading && trackOrHeading <= 292.5){
            return '←';
        }
        if( 292.5 < trackOrHeading && trackOrHeading <= 337.5){
            return '↖';
        }

        return 0;
    }
    @Test
    public void TeacherTest() throws IOException{
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(new FileInputStream("resources/messages_20230318_0915" +
                        ".bin")))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                System.out.printf("%13d: %s\n", timeStampNs, message);
            }
        } catch (EOFException e) { /* nothing to do */ }
    }

}