package ch.epfl.javions.aircraft;

import static java.util.Objects.requireNonNull;

public record AircraftData (AircraftRegistration registration,
                            AircraftTypeDesignator typeDesignator,
                            String model,
                            AircraftDescription description,
                            WakeTurbulenceCategory wakeTurbulenceCategory) {


    public AircraftData{

        requireNonNull(registration);
        requireNonNull(typeDesignator);
        requireNonNull(model);
        requireNonNull(description);
        requireNonNull(wakeTurbulenceCategory);
    }

}
