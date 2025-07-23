package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;

/** First screen of the application. Displayed after the application is created. */
public class GameScreen implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private List<Car> cars = new ArrayList<>();

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;    // Camera to view the game world
    private Viewport viewport;            // Viewport to adjust game world to screen size

    private List<Rectangle> borderRectangles;
    private List<Rectangle> triggerZones;
    private boolean dialogShown = false; // To make sure popup only happens once per overlap


    private Stage uiStage;
    private Skin skin;

    public GameScreen(Game game) {
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

// Main toggle button
        TextButton toggleButton = new TextButton("Menu", skin);  // Hamburger icon style
        uiTable.add(toggleButton).pad(10).width(60);

// Menu table (initially hidden)
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

// Add both toggle button and menu to UI stage
        uiTable.row();
        uiTable.add(menuTable).padTop(5);

        uiStage.addActor(uiTable);





        // Map Stuff
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("Map.tmx");

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

        triggerZones = new ArrayList<>(); // ✅ Always initialize!

        MapLayer triggerLayer = map.getLayers().get("Teleports");
        if (triggerLayer != null) {
            for (MapObject object : triggerLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    triggerZones.add(rect);
                }
            }
        } else {
            System.out.println("Trigger layer not found.");
        }





        batch = new SpriteBatch();

        String selectedCarTexture = CarSelectionData.getSelectedCarTexture();
        CarStats stats = CarRegistry.getStats(selectedCarTexture);
        cars.add(new Car(selectedCarTexture, 6910, 10240, stats.acceleration, stats.speed, stats.handling));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        for (Rectangle trigger : triggerZones) {
            if (trigger.overlaps(playerCar.getBoundingRectangle())) {
                if (!dialogShown) {
                    showMapChangeDialog();
                    dialogShown = true; // Prevents repeated dialogs
                }
                break;
            } else {
                dialogShown = false; // Reset when not in trigger
            }
        }


        camera.position.set(playerCar.getX(), playerCar.getY(), 0);




        uiStage.act(delta);
        uiStage.draw();

    }

    private void showMapChangeDialog() {
        Dialog dialog = new Dialog("Travel", skin) {
            @Override
            protected void result(Object obj) {
                if (obj.equals("solo")) {
                    game.setScreen(new DragRaceScreen2(game)); // new screen with no AI
                } else if (obj.equals("multi")) {
                    game.setScreen(new DragRaceScreen(game));  // existing drag race with AI
                } else {
                    // Cancelled
                }
            }
        };
        dialog.text("Choose race type:");
        dialog.button("Race Solo", "solo");
        dialog.button("Race Opponent", "multi");
        dialog.button("Exist", "cancel");
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
