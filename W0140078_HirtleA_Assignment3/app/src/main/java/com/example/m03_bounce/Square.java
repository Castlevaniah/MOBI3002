package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Square {

    // QUESTION 5: SQUARE CLASS
    // THIS CLASS REPRESENTS A BOUNCING SQUARE IN THE VIEW.

    float size = 100;      // SIZE OF THE SQUARE
    float x, y;            // CENTER POSITION OF THE SQUARE
    float speedX, speedY;  // VELOCITY IN X AND Y DIRECTIONS
    private Paint paint;    // PAINT OBJECT TO DEFINE COLOR/STYLE
    private RectF bounds;  // RECTANGLE USED FOR DRAWING

    // CONSTRUCTOR
    public Square(int color, float x, float y, float speedX, float speedY) {

        // QUESTION 5: INITIALIZE SQUARE PROPERTIES
        // SET THE CENTER POSITION
        this.x = x;
        this.y = y;

        // SET THE SPEED
        this.speedX = speedX;
        this.speedY = speedY;

        // CREATE PAINT OBJECT AND SET COLOR
        paint = new Paint();
        paint.setColor(color);

        // CREATE RECTANGLE BOUNDS USED FOR DRAWING
        bounds = new RectF();
    }

    // MOVE THE SQUARE WITH COLLISION DETECTION
    public void moveWithCollisionDetection(Box box) {

        // QUESTION 5: UPDATE POSITION BASED ON SPEED
        x += speedX;
        y += speedY;

        // QUESTION 5: COLLISION DETECTION
        // IF SQUARE HITS THE RIGHT EDGE, BOUNCE BACK
        if (x + size / 2 > box.xMax) { speedX = -speedX; x = box.xMax - size / 2; }
        // IF SQUARE HITS THE LEFT EDGE, BOUNCE BACK
        else if (x - size / 2 < box.xMin) { speedX = -speedX; x = box.xMin + size / 2; }

        // IF SQUARE HITS THE BOTTOM EDGE, BOUNCE BACK
        if (y + size / 2 > box.yMax) { speedY = -speedY; y = box.yMax - size / 2; }
        // IF SQUARE HITS THE TOP EDGE, BOUNCE BACK
        else if (y - size / 2 < box.yMin) { speedY = -speedY; y = box.yMin + size / 2; }
    }

    // DRAW THE SQUARE
    public void draw(Canvas canvas) {

        // QUESTION 5: DRAWING THE SQUARE
        // UPDATE THE RECTANGLE BOUNDS BASED ON CURRENT POSITION
        bounds.set(x - size / 2, y - size / 2, x + size / 2, y + size / 2);

        // DRAW THE RECTANGLE ON THE CANVAS USING THE PAINT COLOR
        canvas.drawRect(bounds, paint);
    }
}
