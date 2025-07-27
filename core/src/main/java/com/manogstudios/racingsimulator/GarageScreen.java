package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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

import java.util.ArrayList;

public class GarageScreen implements Screen {
    private final Game game;
    private Stage stage;
    private Skin skin;
    private Label cashLabel;
    private Table infoPanel;
    private Label selectedCarLabel, selectedPriceLabel, horsepowerLabel, weightLabel, engineLabel, historyLabel;
    private Image carPreviewImage;



    public GarageScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Info Panel
        infoPanel = new Table(skin);
        infoPanel.setBackground("default-round");
        infoPanel.setColor(0.1f, 0.1f, 0.1f, 0.9f);
        infoPanel.pad(20);
        infoPanel.defaults().space(10);

        carPreviewImage = new Image();
        carPreviewImage.setScaling(Scaling.fit);
        carPreviewImage.setSize(300, 150);





        selectedCarLabel = new Label("Select a car", skin);
        selectedCarLabel.setFontScale(1.4f);
        selectedPriceLabel = new Label("", skin);
        horsepowerLabel = new Label("", skin);
        weightLabel = new Label("", skin);
        engineLabel = new Label("", skin);
        historyLabel = new Label("", skin);
        historyLabel.setWrap(true);

        Table infoContent = new Table();
        infoContent.add(carPreviewImage).size(300, 150).row();
        infoContent.add(selectedCarLabel).center().row();
        infoContent.add(selectedPriceLabel).center().row();
        infoContent.add(horsepowerLabel).center().row();
        infoContent.add(weightLabel).center().row();
        infoContent.add(engineLabel).center().row();
        infoContent.add(historyLabel).width(300).padTop(10).colspan(2).row();

        infoPanel.add(infoContent).expand().center();


        Table root = new Table();
        root.setFillParent(true);
        root.center();
        stage.addActor(root);

        // Scrollable container for many cars
        ScrollPane scrollPane;
        Table carListTable = new Table();
        carListTable.pad(10).defaults().space(30);

        // Load owned cars
        CarOwnershipManager.loadOwnedCars(); // Ensure loaded

        CarOwnershipManager.loadOwnedCars();
        CarDataBase.load();

        for (String imagePath : CarOwnershipManager.getOwnedCars()) {
            CarData car = CarDataBase.getCarByImage(imagePath);
            if (car != null) {
                addCarTemplate(carListTable, car.image, car.title, car.description);
            } else {
                System.err.println("Missing car data for: " + imagePath);
            }
        }


        scrollPane = new ScrollPane(carListTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        root.add(scrollPane).expand().fill();
        root.add(infoPanel).width(400).expandY().fillY().padRight(10);  // Adjust width/padding if needed

    }




