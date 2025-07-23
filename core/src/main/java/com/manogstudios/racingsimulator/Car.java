package com.manogstudios.racingsimulator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Car {
    private Texture texture;
    private float x, y;
    private float rotation;
    private Vector2 velocity;

    private final float acceleration;
    private final float maxSpeed;
    private final float turnSpeed;

    public Rectangle getBoundingRectangle() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }



    public Car(String texturePath, float startX, float startY, float acceleration, float maxSpeed, float turnSpeed) {
        this.texture = new Texture(texturePath);
        this.x = startX;
        this.y = startY;
        this.rotation = 0f;
        this.velocity = new Vector2(0, 0);
        this.acceleration = acceleration;
        this.maxSpeed = maxSpeed;
        this.turnSpeed = turnSpeed;
    }



    public void update(float delta, boolean moveForward, boolean brake, boolean turnLeft, boolean turnRight, List<Rectangle> borders) {
        rotation = (rotation + 360) % 360;

        Vector2 direction = new Vector2(MathUtils.cosDeg(rotation + 90), MathUtils.sinDeg(rotation + 90));

        if (moveForward) {
            velocity.add(direction.scl(acceleration * delta));
        }
        if (brake) {
            velocity.scl(0.99f);
        }
        if (!moveForward && !brake) {
            velocity.scl(0.99f);
        }
        if (velocity.len() > maxSpeed) {
            velocity.setLength(maxSpeed);
        }

        Vector2 lateral = new Vector2(-velocity.y, velocity.x).nor();
        float lateralSpeed = velocity.dot(lateral);
        velocity.sub(lateral.scl(lateralSpeed * 0.9f)); //higher = more grip   0-1 scale

        if (velocity.len() > 2f) {
            float turnMultiplier = MathUtils.clamp(1 - (velocity.len() / maxSpeed), 0.4f, 1f);
            if (turnLeft) rotation += turnSpeed * delta * 80 * turnMultiplier;
            if (turnRight) rotation -= turnSpeed * delta * 80 * turnMultiplier;
        }



//        if (!moveForward && !brake && velocity.len() > 5f) {
//            float targetAngle = MathUtils.atan2(velocity.y, velocity.x) * MathUtils.radiansToDegrees;
//           float angleDiff = (targetAngle - rotation + 540) % 360 - 180;
//            rotation += MathUtils.clamp(angleDiff, -3f, 3f) * 2f * delta;
//        }

        float nextX = x + velocity.x * delta;
        float nextY = y + velocity.y * delta;

        boolean collisionX = false;
        boolean collisionY = false;

        Rectangle carRectX = new Rectangle(nextX, y, texture.getWidth(), texture.getHeight());
        Rectangle carRectY = new Rectangle(x, nextY, texture.getWidth(), texture.getHeight());

        for (Rectangle border : borders) {
            if (carRectX.overlaps(border)) {
                collisionX = true;
                break;
            }
        }

        for (Rectangle border : borders) {
            if (carRectY.overlaps(border)) {
                collisionY = true;
                break;
            }
        }

        if (!collisionX) x = nextX;
        else velocity.x = 0;

        if (!collisionY) y = nextY;
        else velocity.y = 0;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.velocity.set(0, 0); // Optional: stop movement when reset
        this.rotation = 0f;      // Optional: reset rotation if needed
    }


    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, texture.getWidth() / 2f, texture.getHeight() / 2f,
            texture.getWidth(), texture.getHeight(), 1, 1, rotation, 0, 0,
            texture.getWidth(), texture.getHeight(), false, false);
    }

    public void dispose() {
        texture.dispose();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
