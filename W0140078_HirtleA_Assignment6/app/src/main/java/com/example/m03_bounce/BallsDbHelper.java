package com.example.m03_bounce;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

// SQLite helper for saving/loading balls so they persist between runs.
public class BallsDbHelper extends SQLiteOpenHelper {

    public static final String TAG = "BallsDb";
    private static final String DB_NAME = "balls.db";
    private static final int DB_VERSION = 1;

    // table + columns
    public static final String TBL = "balls";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_X = "x";
    public static final String COL_Y = "y";
    public static final String COL_DX = "dx";
    public static final String COL_DY = "dy";
    public static final String COL_COLOR = "color";

    // simple schema: one row per ball
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

    public BallsDbHelper(Context ctx) { super(ctx, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREATE); }

    // if schema changes later, drop + recreate (for this assignment it's fine)
    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL);
        onCreate(db);
    }

    // insert one ball row
    public long insertBall(String name, float x, float y, float dx, float dy, int color) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_X, x);
        cv.put(COL_Y, y);
        cv.put(COL_DX, dx);
        cv.put(COL_DY, dy);
        cv.put(COL_COLOR, color);
        long id = db.insert(TBL, null, cv);
        Log.d(TAG, "insertBall(): " + name + " x=" + x + " y=" + y + " dx=" + dx + " dy=" + dy + " color=" + color + " -> id=" + id);
        return id;
    }

    // read all rows back in insertion order
    public List<StoredBall> getAllBalls() {
        ArrayList<StoredBall> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TBL, null, null, null, null, null, COL_ID + " ASC");
        try {
            while (c.moveToNext()) {
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
        } finally { c.close(); }
        Log.d(TAG, "getAllBalls(): count=" + list.size());
        return list;
    }

    // remove everything (used by Clear)
    public int deleteAll() {
        int rows = getWritableDatabase().delete(TBL, null, null);
        Log.d(TAG, "deleteAll(): rows=" + rows);
        return rows;
    }

    // simple holder for one DB row
    public static class StoredBall {
        public final long id;
        public final String name;
        public final float x, y, dx, dy;
        public final int color;

        public StoredBall(long id, String name, float x, float y, float dx, float dy, int color) {
            this.id = id; this.name = name; this.x = x; this.y = y; this.dx = dx; this.dy = dy; this.color = color;
        }
    }
}
