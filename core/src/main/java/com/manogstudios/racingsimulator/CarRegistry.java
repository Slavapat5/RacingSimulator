package com.manogstudios.racingsimulator;

import java.util.HashMap;
import java.util.Map;

public class CarRegistry {
    private static final Map<String, CarStats> statsMap = new HashMap<>();

    static {
        statsMap.put("Ferrari F40 - 1987.png", new CarStats(1200f, 400f, 3f));
        statsMap.put("Audi A4 - 2020.png", new CarStats(700f, 250f, 2f));
        statsMap.put("Lamborghini Aventador - 2020.png", new CarStats(1000f, 380f, 2f));
        // Add more cars here
    }

    public static CarStats getStats(String carImageName) {
        return statsMap.getOrDefault(carImageName, new CarStats(1000f, 200f, 2f)); // fallback stats
    }
}
