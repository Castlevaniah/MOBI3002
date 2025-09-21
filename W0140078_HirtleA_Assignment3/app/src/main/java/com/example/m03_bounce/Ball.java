package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import java.util.Random;

public class Ball {

    public float radius = 50;
    public float x, y;
    public float speedX, speedY;
    private Paint paint;
    private RectF bounds;
    public boolean hasCollided = false; // Q7: COLLISION FLAG

    public Ball(int color, float x, float y, float speedX, float speedY) {
        this.x = x; this.y = y;
        this.speedX = speedX; this.speedY = speedY;
        paint = new Paint();
        paint.setColor(color);
        bounds = new RectF();
    }

    public void moveWithCollisionDetection(Box box) {
        x += speedX; y += speedY;
        if (x+radius > box.xMax || x-radius < box.xMin) speedX = -speedX;
        if (y+radius > box.yMax || y-radius < box.yMin) speedY = -speedY;
        x = Math.max(box.xMin+radius, Math.min(x, box.xMax-radius));
        y = Math.max(box.yMin+radius, Math.min(y, box.yMax-radius));
    }

    public void draw(Canvas canvas) {
        bounds.set(x-radius, y-radius, x+radius, y+radius);
        canvas.drawOval(bounds, paint);
    }
}
