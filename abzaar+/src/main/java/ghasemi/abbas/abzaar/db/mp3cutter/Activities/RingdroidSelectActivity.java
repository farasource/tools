/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ghasemi.abbas.abzaar.db.mp3cutter.Activities;

/**
 * Main screen that shows up when you launch Ringdroid. Handles selecting
 * an audio file or using an intent to record a new one, and then
 * launches RingdroidEditActivity from here.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.mp3cutter.Adapters.SongsAdapter;
import ghasemi.abbas.abzaar.db.mp3cutter.Models.SongsModel;
import ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Constants;
import ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Utils;
import ghasemi.abbas.abzaar.db.mp3cutter.Views.FastScroller;
import com.android.bahaar.ToastActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;
import static ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Constants.REQUEST_ID_MULTIPLE_PERMISSIONS;
import static ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Constants.REQUEST_ID_READ_CONTACTS_PERMISSION;
import static ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Constants.REQUEST_ID_RECORD_AUDIO_PERMISSION;


/**
 * Main screen that shows up when you launch Ringdroid. Handles selecting
 * an audio file or using an intent to record a new one, and then
 * launches RingdroidEditActivity from here.
 */
public class RingdroidSelectActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    // Result codes
    private static final int REQUEST_CODE_EDIT = 1;
    public static Context mContext;
    int mPos;
    private Toolbar toolbar;
    private SearchView mSearchView;
    /**
     * Called when the activity is first created.
     */
    private RecyclerView mRecyclerView;
    private SongsAdapter mSongsAdapter;
    private ArrayList<SongsModel> mData;
    private FastScroller mFastScroller;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            showFinalAlert("متاسفیم، لطفا دسترسی های لازم را به برنامه اجازه دهید.");
            return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            showFinalAlert("متاسفیم، لطفا دسترسی های لازم را به برنامه اجازه دهید.");
            return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            showFinalAlert("متاسفیم، لطفا دسترسی های لازم را به برنامه اجازه دهید.");
            return;
        }

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.arm_media_select);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("برش آهنگ");

        mData = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_view);

        mFastScroller = findViewById(R.id.fast_scroller);
        mFastScroller.setRecyclerView(mRecyclerView);


        mSongsAdapter = new SongsAdapter(this, mData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mSongsAdapter);

        // Utils.initImageLoader(mContext);

        if (Utils.checkAndRequestPermissions(this, false)) {
            loadData();
        } else {
            mFastScroller.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        loadData();
                        mFastScroller.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        invalidateOptionsMenu();
                    }
                }
                break;
            }
            case REQUEST_ID_RECORD_AUDIO_PERMISSION:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        onRecord();
                    }
                }
                break;
            case REQUEST_ID_READ_CONTACTS_PERMISSION:
                Map<String, Integer> perm = new HashMap<>();
                perm.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perm.put(permissions[i], grantResults[i]);
                    if (perm.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        chooseContactForRingtone(mPos);
                    }
                }
                break;
        }
    }

    private void loadData() {
        mData.addAll(Utils.getSongList(getApplicationContext(), true, null));
        mData.addAll(Utils.getSongList(getApplicationContext(), false, null));
        mSongsAdapter.updateData(mData);
    }

    /**
     * Called with an Activity we started with an Intent returns.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        if (requestCode != REQUEST_CODE_EDIT) {
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        setResult(RESULT_OK, dataIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arm_select_options, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        if (Utils.checkAndRequestPermissions(this, false)) {
            menu.findItem(R.id.menu_search).setVisible(true);
        } else {
            menu.findItem(R.id.menu_search).setVisible(false);
        }

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);
        mSearchView.clearFocus();
        mSearchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_record:
                if (Utils.checkAndRequestAudioPermissions(RingdroidSelectActivity.this)) {
                    onRecord();
                }
                return true;
            case R.id.item_help:
                HelpAlert(this, "بعد از ذخیره سازی ، آهنگ شما در پوشه abz ذخیره می گردد.\n\n شما می توانید آهنگ مورد علاقه خود را برای زنگ تماس هر شخص اختصاص دهید.");
                return true;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), RingdroidSelectActivity.class.getCanonicalName());
                return true;
            default:
                return false;
        }
    }


    public void onPopUpMenuClickListener(CharSequence t,final int position) {
        mPos = position;
        View view = getLayoutInflater().inflate(R.layout.arm_popup_song, null);
        TextView title = view.findViewById(R.id.title);
        title.setText(t);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        view.findViewById(R.id.popup_song_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startEditor(position);
            }
        });
        view.findViewById(R.id.popup_song_assign_to_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Utils.checkAndRequestContactsPermissions(RingdroidSelectActivity.this)) {
                    chooseContactForRingtone(position);
                }
            }
        });
        view.findViewById(R.id.popup_song_set_default_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                setAsDefaultRingtoneOrNotification(position);
            }
        });
        view.findViewById(R.id.popup_song_set_default_ringtone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                setAsDefaultRingtoneOrNotification(position);
            }
        });
        if (mData.get(position).mFileType.equalsIgnoreCase(Constants.IS_RINGTONE)) {
            view.findViewById(R.id.popup_song_set_default_notification).setVisibility(View.GONE);
        } else if (mData.get(position).mFileType.equalsIgnoreCase(Constants.IS_NOTIFICATION)) {
            view.findViewById(R.id.popup_song_set_default_ringtone).setVisibility(View.GONE);
            view.findViewById(R.id.popup_song_assign_to_contact).setVisibility(View.GONE);
        } else if (mData.get(position).mFileType.equalsIgnoreCase(Constants.IS_MUSIC)) {
            view.findViewById(R.id.popup_song_set_default_notification).setVisibility(View.GONE);
        }
        dialog.setContentView(view);
        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void setAsDefaultRingtoneOrNotification(int pos) {
        if (!Utils.checkSystemWritePermission(this)) return;
        if (mData.get(pos).mFileType.equalsIgnoreCase(Constants.IS_RINGTONE)) {

            RingtoneManager.setActualDefaultRingtoneUri(
                    RingdroidSelectActivity.this,
                    RingtoneManager.TYPE_RINGTONE,
                    getInternalUri(pos));
            ToastActivity.Toast(RingdroidSelectActivity.this, "زنگ پیش فرض تغییر کرد.", Toast.LENGTH_SHORT);
        } else if (mData.get(pos).mFileType.equalsIgnoreCase(Constants.IS_MUSIC)) {
            RingtoneManager.setActualDefaultRingtoneUri(
                    RingdroidSelectActivity.this,
                    RingtoneManager.TYPE_RINGTONE,
                    getExtUri(pos));

            ToastActivity.Toast(RingdroidSelectActivity.this, "زنگ پیش فرض تغییر کرد.", Toast.LENGTH_SHORT);


        } else {
            RingtoneManager.setActualDefaultRingtoneUri(
                    RingdroidSelectActivity.this,
                    RingtoneManager.TYPE_NOTIFICATION,
                    getInternalUri(pos));

            ToastActivity.Toast(RingdroidSelectActivity.this, "زنگ اعلانات تغییر کرد.", Toast.LENGTH_SHORT);

        }
    }


    private Uri getInternalUri(int pos) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, Long.parseLong(mData.get(pos)._ID));
    }

    private Uri getExtUri(int pos) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mData.get(pos)._ID));
    }


    private boolean chooseContactForRingtone(int pos) {

        Intent intent = new Intent(RingdroidSelectActivity.this, ChooseContactActivity.class);
        if (mData.get(pos).mFileType.equalsIgnoreCase(Constants.IS_RINGTONE)) {
            intent.putExtra(Constants.FILE_NAME, String.valueOf(getInternalUri(pos)));
        } else if (mData.get(pos).mFileType.equalsIgnoreCase(Constants.IS_MUSIC)) {
            intent.putExtra(Constants.FILE_NAME, String.valueOf(getExtUri(pos)));
        } else {
            intent.putExtra(Constants.FILE_NAME, String.valueOf(getInternalUri(pos)));
        }
        startActivity(intent);

        return true;
    }


    private void showFinalAlert(CharSequence message) {
        AlertDialog show = new AlertDialog.Builder(RingdroidSelectActivity.this,R.style.Theme_Dialog_Alert)
                .setTitle("خطا")
                .setMessage(message)
                .setPositiveButton(
                        "باشه",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                finish();
                            }
                        })
                .setCancelable(false)
                .create();
        show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
        show.show();
        ;
    }

    private void onRecord() {
        try {
            Intent intent = new Intent(RingdroidSelectActivity.this, RingdroidEditActivity.class);
            intent.putExtra("FILE_PATH", "record");
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't start editor");
        }
    }


    public void onItemClicked(int adapterPosition) {
        startEditor(adapterPosition);
    }

    private void startEditor(int pos) {
        try {
            Intent intent = new Intent(mContext, RingdroidEditActivity.class);
            intent.putExtra("FILE_PATH", mData.get(pos).mPath);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't start editor");
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mData.clear();
        mData.addAll(Utils.getSongList(getApplicationContext(), true, newText));
        mData.addAll(Utils.getSongList(getApplicationContext(), false, newText));
        mSongsAdapter.updateData(mData);
        return false;
    }

}