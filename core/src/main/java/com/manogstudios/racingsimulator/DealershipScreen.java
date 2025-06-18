package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class DealershipScreen implements Screen {
    private final Game game;
    private Stage stage;
    private Skin skin;


    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        CarOwnershipManager.loadOwnedCars();
        CarDataBase.load();

        Table carListTable = new Table();
        carListTable.pad(10).defaults().space(30);

        for (CarData car : CarDataBase.getAllCars()) {
            addCarToList(carListTable, car);
        }

        ScrollPane scrollPane = new ScrollPane(carListTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        root.add(scrollPane).expand().fill();

        // Cash label in top corner
        Label cashLabel = new Label("Cash: $" + formatCash(CashManager.getCash()), skin);
        cashLabel.setFontScale(1.2f);

        Table topBar = new Table();
        topBar.top().left().pad(10);
        topBar.setFillParent(true);
        topBar.add(cashLabel).left();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        topBar.add(backButton).expandX().right(); // Pushes it to the right side


        stage.addActor(topBar);
    }

    private void addCarToList(Table parent, CarData car) {
        Table carBox = new Table(skin);
        carBox.setBackground("default-round");
        carBox.pad(10).defaults().space(10);

        try {
            Texture carTexture = new Texture(Gdx.files.internal(car.image));
            Image carImage = new Image(carTexture);
            carImage.setScaling(Scaling.fit);
            carBox.add(carImage).size(300, 150).row();
        } catch (Exception e) {
            System.err.println("Failed to load image: " + car.image);
        }

        carBox.add(new Label(car.title, skin)).row();
        carBox.add(new Label(car.description, skin)).width(400).row();
        carBox.add(new Label("Price: $" + car.price, skin)).row();

        // Buy button if not owned
        if (!CarOwnershipManager.ownsCar(car.image)) {
            TextButton buyButton = new TextButton("Buy", skin);
            buyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (CashManager.getCash() >= car.price) {
                        CashManager.subtractCash(car.price);
                        CarOwnershipManager.addCar(car.image);
                        System.out.println("Bought " + car.title);
                        game.setScreen(new DealershipScreen(game)); // Refresh screen
                    } else {
                        System.out.println("Not enough cash to buy " + car.title);
                    }
                }
            });
            carBox.add(buyButton).padTop(10).row();
        } else {
            Label ownedLabel = new Label("Owned", skin);
            ownedLabel.setFontScale(1.1f);
            carBox.add(ownedLabel).padTop(10).row();
        }

        parent.add(carBox).pad(20);
        parent.row();
    }

    public DealershipScreen(Game game) {
        this.game = game;
    }

    private String formatCash(int cash) {
        return String.format("%,d", cash);
    }


    @Override public void render(float delta) { stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
