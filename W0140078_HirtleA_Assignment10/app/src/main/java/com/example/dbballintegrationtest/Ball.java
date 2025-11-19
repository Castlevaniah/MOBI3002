package com.example.dbballintegrationtest;

// We need these to draw on the screen
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Ball {

    // Name of the ball (can help for debugging / saving)
    public String name = "ball";

    // Size of the ball
    public float radius = 40;

    // Position (x, y) and movement (dx, dy)
    public float x, y, dx, dy;

    // How the ball looks (color, style)
    private final Paint paint = new Paint();

    // Used to store the oval shape area
    private final RectF bounds = new RectF();

    // Constructor: runs when we make a new Ball
    public Ball(String name, int color, float x, float y, float dx, float dy) {
        this.name = name;   // set ball name
        this.x = x;         // start x
        this.y = y;         // start y
        this.dx = dx;       // speed in x
        this.dy = dy;       // speed in y

        paint.setColor(color);  // set ball color
    }

    // Draw the ball on the screen
    public void draw(Canvas c) {

        // Make a rectangle around the center (x, y)
        bounds.set(
                x - radius,  // left
                y - radius,  // top
                x + radius,  // right
                y + radius   // bottom
        );

        // Draw the oval (the ball) inside that rectangle
        c.drawOval(bounds, paint);
    }
}
