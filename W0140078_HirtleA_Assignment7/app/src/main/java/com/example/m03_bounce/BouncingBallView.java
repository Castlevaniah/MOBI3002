package com.example.m03_bounce;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

// custom View: animates balls, applies gravity, draws a touch cursor + trail
public class BouncingBallView extends View {

    // list of balls we animate/draw
    private final ArrayList<Ball> balls = new ArrayList<>();

    // background box (also the bounce bounds)
    private Box box;

    // ---------- GRAVITY ----------
    // current gravity acceleration (in pixels/s^2 after scaling)
    private float gravityX = 0f;
    private float gravityY = 800f; // default gentle down so it moves even if sensor flat

    // scale converts sensor m/s^2 into on-screen px/s^2 (tweak to taste)
    private static final float GRAVITY_SCALE = 70f;

    // last frame time to compute delta time (dt)
    private long lastTimeNs = 0;

    // ---------- TOUCH TRAIL ----------
    private final Path  touchPath  = new Path();           // the line following my finger
    private float       cursorX    = -1f, cursorY = -1f;   // tiny circle at the finger
    private final Paint trailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cursorPaint= new Paint(Paint.ANTI_ALIAS_FLAG);

    public BouncingBallView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // make the blue box background
        box = new Box(android.graphics.Color.BLUE);

        // allow focus so we can receive touch
        setFocusable(true);
        setFocusableInTouchMode(true);

        // style the trail (thin neon line)
        trailPaint.setStyle(Paint.Style.STROKE);
        trailPaint.setStrokeWidth(6f);
        trailPaint.setColor(0xFF6C6DFF);

        // style the cursor (small bright dot)
        cursorPaint.setStyle(Paint.Style.FILL);
        cursorPaint.setColor(0xFFEDEBFF);
    }

    // Activity calls this after inserting a row in DB
    public void addBall(Ball b) {
        balls.add(b);
        invalidate(); // redraw to see the new ball
    }

    // clear everything from screen (Activity handles DB clear)
    public void clearAll() {
        balls.clear();
        touchPath.reset();
        cursorX = cursorY = -1f;
        invalidate();
    }

    // Activity pushes gravity here (called from onSensorChanged)
    public void setGravity(float gx, float gy) {
        // convert from m/s^2 to pixels/s^2 so it feels nice
        gravityX = gx * GRAVITY_SCALE;
        gravityY = gy * GRAVITY_SCALE;
    }

    // when view size is known (or changes), set bounds and reset timer
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        box.set(0, 0, w, h);
        lastTimeNs = System.nanoTime(); // start timing for dt
    }

    // main draw/animate loop (Android calls this repeatedly)
    @Override
    protected void onDraw(Canvas canvas) {
        // compute time since last frame (in seconds)
        long now = System.nanoTime();
        float dt = (lastTimeNs == 0) ? 0f : (now - lastTimeNs) / 1_000_000_000f;

        // clamp dt so a hiccup doesn’t yeet balls into space
        if (dt > 0.05f) dt = 0.05f;
        lastTimeNs = now;

        // draw background box
        box.draw(canvas);

        // update + draw all balls
        for (Ball b : balls) {
            // apply gravity to velocity: v = v + a*dt
            b.speedX += gravityX * dt;
            b.speedY += gravityY * dt;

            // draw the ball (with its name)
            b.draw(canvas);

            // move + bounce off walls + clamp inside the box
            b.moveWithCollisionDetection(box);
        }

        // draw the finger trail and cursor on top
        canvas.drawPath(touchPath, trailPaint);
        if (cursorX >= 0 && cursorY >= 0) {
            canvas.drawCircle(cursorX, cursorY, 10f, cursorPaint);
        }

        // keep the animation going
        invalidate();
    }

    // handle touches to draw the cursor + trail
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // start a new path at this point
                touchPath.moveTo(x, y);
                cursorX = x; cursorY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // extend the path to the new finger point
                touchPath.lineTo(x, y);
                cursorX = x; cursorY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // finger lifted -> hide the cursor (keep trail)
                cursorX = cursorY = -1f;
                break;
        }
        return true; // we handled the event
    }
}
