package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.manogstudios.racingsimulator.network.SupabaseAuth;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public String selectedCarName;

    @Override
    public void create() {
        CashManager.loadCash();

        if (SupabaseAuth.isLoggedIn) {
            this.setScreen(new MenuScreen(this));  // or PlayScreen
        } else {
            this.setScreen(new LoginScreen(this));
        }
    }
}
