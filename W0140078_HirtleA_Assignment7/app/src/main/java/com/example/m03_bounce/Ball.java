package com.example.m03_bounce;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

// one moving ball (position, velocity, size, color) + draw + bounce + name label
public class Ball {

    public String name = "ball";   // label shown above the ball
    public float radius = 50;      // ball size
    public float x, y;             // center position
    public float speedX, speedY;   // velocity (px/s)

    private Paint paint;           // fill for the ball
    private RectF bounds;          // reused oval bounds

    // paints for text: stroke (outline) + fill (inner)
    private final Paint textStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textFill   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect  textBounds = new Rect();

    // constructor with name (used for GUI + DB loads)
    public Ball(String name, int color, float x, float y, float speedX, float speedY) {
        this.name = name;
        this.x = x; this.y = y;
        this.speedX = speedX; this.speedY = speedY;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);

        // set up label paints once
        float textSize = radius * 0.9f;  // simple scale with ball
        textFill.setTextSize(textSize);
        textFill.setColor(0xFFFFFFFF);   // white fill for readability
        textFill.setFakeBoldText(true);

        textStroke.setTextSize(textSize);
        textStroke.setStyle(Paint.Style.STROKE);
        textStroke.setStrokeWidth(3f);
        textStroke.setColor(0xFF111111);

        bounds = new RectF();
    }

    // move the ball and bounce off the box walls
    public void moveWithCollisionDetection(Box box) {
        // update position by velocity
        x += speedX;
        y += speedY;

        // reverse velocity when hitting walls (simple bounce)
        if (x + radius > box.xMax || x - radius < box.xMin) {
            speedX = -speedX;
        }
        if (y + radius > box.yMax || y - radius < box.yMin) {
            speedY = -speedY;
        }

        // clamp inside the box just in case
        x = Math.max(box.xMin + radius, Math.min(x, box.xMax - radius));
        y = Math.max(box.yMin + radius, Math.min(y, box.yMax - radius));
    }

    // draw the ball and its name label
    public void draw(Canvas canvas) {
        // draw the ball
        bounds.set(x - radius, y - radius, x + radius, y + radius);
        canvas.drawOval(bounds, paint);

        // pick label
        String label = (name == null || name.isEmpty()) ? "ball" : name;

        // measure text width so we can center it
        textFill.getTextBounds(label, 0, label.length(), textBounds);

        // place label a bit above the ball
        float textX = x - (textBounds.width() / 2f);
        float gap   = radius * 0.35f;
        float textY = y - radius - gap;

        // outline then fill so it pops on any background
        canvas.drawText(label, textX, textY, textStroke);
        canvas.drawText(label, textX, textY, textFill);
    }
}
