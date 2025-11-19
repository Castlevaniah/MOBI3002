package com.example.dbballintegrationtest;

// Android view + drawing imports
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

// Custom View that shows and animates bouncing balls
public class BouncingBallView extends View {

    // List of all balls we are showing
    private final ArrayList<Ball> balls = new ArrayList<>();

    // Paint for the background color
    private final Paint bg = new Paint();

    // Constructor used when the view is in XML layouts
    public BouncingBallView(Context c, AttributeSet a) {
        super(c, a);

        // Set a dark blue background color (ARGB)
        bg.setColor(0xFF113366);
    }

    // Add a new ball to the view
    public void addBall(Ball b) {
        balls.add(b);  // store the ball in the list
        invalidate();  // ask Android to redraw the view
    }

    // Remove all balls from the view
    public void clearAll() {
        balls.clear(); // empty the list
        invalidate();  // redraw (now empty)
    }

    // Called by Android when the view needs to be drawn
    @Override
    protected void onDraw(Canvas canvas) {
        // 1) draw the background rectangle (full view size)
        canvas.drawRect(0, 0, getWidth(), getHeight(), bg);

        // 2) draw and update each ball
        for (Ball b : balls) {
            // draw the ball
            b.draw(canvas);

            // move the ball by its velocity
            b.x += b.dx;
            b.y += b.dy;

            // if the ball hits left or right edge, reverse dx
            if (b.x + b.radius > getWidth() || b.x - b.radius < 0) {
                b.dx = -b.dx;
            }

            // if the ball hits top or bottom edge, reverse dy
            if (b.y + b.radius > getHeight() || b.y - b.radius < 0) {
                b.dy = -b.dy;
            }

            // make sure the ball stays inside the view (no clipping)
            b.x = Math.max(b.radius, Math.min(b.x, getWidth() - b.radius));
            b.y = Math.max(b.radius, Math.min(b.y, getHeight() - b.radius));
        }

        // 3) call invalidate() so onDraw() runs again (animation loop)
        invalidate();
    }
}
