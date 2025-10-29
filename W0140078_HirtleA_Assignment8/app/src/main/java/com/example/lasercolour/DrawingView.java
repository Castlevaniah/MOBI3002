package com.example.lasercolour; // package name for this app module

import android.annotation.SuppressLint;
import android.content.Context;             // needed for custom View constructors
import android.graphics.BlurMaskFilter;     // gives us the glow/blur effect
import android.graphics.Canvas;             // where we draw
import android.graphics.Paint;              // brush settings (color, width, style)
import android.graphics.Path;               // path = the line we draw as the finger moves
import android.util.AttributeSet;           // XML attributes for Views
import android.view.MotionEvent;            // touch events (down/move/up)
import android.view.View;                   // base class for custom views

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    // One finished stroke: path + color + width.
    // We keep these so drawings stay on screen after finger lifts.
    private static class Stroke {
        final Path path;    // the actual shape/line
        final int color;    // ARGB color used
        final float width;  // stroke width used
        Stroke(Path p, int c, float w) { this.path = p; this.color = c; this.width = w; }
    }

    // All completed strokes in drawing order
    private final List<Stroke> strokes = new ArrayList<>();
    // The path currently being drawn (finger is down and moving)
    private Path currentPath;

    // Current brush settings (what new lines will use)
    private int currentColor = 0xFFFFFFFF; // default = white (nice glow on dark/blue bg)
    private float currentStrokeWidth = 12f;

    // Glow/halo settings for that neon look
    private final float glowRadius = 24f;   // how soft/wide the blur is
    private final float glowScale  = 2.2f;  // glow line is thicker than the core line

    // --- Required constructors for custom Views (used by code and XML) ---
    public DrawingView(Context context) {
        super(context); init(); // called when created in code
    }
    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs); init(); // called when inflated from XML
    }
    public DrawingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); init(); // XML + style
    }

    private void init() {
        // Important: enable software rendering so BlurMaskFilter works for the glow.
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        // Tell View we *will* draw (avoid some optimizations)
        setWillNotDraw(false);
    }

    // Create the "core" paint (the sharp inner line)
    private Paint makeCorePaint(int color, float width) {
        Paint p = new Paint();
        p.setColor(color);                         // exact color requested
        p.setAntiAlias(true);                      // smooth edges
        p.setStyle(Paint.Style.STROKE);            // draw as a line (not filled shape)
        p.setStrokeJoin(Paint.Join.ROUND);         // round corners
        p.setStrokeCap(Paint.Cap.ROUND);           // round line ends
        p.setStrokeWidth(width);                   // thickness
        return p;
    }

    // Create the "glow" paint (the soft outer halo under the core line)
    private Paint makeGlowPaint(int color, float width) {
        Paint p = new Paint();

        // Make the glow slightly transparent (80% of original alpha)
        int a = (color >>> 24);                    // pull alpha from ARGB
        int rgb = color & 0x00FFFFFF;              // keep RGB as-is
        int glowColor = ((int)(a * 0.8f) << 24) | rgb;

        p.setColor(glowColor);                     // use softened alpha
        p.setAntiAlias(true);                      // smooth edges
        p.setStyle(Paint.Style.STROKE);            // still a line
        p.setStrokeJoin(Paint.Join.ROUND);         // round corners
        p.setStrokeCap(Paint.Cap.ROUND);           // round ends
        p.setStrokeWidth(width * glowScale);       // make halo wider than core
        p.setMaskFilter(new BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.NORMAL)); // add blur
        return p;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1) Draw all finished strokes in order:
        for (Stroke s : strokes) {
            // draw glow first (underneath) so the core sits on top
            canvas.drawPath(s.path, makeGlowPaint(s.color, s.width));
            // draw the sharp core line
            canvas.drawPath(s.path, makeCorePaint(s.color, s.width));
        }

        // 2) Draw the current in-progress path (if finger is down)
        if (currentPath != null) {
            canvas.drawPath(currentPath, makeGlowPaint(currentColor, currentStrokeWidth));
            canvas.drawPath(currentPath, makeCorePaint(currentColor, currentStrokeWidth));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // Get touch coordinates
        float x = e.getX();
        float y = e.getY();

        // Handle touch actions
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Start a new path where the finger touched
                currentPath = new Path();
                currentPath.moveTo(x, y);
                invalidate();      // request redraw
                return true;       // we handled it

            case MotionEvent.ACTION_MOVE:
                // Extend the path as the finger moves
                if (currentPath != null) currentPath.lineTo(x, y);
                invalidate();      // update the screen
                return true;

            case MotionEvent.ACTION_UP:
                // Finger lifted: finalize this stroke and store it
                if (currentPath != null) {
                    Path done = new Path(currentPath); // copy so we don't mutate later
                    strokes.add(new Stroke(done, currentColor, currentStrokeWidth));
                    currentPath = null;                // reset for the next stroke
                }
                invalidate();      // redraw with the new stroke saved
                return true;

            default:
                return false;      // not handling other actions
        }
    }

    // Clear all drawings (leave background image alone)
    public void clear() {
        strokes.clear();   // remove stored strokes
        currentPath = null;
        invalidate();      // refresh the view
    }

    // Change brush color for future strokes
    public void setStrokeColor(int color) {
        this.currentColor = color;
        invalidate(); // if drawing now, show the new color on the live path
    }

    // Change brush width for future strokes
    public void setStrokeWidth(float width) {
        this.currentStrokeWidth = width;
        invalidate(); // refresh if needed
    }
}
