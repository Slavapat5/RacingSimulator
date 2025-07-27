package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class DragRaceScreen2 implements Screen {

    private final Game game;
    private SpriteBatch batch;
    private List<Car> cars = new ArrayList<>();

    private Rectangle finishZone;
    private long raceStartTime;
    private boolean raceFinished = false;
    private boolean raceStarted = false;
    private Table startOverlay;
    private boolean countdownActive = false;
    private long countdownStartTime;
    private Label countdownLabel;
    private float carStartX = 1750;
    private float carStartY = 790;
    private String winner = "";





    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;    // Camera to view the game world
    private Viewport viewport;            // Viewport to adjust game world to screen size

    private List<Rectangle> borderRectangles;
    private List<Rectangle> triggerZones;
    private boolean dialogShown = false; // To make sure popup only happens once per overlap

    private Stage uiStage;
    private Skin skin;

    public DragRaceScreen2(Game game) {
        this.game = game;
    }

    public List<Car> getCars() {
        return cars;
    }


    @Override
    public void show() {

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);  // Routes input to UI

        // Container Table for the UI
        Table uiTable = new Table();
        uiTable.top().right();  // Position at top right
        uiTable.setFillParent(true);


        startOverlay = new Table();
        startOverlay.setFillParent(true);
        startOverlay.center();

        TextButton startButton = new TextButton("Start Race", skin);

        Label versusLabel = new Label("SOLO RACE", skin);
        versusLabel.setFontScale(3f);
        versusLabel.setAlignment(Align.center);
        startOverlay.add(versusLabel).width(300).padBottom(40).row();
        startOverlay.add(startButton).width(300).height(80);


        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startOverlay.remove();
                countdownActive = true;
                countdownStartTime = TimeUtils.millis();
            }
        });

        startOverlay.add(startButton).width(300).height(80);
        uiStage.addActor(startOverlay);

        countdownLabel = new Label("", skin);
        countdownLabel.setFontScale(4f);
        countdownLabel.setVisible(false);

        Table countdownTable = new Table();
        countdownTable.setFillParent(true);
        countdownTable.center();
        countdownTable.add(countdownLabel);
        uiStage.addActor(countdownTable);



// Main toggle button
        TextButton toggleButton = new TextButton("Menu", skin);
        uiTable.add(toggleButton).pad(10).width(60);

// Menu table
        final Table menuTable = new Table(skin);
        menuTable.setVisible(false);  // Start hidden
        menuTable.defaults().pad(5).fillX().uniformX();
        menuTable.background("default-round");  // Optional style

// Buttons in the dropdown
        TextButton option1 = new TextButton("Switch Car", skin);
        TextButton option2 = new TextButton("Home", skin);
        TextButton option3 = new TextButton("Quit", skin);

// Add buttons to the menu
        menuTable.add(option1).row();
        menuTable.add(option2).row();
        menuTable.add(option3).row();

