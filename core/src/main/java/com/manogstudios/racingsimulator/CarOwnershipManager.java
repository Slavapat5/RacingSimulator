package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CarOwnershipManager {
    private static final String FILE_PATH = "owned_cars.txt";
    private static Set<String> ownedCars = new HashSet<>();

    public static void loadOwnedCars() {
        FileHandle file = Gdx.files.local(FILE_PATH);
        if (file.exists()) {
            String[] lines = file.readString().split("\n");
            ownedCars.addAll(Arrays.asList(lines));
        } else {
            // Add default car if file doesn't exist
            ownedCars.add("Audi A4 - 2020.png");
            saveOwnedCars();
        }
    }

    public static void saveOwnedCars() {
        FileHandle file = Gdx.files.local(FILE_PATH);
        file.writeString(String.join("\n", ownedCars), false);
    }

    public static boolean ownsCar(String carImagePath) {
        return ownedCars.contains(carImagePath);
    }

    public static void addCar(String imagePath) {
        if (!ownedCars.contains(imagePath)) {
            ownedCars.add(imagePath);
            saveOwnedCars();
        }
    }


    public static void removeCar(String imagePath) {
        if (ownedCars.contains(imagePath)) {
            ownedCars.remove(imagePath);
            saveOwnedCars();
        }
    }

    public static void clearOwnedCars() {
        ownedCars.clear();
    }

    public static Set<String> getOwnedCars() {
        return ownedCars;
    }
}
