package com.example.dbballintegrationtest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Our custom view that draws and animates the balls
    private BouncingBallView canvas;

    // Our DB “store” for saving/loading balls
    private BallStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use the layout file activity_main.xml
        setContentView(R.layout.activity_main);  // make sure this layout exists

        // Find the BouncingBallView in the layout by its ID
        canvas = findViewById(R.id.canvas);

        // Create the real DB store using one main DB file
        store = new SqliteBallStore(this, "balls_main.db");

        // ----- Reload any balls that were saved in the DB -----
        List<BallsDbHelper.StoredBall> saved = store.getAll();
        for (BallsDbHelper.StoredBall sb : saved) {
            // For each saved row, make a Ball object and add it to the view
            canvas.addBall(new Ball(sb.name, sb.color, sb.x, sb.y, sb.dx, sb.dy));
        }

        // ----- Add button: save a new ball + show it -----
        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            // 1) Save a new ball into the DB
            store.addBall("Ball", 200f, 200f, 4f, 3f, 0xFFFF0000);
            // 2) Add the same ball into the view so it appears and moves
            canvas.addBall(new Ball("Ball", 0xFFFF0000, 200f, 200f, 4f, 3f));
        });

        // ----- Clear button: remove everything -----
        findViewById(R.id.btnClear).setOnClickListener(v -> {
            // Clear all balls from the DB
            store.clearAll();
            // Clear all balls from the view
            canvas.clearAll();
        });
    }
}
