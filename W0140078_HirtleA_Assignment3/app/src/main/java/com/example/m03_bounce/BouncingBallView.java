package com.example.m03_bounce;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class BouncingBallView extends View {

    // QUESTION 1: LIST OF BALLS AND SQUARES
    private ArrayList<Ball> balls = new ArrayList<>();
    private ArrayList<Square> squares = new ArrayList<>();
    private Ball ball_1;  // FIRST BALL REFERENCE FOR SWIPE
    private Box box;      // BOX BOUNDARY

    // QUESTION 6: RECTANGLE TARGET AND SCORE
    private Rectangle rectTarget;
    private int score = 0;

    // TOUCH TRACKING
    private float previousX;
    private float previousY;

    private Random rand = new Random();

    public BouncingBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        box = new Box(android.graphics.Color.BLUE); // QUESTION 1: CHANGE BOX COLOR
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    // UTILITY: GET RANDOM COLOR
    private int getRandomColor() {
        return android.graphics.Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        box.set(0, 0, w, h);

        // INITIAL BALLS
        if (balls.isEmpty()) {
            float startX1 = rand.nextInt(w - 100) + 50;
            float startY1 = rand.nextInt(h - 100) + 50;
            float speedX1 = rand.nextInt(11) - 5;
            float speedY1 = rand.nextInt(11) - 5;
            if (speedX1 == 0) speedX1 = 3;
            if (speedY1 == 0) speedY1 = 3;
            balls.add(new Ball(getRandomColor(), startX1, startY1, speedX1, speedY1));

            float startX2 = rand.nextInt(w - 100) + 50;
            float startY2 = rand.nextInt(h - 100) + 50;
            float speedX2 = rand.nextInt(11) - 5;
            float speedY2 = rand.nextInt(11) - 5;
            if (speedX2 == 0) speedX2 = 3;
            if (speedY2 == 0) speedY2 = 3;
            balls.add(new Ball(getRandomColor(), startX2, startY2, speedX2, speedY2));

            ball_1 = balls.get(0);
        }

        // QUESTION 6: INITIALIZE RECTANGLE IN CENTER
        rectTarget = new Rectangle(box.xMax / 2f, box.yMax / 2f, 200, 100, android.graphics.Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // DRAW BOX BACKGROUND
        box.draw(canvas);

        // MOVE AND DRAW BALLS
        for (Ball b : balls) {
            b.draw(canvas);
            b.moveWithCollisionDetection(box);
        }

        // MOVE AND DRAW SQUARES
        for (Square s : squares) {
            s.draw(canvas);
            s.moveWithCollisionDetection(box);
        }

        // DRAW RECTANGLE TARGET
        rectTarget.draw(canvas);

        // QUESTION 6: CHECK COLLISIONS AND INCREMENT SCORE
        for (Ball b : balls) {
            if (rectTarget.collidesWith(b)) {
                score++;
                Log.d("SCORE", "Current Score: " + score);
            }
        }

        for (Square s : squares) {
            if (rectTarget.collidesWith(s)) {
                score++;
                Log.d("SCORE", "Current Score: " + score);
            }
        }
        // QUESTION 4: KEEPS ANIMATION RUNNING. WILL CRASH WITHOUT
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        float currentY = event.getY();
        float deltaX, deltaY;

        float scalingFactor = 5.0f / ((box.xMax > box.yMax) ? box.yMax : box.xMax);

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            deltaX = currentX - previousX;
            deltaY = currentY - previousY;

            ball_1.speedX += deltaX * scalingFactor;
            ball_1.speedY += deltaY * scalingFactor;

            float speedX = deltaX * scalingFactor;
            float speedY = deltaY * scalingFactor;

            if (Math.abs(speedX) < 2) speedX = (speedX < 0 ? -2 : 2);
            if (Math.abs(speedY) < 2) speedY = (speedY < 0 ? -2 : 2);

            // QUESTION 5: CREATE SQUARE OR BALL BASED ON SWIPE SPEED
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            float speedThreshold = 10; // ADJUST THIS VALUE FOR FAST VS SLOW SWIPE
            if (distance > speedThreshold) {
                squares.add(new Square(getRandomColor(), previousX, previousY, speedX, speedY));
            } else {
                balls.add(new Ball(getRandomColor(), previousX, previousY, speedX, speedY));
            }

            // LIMIT TOTAL SHAPES
            if (balls.size() + squares.size() > 20) {
                balls.clear();
                squares.clear();

                float clearX = rand.nextInt(box.xMax - box.xMin - 100) + box.xMin + 50;
                float clearY = rand.nextInt(box.yMax - box.yMin - 100) + box.yMin + 50;
                float clearSpeedX = rand.nextInt(11) - 5;
                float clearSpeedY = rand.nextInt(11) - 5;
                if (clearSpeedX == 0) clearSpeedX = 3;
                if (clearSpeedY == 0) clearSpeedY = 3;

                balls.add(new Ball(getRandomColor(), clearX, clearY, clearSpeedX, clearSpeedY));
                ball_1 = balls.get(0);
            }
        }

        previousX = currentX;
        previousY = currentY;

        return true;
    }

    // QUESTION 2: BUTTON PRESS CREATES RANDOM BALL
    public void RussButtonPressed() {
        int viewWidth = this.getMeasuredWidth();
        int viewHeight = this.getMeasuredHeight();
        float x = rand.nextInt(viewWidth - 100) + 50;
        float y = rand.nextInt(viewHeight - 100) + 50;
        float dx = rand.nextInt(21) - 10;
        float dy = rand.nextInt(21) - 10;
        if (dx == 0) dx = 3;
        if (dy == 0) dy = 3;
        balls.add(new Ball(getRandomColor(), x, y, dx, dy));
    }
}
