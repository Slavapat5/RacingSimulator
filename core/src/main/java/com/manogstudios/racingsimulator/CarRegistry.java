package com.manogstudios.racingsimulator;

import java.util.HashMap;
import java.util.Map;

public class CarRegistry {
    private static final Map<String, CarStats> statsMap = new HashMap<>();

    static {
        // Speed = MPH*10, Acceleration = HP/2
        statsMap.put("Ferrari F40 - 1987.png", new CarStats(2010f, 239f, 3f));
        statsMap.put("Audi A4 - 2020.png", new CarStats(1550f, 94f, 2f));
        statsMap.put("Lamborghini Aventador - 2020.png", new CarStats(2170f, 365f, 2f));
        statsMap.put("Lamborghini Huracan - 2010.png", new CarStats(2020f, 305f, 2f));
        statsMap.put("BMW M3 G80 - 2020.png", new CarStats(1800f, 240f, 2.5f));
        statsMap.put("Ferrari F8 Tributo - 2020.png", new CarStats(2110f, 355f, 3f));
        statsMap.put("Mercedes-Benz G-Class C63 AMG - 2020.png", new CarStats(1490f, 288f, 1.8f));
        statsMap.put("Mercedes-Benz S600 - 2020.png", new CarStats(1550f, 255f, 2.5f));
        statsMap.put("Mercedes-Benz C300 - 2010.png", new CarStats(1550f, 114f, 2.5f));
        statsMap.put("Mitsubishi Lancer Evo IX - 2007.png", new CarStats(1550f, 143f, 3f));
        statsMap.put("Ford GT - 2005.png", new CarStats(2050f, 275f, 3f));
        statsMap.put("Bugatti EB110 - 1995.png", new CarStats(2130f, 276f, 3f));
    }

    public static CarStats getStats(String carImageName) {
        return statsMap.getOrDefault(carImageName, new CarStats(1000f, 200f, 2f)); // fallback stats
    }
}
