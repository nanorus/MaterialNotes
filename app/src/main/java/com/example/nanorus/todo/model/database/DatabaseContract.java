package com.example.nanorus.todo.model.database;

import android.provider.BaseColumns;

public final class DatabaseContract {

    public DatabaseContract() {
    }

    public static abstract class DatabaseEntry implements BaseColumns {


        public static final String TABLE_NAME_NOTES = "notes";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_DATE_TIME = "date_time";

        public static final String TEXT_TYPE = " TEXT";
        public static final String COMMA_SEP = ",";

        public static final String FIRST_NOTE_NAME = "Начать жизнь с нуля";
        public static final String FIRST_NOTE_DESCRIPTION = "Делаем зарядку, пьем много воды.\nДышим свежим воздухом.";
        public static final String FIRST_NOTE_PRIORITY = "2";
        public static final String FIRST_NOTE_DATE_TIME = "2007-01-01 10:00:00";

        public static final String SQL_CREATE_TABLE_NOTES =
                "CREATE TABLE " + TABLE_NAME_NOTES + " (" +
                        COLUMN_NAME_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +   // _id
                        COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +              // name
                        COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +       // description
                        COLUMN_NAME_PRIORITY + " INTEGER" + COMMA_SEP +         // priority
                        COLUMN_NAME_DATE_TIME + " TEXT" +                       // date time
                        " )";

        public static final String SQL_DELETE_TABLE_NOTES =
                "DROP TABLE IF EXISTS " + TABLE_NAME_NOTES;

        public static final String SQL_INSERT_FIRST_ENTRY_INTO_NOTES =
                "INSERT INTO " + TABLE_NAME_NOTES + " (" +
                        COLUMN_NAME_NAME + COMMA_SEP +
                        COLUMN_NAME_DESCRIPTION + COMMA_SEP +
                        COLUMN_NAME_PRIORITY + COMMA_SEP +
                        COLUMN_NAME_DATE_TIME +
                        ") VALUES (" +
                        "\"" + FIRST_NOTE_NAME + "\"" + COMMA_SEP +
                        "\"" + FIRST_NOTE_DESCRIPTION + "\"" + COMMA_SEP +
                        FIRST_NOTE_PRIORITY + COMMA_SEP +
                        "\"" + FIRST_NOTE_DATE_TIME + "\"" +
                        ")";

    }


}
