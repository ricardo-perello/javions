package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static java.util.Objects.requireNonNull;

/**
 * ColorRamp represents a color ramp used for mapping altitudes to colors.
 * It provides a collection of colors and a method to retrieve a color based on a given altitude.
 *
 * @author Ricardo Perello Mas ()
 * @author Alejandro Meredith Romero (360864)
 */

public final class ColorRamp {
    private static final double MAX_ALTITUDE = 12000.0;
    private static final double COLOR_EXPONENT = 1.0 / 3.0;
    private final ArrayList<Color> colors = new ArrayList<>();

    public static final ColorRamp PLASMA = new ColorRamp(
            Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
            Color.valueOf("0x320597ff"), Color.valueOf("0x40049dff"),
            Color.valueOf("0x4e02a2ff"), Color.valueOf("0x5b01a5ff"),
            Color.valueOf("0x6800a8ff"), Color.valueOf("0x7501a8ff"),
            Color.valueOf("0x8104a7ff"), Color.valueOf("0x8d0ba5ff"),
            Color.valueOf("0x9814a0ff"), Color.valueOf("0xa31d9aff"),
            Color.valueOf("0xad2693ff"), Color.valueOf("0xb6308bff"),
            Color.valueOf("0xbf3984ff"), Color.valueOf("0xc7427cff"),
            Color.valueOf("0xcf4c74ff"), Color.valueOf("0xd6556dff"),
            Color.valueOf("0xdd5e66ff"), Color.valueOf("0xe3685fff"),
            Color.valueOf("0xe97258ff"), Color.valueOf("0xee7c51ff"),
            Color.valueOf("0xf3874aff"), Color.valueOf("0xf79243ff"),
            Color.valueOf("0xfa9d3bff"), Color.valueOf("0xfca935ff"),
            Color.valueOf("0xfdb52eff"), Color.valueOf("0xfdc229ff"),
            Color.valueOf("0xfccf25ff"), Color.valueOf("0xf9dd24ff"),
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff"));

    public ColorRamp(Color... colors1) {
        Preconditions.checkArgument(colors1.length > 1);
        for (Color color : colors1) {
            requireNonNull(color);
            this.colors.add(color);
        }
    }

    public Color at(double altitude) {
        double colorCode = Math.pow((altitude / MAX_ALTITUDE), COLOR_EXPONENT);
        if (colorCode <= 0) {
            return colors.get(0);
        } else if (colorCode >= 1) {
            return colors.get(colors.size() - 1);
        } else {
            double space = 1.0 / (double) (colors.size() - 1);
            int indexFirstColor = (int) Math.floor(colorCode / space);
            int indexSecondColor = indexFirstColor + 1;
            double differencePercentage = (colorCode - space * indexFirstColor) / space;
            return colors.get(indexFirstColor).interpolate(colors.get(indexSecondColor), differencePercentage);
        }
    }
}