    private void addCarTemplate(Table parent, String imagePath, String title, String description) {
        Table carBox = new Table(skin);
        carBox.setBackground("default-round");
        carBox.setColor(0.1f, 0.1f, 0.1f, 0.85f);
        carBox.pad(10).defaults().space(10);

        // Car image
        try{
        Texture carTexture = new Texture(Gdx.files.internal(imagePath));
        Image carImage = new Image(carTexture);
        carImage.setScaling(Scaling.fit);
        carBox.add(carImage).size(300, 150).row();
    } catch (Exception e) {
        System.err.println("Failed to load image: " + imagePath);
        e.printStackTrace();
    }

        // Car title
        Label nameLabel = new Label(title, skin);
        nameLabel.setFontScale(1.2f);

        // Car description
        Label descriptionLabel = new Label(description, skin);
        descriptionLabel.setColor(Color.LIGHT_GRAY);
        carBox.add(descriptionLabel).width(400).row();
        descriptionLabel.setAlignment(Align.center);
        descriptionLabel.setWrap(true);

        // Select button
        TextButton selectButton = new TextButton("Select", skin);

        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CarSelectionData.setSelectedCarTexture(imagePath);
                System.out.println("Selected car set to: " + imagePath);
                System.out.println("Selected car: " + title);
                if (game instanceof Main) {
                    ((Main) game).selectedCarName = title;
                }
                CarData selected = CarDataBase.getCarByImage(imagePath);
                Texture carTexture = new Texture(Gdx.files.internal(imagePath));
                carPreviewImage.setDrawable(new TextureRegionDrawable(new TextureRegion(carTexture)));

                if (selected != null) {
                    // Update car image preview
                    try {
                         carTexture = new Texture(Gdx.files.internal(selected.image));
                        carPreviewImage.setDrawable(new TextureRegionDrawable(new TextureRegion(carTexture)));
                    } catch (Exception e) {
                        System.err.println("Failed to load selected car image: " + selected.image);
                    }

                    selectedCarLabel.setText(selected.title);
                    selectedPriceLabel.setText("$" + String.format("%,d", selected.price));
                    selectedPriceLabel.setFontScale(1.2f);
                    horsepowerLabel.setText("Horsepower: " + selected.horsepower);
                    weightLabel.setText("Weight: " + selected.weightKg + "kg");
                    engineLabel.setText("Engine: " + selected.engine);
                    historyLabel.setText(selected.longDescription);
                }

                //game.setScreen(new MenuScreen(game)); // Go back to menu
            }
        });

// Sell button
        TextButton sellButton = new TextButton("Sell", skin);
        sellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                // Check if it's the currently selected car
                if (CarSelectionData.getSelectedCarTexture().equals(imagePath)) {
                    Dialog warningDialog = new Dialog("Can't Sell Selected Car", skin) {
                        @Override
                        protected void result(Object object) {
                            // Do nothing, just close dialog
                        }
                    };
                    warningDialog.text("You can't sell the car you're currently using.");
                    warningDialog.button("OK");
                    warningDialog.show(stage);
                    return;
                }

                // Process sale
                CarData car = CarDataBase.getCarByImage(imagePath);
                if (car != null) {
                    int refund = (int) (car.price * 0.75f); // 75% refund

                    // Remove car
                    CarOwnershipManager.removeCar(imagePath);
                    CashManager.addCash(refund);
                    CarOwnershipManager.saveOwnedCars();

                    System.out.println("Sold car: " + title + " for $" + refund);

                    // If no cars are owned now, reassign a default car
                    if (CarOwnershipManager.getOwnedCars().isEmpty()) {
                        SelectedCar.set("Audi A4 - 2020.png"); // Make sure this car exists!
                        CarOwnershipManager.addCar("Audi A4 - 2020.png");
                        CarOwnershipManager.saveOwnedCars();
                    } else {
                        // If the selected car was sold, reassign another one
                        if (SelectedCar.get().equals(imagePath)) {
                            String firstOwned = new ArrayList<>(CarOwnershipManager.getOwnedCars()).get(0);

                            SelectedCar.set(firstOwned);
                            CarSelectionData.setSelectedCarTexture(firstOwned);
                        }
                    }

                    // Refresh screen
                    game.setScreen(new GarageScreen(game));
                } else {
                    System.err.println("Couldn't find car data for: " + imagePath);
                }
            }
        });




        carBox.add(nameLabel).row();
        carBox.add(descriptionLabel).width(400).row();
        carBox.add(selectButton).padTop(10).row();
        carBox.add(sellButton).padTop(10).row();

        parent.add(carBox).pad(20);
        parent.row();

        // Cash display label
        cashLabel = new Label("$" + formatCash(CashManager.getCash()), skin);
        cashLabel.setFontScale(1.2f);
        cashLabel.setAlignment(Align.center);

// Apply the custom background
        Container<Label> cashContainer = new Container<>(cashLabel);
        cashContainer.setBackground(createCashLabelBackground());
        cashContainer.setColor(Color.BLACK);


// Place label in a corner overlay
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

    @Override public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

        //cashLabel.setText("Cash: $" + formatCash(CashManager.getCash()));

    }

    @Override public void resize(int width, int height) {
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
