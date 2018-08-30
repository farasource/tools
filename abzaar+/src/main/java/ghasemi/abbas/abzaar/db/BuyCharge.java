package ghasemi.abbas.abzaar.db;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ghasemi.abbas.abzaar.R;

import com.android.bahaar.ToastActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.BuildVars.setViewSite;
import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 05/02/2018.
 */

public class BuyCharge extends AppCompatActivity {
    private final int PICK_CONTACT = 7;
    Spinner sp1, sp2;
    EditText number;
    Button buy;
    String[] first = {"ایرانسل", "همراه اول", "رایتل"};
    String[] second = {"شارژ 1،000 تومانی", "شارژ 2،000 تومانی", "شارژ 5،000 تومانی", "شارژ 10،000 تومانی", "شارژ 20،000 تومانی"};
    String[] RT = {"شارژ 2،000 تومانی", "شارژ 5،000 تومانی", "شارژ 10،000 تومانی", "شارژ 20،000 تومانی", "شارژ 50،000 تومانی"};
    String[] IR = {"شارژ 2،000 تومانی", "شارژ 5،000 تومانی", "شارژ 10،000 تومانی", "شارژ 20،000 تومانی"};
    String[] HA = {"شارژ 1،000 تومانی", "شارژ 2،000 تومانی", "شارژ 5،000 تومانی", "شارژ 10،000 تومانی", "شارژ 20،000 تومانی"};
    RadioButton rb;
    CheckBox cb;
    TextView Site, Phone;
    LinearLayout linearLayout;
    RadioGroup radioGroup;
    private String type;
    private String charge;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ch);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sp1 = findViewById(R.id.type_operator);
        sp2 = findViewById(R.id.type_charge);
        number = findViewById(R.id.getMobileNumber);
        radioGroup = findViewById(R.id.radio);
        linearLayout = findViewById(R.id.setMobileNumber);
        buy = findViewById(R.id.Done);
        rb = findViewById(R.id.ramz);
        cb = findViewById(R.id.charge_SA);
        Site = findViewById(R.id.site);
        Phone = findViewById(R.id.phone);

        Site.setText(Html.fromHtml("سایت:  <a href=\"http://m.780.ir/contact/\">بخش پشتیبانی</a>"));
        Site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewSite(BuyCharge.this, "http://m.780.ir/contact");
            }
        });
        Phone.setText(Html.fromHtml("تلفن:  <a href=\"tel:02148031780\">48031780-021</a>"));
        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("tel/*");
                    intent.setData(Uri.parse("tel:02148031780"));
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
        });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, first);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, second);
        final ArrayAdapter<String> rt = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, RT);
        final ArrayAdapter<String> ir = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, IR);
        final ArrayAdapter<String> ha = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, HA);
        adapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        rt.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        ir.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        ha.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter1);
        sp2.setAdapter(adapter2);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (rb.isChecked()) {
                    if (position == 0) {
                        sp2.setAdapter(ir);
                    } else if (position == 1) {
                        sp2.setAdapter(ha);
                    } else {
                        sp2.setAdapter(rt);
                    }
                } else {
                    sp2.setAdapter(adapter2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb.isChecked()) {
                    number.setEnabled(false);
                    if (sp1.getSelectedItemPosition() == 0) {
                        sp2.setAdapter(ir);
                    } else if (sp1.getSelectedItemPosition() == 1) {
                        sp2.setAdapter(ha);
                    } else {
                        sp2.setAdapter(rt);
                    }
                } else {
                    number.setEnabled(true);
                    number.requestFocus();
                    sp2.setAdapter(adapter2);
                }
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts();
            }
        });
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((number.getText().toString().length() != 11 && !rb.isChecked()) || (!number.getText().toString().startsWith("09") && !rb.isChecked())) {
                    ToastActivity.Toast(BuyCharge.this, "خطا ، لطفا شماره تلفن را صحیح وارد نمائید", Toast.LENGTH_SHORT);
                    return;
                }
                if (rb.isChecked()) {
                    type = "2*" + (sp1.getSelectedItemPosition() + 1);
                } else {
                    type = "1*" + number.getText().toString();
                }
                switch (sp2.getSelectedItemPosition()) {
                    case 0:
                        charge = "1";
                        break;
                    case 1:
                        charge = "2";
                        break;
                    case 2:
                        charge = "3";
                        break;
                    case 3:
                        charge = "4";
                        break;
                    case 4:
                        charge = "5";
                        break;
                }
                USSD("*780*2*" + type + "*" + charge + "#");
            }
        });
    }

    private void getContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {

                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {

                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null, null);
                        phones.moveToFirst();
                        String cNumber = phones.getString(phones.getColumnIndex("data1"));
                        if (cNumber != null) {

                            if (cNumber.length() >= 10) {
                                cNumber = "0" + cNumber.substring(cNumber.length() - 10);
                            }
                            number.setText(cNumber);
                        }
                    }
                }
            }
        }
    }

    public void USSD(String ussd) {
        Intent call = new Intent(Intent.ACTION_CALL, Uri.parse(("tel:" + ussd).replace("#", Uri.encode("#"))));
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == 0) {
            startActivity(call);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        menu.findItem(R.id.item_help).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), BuyCharge.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
