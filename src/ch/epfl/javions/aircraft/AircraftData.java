package ch.epfl.javions.aircraft;

import static java.util.Objects.requireNonNull;

public record AircraftData(AircraftRegistration registration,
                           AircraftTypeDesignator typeDesignator,
                           String model,
                           AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {

    /**
     * Aircraft data record that models all the static data of the airplane
     *
     * @param registration            AircraftRegistration, registration of the plane
     * @param typeDesignator          AircraftTypeDesignator, type designator of the plane
     * @param model                   string, model of the plane
     * @param description,            AircraftDescription, description of plane
     * @param wakeTurbulenceCategory, WakeTurbulenceCategory, wake turbulence category of the plane
     */
    public AircraftData {

        requireNonNull(registration);
        requireNonNull(typeDesignator);
        requireNonNull(model);
        requireNonNull(description);
        requireNonNull(wakeTurbulenceCategory);
    }

}
