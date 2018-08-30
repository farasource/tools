package ghasemi.abbas.abzaar.db.speedmeter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.TextView;

import ghasemi.abbas.abzaar.R;

import com.android.bahaar.FlatButton.FlatButton;
import com.android.bahaar.ToastActivity;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static android.view.View.GONE;
import static ghasemi.abbas.abzaar.Main.Shortcut;


public class SpeedMeter extends AppCompatActivity implements LocationListener, GpsStatus.Listener {

    private static Data data;
    private LocationManager mLocationManager;
    private FlatButton fab;
    private FlatButton refresh;
    private CheckBox autoStop;
    private TextView satellite;
    private TextView status;
    private TextView accuracy;
    private TextView currentSpeed;
    private AVLoadingIndicatorView progress;
    private TextView maxSpeed;
    private TextView averageSpeed;
    private TextView distance;
    private Chronometer time;
    private Data.OnGpsServiceUpdate onGpsServiceUpdate;

    private boolean firstfix;

    public static Data getData() {
        return data;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
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
                Shortcut(getIntent().getIntExtra("position", 0), SpeedMeter.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedmeter);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ToastActivity.Toast(this, "مجوز صادر نشده است", 0);
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"}, 1);
        }

        data = new Data(onGpsServiceUpdate);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //setTitle("");
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        autoStop = findViewById(R.id.autoStop);
        refresh = findViewById(R.id.refresh);
        refresh.setVisibility(GONE);

        onGpsServiceUpdate = new Data.OnGpsServiceUpdate() {
            @Override
            public void update() {
                double maxSpeedTemp = data.getMaxSpeed();
                double distanceTemp = data.getDistance();
                double averageTemp;
                if (autoStop.isChecked()) {
                    averageTemp = data.getAverageSpeedMotion();
                } else {
                    averageTemp = data.getAverageSpeed();
                }

                String speedUnits;
                String distanceUnits;
                speedUnits = "km/h";
                if (distanceTemp <= 1000.0) {
                    distanceUnits = "m";
                } else {
                    distanceTemp /= 1000.0;
                    distanceUnits = "km";
                }

                SpannableString s = new SpannableString(String.format("%.0f %s", maxSpeedTemp, speedUnits));
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - speedUnits.length() - 1, s.length(), 0);
                maxSpeed.setText(s);

                s = new SpannableString(String.format("%.0f %s", averageTemp, speedUnits));
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - speedUnits.length() - 1, s.length(), 0);
                averageSpeed.setText(s);

