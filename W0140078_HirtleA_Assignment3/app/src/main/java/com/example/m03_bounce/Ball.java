package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

// one moving ball (position, velocity, size, color) + draw + bounce logic
public class Ball {

    // optional label so logs can show which ball is which
    public String name = "ball";

    // size in pixels
    public float radius = 50;

    // current center position
    public float x, y;

    // per-frame velocity (pixels/frame)
    public float speedX, speedY;

    private Paint paint;   // color/style
    private RectF bounds;  // reused rect for drawing as an oval
    public boolean hasCollided = false; // not used for marks, but left here

    // constructor with name (used for DB-loaded + GUI-added balls)
    public Ball(String name, int color, float x, float y, float speedX, float speedY) {
        this.name = name;
        this.x = x; this.y = y;
        this.speedX = speedX; this.speedY = speedY;
        paint = new Paint();
        paint.setColor(color);
        bounds = new RectF();
    }

    // move the ball, bounce on walls, clamp inside the box
    public void moveWithCollisionDetection(Box box) {
        x += speedX;
        y += speedY;

        // left/right bounce
        if (x + radius > box.xMax || x - radius < box.xMin) {
            speedX = -speedX;
        }

        // top/bottom bounce
        if (y + radius > box.yMax || y - radius < box.yMin) {
            speedY = -speedY;
        }

        // keep center legal so it never ends outside the box
        x = Math.max(box.xMin + radius, Math.min(x, box.xMax - radius));
        y = Math.max(box.yMin + radius, Math.min(y, box.yMax - radius));
    }

    // draw as an oval using a preallocated RectF
    public void draw(Canvas canvas) {
        bounds.set(x - radius, y - radius, x + radius, y + radius);
        canvas.drawOval(bounds, paint);
    }
}
