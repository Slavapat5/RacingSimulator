package com.manogstudios.racingsimulator;

public class SelectedCar {
    private static String selectedCarImage = "Audi A4 - 2020.png"; // Default car if none selected

    public static void set(String carImage) {
        selectedCarImage = carImage;
    }

    public static String get() {
        return selectedCarImage;
    }
}
