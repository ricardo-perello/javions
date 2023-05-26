package ch.epfl.javions;

/**
 * verifies condition
 *todo poner sciper
 * @author Ricardo Perello Mas ()
 * @author  Alejandro Meredith Romero (360864)
 */

public final class Preconditions {

    private Preconditions() {
    }

    /**
     * method that allows to check is something is true so that an exception can be thrown
     *
     * @param shouldBeTrue boolean, condition determining if the situation is a case that can work
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
