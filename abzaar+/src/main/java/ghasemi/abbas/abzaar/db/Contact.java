package ghasemi.abbas.abzaar.db;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import ghasemi.abbas.abzaar.BuildVars;
import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.mp3cutter.Models.ContactsModel;
import ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Utils;

import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 07/10/2018.
 */

public class Contact extends AppCompatActivity {
    ArrayList<ContactsModel> mhcData;
    Adapter mContactsAdapter;
    hcAdapter hc;
    AlertDialog show;
    TinyDB tinyDB;
    AVLoadingIndicatorView progress;
    private ArrayList<ContactsModel> mData;

    public static void addNumberToContact(Context context, Long contactRawId, String number) throws RemoteException, OperationApplicationException {
        addInfoToAddressBookContact(
                context,
                contactRawId,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_OTHER,
                number
        );
    }

    public static void addEmailToContact(Context context, Long contactRawId, String email) throws RemoteException, OperationApplicationException {
        addInfoToAddressBookContact(
                context,
                contactRawId,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.TYPE,
                ContactsContract.CommonDataKinds.Email.TYPE_OTHER,
                email
        );
    }

    public static void addURLToContact(Context context, Long contactRawId, String url) throws RemoteException, OperationApplicationException {
        addInfoToAddressBookContact(
                context,
                contactRawId,
                ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Website.URL,
                ContactsContract.CommonDataKinds.Website.TYPE,
                ContactsContract.CommonDataKinds.Website.TYPE_OTHER,
                url
        );
    }

