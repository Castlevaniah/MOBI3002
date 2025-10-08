package com.codelab.basics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// This class actually DOES the database work.
// - It creates the table the first time (onCreate)
// - It upgrades the table when version changes (onUpgrade)
// - It implements all the methods from DB_Interface
public class DBClass extends SQLiteOpenHelper implements DB_Interface {

    public static final String TAG = "DBClass";
    public static final int DATABASE_VERSION = 4;  // if I change columns/seed, I bump this number
    public static final String DATABASE_NAME = "DB_Name.db";

    // Table + column names (keeping all strings in one place so I don’t typo them later)
    private static final String TABLE_NAME = "pokemon";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_NUM = "number";
    private static final String COL_POWER = "power_level";
    private static final String COL_DESC = "description";
    private static final String COL_ACCESS = "access_count";

    // SQL to create the table (runs once when DB is first made)
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + // auto id
                    COL_NAME + " TEXT, " +
                    COL_NUM + " INTEGER, " +
                    COL_POWER + " INTEGER, " +
                    COL_DESC + " TEXT, " +
                    COL_ACCESS + " INTEGER DEFAULT 0" +                        // start access at 0
                    ")";

    // SQL to drop the table (used during upgrade, simple approach)
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    // pass context + DB name + version up to SQLiteOpenHelper
    public DBClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called the very first time the DB is created on the device
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate(): " + SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);

