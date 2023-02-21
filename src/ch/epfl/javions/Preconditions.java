package ch.epfl.javions;

public class Preconditions {

    private Preconditions() {}

    public static void checkArgument (boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
