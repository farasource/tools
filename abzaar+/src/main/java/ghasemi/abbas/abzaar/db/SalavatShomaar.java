package ghasemi.abbas.abzaar.db;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ghasemi.abbas.abzaar.R;

import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 04/19/2018.
 */

public class SalavatShomaar extends AppCompatActivity {
    Toolbar toolbar;
    TinyDB db;
    TextView param;
    Button b1, b2;
    LinearLayout linearLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salavat_shomaar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        db = new TinyDB(getApplicationContext());
        final Animation show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show);
        final Animation in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.in);
        Animation inTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.in_top);
        param = (TextView) findViewById(R.id.textView2);
        b1 = (Button) findViewById(R.id.button2);
        b2 = (Button) findViewById(R.id.button3);
        linearLayout = (LinearLayout) findViewById(R.id.LinearLayout);
        final String PSS = " ذکر";
        param.setText(NumberFormat.getNumberInstance(Locale.US).format(db.getInt("PSS",0))+PSS);
        param.setTextColor(TextColor());
        b1.startAnimation(in);
        b2.startAnimation(in);
        linearLayout.startAnimation(inTop);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pss = db.getInt("PSS",0);
                db.putInt("PSS", pss + 1);
                param.setText(NumberFormat.getNumberInstance(Locale.US).format(db.getInt("PSS",0))+PSS);
                param.setTextColor(TextColor());
                param.startAnimation(show);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pss = db.getInt("PSS",0);
                if (!(pss == 0)) {
                    db.putInt("PSS2", pss);
                }
                db.putInt("PSS", 0);
                param.setText("0"+PSS);
                param.setTextColor(TextColor());
                param.startAnimation(show);
            }
        });
        b2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pss2 = db.getInt("PSS2",0);
                db.putInt("PSS", pss2);
                param.setText(NumberFormat.getNumberInstance(Locale.US).format(pss2)+PSS);
                param.setTextColor(TextColor());
                param.startAnimation(show);
                ToastActivity.Toast(getApplicationContext(),"آخرین تعداد شمارش (" + String.valueOf(pss2) + ") بازگردانی شد.",Toast.LENGTH_SHORT);
                return true;
            }
        });

    }

    int[] sList = {0xff0eafb1, 0xff009454, 0xffff004e, 0xffff9000, 0xff000000, 0xff1b7bf2, 0xff6a9100, 0xff911b00, 0xff7801d3, 0xffe207a2, 0xff19bcbe};
    Random s = new Random();

    public int TextColor() {
        return sList[s.nextInt(sList.length)];
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_help:
                HelpAlert(SalavatShomaar.this,"بعد از ریست کردن تعداد ذکر ها می توانید با لمس طولانی بر روی کلید صفر کننده ، آخرین تعداد ذکر قبل از صفر شدن را باز گردانی نمائید.");
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), SalavatShomaar.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
