package com.example.dbballintegrationtest;

// Needed to get an app Context for the test
import android.content.Context;

// AndroidX test helpers
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

// JUnit imports
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

// Tell JUnit to run this with the Android test runner
@RunWith(AndroidJUnit4.class)
public class BallDbExactValuesTest {

    // Test #1: make sure saving and loading gives us the exact same values
    @Test
    public void saveAndRetrieve_hasExactValues() {
        // Get the app context for creating the DB
        Context ctx = ApplicationProvider.getApplicationContext();

        // Use a unique DB file name just for this test
        BallStore store = new SqliteBallStore(ctx, "balls_exact_test.db");

        // Start with an empty DB
        store.clearAll();

        // Add one ball with very specific values we can check later
        store.addBall("ExactBall",
                123.45f,   // x
                67.89f,    // y
                -3.21f,    // dx
                4.56f,     // dy
                0xFF123456 // color
        );

        // Read all balls back from the DB
        List<BallsDbHelper.StoredBall> all = store.getAll();

        // We expect exactly ONE ball in the list
        assertEquals(1, all.size());

        // Get that one ball
        BallsDbHelper.StoredBall b = all.get(0);

        // Check that every field matches what we saved
        assertEquals("ExactBall", b.name);
        assertEquals(123.45f, b.x, 0.0001f);   // 3rd value is the allowed float error
        assertEquals(67.89f, b.y, 0.0001f);
        assertEquals(-3.21f, b.dx, 0.0001f);
        assertEquals(4.56f, b.dy, 0.0001f);
        assertEquals(0xFF123456, b.color);
    }

    // Test #2: create two different DB files, each with known balls
    @Test
    public void createMultipleDbFiles_withKnownData() {
        // Get the app context again
        Context ctx = ApplicationProvider.getApplicationContext();

        // ---- DB file A ----
        BallStore storeA = new SqliteBallStore(ctx, "balls_setA.db");
        storeA.clearAll();  // empty it first

        // Add two balls to DB A
        storeA.addBall("A1", 10f, 20f, 1f, 2f, 0xFFFF0000);
        storeA.addBall("A2", 30f, 40f, 3f, 4f, 0xFFFF0000);

        // ---- DB file B ----
        BallStore storeB = new SqliteBallStore(ctx, "balls_setB.db");
        storeB.clearAll();  // empty it first

        // Add one ball to DB B
        storeB.addBall("B1", 50f, 60f, -2f, 5f, 0xFF0000FF);

        // Simple checks: DB A should have 2 balls, DB B should have 1 ball
        assertEquals(2, storeA.getAll().size());
        assertEquals(1, storeB.getAll().size());
    }
}
