package ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.TypedValue;

import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.mp3cutter.Models.ContactsModel;
import ghasemi.abbas.abzaar.db.mp3cutter.Models.SongsModel;

import java.util.ArrayList;
import java.util.List;

import static ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Constants.REQUEST_ID_MULTIPLE_PERMISSIONS;
import static ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Constants.REQUEST_ID_READ_CONTACTS_PERMISSION;
import static ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Constants.REQUEST_ID_RECORD_AUDIO_PERMISSION;


/**
 * Created by REYANSH on 4/8/2017.
 */

public class Utils {

    private static final String[] INTERNAL_COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.ALBUM_ID,
            "\"" + MediaStore.Audio.Media.INTERNAL_CONTENT_URI + "\""
    };
    private static final String[] EXTERNAL_COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.ALBUM_ID,
            "\"" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "\""
    };

    public static ArrayList<SongsModel> getSongList(Context context, boolean internal, String searchString) {

        String[] selectionArgs = null;
        String selection = null;
        if (searchString != null && searchString.length() > 0) {
            selection = "title LIKE ?";
            selectionArgs = new String[]{"%" + searchString + "%"};
        }

        ArrayList<SongsModel> songsModels = new ArrayList<>();
        Uri CONTENT_URI;
        String[] COLUMNS;

        if (internal) {
            CONTENT_URI = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
            COLUMNS = INTERNAL_COLUMNS;
        } else {
            CONTENT_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            COLUMNS = EXTERNAL_COLUMNS;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    CONTENT_URI,
                    COLUMNS,
                    selection,
                    selectionArgs,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        } catch (Exception e) {
            //
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String fileType = "";
                try {
                    if (cursor.getString(6).equalsIgnoreCase("1")) {
                        fileType = Constants.IS_RINGTONE;
                    } else if (cursor.getString(7).equalsIgnoreCase("1")) {
                        fileType = Constants.IS_ALARM;
                    } else if (cursor.getString(8).equalsIgnoreCase("1")) {
                        fileType = Constants.IS_NOTIFICATION;
                    } else {
                        fileType = Constants.IS_MUSIC;
                    }
                } catch (Exception e) {
                    //lets assume its ringtone.
                    fileType = Constants.IS_RINGTONE;
                }

                SongsModel song = new SongsModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(10),
                        fileType);
                songsModels.add(song);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return songsModels;
    }

    public static Uri getAlbumArtUri(long paramInt) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), paramInt);
    }


    public static final String makeShortTimeString(final Context context, long secs) {
        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources().getString(
                hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }


    public static boolean checkSystemWritePermission(final Activity context) {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(context);
            if (!retVal) {
                AlertDialog show = new AlertDialog.Builder(context, R.style.Theme_Dialog_Alert)
                        .setTitle("تنظیم آهنگ زنگ")
                        .setMessage("ابتدا دسترسی تنظیم را به برنامه دهید")
                        .setPositiveButton("تنظیمات", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("لغو", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .create();
                show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
                show.show();
            }
        }
        return retVal;
    }

    public static int getDimensionInPixel(Context context, int dp) {
        return (int) TypedValue.applyDimension(0, dp, context.getResources().getDisplayMetrics());
    }


    public static ArrayList<ContactsModel> getContacts(Context context, String searchQuery) {

        String selection = "(DISPLAY_NAME LIKE \"%" + searchQuery + "%\")";

        ArrayList<ContactsModel> contactsModels = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.CUSTOM_RINGTONE,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.LAST_TIME_CONTACTED,
                        ContactsContract.Contacts.STARRED,
                        ContactsContract.Contacts.TIMES_CONTACTED,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER,},
                selection,
                null,
                "STARRED DESC, " +
                        "TIMES_CONTACTED DESC, " +
                        "LAST_TIME_CONTACTED DESC, " +
                        "DISPLAY_NAME ASC");


        if (cursor != null && cursor.moveToFirst()) {
            do {
                ContactsModel contactsModel = new ContactsModel(cursor.getString(2),
                        cursor.getString(0), "");
                contactsModels.add(contactsModel);
            } while (cursor.moveToNext());
        }

        return contactsModels;
    }

    public static ArrayList<ContactsModel> getContactsInfo(Context context, String searchQuery) {
        ArrayList<ContactsModel> contact = new ArrayList<>();
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
        String FILTER = DISPLAY_NAME + " LIKE \"%" + searchQuery + "%\"";

        String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);

        String[] PROJECTION = {
                ContactsContract.Contacts._ID,
                DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };
        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String email = null;
                    Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (ce != null && ce.moveToFirst()) {
                        email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        ce.close();
                    }

                    String phone = null;
                    if (hasPhone > 0) {
                        Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (cp != null && cp.moveToFirst()) {
                            phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            cp.close();
                        }
                    }
                    if ((!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            && !email.equalsIgnoreCase(name)) || (!TextUtils.isEmpty(phone))) {
                        ContactsModel c = new ContactsModel(name, id, phone);
                        contact.add(c);
                    }

                } while (cursor.moveToNext());

                cursor.close();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return contact;
    }

    public static Drawable getMatColor(Context context) {
        int returnColor;
        {
            TypedArray colors = context.getResources().obtainTypedArray(R.array.mdcolor_500);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(new float[]{16, 16, 16, 16, 16, 16, 16, 16}, null, null));
        drawable.getPaint().setColor(returnColor);
        return drawable;
    }

    public static boolean checkAndRequestPermissions(Activity activity, boolean ask) {
        int modifyAudioPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (modifyAudioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            if (ask) {
                ActivityCompat.requestPermissions(activity,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean checkAndRequestAudioPermissions(Activity activity) {
        int modifyAudioPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (modifyAudioPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_ID_RECORD_AUDIO_PERMISSION);
            return false;
        }
        return true;
    }

    public static boolean checkAndRequestContactsPermissions(Activity activity) {
        int modifyAudioPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        if (modifyAudioPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_ID_READ_CONTACTS_PERMISSION);
            return false;
        }
        return true;
    }


}
