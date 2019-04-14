package com.murach.tipcalculator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jake on 4/13/19.
 */

public class TipDatabase {
    public static final String DB_NAME = "tipList.db";
    public static final int DB_VERSION = 1;

    public static final String TIP_TABLE = "tip";

    public static final String TIP_ID = "_id";
    public static final int TIP_ID_COL = 0;

    public static final String TIP_BILL_DATE = "bill_date";
    public static final int TIP_BILL_DATE_COL = 1;

    public static final String TIP_BILL_AMOUNT = "bill_amount";
    public static final int TIP_BILL_AMOUNT_COL = 2;

    public static final String TIP_TIP_PERCENT = "tip_percent";
    public static final int TIP_TIP_PERCENT_COL = 3;

    public static final String CREATE_TIP_TABLE = "CREATE TABLE " + TIP_TABLE + " (" +
            TIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TIP_BILL_DATE + " INTEGER NOT NULL, " +
            TIP_BILL_AMOUNT + " REAL NOT NULL, " +
            TIP_TIP_PERCENT + " REAL NOT NULL);";

    public static final String DROP_TIP_TABLE = "DROP TABLE IF EXISTS " + TIP_TABLE;

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public TipDatabase(Context context){
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    private void openReadableDB(){
        db = dbHelper.getReadableDatabase();
    }

    private void openWritableDB(){
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB(){
        if (db != null)
            db.close();
    }


    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TIP_TABLE);

            db.execSQL("INSERT INTO tip VALUES (0, 42.6, .15)");
            db.execSQL("INSERT INTO tip VALUES (0, 22.7, .1)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {

            db.execSQL(DROP_TIP_TABLE);
            onCreate(db);
        }
    }

    public ArrayList<Tip> getTips() {

        this.openReadableDB();

        Cursor cursor = db.query(TIP_TABLE, null, null, null, null, null, null);

        ArrayList <Tip> tips = new ArrayList<Tip>();
        while(cursor.moveToNext()) {
            tips.add(getTipFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        this.closeDB();
        return tips;
    }

    public void insertTip(Tip tip){
        this.openWritableDB();

        db.execSQL("INSERT INTO tip VALUES (" + tip.getId() +
                ", 0" +
                ", " + tip.getBillAmount() +
                ", " + tip.getTipPercent() + ")");
    }

    private static Tip getTipFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0){
            return null;
        }
        else{
            try {

                Tip tip = new Tip(cursor.getLong(TIP_ID_COL),
                        cursor.getLong(TIP_BILL_DATE_COL),
                        cursor.getFloat(TIP_BILL_AMOUNT_COL),
                        cursor.getFloat(TIP_TIP_PERCENT_COL)
                        );
                return tip;
            }
            catch (Exception e){
                return null;
            }
        }
    }
}

