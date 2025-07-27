package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class CashManager {
    private static final String FILE_PATH = "cash.txt";
    private static int cash = 80000; // default starting cash
    public static boolean enableSaving = true;

    public static void loadCash() {
        FileHandle file = Gdx.files.local(FILE_PATH);
        if (file.exists()) {
            try {
                cash = Integer.parseInt(file.readString().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                cash = 50000; // fallback
            }
        } else {
            saveCash(); // create file if not exists
        }
    }

    public static void saveCash() {
        FileHandle file = Gdx.files.local(FILE_PATH);
        file.writeString(String.valueOf(cash), false);
    }

    public static int getCash() {
        return cash;
    }

    public static void addCash(int amount) {
        cash += amount;
        saveCash();
    }

    public static boolean subtractCash(int amount) {
        if (cash >= amount) {
            cash -= amount;
            saveCash();
            return true;
        }
        return false;
    }

    public static void setCash(int newCash) {
        cash = newCash;
    }
}
