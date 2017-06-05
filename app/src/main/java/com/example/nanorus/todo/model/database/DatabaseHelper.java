package com.example.nanorus.todo.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todoDatabase.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.DatabaseEntry.SQL_CREATE_TABLE_NOTES);
        db.execSQL(DatabaseContract.DatabaseEntry.SQL_INSERT_FIRST_ENTRY_INTO_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.DatabaseEntry.SQL_DELETE_TABLE_NOTES);
        onCreate(db);
    }
}
