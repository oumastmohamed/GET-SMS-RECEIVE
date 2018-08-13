package org.m.o.getsmsreceive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by poste05 on 04/04/2018.
 */

public class DBConnectionSetting extends SQLiteOpenHelper {
    private static final String DbName = "oumastSetting.db";
    private static final int Version = 1;
    public DBConnectionSetting(Context context) {
        super(context, DbName, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table IF NOT EXISTS sett (id INTEGER primary key, link TEXT, block TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("Drop table if EXISTS sett");
    onCreate(db);
    }



    public void insertLinkAndNumberBlock(String link, String listBlock){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("link",link);
        contentValues.put("block",listBlock);
        db.insert("sett", null, contentValues);
    }

    public String getSettingLink(){
        String link = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from sett", null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            link = res.getString(res.getColumnIndex("link"));
            res.moveToNext();
        }
        return link;
    }

    public String getNumberBlock(){
        String numbers = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from sett", null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            numbers = res.getString(res.getColumnIndex("block"));
            res.moveToNext();
        }
        return numbers;
    }
}
