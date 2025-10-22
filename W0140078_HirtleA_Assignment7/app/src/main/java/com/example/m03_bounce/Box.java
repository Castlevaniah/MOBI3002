package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

// solid background rectangle that defines the play area bounds
public class Box {

    // inclusive edges the shapes bounce against
    int xMin, xMax, yMin, yMax;

    private Paint paint;  // fill color
    private Rect bounds;  // reused rect for drawing

    public Box(int color) {
        paint = new Paint();
        paint.setColor(color);
        bounds = new Rect();
    }

    // set edges from top-left and width/height
    public void set(int x, int y, int width, int height) {
        xMin = x;
        xMax = x + width - 1;
        yMin = y;
        yMax = y + height - 1;
        bounds.set(xMin, yMin, xMax, yMax);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(bounds, paint);
    }
}
