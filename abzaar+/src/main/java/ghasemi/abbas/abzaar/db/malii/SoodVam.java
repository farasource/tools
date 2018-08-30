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

public class SoodVam extends AppCompatActivity {
    Spinner sp1;
    EditText et1, et2, et3, et4;
    TextView tv, sText;
    Button done;
    LinearLayout ll;
    Animation inTop;
    private String[] tSep = {"نرخ سود", "مبلغ هر قسط", "مبلغ بازگشتی"};
    //    private String[] rSep = {"ساده", "تورم"};
    private String[] hint = {"  %", "", ""};


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amg_m_msv);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tv = findViewById(R.id.typeVam);
        sText = findViewById(R.id.setText);
        et1 = findViewById(R.id.mizanVam);
        et2 = findViewById(R.id.soodVam);
        et3 = findViewById(R.id.mfNumber);
        et4 = findViewById(R.id.maNumber);
        sp1 = findViewById(R.id.mabnaVam);
//        sp2 = findViewById(R.id.tavaromVam);
        done = findViewById(R.id.Done);
        ll = findViewById(R.id.linear0);
        inTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_1line, tSep);
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_1line, rSep);
        adapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter1);
//        sp2.setAdapter(adapter2);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                tv.setText(tSep[position]);
                et2.setHint(hint[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et1.getText().toString().equals("") || et2.getText().toString().equals("") || et3.getText().toString().equals("") || et4.getText().toString().equals("")) {
                    sText.setText("هیچ یک از مقادیر نباید خالی باشد.");
                    ll.setVisibility(View.VISIBLE);
                    ll.startAnimation(inTop);
                } else {
                    String math = getMath(sp1.getSelectedItemPosition());
                    sText.setText(math);
                    ll.setVisibility(View.VISIBLE);
                    ll.startAnimation(inTop);
                }
            }
        });

    }

    private String getMath(int Position) {
        Log.d("Abc", String.valueOf(Position));
        int mVam = Integer.parseInt(et1.getText().toString());
        float nVam = Float.parseFloat(et2.getText().toString()) / 100;
        int kVam = Integer.parseInt(et3.getText().toString());
        int pVam = Integer.parseInt(et4.getText().toString());
        try {
            if (Position == 0) {
                float i = (nVam / 12) * kVam;
                int payment_amount = (int) (mVam * ((Math.pow(1 + i, pVam) * i) / (Math.pow(1 + i, pVam) - 1)));
                int total_amount = payment_amount * pVam;
                return "مبلغ هر قسط: " + NumberFormat.getNumberInstance(Locale.US).format(payment_amount) + "\n" + "بازپرداخت: " + NumberFormat.getNumberInstance(Locale.US).format(total_amount);
            } else if (Position == 1) {
                double error = 0.0003;
                float l = nVam * 100;
                int interest_rate = 0;
                for (double i = 0.01; i < 500; i += 0.01) {
                    double j = i / 100;
                    double k = mVam * ((Math.pow(1 + j, pVam) * j) / (Math.pow(1 + j, pVam) - 1));
                    double p = Math.abs(k - l) / l;
                    if (p < error) {
                        interest_rate = (int) Math.ceil(i * 12 / pVam);
                        break;
                    }
                }
                int total_amount = Math.round(l * pVam);
                return "نرخ بهره: " + NumberFormat.getNumberInstance(Locale.US).format(interest_rate) + "%" + "\n" + "بازپرداخت: " + NumberFormat.getNumberInstance(Locale.US).format(total_amount);
            } else {
                double error = 0.0003;
                float payment_amount = (nVam * 100) / pVam;
                int interest_rate = 0;
                for (double i = 0.01; i < 500; i += 0.01) {
                    double j = i / 100;
                    double k = mVam * ((Math.pow(1 + j, pVam) * j) / (Math.pow(1 + j, pVam) - 1));
                    double p = Math.abs(k - payment_amount) / payment_amount;
                    if (p < error) {
                        interest_rate = (int) Math.ceil(i * 12 / pVam);
                        break;
                    }
                }
                return "نرخ بهره: " + NumberFormat.getNumberInstance(Locale.US).format(interest_rate) + "%" + "\n" + "مبلغ هر قسط: " + NumberFormat.getNumberInstance(Locale.US).format(payment_amount);
            }
        } catch (Exception e) {
            Log.e("Abc", String.valueOf(e));
        }
        return "";
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
