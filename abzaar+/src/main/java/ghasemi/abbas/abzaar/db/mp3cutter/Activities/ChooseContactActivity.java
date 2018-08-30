/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ghasemi.abbas.abzaar.db.mp3cutter.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.mp3cutter.Adapters.ContactsAdapter;
import ghasemi.abbas.abzaar.db.mp3cutter.Models.ContactsModel;
import ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Constants;
import ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Utils;
import com.android.bahaar.ToastActivity;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


/**
 * After a ringtone has been saved, this activity lets you pick a contact
 * and assign the ringtone to that contact.
 */
public class ChooseContactActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Uri mRingtoneUri;

    /**
     * Called when the activity is first created.
     */
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private ContactsAdapter mContactsAdapter;
    private ArrayList<ContactsModel> mData;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mRingtoneUri = Uri.parse(intent.getExtras().getString(Constants.FILE_NAME));

        setContentView(R.layout.arm_choose_contact);

        mData = new ArrayList<>();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("مخاطبان");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mData = Utils.getContacts(this, "");
        mContactsAdapter = new ContactsAdapter(this, mData);
        mRecyclerView.setAdapter(mContactsAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arm_menu_search, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint("جستجو ...");

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.menu_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });

        menu.findItem(R.id.menu_search).expandActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mData = Utils.getContacts(this, newText);
        mContactsAdapter.updateData(mData);
        return false;
    }


    public void onItemClicked(int adapterPosition) {

        ContactsModel contactsModel = mData.get(adapterPosition);

        Uri uri = Uri.withAppendedPath(Contacts.CONTENT_URI, contactsModel.mContactId);

        ContentValues values = new ContentValues();
        values.put(Contacts.CUSTOM_RINGTONE, mRingtoneUri.toString());
        getContentResolver().update(uri, values, null, null);

        String message = "آهنگ با موفقیت برای " + contactsModel.mName+" اختصاص یافت.";
        ToastActivity.Toast(this,message,Toast.LENGTH_SHORT);
        finish();
    }


}
