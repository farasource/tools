package ghasemi.abbas.abzaar.db.foucault;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Environment;

import ghasemi.abbas.abzaar.Application;
import ghasemi.abbas.abzaar.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class TSQL extends SQLiteOpenHelper {

    private String TABLE_IMG_SAVE = "img_foucault";
    private static TSQL tsql;

    public static TSQL getInstanse() {
        if (tsql == null) {
            tsql = new TSQL(Main.activity);
        }
        return tsql;
    }

    private TSQL(Context context) {
        super(context, "tsql_abzaar", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_IMG_SAVE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," + "img" + " TEXT," + "date" + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMG_SAVE);
        onCreate(sqLiteDatabase);
    }


    public void addImgfoucault(Bitmap bitmap) throws Exception {
        Random random = new Random();
        String u = saveAndGetName(bitmap, "ImagesFoucault", "" + random.nextInt(999999999));
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("img", u);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        contentValues.put("date", dateFormat.format(new Date()));
        sqLiteDatabase.insert(TABLE_IMG_SAVE, null, contentValues);
        sqLiteDatabase.close();

    }

    private String saveAndGetName(Bitmap bitmap, String p, String name) throws Exception {
        String stringBuilder = Environment.getExternalStorageDirectory() + "/abz/" + p + "/";
        File file = new File(new File(stringBuilder), "");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file, ".nomedia");
        if (!file.exists()) {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.flush();
            fileWriter.close();
        }
        file = new File(new File(stringBuilder), name + ".jpg");
        if (file.exists()) {
            file.delete();
        }
        OutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        return file.getAbsolutePath();
    }

    public ArrayList<HashMap<String, Object>> getImgSave() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ArrayList<HashMap<String, Object>> lists = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_IMG_SAVE + " order by ID desc", null);
        if (cursor.moveToFirst()) {
            do {
                String url = cursor.getString(1);
                File file = new File(url);
                if (file.exists()) {
                    HashMap<String, Object> list = new HashMap<>();
                    list.put("id", cursor.getInt(0));
                    list.put("img", url);
                    list.put("date", cursor.getString(2));
                    lists.add(list);
                } else {
                    deleteImgSave(cursor.getInt(0), url);
                }
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        cursor.close();
        return lists;
    }

    public void deleteImgSave(int id, String url) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        if (id == -1) {
            sqLiteDatabase.delete(TABLE_IMG_SAVE, null, null);
            File file = new File(Environment.getExternalStorageDirectory() + "/abz/ImagesFoucault/");
            File[] files = file.listFiles();
            if(files == null){
                return;
            }
            for (File child : files) {
                child.delete();
            }
            file.delete();
        } else {
            sqLiteDatabase.delete(TABLE_IMG_SAVE, "ID = ?", new String[]{String.valueOf(id)});
            new File(url).delete();
        }
        sqLiteDatabase.close();
    }
}
