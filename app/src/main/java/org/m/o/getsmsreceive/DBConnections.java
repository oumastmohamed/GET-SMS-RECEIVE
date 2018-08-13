package org.m.o.getsmsreceive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by poste05 on 04/04/2018.
 */

public class DBConnections extends SQLiteOpenHelper {
    private static final String DbName = "oumastMain.db";
    private static final int Version = 1;
    public DBConnections(Context context) {
        super(context, DbName, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table IF NOT EXISTS sms (id INTEGER primary key, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("Drop table if EXISTS sms");
    onCreate(db);
    }

    public void insertDateOfLastSMS(Long date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",String.valueOf(date));
        db.insert("sms", null, contentValues);
    }

    public Long getLastrecord(){
        Long date = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from sms", null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            date = Long.parseLong(res.getString(res.getColumnIndex("date")));
            res.moveToNext();
        }
        return date;
    }
}