// Toggle show/hide on main button click
        toggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuTable.setVisible(!menuTable.isVisible());
            }
        });

        option1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GarageScreen(game));
            }
        });

        option2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        option3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
            }
        });


        uiTable.row();
        uiTable.add(menuTable).padTop(5);

        uiStage.addActor(uiTable);





        // Map Stuff
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("Map2.tmx");

        if (map == null) {
            throw new RuntimeException("ERROR: Failed to load map! Check if the file is in core/assets/");
        }

        mapRenderer = new OrthogonalTiledMapRenderer(map);

        MapLayer borderLayer = map.getLayers().get("Borders");

        if (borderLayer != null) {
            MapObjects objects = borderLayer.getObjects();

            borderRectangles = new ArrayList<>();


            for (MapObject object : objects) {
                if(object instanceof RectangleMapObject){
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    borderRectangles.add(rect);
                }
            }
        }else{
            throw new RuntimeException("ERROR: Borders layer not found!");
        }

        MapLayer finishLayer = map.getLayers().get("FinishZone");
        if (finishLayer != null) {
            for (MapObject object : finishLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle finishRect = ((RectangleMapObject) object).getRectangle();
                    // Store it for checking later
                    this.finishZone = finishRect;
                }
            }
        }



        batch = new SpriteBatch();

        String selectedCarTexture = CarSelectionData.getSelectedCarTexture();
        CarStats stats = CarRegistry.getStats(selectedCarTexture);
        cars.add(new Car(selectedCarTexture, carStartX, carStartY, stats.acceleration, stats.speed, stats.handling));





        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        raceStartTime = TimeUtils.millis();
        raceFinished = false;

    }

    private void showFinishDialog(long timeMillis) {
        Label rewardLabel = new Label("+$10,000", skin);
        rewardLabel.setFontScale(3.5f);
        rewardLabel.setAlignment(Align.center);
        rewardLabel.setColor(Color.GREEN);

        Label timeLabel = new Label(String.format("Time: %.2f seconds", timeMillis / 1000f), skin);
        timeLabel.setFontScale(4f);
        timeLabel.setAlignment(Align.center);

        Table summaryTable = new Table();
        summaryTable.setFillParent(true);
        summaryTable.center();
        summaryTable.setBackground(skin.newDrawable("white", 0, 0, 0, 0.75f));

        Label resultLabel = new Label("You Finished!", skin);
        resultLabel.setFontScale(4f);
        resultLabel.setColor(Color.GREEN);
        resultLabel.setAlignment(Align.center);

        summaryTable.add(resultLabel).padBottom(30).row();
        summaryTable.add(rewardLabel).padBottom(30).row();
        summaryTable.add(timeLabel).padBottom(40).row();

        TextButton retryButton = new TextButton("Retry", skin);
        retryButton.getLabel().setFontScale(2f);
        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                summaryTable.remove();
                raceFinished = false;
                raceStarted = false;
                countdownActive = false;
                countdownLabel.setVisible(false);
                cars.get(0).setPosition(carStartX, carStartY);
                uiStage.addActor(startOverlay);
            }
        });

        TextButton returnButton = new TextButton("Back to Map", skin);
        returnButton.getLabel().setFontScale(2f);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        summaryTable.add(retryButton).width(300).height(80).padBottom(20).row();
        summaryTable.add(returnButton).width(300).height(80);

        uiStage.addActor(summaryTable);
    }


    @Override
    public void render(float delta) {



        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (countdownActive) {
            long elapsed = TimeUtils.timeSinceMillis(countdownStartTime);
            int remaining = 3 - (int)(elapsed / 1000);

            if (remaining > 0) {
                countdownLabel.setText(String.valueOf(remaining));
                countdownLabel.setVisible(true);
                uiStage.act(delta);
                uiStage.draw();
                return; // Wait until countdown ends
            } else {
                countdownActive = false;
                raceStarted = true;
                countdownLabel.setVisible(false);
                raceStartTime = TimeUtils.millis(); // Final race timer start
            }
        }

        if (!raceStarted) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            uiStage.act(delta);
            uiStage.draw();
            return;
        }

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        for (Car car : cars) {
            car.render(batch);
        }
        batch.end();

        Car playerCar = cars.get(0);




        cars.get(0).update(delta,
            Gdx.input.isKeyPressed(Input.Keys.W),
            Gdx.input.isKeyPressed(Input.Keys.S),
            Gdx.input.isKeyPressed(Input.Keys.A),
            Gdx.input.isKeyPressed(Input.Keys.D),
            borderRectangles
        );



        camera.position.set(playerCar.getX(), playerCar.getY(), 0);

        if (!raceFinished && finishZone != null) {
             playerCar = cars.get(0);
            boolean playerFinished = finishZone.overlaps(playerCar.getBoundingRectangle());
            if (playerFinished) {
                long raceEndTime = TimeUtils.timeSinceMillis(raceStartTime);
                raceFinished = true;
                CashManager.addCash(4000);
                showFinishDialog(raceEndTime);
            }
        }



        uiStage.act(delta);
        uiStage.draw();

    }

    private void showMapChangeDialog() {
        Dialog dialog = new Dialog("Travel", skin) {
            @Override
            protected void result(Object obj) {
                boolean accepted = (Boolean) obj;
                if (accepted) {
                    // TODO: Load another screen or map here
                    System.out.println("Player accepted travel.");
                    game.setScreen(new DragRaceScreen(game));
                } else {
                    System.out.println("Player declined travel.");
                }
            }
        };
        dialog.text("Do you want to travel to another map?");
        dialog.button("Yes", true);
        dialog.button("No", false);
        dialog.show(uiStage);
    }


    @Override
    public void resize(int width, int height) {
        Gdx.graphics.setWindowedMode(1920, 1080);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        for (Car car : cars) {
            car.dispose();
        }
        map.dispose();
        mapRenderer.dispose();
        batch.dispose();
        uiStage.dispose();
        skin.dispose();

    }
}
