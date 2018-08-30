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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import ghasemi.abbas.abzaar.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 04/25/2018.
 */

public class Bmi extends AppCompatActivity {
    Animation inTop;
    TextView setText;
    EditText getGad, getVazn, getSen;
    Button Done;
    Spinner spinner;
    String[] types = {"سبک زندگی نا مشخص", "سبک زندگی بدون تحرک", "سبک زندگی کم تحرک", "سبک زندگی متوسط روزانه", "سبک زندگی پر تحرک"};
    String BMR = "";
    int type = 0;
    RadioButton m, z;
    LinearLayout linearLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setText = findViewById(R.id.setText);
        getGad = findViewById(R.id.getGad);
        getVazn = findViewById(R.id.getVazn);
        getSen = findViewById(R.id.getSen);
        spinner = findViewById(R.id.spinner);
        Done = findViewById(R.id.Done);
        m = findViewById(R.id.mard);
        z = findViewById(R.id.zan);
        linearLayout = findViewById(R.id.LinearLayout);
        inTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, types);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getGad.getText().toString().equals("") || getVazn.getText().toString().equals("") || getSen.getText().toString().equals("")) {
                    setText.setText("هیچ یک از مقادیر نباید خالی باشد.");
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.startAnimation(inTop);
                } else if (Integer.parseInt(getGad.getText().toString()) > 250 || Integer.parseInt(getGad.getText().toString()) < 20) {
                    setText.setText("حداقل و حداکثر قد 20 و 250 سانتی متر است.");
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.startAnimation(inTop);
                } else if (Integer.parseInt(getVazn.getText().toString()) > 200 || Integer.parseInt(getVazn.getText().toString()) < 2) {
                    setText.setText("حداقل و حداکثر وزن 2 و 200 کیلو گرم است.");
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.startAnimation(inTop);
                } else if (Integer.parseInt(getSen.getText().toString()) > 150 || Integer.parseInt(getSen.getText().toString()) < 1) {
                    setText.setText("حداقل و حداکثر سن 1 و 150 سال است.");
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.startAnimation(inTop);
                } else if (m.isChecked()) {
                    BMR = getBMR(Double.parseDouble(getGad.getText().toString()),
                            Double.parseDouble(getVazn.getText().toString()),
                            Double.parseDouble(getSen.getText().toString()), type, "m");
                    setResult();
                } else {
                    BMR = getBMR(Double.parseDouble(getGad.getText().toString()),
                            Double.parseDouble(getVazn.getText().toString()),
                            Double.parseDouble(getSen.getText().toString()), type, "z");
                    setResult();
                }

            }
        });

    }

    private void setResult() {
        double BMI = getBMIKg(Double.parseDouble(getGad.getText().toString()), Double.parseDouble(getVazn.getText().toString()));
        setText.setText("شاخص توده بدنی (BMI) شما:\n" + new DecimalFormat("##.#")
                .format(BMI) + "\n\n" + "بدن شما در وضعیت :\n" + getBMIClassification(BMI) + "\n\n" + "مقدار کالری مصرف روزانه:\n" + BMR);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.startAnimation(inTop);
    }


    public String getBMR(double height, double weight, double age, int type, String sex) {
        double BMR = 0;
        if (sex.equals("m")) {
            BMR = -(age * 6.755) + (height * 5.003) + (weight * 13.75) + 66.5;
        } else if (sex.equals("z")) {
            BMR = -(age * 6.5) + (height * 2.7) + (weight * 13.55) + 220;
        }
        if (type != 0) {
            if (type == 1) {
                BMR = BMR * 1.18;
            } else if (type == 2) {
                BMR = BMR * 1.27;
            } else if (type == 3) {
                BMR = BMR * 1.36;
            } else if (type == 4) {
                BMR = BMR * 1.45;
            }
        }
        return String.valueOf(NumberFormat.getNumberInstance(Locale.US).format((int) BMR));
    }


    public double getBMIKg(double height, double weight) {
        double meters = height / 100;
        return weight / Math.pow(meters, 2);
    }

    public double getBMILb(double height, double heightInch, double weight) {
        int inch = (int) ((height * 12) + heightInch);
        return ((weight * 703) / Math.pow(inch, 2));
    }

    public String getBMIClassification(double bmi) {
        if (bmi <= 0 || bmi >= 48) return "نامشخص";
        String classification;

        if (bmi < 16.5) {
            classification = "دچار کمبود وزن شدید";
        } else if (bmi < 18.5) {
            classification = "کمبود وزن";
        } else if (bmi < 25) {
            classification = "وزن مناسب";
        } else if (bmi < 30) {
            classification = "اضافه وزن";
        } else if (bmi < 35) {
            classification = "چاقی کلاس ۱";
        } else if (bmi < 40) {
            classification = "چاقی کلاس 2";
        } else {
            classification = "چاقی کلاس 3";
        }
        return classification;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page,menu);
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
                Shortcut(getIntent().getIntExtra("position",0), Bmi.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
