package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

// simple center-positioned rectangle. we draw it and also use it for overlap tests.
public class Rectangle {

    float x, y;        // center
    float width, height;

    private Paint paint;
    private RectF bounds;

    public Rectangle(float x, float y, float width, float height, int color) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;

        paint = new Paint();
        paint.setColor(color);
        bounds = new RectF();
        updateBounds();
    }

    // recompute edges from center + size
    public void updateBounds() {
        bounds.set(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
    }

    public void draw(Canvas canvas) { canvas.drawRect(bounds, paint); }

    // overlap vs a ball's bounding box (fast and fine here)
    public boolean collidesWith(Ball ball) {
        return RectF.intersects(
                bounds,
                new RectF(ball.x - ball.radius, ball.y - ball.radius,
                        ball.x + ball.radius, ball.y + ball.radius));
    }


}
