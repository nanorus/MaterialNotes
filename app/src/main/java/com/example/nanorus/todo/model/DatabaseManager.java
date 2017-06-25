package com.example.nanorus.todo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.nanorus.todo.model.database.DatabaseContract;
import com.example.nanorus.todo.model.database.DatabaseHelper;
import com.example.nanorus.todo.model.pojo.DateTimePojo;
import com.example.nanorus.todo.model.pojo.NotePojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;


public final class DatabaseManager {
    public final static int SORT_BY_DATE_CREATING = 0;
    public final static int SORT_BY_NAME = 1;
    public final static int SORT_BY_PRIORITY = 2;
    public final static int SORT_BY_DATE_TIME = 3;


    public static void updateNote(NotePojo notePojo, Context context, int id) {
        // UPDATE NOTE SET name,  description
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_NAME, notePojo.getName());
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_DESCRIPTION, notePojo.getDescription());
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY, notePojo.getPriority());
        String dateTimeString = dateTimePojoToString(notePojo.getDateTimePojo());
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_DATE_TIME, dateTimeString);


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
        String dateTimeString = dateTimePojoToString(notePojo.getDateTimePojo());
        cv.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_DATE_TIME, dateTimeString);


        db.insert(DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES, null, cv);

        Toast.makeText(context, "Note added", Toast.LENGTH_SHORT).show();

    }

    public static void deleteNote(int id, Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        db.delete(DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES,
                DatabaseContract.DatabaseEntry.COLUMN_NAME_ID + "=?",
                new String[]{String.valueOf(id)});
        Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
    }

    public static Observable<List<NotePojo>> getAllNotesObservable(final Context context, final int sortBy, final int offset) {
        // System.out.println("new Observable()");
        Observable<List<NotePojo>> observable =
                Observable.create(
                        new Observable.OnSubscribe<List<NotePojo>>() {
                            @Override
                            public void call(Subscriber<? super List<NotePojo>> subscriber) {

                                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                                SQLiteDatabase db = databaseHelper.getReadableDatabase();


                                String orderBy;
                                int current_offset = offset;
                                Cursor c;
                                // switching orderBy
                                switch (sortBy) {
                                    case DatabaseManager.SORT_BY_DATE_CREATING:
                                        orderBy = DatabaseContract.DatabaseEntry.COLUMN_NAME_ID;
                                        break;
                                    case DatabaseManager.SORT_BY_NAME:
                                        orderBy = DatabaseContract.DatabaseEntry.COLUMN_NAME_NAME;
                                        break;
                                    case DatabaseManager.SORT_BY_PRIORITY:
                                        orderBy = DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY;
                                        break;
                                    case DatabaseManager.SORT_BY_DATE_TIME:
                                        orderBy = DatabaseContract.DatabaseEntry.COLUMN_NAME_DATE_TIME + " DESC";
                                        break;
                                    default:
                                        orderBy = DatabaseContract.DatabaseEntry.COLUMN_NAME_ID;
                                        break;
                                }

                                // getting count of notes
                                c = db.rawQuery("SELECT COUNT(" + DatabaseContract.DatabaseEntry.COLUMN_NAME_ID + ") FROM " +
                                        DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES, null);


                                if (c.moveToFirst()) {
                                    int notesCount = c.getInt(0);
                                    final int limit = 3;
                                    int queryCount = (notesCount / limit) + 1;

                                    NotePojo currentNote;
                                    ArrayList<NotePojo> currentNotesList = new ArrayList<>();

                                    // System.out.println("notesCount: " + notesCount);
                                    // System.out.println("queryCount: " + queryCount);
                                    for (int i = 0; i < queryCount; i++) {
                                        // making cursor with some notes
                                        c = db.query(DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES, new String[]{"*"},
                                                null, null, null, null, String.valueOf(orderBy) + " LIMIT " + limit + " OFFSET " + String.valueOf(current_offset));
                                        String dateString;
                                        DateTimePojo dateTimePojo = null;

                                        // creating noteList
                                        if (c.moveToFirst()) {
                                            do {
                                                // getting some notes from cursor
                                                dateString = c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_DATE_TIME));
                                                if (dateString != null) {
                                                    dateTimePojo = stringToDateTimePojo(dateString);
                                                }
                                                currentNote = new NotePojo(
                                                        c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_NAME)),
                                                        dateTimePojo,
                                                        c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_DESCRIPTION)),
                                                        c.getInt(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY))
                                                );
                                                currentNotesList.add(currentNote);
                                            } while (c.moveToNext());

                                            // test
                                            /*
                                            for (int ii = 0; ii < currentNotesList.size(); ii++){
                                                System.out.println("count: " + ii);
                                                System.out.println("name: " + currentNotesList.get(ii).getName());
                                                System.out.println("priority: " + currentNotesList.get(ii).getPriority());
                                            }
                                            */

                                            subscriber.onNext(currentNotesList);
                                            currentNotesList.clear();
                                            current_offset += limit;
                                            /*
                                            try {
                                                // System.out.println("SLEEP");
                                                Thread.sleep(10);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            */
                                            // System.out.println("end SLEEP");

                                        }
                                        c.close();
                                    }
                                    subscriber.onCompleted();
                                    databaseHelper.close();
                                }
                            }
                        });
        // creating and returning Observable
        return observable;

    }

    public static NotePojo getNote(Context context, int id) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor c = db.query(DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES, new String[]{"*"},
                DatabaseContract.DatabaseEntry.COLUMN_NAME_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (c.moveToFirst()) {
            String dateString = c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_DATE_TIME));
            DateTimePojo dateTimePojo = null;
            if (dateString != null) {
                dateTimePojo = stringToDateTimePojo(dateString);
            }

            return new NotePojo(
                    c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_NAME)),
                    dateTimePojo,
                    c.getString(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_DESCRIPTION)),
                    c.getInt(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY))
            );

        } else return null;
    }


    public static void clearNotes(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL(DatabaseContract.DatabaseEntry.SQL_DELETE_TABLE_NOTES);
        db.execSQL(DatabaseContract.DatabaseEntry.SQL_CREATE_TABLE_NOTES);
        db.execSQL(DatabaseContract.DatabaseEntry.SQL_INSERT_FIRST_ENTRY_INTO_NOTES);
    }

    public static int getNoteDbIdByPosition(Context context, int position, int sortType) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String orderBy;

        switch (sortType) {
            case DatabaseManager.SORT_BY_DATE_CREATING:
                orderBy = DatabaseContract.DatabaseEntry.COLUMN_NAME_ID;
                break;
            case DatabaseManager.SORT_BY_NAME:
                orderBy = DatabaseContract.DatabaseEntry.COLUMN_NAME_NAME;
                break;
            case DatabaseManager.SORT_BY_PRIORITY:
                orderBy = DatabaseContract.DatabaseEntry.COLUMN_NAME_PRIORITY;
                break;
            case DatabaseManager.SORT_BY_DATE_TIME:
                orderBy = DatabaseContract.DatabaseEntry.COLUMN_NAME_DATE_TIME + " DESC";
                break;
            default:
                orderBy = DatabaseContract.DatabaseEntry.COLUMN_NAME_ID;
                break;
        }
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.DatabaseEntry.TABLE_NAME_NOTES +
                " ORDER BY " + orderBy +
                " LIMIT 1 OFFSET " + position, null);

        int id = 1;
        if (c.moveToFirst()) {
            id = c.getInt(c.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME_ID));
        }
        return id;
    }

    public static String dateTimePojoToString(DateTimePojo dateTimePojo) {
        // to '2007-01-01 10:00:00'

        String dateTimeString = dateTimePojo.getYear() + "-" + dateTimePojo.getMonth() + "-" + dateTimePojo.getDay() +
                " " + dateTimePojo.getHour() + ":" + dateTimePojo.getMinute() + ":00";

        return dateTimeString;
    }

    public static DateTimePojo stringToDateTimePojo(String dateTimeString) {
        // from '2007-01-01 10:00:00'
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateTimePojo dateTimePojo = null;
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            dateTimePojo = new DateTimePojo(year, month, day, hour, minute);
        }
        return dateTimePojo;
    }


}