package ghasemi.abbas.abzaar.db.malii;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import ghasemi.abbas.abzaar.R;

import java.text.NumberFormat;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * on 06/09/2018.
 */

public class SoodSep extends AppCompatActivity {
    Spinner sp1, sp2;
    EditText et1, et2, et3, et4;
    TextView sText;
    Button done;
    LinearLayout ll1;
    FrameLayout ll2;
    Animation inTop;
    private String[] tSep = {"ساده", "مرکب (سود روی سود)"};
    private String[] rSep = {"ماهانه", "سه ماهه", "شش ماهه", "سالانه"};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amg_m_mss);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        sText = findViewById(R.id.setText);
        et1 = findViewById(R.id.mizanSep);
        et2 = findViewById(R.id.soodSep);
        et3 = findViewById(R.id.soodMoSep);
        et4 = findViewById(R.id.monthNumber);
        sp1 = findViewById(R.id.typeSep);
        sp2 = findViewById(R.id.residSep);
        done = findViewById(R.id.Done);
        ll1 = findViewById(R.id.linear0);
        ll2 = findViewById(R.id.frame);
        inTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_1line, tSep);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_1line, rSep);
        adapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter1);
        sp2.setAdapter(adapter2);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (position == 0) {
                    ll2.setVisibility(View.GONE);
                } else {
                    ll2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et1.getText().toString().equals("") || et2.getText().toString().equals("") || (et3.getText().toString().equals("") && sp1.getSelectedItemPosition() == 1) || et4.getText().toString().equals("")) {
                    sText.setText("هیچ یک از مقادیر نباید خالی باشد.");
                    ll1.setVisibility(View.VISIBLE);
                    ll1.startAnimation(inTop);
                } else {
                    String math = getMath(sp1.getSelectedItemPosition(), sp2.getSelectedItemPosition());
                    sText.setText(math);
                    ll1.setVisibility(View.VISIBLE);
                    ll1.startAnimation(inTop);
                }
            }
        });

    }

    private String getMath(int SI1, int SI2) {
        Log.d("Abc", String.valueOf(SI1+SI2));
        try {
            int mSep = Integer.parseInt(et1.getText().toString());
            float nSep = Float.parseFloat(et2.getText().toString()) / 1200;
            float nSepM = 0;
            try {
                nSepM = Float.parseFloat(et3.getText().toString()) / 1200;
            } catch (Exception e) {
                Log.e("Abc", String.valueOf(e));
            }
            int dSep = Integer.parseInt(et4.getText().toString());

            int m30 = (int) ((mSep * nSep * 36000) / 36500);
            int m31 = (int) ((mSep * nSep * 37200) / 36500);
            int m1 = m30 / 30;
            int m365 = (m30 * 6) + (m31 * 6);
            int mAll = m30 * dSep;
            int dmSep = getDM(SI2, dSep);

            float SoodM = 0;
            if (SI1 == 1) {
                for (int k = 1; k <= dmSep; k++) {
                    SoodM = (float) (SoodM + nSep * mSep * ((Math.pow(1 + nSepM, k) - 1) / nSepM) - nSep * mSep * ((Math.pow(1 + nSepM, k - 1) - 1) / nSepM));
                }
                mAll = (int) SoodM;
            }
            String M = "سود روزانه: " + NumberFormat.getNumberInstance(Locale.US).format(m1) + "\n" + "سود ماه 30 روزه: " + NumberFormat.getNumberInstance(Locale.US).format(m30) + "\n" +
                    "سود ماه 31 روزه: " + NumberFormat.getNumberInstance(Locale.US).format(m31) + "\n" + "سود سالانه: " + NumberFormat.getNumberInstance(Locale.US).format(m365) + "\n\n";

            return M + "سود کل: " + NumberFormat.getNumberInstance(Locale.US).format(mAll) + "\n" + "سرمایه نهائی: " + NumberFormat.getNumberInstance(Locale.US).format(mAll + mSep);
        }catch (Exception e) {
            Log.e("Abc", String.valueOf(e));
        }
        return "";
    }

    private int getDM(int si2, int dSep) {
        if (si2 == 1) {
            return dSep / 3;
        } else if (si2 == 2) {
            return dSep / 6;
        } else if (si2 == 3) {
            return dSep / 12;
        }
        return dSep;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
