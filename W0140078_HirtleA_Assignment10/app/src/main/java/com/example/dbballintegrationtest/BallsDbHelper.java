package com.example.dbballintegrationtest;

// Android classes for SQLite and logging
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// Small helper class to manage the balls SQLite DB
public class BallsDbHelper extends SQLiteOpenHelper {

    // Tag for Logcat messages
    public static final String TAG = "BallsDb";

    // DB version (change this if you change the schema)
    private static final int DB_VERSION = 1;

    // The actual file name of the DB
    private final String dbName;

    // Table and column names
    public static final String TBL = "balls";
    public static final String COL_ID = "_id",
            COL_NAME = "name",
            COL_X = "x",
            COL_Y = "y",
            COL_DX = "dx",
            COL_DY = "dy",
            COL_COLOR = "color";

    // SQL to create the table
    private static final String SQL_CREATE =
            "CREATE TABLE " + TBL + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_NAME + " TEXT NOT NULL," +
                    COL_X + " REAL NOT NULL," +
                    COL_Y + " REAL NOT NULL," +
                    COL_DX + " REAL NOT NULL," +
                    COL_DY + " REAL NOT NULL," +
                    COL_COLOR + " INTEGER NOT NULL" +
                    ");";

    // Default constructor: use "balls.db" as file name
    public BallsDbHelper(Context ctx) {
        this(ctx, "balls.db");
    }

    // Constructor that lets us choose the DB file name
    public BallsDbHelper(Context ctx, String dbName) {
        // Call parent constructor with context, name, and version
        super(ctx, dbName, null, DB_VERSION);
        this.dbName = dbName;
    }

    // Called the first time the DB is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);  // run the CREATE TABLE command
    }

    // Called when DB_VERSION changes (upgrade logic)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // For now: drop old table and recreate (simple approach)
        db.execSQL("DROP TABLE IF EXISTS " + TBL);
        onCreate(db);
    }

    // Insert one ball into the table
    public long insertBall(String name, float x, float y, float dx, float dy, int color) {
        // ContentValues is like a map<column, value>
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_X, x);
        cv.put(COL_Y, y);
        cv.put(COL_DX, dx);
        cv.put(COL_DY, dy);
        cv.put(COL_COLOR, color);

        // Insert row and get back its new ID
        long id = getWritableDatabase().insert(TBL, null, cv);

        // Log what we just inserted (useful for debugging)
        Log.d(TAG, "insert(" + dbName + "): " + name + " x=" + x + " y=" + y +
                " dx=" + dx + " dy=" + dy + " color=" + color + " -> id=" + id);

        return id;
    }

    // Read all balls from the table
    public List<StoredBall> getAll() {
        ArrayList<StoredBall> list = new ArrayList<>();

        // Query the table for all rows, ordered by id ASC
        Cursor c = getReadableDatabase().query(
                TBL,       // table
                null,      // all columns
                null, null,// no WHERE
                null, null,// no GROUP BY / HAVING
                COL_ID + " ASC" // ORDER BY _id ASC
        );

        try {
            // Move through each row
            while (c.moveToNext()) {
                // Build a StoredBall from the current row
                list.add(new StoredBall(
                        c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_X)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_Y)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_DX)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_DY)),
                        c.getInt(c.getColumnIndexOrThrow(COL_COLOR))
                ));
            }
        } finally {
            // Always close the cursor when done
            c.close();
        }

        // Log how many balls we read
        Log.d(TAG, "getAll(" + dbName + "): count=" + list.size());

        return list;
    }

    // Delete all rows from the table
    public int deleteAll() {
        int rows = getWritableDatabase().delete(TBL, null, null);
        Log.d(TAG, "deleteAll(" + dbName + "): rows=" + rows);
        return rows;
    }

    // Simple holder class for one row in the table
    public static class StoredBall {
        public final long id;
        public final String name;
        public final float x, y, dx, dy;
        public final int color;

        public StoredBall(long id, String name, float x, float y,
                          float dx, float dy, int color) {
            this.id = id;       // DB row ID
            this.name = name;   // ball name
            this.x = x;         // position x
            this.y = y;         // position y
            this.dx = dx;       // velocity x
            this.dy = dy;       // velocity y
            this.color = color; // color as int (ARGB)
        }
    }
}
