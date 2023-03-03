package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

public final class AircraftDatabase {
    String fileName;

    public AircraftDatabase(String fileName) {
        requireNonNull(fileName);
        this.fileName = fileName;
    }

    /**
     *
     * @param address input ICAO address of the plane you want to know about
     * @return AircraftDatabase with the information about the plane with the input ICAO address.
     * @throws IOException when there is an error while reading
     */
    public AircraftData get(IcaoAddress address) throws IOException {
        AircraftRegistration registration;
        AircraftTypeDesignator typeDesignator;
        String model;
        AircraftDescription description;
        WakeTurbulenceCategory wakeTurbulenceCategory;
        try (ZipFile zip = new ZipFile("resources"+fileName);
             InputStream stream = zip.getInputStream(zip.getEntry(address.string().substring(4,6)+".csv"));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader buffer = new BufferedReader(reader)) {
            String line;
            String string = null;
            while (((line = buffer.readLine()) != null)) {
                string = line;
                if ((line.compareTo(address.string()) > 0)) {
                    break;
                }
            }
            assert line != null;
            if (string.startsWith(address.string())) {
                String[] data = line.split(",");
                registration = new AircraftRegistration(data[1]);
                typeDesignator = new AircraftTypeDesignator(data[2]);
                model = data[3];
                description = new AircraftDescription(data[4]);
                wakeTurbulenceCategory = WakeTurbulenceCategory.of(data[5]);
                return new AircraftData(registration, typeDesignator, model, description, wakeTurbulenceCategory);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

