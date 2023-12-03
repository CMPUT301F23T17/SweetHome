package com.example.sweethome;

/* necessary imports */
import android.graphics.Color;
import java.util.Random;

/**
 * @class Color Generator
 *
 * <p>This class is responsible for generating random color codes and determining
 * suitable text color codes based on the background color. This class uses the Color class
 * methods to obtain the generated color code that contrasts well with the background color.</p>
 *
 * @date <p>November 10, 2023</p>
 */
public class ColorGenerator {
    /* attributes of this class */
    private int colorCode;

    /**
     * The random color code is generated during the instantiation of the Color object using RGB
     * values and is formatted as a hexadecimal string. It is then parsed into an integer using
     * the Color.parseColor method.
     */
    public ColorGenerator() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        String colorCode = String.format("#%02X%02X%02X", red, green, blue);
        this.colorCode = Color.parseColor(colorCode);
    }

    /**
     * The getColorCode method enables access to the colorCode attribute of the ColorGenerator class.
     * @return colorCode
     */
    public int getColorCode() {
        return this.colorCode;
    }

    /**
     * The getTextColorCode method calculates the darkness of the background color using a weighted
     * sum of its RGB components. A suitable text color code (either BLACK or WHITE) is returned
     * based on the darkness value to ensure good contrast with the background color.
     * @return color code for text (int)
     */
    public int getTextColorCode() {
        int backgroundColorInt = this.colorCode;
        double darkness = 1 - (0.299 * Color.red(backgroundColorInt) + 0.587 * Color.green(backgroundColorInt) + 0.114 * Color.blue(backgroundColorInt)) / 255;
        if (darkness < 0.5) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    public void setColorCode(int backgroundColor) {
        this.colorCode = backgroundColor;
    }
}
