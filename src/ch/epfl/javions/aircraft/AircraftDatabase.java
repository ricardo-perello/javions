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
//todo add documentation
    public AircraftData get(IcaoAddress address) throws IOException {
        //String zipName = requireNonNull(getClass().getResource("/resources/aircraft.zip")).getFile();//todo ask about this
        boolean found = false;
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
            while (((line = buffer.readLine()) != null)) {
                if (line.startsWith(address.string())) {
                    break;
                }
            }
            assert line != null;
            String[] data = line.split(",");
            registration = new AircraftRegistration(data[1]);
            typeDesignator = new AircraftTypeDesignator(data[2]);
            model = data[3];
            description = new AircraftDescription(data[4]);
            wakeTurbulenceCategory = WakeTurbulenceCategory.of(data[5]);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new AircraftData(registration,typeDesignator,model,description,wakeTurbulenceCategory);
    }
}

