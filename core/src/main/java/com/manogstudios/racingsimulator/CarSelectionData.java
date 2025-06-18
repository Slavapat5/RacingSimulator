package com.manogstudios.racingsimulator;

public class CarSelectionData {
    private static String selectedCarTexture = "Ferrari F40 - 1987.png";

    public static void setSelectedCarTexture(String texturePath){
        selectedCarTexture = texturePath;
    }

    public static String getSelectedCarTexture(){
        return selectedCarTexture;
    }
}
