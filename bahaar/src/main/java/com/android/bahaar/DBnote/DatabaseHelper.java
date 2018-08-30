package com.android.bahaar.DBnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * on 07/10/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DB_Note";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TEXT = "Note";


    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // type is fav,
        db.execSQL("create table " + TABLE_TEXT + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,Text TEXT,Time TEXT,Type TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXT);

        onCreate(db);
    }


    public long insertIntoDB(String note) {
        Calendar c = Calendar.getInstance();
        int Year = c.get(Calendar.YEAR);
        int Month = c.get(Calendar.MONTH);
        int Day = c.get(Calendar.DATE);
        int Hour = c.get(Calendar.HOUR_OF_DAY);
        int Minute = c.get(Calendar.MINUTE);
        int Second = c.get(Calendar.SECOND);
        String date = Year + " " + Month + " " + Day + " ، " + Hour + ":" + Minute + ":" + Second;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("Text", note);
        values.put("Time", date);
        values.put("Type", "0");
        long x = db.insert(TABLE_TEXT, null, values);
        db.close();
        return x;
    }

    public void updateIntoDB(String id, String note, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Calendar c = Calendar.getInstance();
        int Year = c.get(Calendar.YEAR);
        int Month = c.get(Calendar.MONTH);
        int Day = c.get(Calendar.DATE);
        int Hour = c.get(Calendar.HOUR_OF_DAY);
        int Minute = c.get(Calendar.MINUTE);
        int Second = c.get(Calendar.SECOND);
        String date = Year + " " + Month + " " + Day + " ، " + Hour + ":" + Minute + ":" + Second;
        if (note != null) {
            values.put("Text", note);
        }
        if (type != null) {
            values.put("Type", date);
        }
        values.put("Time", date);
        db.update(TABLE_TEXT, values, "ID = ? ", new String[]{id});
        db.close();
    }

    public List<DatabaseModel> getDataFromDB() {
        List<DatabaseModel> modelList = new ArrayList<>();
        String query = "select * from " + TABLE_TEXT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                DatabaseModel model = new DatabaseModel();
                model.setID(cursor.getInt(0));
                model.setNote(cursor.getString(1));
                model.setTime(cursor.getString(2));
                model.setType(cursor.getString(3));
                modelList.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return modelList;
    }

    public List<DatabaseModel> getDataStarFromDB() {
        List<DatabaseModel> modelList = new ArrayList<>();
        String query = "select * from " + TABLE_TEXT + " where Type='1' ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                DatabaseModel model = new DatabaseModel();
                model.setID(cursor.getInt(0));
                model.setNote(cursor.getString(1));
                model.setTime(cursor.getString(2));
                model.setType(cursor.getString(3));
                modelList.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return modelList;
    }


    public void deleteRow(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEXT, "ID" + " = ?", new String[]{id});
        db.close();
    }

    public boolean backup() {

        //database path
        final String inFileName = mContext.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory() + "/abz/.DataAbz/note_backup.abz";
            File f = new File(outFileName);
            f.mkdirs();
            f.delete();
//            f.createNewFile();
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            return true;

        } catch (Exception e) {
            Log.e("aabbas", e.toString());

        }
        return false;
    }

    public boolean importDB() {

        final String outFileName = mContext.getDatabasePath(DATABASE_NAME).toString();

        try {
            String inFileName = Environment.getExternalStorageDirectory() + "/abz/.DataAbz/note_backup.abz";
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            return true;
        } catch (Exception e) {
            //
        }
        return false;
    }

}