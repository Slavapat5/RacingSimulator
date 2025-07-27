package com.manogstudios.racingsimulator;

public class CarSelectionData {
    private static String selectedCarTexture = "Audi A4 - 2020.png";

    public static void setSelectedCarTexture(String texturePath){
        selectedCarTexture = texturePath;
    }

    public static String getSelectedCarTexture(){
        return selectedCarTexture;
    }
}
