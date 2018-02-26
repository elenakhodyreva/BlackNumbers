package com.blacknumapps.elena.anticollector;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Elena on 31.08.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION= 1;
    public static final String DB_NAME= "DB_BLACK_LIST";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table blcontacts ("
                + "_id integer primary key autoincrement,"
                + "phonenumber text,"
                + "name text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
