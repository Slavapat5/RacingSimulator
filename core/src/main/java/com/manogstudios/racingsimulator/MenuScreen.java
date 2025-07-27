package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.manogstudios.racingsimulator.network.SupabaseAuth;
import com.manogstudios.racingsimulator.network.SupabaseDatabase;

public class MenuScreen implements Screen {
    private final Game game;
    private Stage stage;
    private Skin skin;
    private Label cashLabel;
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private Texture dealershipTexture, driveTexture, driveTexture2, driveTexture3, garageTexture;





    public MenuScreen(Game game) {
        this.game = game;
    }

    public Label getCashLabel() {
        return cashLabel;
    }


    @Override
    public void show() {

        if (!SupabaseAuth.isLoggedIn) {
            game.setScreen(new LoginScreen(game));
        }

        backgroundTexture = new Texture(Gdx.files.internal("menuscreen1.png"));
        batch = new SpriteBatch();

//        String token = SupabaseAuth.accessToken; // Already stored after login
//        SupabaseDatabase.fetchProfile(token, profile -> {
//            if (profile != null) {
//                int cash = profile.getInt("cash");
//                Gdx.app.postRunnable(() -> {
//                    System.out.println("Cash: $" + cash);
//                    // Update UI or use in game logic
//                });
//            }
//        });






        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        // Built-in skin (ugly but it works)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        dealershipTexture = new Texture("buttonbg.png");
        driveTexture = new Texture("buttonbg1.png");
        driveTexture2 = new Texture("buttonbg2.png");
        driveTexture3 = new Texture("buttonbg3.png");

        garageTexture = new Texture("buttonbg.png");

        ImageTextButton.ImageTextButtonStyle dealershipStyle = new ImageTextButton.ImageTextButtonStyle();
        dealershipStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture("buttonbg5.png")));
        dealershipStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture("buttonbg6.png")));
        dealershipStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture("buttonbg7.png")));
        dealershipStyle.font = skin.getFont("default-font");

        ImageTextButton.ImageTextButtonStyle driveStyle = new ImageTextButton.ImageTextButtonStyle();
        driveStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture("buttonbg1.png")));
        driveStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture("buttonbg2.png")));
        driveStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture("buttonbg4.png")));
        driveStyle.font = skin.getFont("default-font");

        ImageTextButton.ImageTextButtonStyle garageStyle = new ImageTextButton.ImageTextButtonStyle();
        garageStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture("buttonbg8.png")));
        garageStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture("buttonbg9.png")));
        garageStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture("buttonbg10.png")));
        garageStyle.font = skin.getFont("default-font");

        TextButton button1 = new TextButton("DEALERSHIP", dealershipStyle);
        TextButton button2 = new TextButton("DRIVE", driveStyle);
        TextButton button3 = new TextButton("GARAGE", garageStyle);


        button1.getLabel().setFontScale(2);
        button2.getLabel().setFontScale(2);
        button3.getLabel().setFontScale(2);

        float buttonWidth = 300;
        float buttonHeight = 300;

        button1.setSize(buttonWidth, buttonHeight);
        button2.setSize(buttonWidth, buttonHeight);
        button3.setSize(buttonWidth, buttonHeight);

        table.add(button1).width(buttonWidth).height(buttonHeight).pad(20);
        table.add(button2).width(buttonWidth).height(buttonHeight).pad(20);
        table.add(button3).width(buttonWidth).height(buttonHeight).pad(20);





        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                game.setScreen(new GarageScreen(game));
            }
        });

        // Add click listener for middle button
        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                game.setScreen(new GameScreen(game));

                if (game instanceof Main) {
                    String selectedCar = ((Main) game).selectedCarName;
                    System.out.println("Driving with: " + selectedCar);
                }
            }
        });

        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                game.setScreen(new DealershipScreen(game));
            }
        });

        // --- SETTINGS PANEL ---
        Table settingsTable = new Table();
        settingsTable.bottom().right().pad(30);
        settingsTable.setFillParent(true);
        stage.addActor(settingsTable);

        TextButton settingsButton = new TextButton("Settings", skin); // Gear symbol
        settingsTable.add(settingsButton).size(120, 40);

        // Cash display label

        cashLabel = new Label("$" + formatCash(CashManager.getCash()), skin);
        cashLabel.setFontScale(1.2f);
        cashLabel.setAlignment(Align.center);

// Apply the custom background
        Container<Label> cashContainer = new Container<>(cashLabel);
        cashContainer.setBackground(createCashLabelBackground());
        cashContainer.setColor(Color.BLACK);



// Create a table for top bar
        Table topBar = new Table();
        topBar.top().left().pad(10);
        topBar.setFillParent(true);
        topBar.add(cashContainer).left().expandX();


        stage.addActor(topBar);


        // Popup buttons (initially hidden)
        final TextButton logoutButton = new TextButton("Log out", skin);
        final TextButton exitButton = new TextButton("Exit", skin);
        final TextButton quitButton = new TextButton("Quit Game", skin);
        final TextButton resetButton = new TextButton("Reset Stats", skin);

        logoutButton.setVisible(false);
        exitButton.setVisible(false);
        quitButton.setVisible(false);
        resetButton.setVisible(false);

        // Floating buttons table
        Table popupTable = new Table();
        popupTable.bottom().right().pad(80);
        popupTable.setFillParent(true);
        popupTable.add(logoutButton).size(120, 40).row();
        popupTable.add(exitButton).size(120, 40).row();
        popupTable.add(quitButton).size(120, 40);
        popupTable.add(resetButton).size(120, 40);
        stage.addActor(popupTable);

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog confirmDialog = new Dialog("Confirm Logout", skin) {
                    protected void result(Object object) {
                        if ((Boolean) object) {
                            //SupabaseAuth.logout();
                            game.setScreen(new LoginScreen(game));
                        }
                    }
                };
                confirmDialog.text("Are you sure you want to log out?");
                confirmDialog.button("Yes", true);
                confirmDialog.button("No", false);
                confirmDialog.show(stage);
            }
        });

        // Toggle popup visibility
        settingsButton.addListener(new ClickListener() {
            private boolean visible = false;

            @Override
            public void clicked(InputEvent event, float x, float y) {
                visible = !visible;
                logoutButton.setVisible(visible);
                exitButton.setVisible(visible);
                quitButton.setVisible(visible);
                resetButton.setVisible(visible);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
            }
        });

        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog confirmDialog = new Dialog("Confirm Reset", skin) {
                    protected void result(Object object) {
                        if ((Boolean) object) {

                            CashManager.setCash(80000);
                        }
                    }
                };
                confirmDialog.text("Are you sure you want to reset your cash?");
                confirmDialog.button("Yes", true);
                confirmDialog.button("No", false);
                confirmDialog.show(stage);
            }
        });



    }

    private String formatCash(int cash) {
        return String.format("%,d", cash);
    }

    private Drawable createCashLabelBackground() {
        int width = 200;
        int height = 50;

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.LIGHT_GRAY);
        pixmap.fill();

        pixmap.setColor(Color.BLACK);
        pixmap.drawRectangle(0, 0, width, height);

        Texture texture = new Texture(pixmap);
        pixmap.dispose(); // Free memory

        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();

        cashLabel.setText("$" + formatCash(CashManager.getCash()));
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
        batch.dispose();
        dealershipTexture.dispose();
        driveTexture.dispose();
        garageTexture.dispose();
    }
}
