package ghasemi.abbas.abzaar.db.fall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ghasemi.abbas.abzaar.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.BuildVars.ReadFromFile;
import static ghasemi.abbas.abzaar.BuildVars.getAppUrl;
import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;

public class FalHafezActivity extends AppCompatActivity {
    LinearLayout ll1, ll2, ll3;
    Button b1, b2, b3;
    TextView t1, t2;
    String allFall;
    String sher, tafsir;
    private Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faal_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ll1 = (LinearLayout) findViewById(R.id.LinearLayout1);
        ll2 = (LinearLayout) findViewById(R.id.LinearLayout2);
        ll3 = (LinearLayout) findViewById(R.id.LinearLayout3);
        b1 = (Button) findViewById(R.id.getFall);
        b2 = (Button) findViewById(R.id.fall);
        b3 = (Button) findViewById(R.id.share);
        t1 = (TextView) findViewById(R.id.sher);
        t2 = (TextView) findViewById(R.id.tafsir);

        allFall = ReadFromFile("fall.json", this);

        t1.setTextIsSelectable(true);
        t2.setTextIsSelectable(true);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.GONE);
                ll2.setVisibility(View.VISIBLE);
                ll3.setVisibility(View.VISIBLE);
                try {
                    onRandomClick();
                } catch (Exception e) {

                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onRandomClick();
                } catch (Exception e) {

                }
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "شعر: \n" + sher + "\n تفسیر: \n" + tafsir + "\n\n" + getAppUrl());
                intent.setType("text/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, "برنامه ای را جهت ارسال انتخاب نمائید"), 500);
            }
        });
    }


    public void onRandomClick() {
        try {
            Random rand = new Random();
            JSONArray b = new JSONArray(allFall);
            int randomNum = rand.nextInt(b.length());
            JSONObject object = b.getJSONObject(randomNum);
            t1.setText(object.getString("Sher"));
            t2.setText(object.getString("Mani"));
            sher = object.getString("Sher");
            tafsir = object.getString("Mani");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position", 0), FalHafezActivity.class.getCanonicalName());
                break;
            case R.id.item_help:
                HelpAlert(this, "منبع:\n" + "- دیوان حافظ شیرازی" + "\n\n" + "تفسیرگر اشعار:" + "\n" + "گروه توسعه نرم افزار");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}