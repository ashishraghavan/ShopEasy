package com.shopping.shopeasy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ShopDBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private static final String TAG = ShopDBHelper.class.getSimpleName();

    // Table Names
    public static final String AUTH_TOKEN = "auth_token";

    public static final String AUTH_TOKEN_ID = "_id";
    public static final String USER_EMAIL = "email";
    public static final String TIME_STAMP = "timestamp";
    public static final String TOKEN_TYPE = "token_type";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String EXPIRES = "expires";
    public static final String EXTRAS = "extras";

    // Table Create Statements
    private static final String CREATE_TOKEN_TABLE = "CREATE TABLE IF NOT EXISTS "
            + AUTH_TOKEN
            + "("
            + AUTH_TOKEN_ID
            + " INTEGER PRIMARY KEY,"
            + ACCESS_TOKEN
            + " TEXT NOT NULL,"
            + TIME_STAMP
            + " TEXT,"
            + TOKEN_TYPE
            + " TEXT,"
            + REFRESH_TOKEN
            + " TEXT,"
            + EXPIRES
            + " TEXT,"
            + EXTRAS
            + " TEXT,"
            + USER_EMAIL
            + " TEXT " + ")";

    private static final String DELETE_TOKEN_TABLE =
            "DROP TABLE IF EXISTS " + AUTH_TOKEN;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public ShopDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ShopDBHelper(Context context) {
        super(context, AUTH_TOKEN, null,1);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG,"Created database with version "+db.getVersion());
        db.execSQL(CREATE_TOKEN_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG,"Upgraded db from "+oldVersion+ " to "+newVersion);
        this.db = db;
        // on upgrade drop older tables
        db.execSQL(DELETE_TOKEN_TABLE);
        // create new tables
        onCreate(db);
    }

}
