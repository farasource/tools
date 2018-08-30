package ghasemi.abbas.abzaar.db.malii;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.bahaar.TinyDB;

import java.text.NumberFormat;

import ghasemi.abbas.abzaar.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class commission extends AppCompatActivity {

    RadioGroup radioGroup;
    TextView title;
    FrameLayout part, part2;
    EditText ejareh, rahn, nerkhe_tabdil, maliat;
    Animation inTop;
    LinearLayout ll1;
    TextView sText;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyDB.getInstance(getApplicationContext()).putInt("version_" + commission.class.getCanonicalName(), 1);
        setContentView(R.layout.mali_commission);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        radioGroup = findViewById(R.id.radioGroup);
        title = findViewById(R.id.title);
        part = findViewById(R.id.part);
        part2 = findViewById(R.id.part2);
        ejareh = findViewById(R.id.ejareh);
        rahn = findViewById(R.id.rahn);
        nerkhe_tabdil = findViewById(R.id.nerkhe_tabdil);
        maliat = findViewById(R.id.maliat);
        sText = findViewById(R.id.setText);
        ll1 = findViewById(R.id.LinearLayout);
        inTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.buy) {
                    part2.setVisibility(View.GONE);
                    part.setVisibility(View.GONE);
                    title.setText("مبلغ مبادله");
                } else {
                    part2.setVisibility(View.VISIBLE);
                    part.setVisibility(View.VISIBLE);
                    title.setText("مبلغ اجاره");
                }
            }
        });

        findViewById(R.id.Done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.VISIBLE);
                ll1.startAnimation(inTop);
                if (maliat.getText().toString().trim().isEmpty() || ejareh.getText().toString().isEmpty()) {
                    sText.setText("هیچ یک از مقادیر نمی تواند خالی باشد.");
                    return;
                }
                double Maliat, Ejareh, Rahn, Nerkhe_tabdil;
                Maliat = Long.parseLong(maliat.getText().toString());
                Ejareh = Long.parseLong(ejareh.getText().toString());
                if (radioGroup.getCheckedRadioButtonId() == R.id.buy) {
                    if (Ejareh > 500000000) {
                        Ejareh = ((Ejareh - 500000000) * 0.0025) + 2500000;
                    } else {
                        Ejareh = Ejareh * 0.005;
                    }
                    Maliat = Ejareh * Maliat / 100;
                    sText.setText("مبلغ کمیسیون: " + NumberFormat.getNumberInstance().format(Ejareh + Maliat));
                } else if (nerkhe_tabdil.getText().toString().trim().isEmpty() || rahn.getText().toString().isEmpty()) {
                    sText.setText("هیچ یک از مقادیر نمی تواند خالی باشد.");
                } else {
                    Nerkhe_tabdil = Long.parseLong(nerkhe_tabdil.getText().toString());
                    Rahn = Long.parseLong(rahn.getText().toString());
                    Nerkhe_tabdil = Rahn / 1000000 * Nerkhe_tabdil;
                    Rahn = (Nerkhe_tabdil + Ejareh) * 0.25;
                    Maliat = Rahn * Maliat / 100;
                    sText.setText("مبلغ کمیسیون: " + NumberFormat.getNumberInstance().format(Rahn + Maliat));
                }
            }
        });
    }
}