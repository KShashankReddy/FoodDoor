package com.example.shashankreddy.fooddoor.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by shashank reddy on 1/20/2017.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DBNAME="ricky_db";
    public static final String TABLENAME="food_door";
    public static final int VERSION=1;

    public static final String USERNAME="user_name";
    public static final String FOODNAME="food_name";
    public static final String RATE="rate";
    public static final String REVIEW="user_review";
    public static final String DATE="date";
    public static final String ID="key_id";

    public DbHelper(Context context)
    {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTable="CREATE TABLE "+TABLENAME+"("+ID+" TEXT PRIVATE KEY,"+ USERNAME +" TEXT,"+ FOODNAME +" TEXT,"+ RATE +" TEXT,"
                + DATE +" TEXT,"+REVIEW+" TEXT"+")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // called when tables are removed or created
        Log.v(DbHelper.this.getDatabaseName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS "+TABLENAME);
        onCreate(db);
    }

    // add review
    public boolean addRecord(String id, String user,String food,String rate, String date, String review)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(USERNAME, user);
        contentValues.put(FOODNAME, food);
        contentValues.put(RATE, rate);
        contentValues.put(DATE, date);
        contentValues.put(REVIEW, review);
        long result=db.insert(TABLENAME, null, contentValues);
        if(result==-1)
            return false;
        else
            return true;
    }

    // search item by food
    public Cursor searchRecord(String food)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor record=db.rawQuery("select * from "+TABLENAME+" where "+FOODNAME+" = ? ", new String[]{food} );
        return record;
    }

}

