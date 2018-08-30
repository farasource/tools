package ghasemi.abbas.abzaar.db.calendar;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import ghasemi.abbas.abzaar.R;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

public class Convert extends AppCompatActivity {
    Animation inTop;
    LinearLayout ll1;
    TextView sText;
    RadioGroup radioGroup;
    EditText day, month, year;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            Shortcut(getIntent().getIntExtra("position", 0), Convert.class.getCanonicalName());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        menu.findItem(R.id.item_help).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_convert);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.shamci) {
                    year.setHint("1398");
                } else if (checkedId == R.id.qamari) {
                    year.setHint("1440");
                } else {
                    year.setHint("2019");
                }
            }
        });
        day = findViewById(R.id.day);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);

        sText = findViewById(R.id.setText);
        ll1 = findViewById(R.id.LinearLayout);
        inTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button);

        findViewById(R.id.Done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.VISIBLE);
                ll1.startAnimation(inTop);
                if (!day.getText().toString().isEmpty() && !month.getText().toString().isEmpty() && !year.getText().toString().isEmpty()) {
                    int Year = Integer.parseInt(year.getText().toString());
                    int Month = Integer.parseInt(month.getText().toString());
                    int Day = Integer.parseInt(day.getText().toString());
                    StringBuilder builder = new StringBuilder();
                    PersianDate persianDate;
                    IslamicDate islamicDate;
                    CivilDate civilDate;
                    try {
                        if (radioGroup.getCheckedRadioButtonId() == R.id.shamci) {
                            persianDate = new PersianDate(Year, Month, Day);
                            islamicDate = DateConverter.persianToIslamic(persianDate);
                            civilDate = DateConverter.persianToCivil(persianDate);
                        } else if (radioGroup.getCheckedRadioButtonId() == R.id.qamari) {
                            islamicDate = new IslamicDate(Year, Month, Day);
                            persianDate = DateConverter.islamicToPersian(islamicDate);
                            civilDate = DateConverter.islamicToCivil(islamicDate);
                        } else {
                            civilDate = new CivilDate(Year, Month, Day);
                            persianDate = DateConverter.civilToPersian(civilDate);
                            islamicDate = DateConverter.civilToIslamic(civilDate, 0);
                        }
                        builder.append("ماه ").append(civilDate.getNameMonth()).append(" - ")
                                .append(civilDate.getYear()).append("/").append(civilDate.getMonth())
                                .append("/").append(civilDate.getDayOfMonth()).append("\n\n")
                                .append("ماه ").append(islamicDate.getNameMonth()).append(" - ")
                                .append(islamicDate.getYear()).append("/").append(islamicDate.getMonth())
                                .append("/").append(islamicDate.getDayOfMonth()).append("\n\n")
                                .append("ماه ").append(persianDate.getNameMonth()).append(" - ")
                                .append(persianDate.getYear()).append("/").append(persianDate.getMonth())
                                .append("/").append(persianDate.getDayOfMonth()).append("\n").append("روز ")
                                .append(persianDate.getNameDay());
                        sText.setText(builder.toString());
                    } catch (Exception e) {
                        sText.setText("لطفا یک تاریخ معتبر وارد نمائید.");
                    }
                } else {
                    sText.setText("لطفا تاریخ را کامل وارد نمائید.");
                }
            }
        });
    }
}
