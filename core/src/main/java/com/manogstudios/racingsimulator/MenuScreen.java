package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {
    private final Game game;
    private Stage stage;
    private Skin skin;
    private Label cashLabel;


    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        // Built-in skin (ugly but it works)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton button1 = new TextButton("DEALERSHIP", skin);
        TextButton button2 = new TextButton("DRIVE", skin); // this one leads to FirstScreen
        TextButton button3 = new TextButton("GARAGE", skin);

        button1.getLabel().setFontScale(2);
        button2.getLabel().setFontScale(2);
        button3.getLabel().setFontScale(2);

        float buttonWidth = 300;
        float buttonHeight = 100;

        button1.setSize(buttonWidth, buttonHeight);
        button2.setSize(buttonWidth, buttonHeight);
        button3.setSize(buttonWidth, buttonHeight);

        table.add(button1).width(buttonWidth).height(buttonHeight).pad(20);
        table.add(button2).width(buttonWidth).height(buttonHeight).pad(20);
        table.add(button3).width(buttonWidth).height(buttonHeight).pad(20);



        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GarageScreen(game));
            }
        });

        // Add click listener for middle button
        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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

        cashLabel = new Label("Cash: $" + formatCash(CashManager.getCash()), skin);
        cashLabel.setFontScale(1.2f);
        cashLabel.setAlignment(Align.topLeft);

// Create a table for top bar
        Table topBar = new Table();
        topBar.top().left().pad(10);
        topBar.setFillParent(true);
        topBar.add(cashLabel).left().expandX();

        stage.addActor(topBar);


        // Popup buttons (initially hidden)
        final TextButton audioButton = new TextButton("Audio", skin);
        final TextButton cashButton = new TextButton("Free Cash", skin);
        final TextButton quitButton = new TextButton("Quit Game", skin);

        audioButton.setVisible(false);
        cashButton.setVisible(false);
        quitButton.setVisible(false);

        // Floating buttons table
        Table popupTable = new Table();
        popupTable.bottom().right().pad(80);
        popupTable.setFillParent(true);
        popupTable.add(audioButton).size(120, 40).row();
        popupTable.add(cashButton).size(120, 40).row();
        popupTable.add(quitButton).size(120, 40);
        stage.addActor(popupTable);

        // Toggle popup visibility
        settingsButton.addListener(new ClickListener() {
            private boolean visible = false;

            @Override
            public void clicked(InputEvent event, float x, float y) {
                visible = !visible;
                audioButton.setVisible(visible);
                cashButton.setVisible(visible);
                quitButton.setVisible(visible);
            }
        });

        cashButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CashManager.addCash(10000); // Give 10,000 cash
                System.out.println("Added $10,000 cash. Total now: $" + CashManager.getCash());
            }
        });




    }

    private String formatCash(int cash) {
        return String.format("%,d", cash);
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        cashLabel.setText("Cash: $" + formatCash(CashManager.getCash()));

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
