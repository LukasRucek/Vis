package com.example.vis;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBName = "MyDatabase.db";

    public static final String TableName = "Message";
    public static final String Page = "page";
    public static final String Title = "title";
    public static final String Sender = "sender";
    public static final String Time = "time";
    public static final String Ctx = "ctx";


    public DBHelper(Context context) {
        super(context, DBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Message" +
                "(id integer PRIMARY KEY, page integer, title text, sender text, time text, ctx text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Message");
        onCreate(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public boolean insertData(int page, String title, String sender, String time, String ctx){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor crs = db.rawQuery("SELECT EXISTS(SELECT 1 FROM Message WHERE sender=? AND time=? AND ctx=?) as isIn",
                new String[] {sender, time, ctx});

        crs.moveToFirst();
        if(Integer.parseInt(crs.getString(crs.getColumnIndex("isIn"))) == 1){
            return true;
        }

        ContentValues cv = new ContentValues();
        cv.put(Page, page);
        cv.put(Title, title);
        cv.put(Sender, sender);
        cv.put(Time, time);
        cv.put(Ctx, ctx);

        db.insert(TableName, null, cv);

        return true;
    }

    @SuppressLint("Range")
    public ArrayList<MessageModel> getPage(){
        ArrayList<MessageModel> arrModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor crs = db.rawQuery("SELECT * FROM Message",null);

        crs.moveToFirst();

        while(crs.isAfterLast() == false){
            MessageModel msgModel = new MessageModel();
            msgModel.setPage(crs.getString(crs.getColumnIndex(Page)));
            msgModel.setTitle(crs.getString(crs.getColumnIndex(Title)));
            msgModel.setSender(crs.getString(crs.getColumnIndex(Sender)));
            msgModel.setTime(crs.getString(crs.getColumnIndex(Time)));
            msgModel.setCtx(crs.getString(crs.getColumnIndex(Ctx)));

            arrModels.add(msgModel);
            crs.moveToNext();
        }



        return arrModels;
    }


}
