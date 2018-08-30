package ghasemi.abbas.abzaar.db;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;

import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

public class Magnetic extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    CheckBox checkBox;
    int x;
    TextView magnetic, find, magnetic_darsad;
    ProgressBar progressBar;
    TinyDB tinyDB;
    Vibrator vibrator;
    LineChart mChart;
    long savedTime = 0;
    private ArrayList<Entry> yVals;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public Boolean isMagneticSensorAvailable() {
        SensorManager sensorManager = (SensorManager) Main.activity.getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        return magneticSensor != null;
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
                Shortcut(getIntent().getIntExtra("position",0), Magnetic.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateData(double val) {
        if (mChart == null) {
            return;
        }
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            LineDataSet set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            Entry entry = new Entry(savedTime, (float) val);
            set1.addEntry(entry);
            if (set1.getEntryCount() > 200) {
                set1.removeFirst();
                set1.setDrawFilled(false);
            }
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            savedTime++;
        }
    }

    private void initChart() {
        mChart = findViewById(R.id.chart1);
        mChart.setViewPortOffsets(50, 20, 5, 60);
        // no description text
        Description description = new Description();
        description.setText("μT");
        mChart.setDescription(description);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        //mChart.setMaxHighlightDistance(400);
        XAxis x = mChart.getXAxis();
        x.setLabelCount(8, false);
        x.setEnabled(true);

        x.setTextColor(0xcc000000);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(true);
        x.setAxisLineColor(0xff78909C);
        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(0xcc000000);

        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(0xffB5C3C9);
        y.setAxisMinimum(0);

        mChart.getAxisRight().setEnabled(true);
        yVals = new ArrayList<Entry>();
        yVals.add(new Entry(0, 0));
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");

        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.02f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setCircleColor(0xcc000000);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(0xcc000000);
        set1.setFillColor(0xffB5C3C9);
        set1.setFillAlpha(100);
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -10;
            }
        });
        LineData data;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            data = mChart.getLineData();
            data.clearValues();
            data.removeDataSet(0);
            data.addDataSet(set1);
        } else {
            data = new LineData(set1);
        }

        data.setValueTextSize(9f);
        data.setDrawValues(false);
        mChart.setData(data);
        mChart.getLegend().setEnabled(false);
        mChart.animateXY(2000, 2000);
        // dont forget to refresh the drawing
        mChart.invalidate();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetic);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!isMagneticSensorAvailable()) {
            ToastActivity.Toast(this, "دستگاه شما مجهز به سنسور فلز یاب نیست!", 0);
            finish();
        }
        tinyDB = new TinyDB(this);
        checkBox = findViewById(R.id.checkBox);
//        checkBox.setChecked(tinyDB.getBoolean("magnetic_vibrator",true));
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                tinyDB.putBoolean("magnetic_vibrator",isChecked);
//            }
//        });
        AppCompatEditText editText = findViewById(R.id.min);
//        editText.setText(tinyDB.getString("magnetic_","80"));
        x = 80;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    x = 80;
                    return;
                }
                x = Integer.parseInt(s.toString());
                if (x == 0) {
                    x++;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        magnetic = findViewById(R.id.magnetic);
        magnetic_darsad = findViewById(R.id.magnetic_darsad);
        find = findViewById(R.id.find);
        progressBar = findViewById(R.id.progressBar);
        initChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double rawTotal;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            rawTotal = Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1]
                    + sensorEvent.values[2] * sensorEvent.values[2]);
            BigDecimal total = new BigDecimal(rawTotal);
            double res = total.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            magnetic.setText(res + " μT");
            int darsad = (int) (100 * res / x);
            progressBar.setProgress(darsad);
            magnetic_darsad.setText(darsad + "%");
            updateData(res);
            if (res < x) {
                find.setText("فلز پیدا نشده.");
                find.setTextColor(0xffD81B60);
            } else {
                find.setText("فلز پیدا شد.");
                find.setTextColor(0xff7CB342);
                if (checkBox.isChecked()) {
                    vibrator.vibrate(100);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
