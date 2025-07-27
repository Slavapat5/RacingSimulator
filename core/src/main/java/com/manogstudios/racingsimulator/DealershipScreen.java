package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class DealershipScreen implements Screen {
    private final Game game;
    private Stage stage;
    private Skin skin;
    private Table infoPanel;
    private Label selectedCarLabel;
    private Label selectedPriceLabel;
    public int horsepower;
    public int weightKg;
    public String engine;
    private Label horsepowerLabel;
    private Label weightLabel;
    private Label engineLabel;
    private Label historyLabel;
    private Label cashLabel;
    private Image carPreviewImage;





    public boolean tryBuyCar(CarData car) {
        if (CashManager.getCash() >= car.price) {
            CashManager.subtractCash(car.price);
            CarOwnershipManager.addCar(car.image);
            return true;
        }
        return false;
    }




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



        // Create info panel
        infoPanel = new Table(skin);
        infoPanel.setBackground("default-round");
        infoPanel.setColor(0.1f, 0.1f, 0.1f, 0.9f);
        infoPanel.pad(20);
        infoPanel.defaults().space(15);

        historyLabel = new Label("Select a car to see its history.", skin);
        historyLabel.setWrap(true);
        historyLabel.setAlignment(Align.topLeft);
        historyLabel.setColor(Color.LIGHT_GRAY);

        selectedCarLabel = new Label("Select a car", skin);
        selectedCarLabel.setFontScale(1.5f);
        selectedCarLabel.setAlignment(Align.center);

        selectedPriceLabel = new Label("", skin);
        selectedPriceLabel.setFontScale(1.2f);
        selectedPriceLabel.setAlignment(Align.center);

        horsepowerLabel = new Label("", skin);
        weightLabel = new Label("", skin);
        engineLabel = new Label("", skin);

        carPreviewImage = new Image(); // Empty at first
        carPreviewImage.setScaling(Scaling.fit);
        carPreviewImage.setSize(300, 150); // Adjust as needed


        // Content table
        Table infoContent = new Table();
        infoContent.add(carPreviewImage).size(300, 150).row();
        infoContent.add(selectedCarLabel).center().row();
        infoContent.add(selectedPriceLabel).center().row();
        infoContent.add(horsepowerLabel).center().row();
        infoContent.add(weightLabel).center().row();
        infoContent.add(engineLabel).center().row();


        infoContent.add(historyLabel).width(300).padTop(10).colspan(2).row();

        infoPanel.add(infoContent).expand().center();




        // Add the scrollPane and infoPanel to the root
        root.add(scrollPane).expand().fill().left();
        root.add(infoPanel).width(400).expandY().fillY().padRight(10); // Side box on the right


        cashLabel = new Label("$" + formatCash(CashManager.getCash()), skin);
        cashLabel.setFontScale(1.2f);
        cashLabel.setAlignment(Align.center);

        // Apply the custom background
        Container<Label> cashContainer = new Container<>(cashLabel);
        cashContainer.setBackground(createCashLabelBackground());
        cashContainer.setColor(Color.BLACK);


        Table topBar = new Table();
        topBar.top().left().pad(10);
        topBar.setFillParent(true);
        topBar.add(cashContainer).left();


        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        topBar.add(backButton).expandX().right();


        stage.addActor(topBar);
    }

    private void addCarToList(Table parent, CarData car) {
        Table carBox = new Table(skin);
        carBox.setBackground("default-round");
        carBox.setColor(0.1f, 0.1f, 0.1f, 0.85f);
        carBox.pad(10).defaults().space(10);

        carBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    Texture newTexture = new Texture(Gdx.files.internal(car.image));
                    carPreviewImage.setDrawable(new TextureRegionDrawable(new TextureRegion(newTexture)));
                } catch (Exception e) {
                    System.err.println("Failed to load preview image: " + car.image);
                }

                selectedCarLabel.setText(car.title);
                selectedPriceLabel.setText("Price: $" + String.format("%,d", car.price));
                horsepowerLabel.setText("Power: " + car.horsepower + " hp");
                weightLabel.setText("Weight: " + car.weightKg + " kg");
                engineLabel.setText("Engine: " + car.engine);
                historyLabel.setText(car.longDescription);




            }
        });



        try {
            Texture carTexture = new Texture(Gdx.files.internal(car.image));
            Image carImage = new Image(carTexture);
            carImage.setScaling(Scaling.fit);
            carBox.add(carImage).size(300, 150).row();
        } catch (Exception e) {
            System.err.println("Failed to load image: " + car.image);
        }

        carBox.add(new Label(car.title, skin)).row();

        Label descriptionLabel = new Label(car.description, skin);
        descriptionLabel.setColor(Color.LIGHT_GRAY);
        descriptionLabel.setAlignment(Align.center);
        descriptionLabel.setWrap(true);
        carBox.add(descriptionLabel).width(400).row();




        String formattedPrice = String.format("%,d", car.price);
        Label priceLabel = new Label("$" + formattedPrice, skin);
        priceLabel.setColor(Color.GREEN);
        priceLabel.setFontScale(1.2f);
        priceLabel.setAlignment(Align.center);
        carBox.add(priceLabel).row();



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

    public String formatCash(int cash) {
        return String.format("%,d", cash);
    }

    private Drawable createCashLabelBackground() {
        int width = 200;
        int height = 50;

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.LIGHT_GRAY); // Fill color
        pixmap.fill();

        pixmap.setColor(Color.BLACK); // Border color
        pixmap.drawRectangle(0, 0, width, height);

        Texture texture = new Texture(pixmap);
        pixmap.dispose(); // Free memory

        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override public void render(float delta) { stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
