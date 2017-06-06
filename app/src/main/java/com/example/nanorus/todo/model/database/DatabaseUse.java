package com.example.nanorus.todo.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.nanorus.todo.model.pojo.NotePojo;

import java.util.ArrayList;
import java.util.List;

public final class DatabaseUse {

    public static void updateNote(NotePojo notePojo, Context context, int id) {
        // UPDATE NOTE SET name,  description
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_NAME, notePojo.getName());
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_DESCRIPTION, notePojo.getDescription());
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY, notePojo.getPriority());

        db.update(DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES, values,
                DatabaseContract.DatabaseEntry.COLUMN_NAME_ID + "=" + id, null);

        Toast.makeText(context, "Note updated", Toast.LENGTH_SHORT).show();
    }

    public static void addNote(NotePojo notePojo, Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_NAME, notePojo.getName());
        cv.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_DESCRIPTION, notePojo.getDescription());
        cv.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY, notePojo.getPriority());

        db.insert(DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES, null, cv);

        Toast.makeText(context, "Note added", Toast.LENGTH_SHORT).show();

    }

    public static void deleteNote(int id, Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        db.delete(DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES,
                DatabaseContract.DatabaseEntry.COLUMN_NAME_ID+"=?",
                new String[]{String.valueOf(id)});
        Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
    }

    public static List<NotePojo> getAllNotes(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor c = db.query(DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES, new String[]{"*"}, null, null, null, null, null);

        ArrayList<NotePojo> notesList = new ArrayList<>();
        if (c.moveToFirst()) {
            notesList.add(new NotePojo(
                    c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_NAME)),
                    null,
                    c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_DESCRIPTION)),
                    c.getInt(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY))
            ));

            while (c.moveToNext()) {
                notesList.add(new NotePojo(
                        c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_NAME)),
                        null,
                        c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_DESCRIPTION)),
                        c.getInt(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY))
                ));
            }

        }


        return notesList;
    }

    public static NotePojo getNote(Context context, int id) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor c = db.query(DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES, new String[]{"*"},
                DatabaseContract.DatabaseEntry.COLUMN_NAME_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (c.moveToFirst()) {
            NotePojo notePojo = new NotePojo(
                    c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_NAME)),
                    null,
                    c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_DESCRIPTION)),
                    c.getInt(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY))
            );
            return notePojo;

        } else return null;
    }


    public static void clearNotes(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL(DatabaseContract.DatabaseEntry.SQL_DELETE_TABLE_NOTES);
        db.execSQL(DatabaseContract.DatabaseEntry.SQL_CREATE_TABLE_NOTES);
    }

    public static int getNoteDbIdByPosition(Context context, int position) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES +
                " LIMIT 1 OFFSET " + position, null);

        int id = 1;
        if (c.moveToFirst()) {
            id = c.getInt(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_ID));
        }
        return id;
    }

}