    private static void addInfoToAddressBookContact(Context context, Long contactRawId, String mimeType, String whatToAdd, String typeKey, int type, String data) throws RemoteException, OperationApplicationException {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            return;
        }
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactRawId)
                .withValue(ContactsContract.Data.MIMETYPE, mimeType)
                .withValue(whatToAdd, data)
                .withValue(typeKey, type)
                .build());
        context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
    }

    public boolean insertContact(String firstName, String mobileNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinyDB = new TinyDB(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.activity_main, null);
        FrameLayout frameLayout = new FrameLayout(this);
        progress = new AVLoadingIndicatorView(this);
        progress.setIndicatorColor(Color.BLACK);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) getResources().getDimension(R.dimen.w_p), (int) getResources().getDimension(R.dimen.h_p));
        layoutParams.gravity = Gravity.CENTER;
        frameLayout.addView(view);
        frameLayout.addView(progress, layoutParams);
        setContentView(frameLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        init();
        mData = new ArrayList<>();
        RecyclerView mRecyclerView = findViewById(R.id.list_main);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mContactsAdapter = new Adapter();
        mRecyclerView.setAdapter(mContactsAdapter);

        new Task().execute();
    }

    private void init() {
        TextView main_name = findViewById(R.id.main_name);
        main_name.setText("مخفی سازی مخاطبین");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        menu.findItem(R.id.item_hide).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_help:
                HelpAlert(this, "امکن مخفی سازی مخاطبین و مدیریت ساده و جامع آنها در قسمت شماره های مخفی شده");
                break;
            case R.id.item_hide:
                show();
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), Contact.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void show() {
        mhcData = new ArrayList<>();
        mhcData.clear();
        String hide = tinyDB.getString("my_contact_hide");
        if (!hide.equals("")) {
            String[] all = hide.split(",");
            for (String part : all) {
                String[] part2 = part.split("==");
                ContactsModel contactsModel = new ContactsModel(part2[0], "", part2[1]);
                mhcData.add(contactsModel);
            }
        } else {
            String smc = "";
            try {
                smc = BuildVars.ReadFromFileInSD("SMC");
                tinyDB.putString("my_contact_hide", smc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!smc.equals("")) {
                show();
            } else {
                ToastActivity.Toast(this, "لیست خالیست.", Toast.LENGTH_SHORT);
            }
            return;
        }

        RecyclerView mRecyclerView = new RecyclerView(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        hc = new hcAdapter();
        mRecyclerView.setAdapter(hc);

        show = new AlertDialog.Builder(this, R.style.Theme_Dialog_Alert)
                .setView(mRecyclerView)
                .create();
        show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
        show.show();
    }

    private void onItemClicked(int p) {
        if (tinyDB.getString("my_contact_hide").equals("")) {
            tinyDB.putString("my_contact_hide", mData.get(p).mName + "==" + mData.get(p).mNumber);
        } else {
            String t = tinyDB.getString("my_contact_hide") + "," + mData.get(p).mName + "==" + mData.get(p).mNumber;
            tinyDB.putString("my_contact_hide", t);
        }
        if (deleteContact(mData.get(p).mNumber, mData.get(p).mName)) {
            ToastActivity.Toast(this, "مخفی شد.", Toast.LENGTH_SHORT);
        } else {
            ToastActivity.Toast(this, "Error In Contact Hide", Toast.LENGTH_SHORT);
        }
        mData.remove(p);
        mContactsAdapter.notifyDataSetChanged();
        try {
            BuildVars.BackupInfoToSD(tinyDB.getString("my_contact_hide"), "SMC");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteContact(String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        getContentResolver().delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return false;
    }

    private class Task extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean is = false;
            try {
                mData = Utils.getContactsInfo(Contact.this, "");
                is = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return is;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progress != null) {
                        progress.hide();
                    }
                    if (result) {
                        mContactsAdapter.notifyDataSetChanged();
                    } else {
                        onBackPressed();
                        Toast.makeText(Contact.this, "error", Toast.LENGTH_LONG).show();
                    }
                }
            }, 2000);
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view2 = LayoutInflater.from(Contact.this).inflate(R.layout.arm_item_contacts, parent, false);
            return new ViewHolder(view2);
        }

        @Override
        public void onBindViewHolder(final Adapter.ViewHolder holder, final int position) {
            ContactsModel c = mData.get(position);

            holder.mContactName.setText(c.mName);
            holder.mContactID.setText(c.mNumber);

            try {
                String letter = String.valueOf(mData.get(position).mName.charAt(0));
                holder.mOneLetter.setText(letter);
                holder.mOneLetter.setBackground(Utils.getMatColor(Contact.this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mContactName;
            private TextView mOneLetter, mContactID;

            public ViewHolder(View itemView) {
                super(itemView);
                mContactName = itemView.findViewById(R.id.text_view_name);
                mContactID = itemView.findViewById(R.id.text_view_id);
                mOneLetter = itemView.findViewById(R.id.one_letter);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                View inflate = getLayoutInflater().inflate(R.layout.dialog_sheet_hidden, null);
                TextView title = inflate.findViewById(R.id.title);
                title.setText("آیا می خواهید شماره " + mData.get(getAdapterPosition()).mNumber + " مخفی گردد؟");
                final BottomSheetDialog dialog = new BottomSheetDialog(Contact.this);
                dialog.setContentView(inflate);
                dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
                inflate.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                inflate.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Contact.this.onItemClicked(getAdapterPosition());
                    }
                });
                dialog.show();
            }
        }

    }

    class hcAdapter extends RecyclerView.Adapter<hcAdapter.ViewHolder> {

        @Override
        public hcAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view2 = LayoutInflater.from(Contact.this).inflate(R.layout.arm_item_contacts, parent, false);
            return new ViewHolder(view2);
        }

        @Override
        public void onBindViewHolder(final hcAdapter.ViewHolder holder, final int position) {
            ContactsModel c = mhcData.get(position);

            holder.mContactName.setText(c.mName);
            holder.mContactID.setText(c.mNumber);

            try {
                String letter = String.valueOf(mhcData.get(position).mName.charAt(0));
                holder.mOneLetter.setText(letter);
                holder.mOneLetter.setBackground(Utils.getMatColor(Contact.this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mhcData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mContactName;
            private TextView mOneLetter, mContactID;

            public ViewHolder(View itemView) {
                super(itemView);
                mContactName = itemView.findViewById(R.id.text_view_name);
                mContactID = itemView.findViewById(R.id.text_view_id);
                mOneLetter = itemView.findViewById(R.id.one_letter);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                final ContactsModel c = mhcData.get(getAdapterPosition());
                Drawable drawable = getResources().getDrawable(R.drawable.ic_person);
                drawable.setColorFilter(0xcc000000, PorterDuff.Mode.MULTIPLY);
                AlertDialog dialog = new AlertDialog.Builder(Contact.this, R.style.Theme_Dialog_Alert)
                        .setTitle(mContactID.getText().subSequence(0, 4) + " " +
                                mContactID.getText().subSequence(4, 7) + " " +
                                mContactID.getText().subSequence(7, 11))
                        .setIcon(drawable)
                        .setNegativeButton("آشکار سازی", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                insertContact(c.mName, c.mNumber);
                                ContactsModel contactsModel = new ContactsModel(c.mName, "", c.mNumber);
                                mData.add(contactsModel);
                                mhcData.remove(getAdapterPosition());
                                hc.notifyDataSetChanged();
                                mContactsAdapter.notifyDataSetChanged();
                                ToastActivity.Toast(Contact.this, "مخاطب آشکار شده به آخر لیست اضافه شد.", Toast.LENGTH_SHORT);
                                for (int i = 0; i < mhcData.size(); i++) {
                                    if (i == 0) {
                                        tinyDB.putString("my_contact_hide", mhcData.get(i).mName + "==" + mhcData.get(i).mNumber);
                                    } else {
                                        String t = tinyDB.getString("my_contact_hide") + "," + mhcData.get(i).mName + "==" + mhcData.get(i).mNumber;
                                        tinyDB.putString("my_contact_hide", t);
                                    }
                                }

                                if (mhcData.isEmpty()) {
                                    tinyDB.putString("my_contact_hide", "");
                                    show.dismiss();
                                }
                                dialog.dismiss();
                                try {
                                    BuildVars.BackupInfoToSD(tinyDB.getString("my_contact_hide"), "SMC");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setPositiveButton("تماس", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setType("tel/*");
                                    intent.setData(Uri.parse("tel:" + c.mNumber));
                                    startActivity(intent);
                                } catch (Exception e) {
                                }
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
                dialog.show();
            }
        }

    }

}
