package com.manogstudios.racingsimulator;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.manogstudios.racingsimulator.network.SupabaseAuth;


public class RegisterScreen implements Screen {
    private final Game game;
    private Stage stage;
    private Skin skin;

    public RegisterScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = new Label("Register", skin);
        title.setFontScale(2f);

        TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");

        TextField emailField = new TextField("", skin);
        emailField.setMessageText("Email");

        TextField passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);

        Label statusLabel = new Label("", skin);

        TextButton registerButton = new TextButton("Sign Up", skin);
        TextButton loginButton = new TextButton("Log in", skin);
        TextButton backButton = new TextButton("Back", skin);

        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String email = emailField.getText();
                String password = passwordField.getText();
                String username = usernameField.getText();

                if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    statusLabel.setText("All fields are required.");
                    return;
                }

                new Thread(() -> {
                    boolean success = SupabaseAuth.registerUser(email, password); // no username
                    Gdx.app.postRunnable(() -> {
                        if (success) {
                            statusLabel.setText("✅ Registration successful!");
                        } else {
                            statusLabel.setText("❌ Failed to register.");
                        }
                    });
                }).start();
            }
        });
                backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Go back to main menu or login screen
                game.setScreen(new MenuScreen(game)); // replace with your menu screen
            }
        });

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game)); // replace with your menu screen
            }
        });

        root.add(title).padBottom(30).row();
        root.add(usernameField).width(400).height(50).padBottom(15).row();
        root.add(emailField).width(400).height(50).padBottom(15).row();
        root.add(passwordField).width(400).height(50).padBottom(20).row();
        root.add(registerButton).width(200).height(60).padBottom(10).row();
        root.add(statusLabel).padBottom(20).row();
        root.add(backButton).width(150).height(40);
        root.add(loginButton).width(150).height(40);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {    stage.dispose(); skin.dispose();}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
