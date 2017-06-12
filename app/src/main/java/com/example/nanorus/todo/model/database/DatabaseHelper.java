package com.example.nanorus.todo.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "todoDatabase.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.DatabaseEntry.SQL_CREATE_TABLE_NOTES);
        db.execSQL(DatabaseContract.DatabaseEntry.SQL_INSERT_FIRST_ENTRY_INTO_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2) {
            String sql_add_column =
                    "ALTER TABLE " + DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES + " ADD COLUMN " +
                            DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY + " INTEGER";

            String sql_insert_priority =
                    "INSERT INTO " + DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES +
                            " (" + DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY + ") VALUES (1)";


            db.execSQL(sql_add_column);
            db.execSQL(sql_insert_priority);
        } else if (newVersion == 5) {

            db.execSQL("DROP TABLE " + DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES);

            onCreate(db);
        }


        // WIPE SAMPLE
        /*
            db.execSQL(DatabaseContract.DatabaseEntry.SQL_DELETE_TABLE_NOTES);
            onCreate(db);
         */
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        if (newVersion == 2) {
            String sql_add_column =
                    "ALTER TABLE " + DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES + " ADD COLUMN " +
                            DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY + " INTEGER";

            String sql_insert_priority =
                    "INSERT INTO " + DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES +
                            " (" + DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY + ") VALUES (1)";


            db.execSQL(sql_add_column);
            db.execSQL(sql_insert_priority);
        }


    }
}
