package ch.epfl.javions.aircraft;

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

    /**
     * verifies that object non-null, if it is, a NullPointerException is thrown
     * @param obj Object that we verify
     */
    public void requireNonNull(Object obj){
        if(obj == null){
            throw new NullPointerException();
        }
    }

}
