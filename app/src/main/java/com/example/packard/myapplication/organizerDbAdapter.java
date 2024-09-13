package com.example.packard.myapplication;

//import com.example.packard.myapplication.model.oganizerzadanie;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class organizerDbAdapter{
    private static final String DEBUG_TAG = "SqLiteoganizerManager";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database.db";
    private static final String DB_oganizer_TABLE = "oganizer";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_DESCRIPTION = "description";
    public static final String DESCRIPTION_OPTIONS = "TEXT NOT NULL";
    public static final int DESCRIPTION_COLUMN = 1;
    public static final String KEY_COMPLETED = "completed";
    public static final String COMPLETED_OPTIONS = "INTEGER DEFAULT 0";
    public static final int COMPLETED_COLUMN = 2;

    private static final String DB_CREATE_oganizer_TABLE =
            "CREATE TABLE " + DB_oganizer_TABLE + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_DESCRIPTION + " " + DESCRIPTION_OPTIONS + ", " +
                    KEY_COMPLETED + " " + COMPLETED_OPTIONS +
                    ");";
    private static final String DROP_oganizer_TABLE =
            "DROP TABLE IF EXISTS " + DB_oganizer_TABLE;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,
                              CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_oganizer_TABLE);

            Log.d(DEBUG_TAG, "Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_oganizer_TABLE + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_oganizer_TABLE);

            Log.d(DEBUG_TAG, "Database updating...");
            Log.d(DEBUG_TAG, "Table " + DB_oganizer_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(db);
        }
    }

    public organizerDbAdapter(Context context) {
        this.context = context;
    }

    public organizerDbAdapter open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertoganizer(String description) {
        ContentValues newoganizerValues = new ContentValues();
        newoganizerValues.put(KEY_DESCRIPTION, description);
        return db.insert(DB_oganizer_TABLE, null, newoganizerValues);
    }

    public boolean updateoganizer(oganizerzadanie zadanie) {
        long id = zadanie.getId();
        String description = zadanie.getDescription();
        boolean completed = zadanie.isCompleted();
        return updateoganizer(id, description, completed);
    }

    public boolean updateoganizer(long id, String description, boolean completed) {
        String where = KEY_ID + "=" + id;
        int completedzadanie = completed ? 1 : 0;
        ContentValues updateoganizerValues = new ContentValues();
        updateoganizerValues.put(KEY_DESCRIPTION, description);
        updateoganizerValues.put(KEY_COMPLETED, completedzadanie);
        return db.update(DB_oganizer_TABLE, updateoganizerValues, where, null) > 0;
    }

    public boolean deleteoganizer(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(DB_oganizer_TABLE, where, null) > 0;
    }

    public Cursor getAlloganizers() {
        String[] columns = {KEY_ID, KEY_DESCRIPTION, KEY_COMPLETED};
        return db.query(DB_oganizer_TABLE, columns, null, null, null, null, null);
    }

    public oganizerzadanie getoganizer(long id) {
        String[] columns = {KEY_ID, KEY_DESCRIPTION, KEY_COMPLETED};
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(DB_oganizer_TABLE, columns, where, null, null, null, null);
        oganizerzadanie zadanie = null;
        if(cursor != null && cursor.moveToFirst()) {
            String description = cursor.getString(DESCRIPTION_COLUMN);
            boolean completed = cursor.getInt(COMPLETED_COLUMN) > 0 ? true : false;
            zadanie = new oganizerzadanie(id, description, completed);
        }
        return zadanie;
    }
}