        // Seed the table with at least 6 Pokémon so my app has data to show
        // Note: first insert lists columns; others rely on order (NULL for id = auto)
        db.execSQL("INSERT INTO " + TABLE_NAME +
                "(" + COL_NAME + "," + COL_NUM + "," + COL_POWER + "," + COL_DESC + "," + COL_ACCESS + ") VALUES " +
                "('Pikachu',25,320,'Electric mouse Pokémon. Loves berries and thunder.',0)");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (NULL,'Charizard',6,534,'Spits fire that is hot enough to melt boulders.',0)");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (NULL,'Bulbasaur',1,318,'A strange seed was planted on its back at birth.',0)");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (NULL,'Squirtle',7,314,'Shoots water at prey while in the water.',0)");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (NULL,'Gengar',94,500,'It hides in shadows. It is said to occur on moonless nights.',0)");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (NULL,'Mewtwo',150,680,'Created by a scientist after years of gene splicing and DNA engineering experiments.',0)");
    }

    // Called when DATABASE_VERSION goes up (my simple strategy: drop + recreate)
    // ⚠ This wipes old data. Fine for class demos, but not for real apps.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        Log.d(TAG, "onUpgrade() " + oldV + "→" + newV);
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    // ---------- DB_Interface methods below ----------

    // COUNT(*) query to see how many rows we have
    @Override
    public int count() {
        Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
            return c.moveToFirst() ? c.getInt(0) : 0; // if there is a row, take the first int
        } finally {
            if (c != null) c.close(); // always close cursors
        }
    }

    // Insert one new row using ContentValues
    // Return 1 if insert worked, else 0
    @Override
    public int save(DataModel m) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NAME, m.getName());
        v.put(COL_NUM, m.getNumber());
        v.put(COL_POWER, m.getPowerLevel());
        v.put(COL_DESC, m.getDescription());
        v.put(COL_ACCESS, m.getAccessCount() == null ? 0 : m.getAccessCount());
        long rowId = db.insert(TABLE_NAME, null, v); // rowId > 0 means success
        Log.v(TAG, "save rowId=" + rowId + " " + m);
        return rowId > 0 ? 1 : 0;
    }

    // Update a row matched by its id
    // Returns how many rows were changed (should be 1 if success)
    @Override
    public int update(DataModel m) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NAME, m.getName());
        v.put(COL_NUM, m.getNumber());
        v.put(COL_POWER, m.getPowerLevel());
        v.put(COL_DESC, m.getDescription());
        v.put(COL_ACCESS, m.getAccessCount());
        int rows = db.update(
                TABLE_NAME,
                v,
                COL_ID + "=?",
                new String[]{String.valueOf(m.getId())} // where id = ?
        );
        Log.v(TAG, "update id=" + m.getId() + " rows=" + rows);
        return rows;
    }

    // Delete by id. Return number of rows deleted.
    @Override
    public int deleteById(Long id) {
        int rows = getWritableDatabase().delete(
                TABLE_NAME,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        Log.v(TAG, "deleteById id=" + id + " rows=" + rows);
        return rows;
    }

    // Get all rows in id ASC order and map each Cursor row -> DataModel
    @Override
    public List<DataModel> findAll() {
        List<DataModel> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = getReadableDatabase().query(
                    TABLE_NAME,
                    new String[]{COL_ID, COL_NAME, COL_NUM, COL_POWER, COL_DESC, COL_ACCESS},
                    null, null, null, null,
                    COL_ID + " ASC"
            );
            while (c.moveToNext()) {
                list.add(new DataModel(
                        c.getLong(0),   // id
                        c.getString(1), // name
                        c.getInt(2),    // number
                        c.getInt(3),    // power
                        c.getString(4), // description
                        c.getInt(5)     // access_count
                ));
            }
        } finally {
            if (c != null) c.close();
        }
        Log.v(TAG, "findAll: " + list.size() + " rows");
        return list;
    }

    // Return just the name for a given id (or null if not found)
    @Override
    public String getNameById(Long id) {
        Cursor c = null;
        try {
            c = getReadableDatabase().query(
                    TABLE_NAME,
                    new String[]{COL_NAME},
                    COL_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null,
                    "1" // limit 1
            );
            if (c.moveToFirst()) return c.getString(0);
            return null;
        } finally {
            if (c != null) c.close();
        }
    }

    // Get the Pokémon with the highest power_level (just a bonus helper)
    @Override
    public DataModel getMax() {
        Cursor c = null;
        try {
            c = getReadableDatabase().query(
                    TABLE_NAME,
                    new String[]{COL_ID, COL_NAME, COL_NUM, COL_POWER, COL_DESC, COL_ACCESS},
                    null, null, null, null,
                    COL_POWER + " DESC, " + COL_ID + " DESC", // sort by power desc, tie-break by id desc
                    "1" // limit 1
            );
            if (c.moveToFirst()) {
                return new DataModel(
                        c.getLong(0),
                        c.getString(1),
                        c.getInt(2),
                        c.getInt(3),
                        c.getString(4),
                        c.getInt(5)
                );
            }
            return null;
        } finally {
            if (c != null) c.close();
        }
    }

    // Add 1 to the access_count for a specific id (used when user views details)
    @Override
    public void incAccessCount(long id) {
        // simple UPDATE statement; id comes from app code, so it's safe here
        // (could also do db.update with ContentValues if I want to be super consistent)
        String sql = "UPDATE " + TABLE_NAME + " SET " + COL_ACCESS + " = " + COL_ACCESS + " + 1 WHERE " + COL_ID + "=" + id;
        getWritableDatabase().execSQL(sql);
        Log.d(TAG, "incAccessCount id=" + id);
    }

    // Get the id of the Pokémon with the highest access_count (our "favorite")
    // If table empty, return 0 to signal "none"
    @Override
    public long getMostAccessed() {
        Cursor c = null;
        try {
            c = getReadableDatabase().query(
                    TABLE_NAME,
                    new String[]{COL_ID},
                    null, null, null, null,
                    COL_ACCESS + " DESC, " + COL_ID + " ASC", // highest access first; stable order
                    "1"
            );
            return c.moveToFirst() ? c.getLong(0) : 0L;
        } finally {
            if (c != null) c.close();
        }
    }

    // Fetch one full row by id (used to show the Favorite card or details screen)
    @Override
    public DataModel getById(long id) {
        Cursor c = null;
        try {
            c = getReadableDatabase().query(
                    TABLE_NAME,
                    new String[]{COL_ID, COL_NAME, COL_NUM, COL_POWER, COL_DESC, COL_ACCESS},
                    COL_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null,
                    "1"
            );
            if (c.moveToFirst()) {
                return new DataModel(
                        c.getLong(0),
                        c.getString(1),
                        c.getInt(2),
                        c.getInt(3),
                        c.getString(4),
                        c.getInt(5)
                );
            }
            return null;
        } finally {
            if (c != null) c.close();
        }
    }

    // Handy for debugging: print everything to Logcat so I can see what’s in the table
    private void dump() {
        List<DataModel> rows = findAll();
        Log.d(TAG, "---- DUMP (" + rows.size() + " rows) ----");
        for (DataModel m : rows) Log.d(TAG, m.toString());
        Log.d(TAG, "------------------------------");
    }
}
