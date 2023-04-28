package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static java.util.Objects.requireNonNull;

public final class ColorRamp {
    private ArrayList<Color> colors = new ArrayList<>();

    public ColorRamp(ArrayList<Color> colors){
        Preconditions.checkArgument(colors.size() > 1);
        this.colors = colors;
    }
    public ColorRamp(Color ... colors1){
        Preconditions.checkArgument(colors1.length > 1);
        for (Color color : colors1) {
            requireNonNull(color);
            this.colors.add(color);
        }
    }

    public Color at(double altitude){
        double colorCode = Math.pow((altitude / 12000), 1/3);
        if(colorCode <= 0){
            return colors.get(0);
        } else if (colorCode >= 1) {
            return colors.get(1);
        }else{
            double space  = 1.0 / (double) (colors.size() - 1);
            int indexFirstColor = (int) Math.floor(colorCode / space);
            int indexSecondColor = indexFirstColor + 1;
            double differencePercentage = (colorCode - space * indexFirstColor) / space;
            return colors.get(indexFirstColor).interpolate(colors.get(indexSecondColor), differencePercentage);
        }
    }
}
