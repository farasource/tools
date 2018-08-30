package ghasemi.abbas.abzaar.db;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import ghasemi.abbas.abzaar.R;

import com.android.bahaar.FlatButton.FlatButton;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;
import static ghasemi.abbas.abzaar.Main.soot;

/**
 * on 06/07/2018.
 */

public class DHA extends AppCompatActivity {
    TextView Fe, Se;
    EditText time;
    int f = R.raw.bi;
    int s = 100;
    FlatButton Done;
    SeekBar Fer, Sed;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dha_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Fe = findViewById(R.id.textF);
        Se = findViewById(R.id.textS);
        time = findViewById(R.id.sleep);
        Fer = findViewById(R.id.seekBar1);
        Sed = findViewById(R.id.seekBar2);
        Done = findViewById(R.id.DONE);

        Fer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        f = R.raw.he;
                        Fe.setText("18K Hz");
                        return;
                    case 1:
                        f = R.raw.no;
                        Fe.setText("19K Hz");
                        return;
                    case 2:
                        f = R.raw.bi;
                        Fe.setText("20K Hz");
                        return;
                    case 3:
                        f = R.raw.by;
                        Fe.setText("21K Hz");
                        return;
                    case 4:
                        f = R.raw.bd;
                        Fe.setText("22K Hz");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Sed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                s = progress;
                Se.setText(String.valueOf(progress) + " %");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getSoot()) {
                    float t = 0;
                    try {
                        t = Float.parseFloat(time.getText().toString());
                    } catch (Exception e) {
                        Log.e("abc", "No Parameter");
                    }
                    SootActive(f, s, t);
                    Done.setText("غیر فعال کردن");
                    Sed.setEnabled(false);
                    Fer.setEnabled(false);
                    time.setEnabled(false);
                    if (t != 0) Snackbar.make(v,
                            "زمان سنج نابود کننده برای " + String.valueOf(t) + " دقیقه دیگر فعال شد.", Snackbar.LENGTH_LONG)
                            .setAction("باشه", null)
                            .show();
                } else {
                    Done.setText("فعال کردن");
                    Sed.setEnabled(true);
                    Fer.setEnabled(true);
                    time.setEnabled(true);
                    try {
                        soot.stop();
                        soot = null;
                    } catch (Exception e) {
//
                    }
                }
            }
        });

        if (getSoot()) {
            Done.setText("غیر فعال کردن");
            Sed.setEnabled(false);
            Fer.setEnabled(false);
            time.setEnabled(false);
        } else {
            Sed.setEnabled(true);
            Fer.setEnabled(true);
            time.setEnabled(true);
            Done.setText("فعال کردن");
        }

    }

    private void SootActive(int s, int v, float t) {
        soot = MediaPlayer.create(this, s);
        soot.setLooping(true);
        soot.setVolume(v, v);
        soot.start();
        if (t != 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (soot.isPlaying()) {
                            soot.stop();
                            soot = null;
                        }
                    } catch (Exception e) {
                        //
                    }
                    Done.setText("فعال کردن");
                    Sed.setEnabled(true);
                    Fer.setEnabled(true);
                    time.setEnabled(true);
                }
            }, (long) (t * 60000));
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
                Shortcut(getIntent().getIntExtra("position",0), DHA.class.getCanonicalName());
                break;
            case R.id.item_help:
                HelpAlert(this, "کارکرد برنامه چگونه است؟ \n در این بخش با فعال سازی برنامه امواجی با فرکانس 18 تا 22 هزار Hz (محدوه شنوائی حشرات) ایجاد می کند،این امواج بر روی فعالیت عصبی حشرات تاثیر می گذارد و به همین جهت حشرات از تلفن شما فاصله گرفته و دور میشوند. \n\n نکات مهم در استفاده:\n تولید فرکانس زیر 20 هزاز Hz در محدوده شنوائی انسان نیز هست،به همین جهت ممکن است موجب ناراحتی افراد خردسال یا ... که شنوائی بهتری دارند بشود،به همین جهت می توانید از فرکانس های بالاتر استفاده کنید که به هیچ عنوان قابل شنیده شدن برای انسان نیستند.\n صدای تلفن شما حتما باید بیش از 50 درصد باشد،هرچقدر صدا بیشتر باشد فاصله حشرات دور تر میگردد \n قرار دادن تلفن به گونه ای که موجب کاهش صدای خروجی گردد مثل گرفتن انگشت در مقابل پخش کننده،موجب عملکرد ضعیف برنامه میگردد. ");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean getSoot() {
        boolean active = false;
        try {
            active = soot.isPlaying();
            return active;
        } catch (Exception e) {
            Log.e("ABC", "Not Active");
            return active;
        }
    }
}
