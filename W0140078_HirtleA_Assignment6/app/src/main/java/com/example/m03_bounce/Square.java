package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

// bouncing square (not needed for one-at-a-time GUI adds, but we keep it)
public class Square {

    float size = 100;      // side length
    float x, y;            // center
    float speedX, speedY;  // velocity

    private Paint paint;
    private RectF bounds;

    public Square(int color, float x, float y, float speedX, float speedY) {
        this.x = x; this.y = y;
        this.speedX = speedX; this.speedY = speedY;
        paint = new Paint();
        paint.setColor(color);
        bounds = new RectF();
    }

    // basic move + wall collisions + clamping
    public void moveWithCollisionDetection(Box box) {
        x += speedX;
        y += speedY;

        if (x + size / 2 > box.xMax) { speedX = -speedX; x = box.xMax - size / 2; }
        else if (x - size / 2 < box.xMin) { speedX = -speedX; x = box.xMin + size / 2; }

        if (y + size / 2 > box.yMax) { speedY = -speedY; y = box.yMax - size / 2; }
        else if (y - size / 2 < box.yMin) { speedY = -speedY; y = box.yMin + size / 2; }
    }

    public void draw(Canvas canvas) {
        bounds.set(x - size / 2, y - size / 2, x + size / 2, y + size / 2);
        canvas.drawRect(bounds, paint);
    }
}
