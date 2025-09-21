package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Rectangle {

    // QUESTION 6: RECTANGLE SHAPE FOR COLLISION AND SCORING
    float x, y;        // CENTER POSITION
    float width, height;
    private Paint paint;
    private RectF bounds;

    public Rectangle(float x, float y, float width, float height, int color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        paint = new Paint();
        paint.setColor(color);
        bounds = new RectF();
        updateBounds();
    }

    // UPDATE RECTANGLE BOUNDS
    public void updateBounds() {
        bounds.set(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
    }

    // DRAW RECTANGLE
    public void draw(Canvas canvas) {
        canvas.drawRect(bounds, paint);
    }

    // CHECK COLLISION WITH A BALL
    public boolean collidesWith(Ball ball) {
        return RectF.intersects(bounds,
                new RectF(ball.x - ball.radius, ball.y - ball.radius,
                        ball.x + ball.radius, ball.y + ball.radius));
    }

    // CHECK COLLISION WITH A SQUARE
    public boolean collidesWith(Square square) {
        return RectF.intersects(bounds,
                new RectF(square.x - square.size / 2, square.y - square.size / 2,
                        square.x + square.size / 2, square.y + square.size / 2));
    }
}
