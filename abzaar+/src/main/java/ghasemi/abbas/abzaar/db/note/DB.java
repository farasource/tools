package ghasemi.abbas.abzaar.db.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import ghasemi.abbas.abzaar.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {

    private static DB db;
    private String TABLE_NOTE = "note";

    private DB(Context context) {
        super(context, "note_abzaar", null, 1);
    }

    public static DB getInstanse() {
        if (db == null) {
            db = new DB(Main.activity);
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NOTE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,content TEXT,password TEXT,date TEXT,fav INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertIntoDB(String note, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", note);
        values.put("date", String.valueOf(System.currentTimeMillis()));
        values.put("password", password == null ? "" : password);
        values.put("fav", 0);
        long x = db.insert(TABLE_NOTE, null, values);
        db.close();
        return x;
    }

    public void updateIntoDB(int id, String note, String password, int fav) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (note != null) {
            values.put("content", note);
        }
        if (password != null) {
            values.put("password", password);
        }
        if (fav != -1) {
            values.put("fav", fav);
        }
        values.put("date", String.valueOf(System.currentTimeMillis()));
        db.update(TABLE_NOTE, values, "ID = ? ", new String[]{String.valueOf(id)});
        db.close();
    }

    ArrayList<Bundle> getDataFromDB() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select * from " + TABLE_NOTE + " order by date desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Bundle model = new Bundle();
                model.putInt("id", cursor.getInt(0));
                model.putString("content", cursor.getString(1));
                model.putString("password", cursor.getString(2));
                model.putString("date", cursor.getString(3));
                model.putInt("fav", cursor.getInt(4));
                bundles.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    ArrayList<Bundle> getDataStarFromDB() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select * from " + TABLE_NOTE + " where fav = 1 order by date desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Bundle model = new Bundle();
                model.putInt("id", cursor.getInt(0));
                model.putString("content", cursor.getString(1));
                model.putString("password", cursor.getString(2));
                model.putString("date", cursor.getString(3));
                model.putInt("fav", cursor.getInt(4));
                bundles.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public void deleteRow(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, "ID" + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public boolean backupDB() {

        //database path
        final String inFileName = Main.activity.getDatabasePath("note_abzaar").toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory() + "/abz/.DataAbz/notes_backup.abz";
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

        final String outFileName = Main.activity.getDatabasePath("note_abzaar").toString();

        try {
            String inFileName = Environment.getExternalStorageDirectory() + "/abz/.DataAbz/notes_backup.abz";
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
