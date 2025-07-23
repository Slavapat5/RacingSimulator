package com.manogstudios.racingsimulator;

public class SelectedCar {
    private static String selectedCarImage = "Ferrari F40 - 1987.png"; // Default car if none selected

    public static void set(String carImage) {
        selectedCarImage = carImage;
    }

    public static String get() {
        return selectedCarImage;
    }
}
