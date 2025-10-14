package com.example.m03_bounce;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

// custom View: owns and animates shapes, loops via invalidate()
public class BouncingBallView extends View {

    private ArrayList<Ball> balls = new ArrayList<>();
    private ArrayList<Square> squares = new ArrayList<>();
    private Ball ball_1; // kept for optional swipe nudges
    private Box box;
    private Rectangle rectTarget;
    private int score = 0;
    private float previousX, previousY;
    private final Random rand = new Random();
    private final boolean allowSwipeSpawn = false; // assignment = GUI-only adds

    public BouncingBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        box = new Box(android.graphics.Color.BLUE);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    // called from Activity after DB insert / form add
    public void addBall(Ball b) {
        if (ball_1 == null) ball_1 = b;
        balls.add(b);
        invalidate();
    }

    // clear DB + screen
    public void clearAll() {
        balls.clear();
        squares.clear();
        ball_1 = null;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        box.set(0, 0, w, h);
        rectTarget = new Rectangle(box.xMax / 2f, box.yMax / 2f, 200, 100, android.graphics.Color.RED);
        // no random seeding; assignment wants deliberate adds
    }

    @Override
    protected void onDraw(Canvas canvas) {
        box.draw(canvas);

        for (Ball b : balls) {
            b.draw(canvas);
            b.moveWithCollisionDetection(box);
        }
        for (Square s : squares) {
            s.draw(canvas);
            s.moveWithCollisionDetection(box);
        }

        rectTarget.draw(canvas);

        // simple scoring demo
        for (Ball b : balls) {
            if (rectTarget.collidesWith(b)) {
                score++;
                Log.d("SCORE", "Ball hit target. score=" + score + " (" + b.name + ")");
            }
        }

        invalidate(); // keep animation running
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // we don't spawn on swipe for this assignment
        previousX = event.getX();
        previousY = event.getY();
        return true;
    }

    // optional demo: random ball
    public void RussButtonPressed() {
        int w = Math.max(1, getMeasuredWidth());
        int h = Math.max(1, getMeasuredHeight());

        float x = rand.nextInt(Math.max(1, w - 100)) + 50;
        float y = rand.nextInt(Math.max(1, h - 100)) + 50;
        float dx = rand.nextInt(21) - 10;
        float dy = rand.nextInt(21) - 10;
        if (dx == 0) dx = 3;
        if (dy == 0) dy = 3;

        addBall(new Ball("Random", getRandomColor(), x, y, dx, dy));
    }

    private int getRandomColor() {
        return android.graphics.Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
}
