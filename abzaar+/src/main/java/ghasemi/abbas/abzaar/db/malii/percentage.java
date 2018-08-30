package ghasemi.abbas.abzaar.db.malii;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import ghasemi.abbas.abzaar.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class percentage extends AppCompatActivity {

    TextView result_1, result_2;
    EditText x, x2, y, y2;


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
        setContentView(R.layout.mali_percentage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        result_1 = findViewById(R.id.result_1);
        result_2 = findViewById(R.id.result_2);

        x = findViewById(R.id.x);
        x2 = findViewById(R.id.x2);
        y2 = findViewById(R.id.y2);
        y = findViewById(R.id.y);
        x.addTextChangedListener(new NumberTextWatcher(x));
        x2.addTextChangedListener(new NumberTextWatcher(x2));
        y.addTextChangedListener(new NumberTextWatcher(y));
        y2.addTextChangedListener(new NumberTextWatcher(y2));
    }

    public class NumberTextWatcher implements TextWatcher {

        @SuppressWarnings("unused")
        private static final String TAG = "NumberTextWatcher";
        private DecimalFormat df;
        private DecimalFormat dfnd;
        private boolean hasFractionalPart;
        private EditText et;

        NumberTextWatcher(EditText et) {
            df = new DecimalFormat("#,###.##");
            df.setDecimalSeparatorAlwaysShown(true);
            dfnd = new DecimalFormat("#,###");
            this.et = et;
            hasFractionalPart = false;
        }

        public void afterTextChanged(Editable s) {
            et.removeTextChangedListener(this);

            try {
                int inilen, endlen;
                inilen = et.getText().length();

                String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                Number n = df.parse(v);
                int cp = et.getSelectionStart();
                if (hasFractionalPart) {
                    et.setText(df.format(n));
                } else {
                    et.setText(dfnd.format(n));
                }
                endlen = et.getText().length();
                int sel = (cp + (endlen - inilen));
                if (sel > 0 && sel <= et.getText().length()) {
                    et.setSelection(sel);
                } else {
                    // place cursor at the end?
                    et.setSelection(et.getText().length() - 1);
                }
            } catch (NumberFormatException nfe) {
                // do nothing?
            } catch (ParseException e) {
                // do nothing?
            }

            et.addTextChangedListener(this);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        public void onTextChanged(CharSequence s, int start, int before, int count) {
            hasFractionalPart = s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()));

            try {
                if (!x.getText().toString().trim().isEmpty() && !y.getText().toString().trim().isEmpty()) {
                    double _x = checkLatinNumber(x.getText().toString());
                    double _y = checkLatinNumber(y.getText().toString());
                    result_1.setText(String.format("%s درصد از %s برابر است با: %s.", NumberFormat.getInstance().format(_x),
                            NumberFormat.getInstance().format(_y)
                            , NumberFormat.getInstance().format(_x * 100 / _y)));
                }

                if (!x2.getText().toString().trim().isEmpty() && !y2.getText().toString().trim().isEmpty()) {
                    double _x = checkLatinNumber(x2.getText().toString());
                    double _y = checkLatinNumber(y2.getText().toString());
                    result_2.setText(String.format("%s درصد %s می شود %s.", NumberFormat.getInstance().format(_x),
                            NumberFormat.getInstance().format(_y), NumberFormat.getInstance().format(_x * _y / 100)));
                }
            } catch (Exception e) {
                Log.e("abbas", e.toString());
            }
        }

        private double checkLatinNumber(String value) {
            value = value.replace(",", "").replace("٬", "").replace("۰", "0")
                    .replace("۱", "1").replace("۲", "2").replace("۳", "3")
                    .replace("۴", "4").replace("۵", "5").replace("۶", "6")
                    .replace("۷", "7").replace("۸", "8").replace("۹", "9");
            return Double.parseDouble(value);
        }
    }
}
