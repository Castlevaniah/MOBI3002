package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

// one moving ball (position, velocity, size, color) + draw + bounce + name label
public class Ball {

    public String name = "ball";   // label for hovering text
    public float radius = 50;      // size
    public float x, y;             // center position
    public float speedX, speedY;   // velocity

    private Paint paint;           // fill for the ball
    private RectF bounds;          // reused oval bounds

    // paints for text: stroke (outline) + fill (inner)
    private final Paint textStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textFill   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect  textBounds = new Rect();

    public boolean hasCollided = false;

    // constructor with name (used for GUI + DB loads)
    public Ball(String name, int color, float x, float y, float speedX, float speedY) {
        this.name = name;
        this.x = x; this.y = y;
        this.speedX = speedX; this.speedY = speedY;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);

        // set up label paints once
        // text size scales with ball size; tweak 0.9f for your taste
        float textSize = radius * 0.9f;
        textFill.setTextSize(textSize);
        textFill.setColor(0xFFFFFFFF);          // white fill for readability
        textFill.setFakeBoldText(true);

        textStroke.setTextSize(textSize);
        textStroke.setStyle(Paint.Style.STROKE); // thin outline so text pops on any background
        textStroke.setStrokeWidth(3f);
        textStroke.setColor(0xFF111111);

        bounds = new RectF();
    }

    public void moveWithCollisionDetection(Box box) {
        x += speedX;
        y += speedY;

        if (x + radius > box.xMax || x - radius < box.xMin) {
            speedX = -speedX;
        }
        if (y + radius > box.yMax || y - radius < box.yMin) {
            speedY = -speedY;
        }

        x = Math.max(box.xMin + radius, Math.min(x, box.xMax - radius));
        y = Math.max(box.yMin + radius, Math.min(y, box.yMax - radius));
    }

    public void draw(Canvas canvas) {
        // draw the ball
        bounds.set(x - radius, y - radius, x + radius, y + radius);
        canvas.drawOval(bounds, paint);

        // measure text so we can center it horizontally
        String label = name == null || name.isEmpty() ? "ball" : name;
        textFill.getTextBounds(label, 0, label.length(), textBounds);

        // compute baseline: a little above the ball (radius + small gap)
        float textX = x - (textBounds.width() / 2f);
        float gap   = radius * 0.35f;
        float textY = y - radius - gap;

        // stroke first (outline), then fill (inside)
        canvas.drawText(label, textX, textY, textStroke);
        canvas.drawText(label, textX, textY, textFill);
    }
}