                s = new SpannableString(String.format("%.3f %s", distanceTemp, distanceUnits));
                s.setSpan(new RelativeSizeSpan(0.5f), s.length() - distanceUnits.length() - 1, s.length(), 0);
                distance.setText(s);
            }
        };

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        satellite = findViewById(R.id.satellite);
        status = findViewById(R.id.status);
        accuracy = findViewById(R.id.accuracy);
        maxSpeed = findViewById(R.id.maxSpeed);
        averageSpeed = findViewById(R.id.averageSpeed);
        distance = findViewById(R.id.distance);
        time = findViewById(R.id.titleTime);
        currentSpeed = findViewById(R.id.currentSpeed);
        progress = findViewById(R.id.progress);

        time.setText("00:00:00");
        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            boolean isPair = true;

            @Override
            public void onChronometerTick(Chronometer chrono) {
                long time;
                if (data.isRunning()) {
                    time = SystemClock.elapsedRealtime() - chrono.getBase();
                    data.setTime(time);
                } else {
                    time = data.getTime();
                }

                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                chrono.setText(hh + ":" + mm + ":" + ss);

                if (data.isRunning()) {
                    chrono.setText(hh + ":" + mm + ":" + ss);
                } else {
                    if (isPair) {
                        isPair = false;
                        chrono.setText(hh + ":" + mm + ":" + ss);
                    } else {
                        isPair = true;
                        chrono.setText("");
                    }
                }

            }
        });
    }

    public void onFabClick(View v) {
        if (!data.isRunning()) {
            fab.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pause, 0);
            fab.setText("توقف");
            data.setRunning(true);
            time.setBase(SystemClock.elapsedRealtime() - data.getTime());
            time.start();
            data.setFirstTime(true);
            startService(new Intent(getBaseContext(), GpsServices.class));
            refresh.setVisibility(GONE);
        } else {
            fab.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.play, 0);
            fab.setText("شروع");
            data.setRunning(false);
            status.setText("");
            stopService(new Intent(getBaseContext(), GpsServices.class));
            refresh.setVisibility(View.VISIBLE);
        }
    }

    public void onRefreshClick(View v) {
        resetData();
        stopService(new Intent(getBaseContext(), GpsServices.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstfix = true;
        if (data == null) {
            data = new Data(onGpsServiceUpdate);
        } else {
            data.setOnGpsServiceUpdate(onGpsServiceUpdate);
        }

        if (mLocationManager.getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
        } else {
            Log.w("MainActivity", "No GPS location provider found. GPS data display will not be available.");
        }

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsDisabledDialog();
        }

        mLocationManager.addGpsStatusListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
        mLocationManager.removeGpsStatusListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), GpsServices.class));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.hasAccuracy()) {
            double acc = location.getAccuracy();
            String units;
            units = "m";
            SpannableString s = new SpannableString(String.format("%.0f %s", acc, units));
            s.setSpan(new RelativeSizeSpan(0.75f), s.length() - units.length() - 1, s.length(), 0);
            accuracy.setText(s);

            if (firstfix) {
                status.setText("");
                fab.setVisibility(View.VISIBLE);
                if (!data.isRunning() && !TextUtils.isEmpty(maxSpeed.getText())) {
                    refresh.setVisibility(View.VISIBLE);
                }
                firstfix = false;
            }
        } else {
            firstfix = true;
        }

        if (location.hasSpeed()) {
            progress.setVisibility(GONE);
            progress.hide();
            double speed = location.getSpeed() * 3.6;
            String units;
            units = "km/h";
            SpannableString s = new SpannableString(String.format(Locale.ENGLISH, "%.0f %s", speed, units));
            s.setSpan(new RelativeSizeSpan(0.25f), s.length() - units.length() - 1, s.length(), 0);
            currentSpeed.setText(s);
        }

    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                int satsInView = 0;
                int satsUsed = 0;
                Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
                for (GpsSatellite sat : sats) {
                    satsInView++;
                    if (sat.usedInFix()) {
                        satsUsed++;
                    }
                }
                satellite.setText(satsUsed + "/" + satsInView);
                if (satsUsed == 0) {
                    fab.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.play, 0);
                    fab.setText("شروع");
                    data.setRunning(false);
                    status.setText("");
                    stopService(new Intent(getBaseContext(), GpsServices.class));
                    fab.setVisibility(View.INVISIBLE);
                    refresh.setVisibility(GONE);
                    accuracy.setText("");
                    status.setText("منتظر باشید");
                    firstfix = true;
                }
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showGpsDisabledDialog();
                }
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
        }
    }

    public void showGpsDisabledDialog() {
        new AlertDialog.Builder(this, R.style.Theme_Dialog_Alert)
                .setTitle("موقعیت مکانی")
                .setMessage("موقعیت مکانی دستگاه شما خاموش است.")
                .setPositiveButton("روشن کردن", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).show();
    }

    public void resetData() {
        fab.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.play, 0);
        fab.setText("شروع");
        refresh.setVisibility(GONE);
        time.stop();
        SpannableString s = new SpannableString("0 m");
        s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 2, s.length(), 0);
        maxSpeed.setText(s);
        averageSpeed.setText(s);
        distance.setText(s);
        time.setText("00:00:00");
        data = new Data(onGpsServiceUpdate);
    }

//    @Override
//    public void onBackPressed() {
//        Intent a = new Intent(Intent.ACTION_MAIN);
//        a.addCategory(Intent.CATEGORY_HOME);
//        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(a);
//    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
