package com.manogstudios.racingsimulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.manogstudios.racingsimulator.network.SupabaseAuth;

public class LoginScreen implements Screen {

    private final Game game;
    private Stage stage;
    private Skin skin;

    public LoginScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("Login", skin);
        title.setFontScale(2f);

        TextField emailField = new TextField("", skin);
        emailField.setMessageText("Email");

        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMessageText("Password");

        Label feedbackLabel = new Label("", skin);
        feedbackLabel.setFontScale(1.2f);
        feedbackLabel.setColor(Color.RED);





        TextButton loginButton = new TextButton("Login", skin);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String email = emailField.getText();
                String password = passwordField.getText();

                if (email.isEmpty() || password.isEmpty()) {
                    feedbackLabel.setText("Please enter both fields.");
                    return;
                }

                SupabaseAuth.login(email, password, success -> {
                    Gdx.app.postRunnable(() -> {
                        if (success) {
                            feedbackLabel.setText("Login successful!");
                            // TODO: Replace with your next screen
                            game.setScreen(new PlayScreen(game));
                        } else {
                            feedbackLabel.setText("Login failed.");
                        }
                    });
                });
            }
        });



        TextButton toRegisterButton = new TextButton("Don't have an account? Register", skin);

        toRegisterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new RegisterScreen(game));
            }
        });

        table.add(title).colspan(2).padBottom(20).row();
        table.add(emailField).width(400).padBottom(10).colspan(2).row();
        table.add(passwordField).width(400).padBottom(10).colspan(2).row();
        table.add(feedbackLabel).padBottom(10).colspan(2).row();
        table.add(loginButton).padBottom(10).colspan(2).row();
        table.add(toRegisterButton).colspan(2).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {    stage.dispose(); skin.dispose();}
    @Override public void dispose() { stage.dispose();     skin.dispose(); }
}
