package com.example.dbballintegrationtest;

import android.content.Context;

import java.util.List;

// This class connects (adapts) BallsDbHelper to the BallStore interface
public class SqliteBallStore implements BallStore {

    // Our helper that actually talks to SQLite
    private final BallsDbHelper db;

    // Constructor: we pass in a Context and a DB file name
    public SqliteBallStore(Context ctx, String dbFile) {
        // Make a new BallsDbHelper for this file
        this.db = new BallsDbHelper(ctx, dbFile);
    }

    // Save one ball into the DB
    @Override
    public long addBall(String n, float x, float y, float dx, float dy, int c) {
        // Just call the helper's insertBall method
        return db.insertBall(n, x, y, dx, dy, c);
    }

    // Remove all balls from the DB
    @Override
    public void clearAll() {
        db.deleteAll();
    }

    // Get all balls from the DB
    @Override
    public List<BallsDbHelper.StoredBall> getAll() {
        return db.getAll();
    }
}
