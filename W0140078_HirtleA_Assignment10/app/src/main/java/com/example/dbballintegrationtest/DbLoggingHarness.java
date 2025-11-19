package com.example.dbballintegrationtest;

import android.content.Context;
import android.util.Log;

import java.util.Locale;

public class DbLoggingHarness {

    // Helper method that runs a full test ("proof") on one DB file
    public static void run(Context ctx, String dbFile) {

        // Use our BallStore with a specific SQLite DB file name
        BallStore s = new SqliteBallStore(ctx, dbFile);

        // Log that we are starting the proof for this file
        Log.d("BallDB-Proof", "=== START === file=" + dbFile);

        // Make sure the DB is empty before we start
        s.clearAll();

        // Add 3 balls with known values
        s.addBall("Red",   10f, 20f,  1.5f,  2.5f, 0xFFFF0000);
        s.addBall("Green", 30f, 40f, -0.5f,  4.0f, 0xFF00FF00);
        s.addBall("Blue",  50f, 60f,  3.2f, -1.0f, 0xFF0000FF);

        // Read all rows back and log their exact values
        for (BallsDbHelper.StoredBall b : s.getAll()) {
            // String.format builds a nice log line with all fields
            Log.d("BallDB-Proof", String.format(Locale.US,
                    "Row{id=%d name=%s x=%.2f y=%.2f dx=%.2f dy=%.2f color=%08X}",
                    b.id, b.name, b.x, b.y, b.dx, b.dy, b.color));
        }

        // Clear the DB at the end so the file is empty again
        s.clearAll();

        // Log that we are done with this proof run
        Log.d("BallDB-Proof", "=== END === file=" + dbFile);
    }
}
