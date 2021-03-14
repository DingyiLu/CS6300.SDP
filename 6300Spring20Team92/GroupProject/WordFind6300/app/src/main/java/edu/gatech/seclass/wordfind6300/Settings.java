package edu.gatech.seclass.wordfind6300;
import android.util.Log;

import java.util.Arrays;

public class Settings {
    private int dimensions;
    private int minutes;
    private int[] weights;
    private boolean useDictionary;
    private static Settings instance = null;

    private Settings(int dimensions, int minutes, int[] weights, boolean useDictionary) {
        this.dimensions = dimensions;
        this.minutes = minutes;
        this.weights = weights;
        this.useDictionary = useDictionary;
    }

    public static Settings getSettings() {
        if (instance == null) {
            int[] weights = new int[26];
            Arrays.fill(weights, 1);
            instance = new Settings(4, 3, weights, false);
        }
        return instance;
    }

    public int getDimensions() {return dimensions;}
    public int getMinutes() {return minutes;}
    public int[] getWeights() {return weights;}
    public boolean getUseDictionary() {return useDictionary;}

    public boolean setDimensions(int dimensions) {
        if (dimensions < 4 || dimensions > 8) {
            return false;
        }
        this.dimensions = dimensions;
        return true;
    }
    public boolean setMinutes(int minutes) {
        if (minutes < 1 || minutes > 5) {
            return false;
        }
        this.minutes = minutes;
        return true;
    }
    public boolean setWeightByPosition(int position, int value) {
        if (value < 1 || value > 5) {
            return false;
        }
        this.weights[position] = value;
        return true;
    }
    public boolean setUseDictionary(boolean useDictionary) {
        this.useDictionary = useDictionary;
        return true;
    }
}
