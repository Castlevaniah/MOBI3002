package com.example.dbballintegrationtest;

import java.util.List;

// This interface is how we talk to our database
public interface BallStore {

    // Add one ball to the store (DB).
    // Returns the new row id.
    long addBall(String name, float x, float y, float dx, float dy, int color);

    // Delete all balls from the store.
    void clearAll();

    // Get all balls from the store as a list.
    List<BallsDbHelper.StoredBall> getAll();
}
