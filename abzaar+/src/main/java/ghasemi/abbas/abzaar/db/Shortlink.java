package ghasemi.abbas.abzaar.db;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;

import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 06/07/2018.
 */

public class Shortlink extends AppCompatActivity {
    Animation inTop;
    TextView setLink, setHistory;
    EditText getLink;
    RadioButton type, yon;
    Switch history;
    Button Done;
    TinyDB db;
    LinearLayout linearLayout;
    AVLoadingIndicatorView progress;
    View view;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new TinyDB(getApplicationContext());
        setContentView(R.layout.activity_shl);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progress = findViewById(R.id.progress);
        progress.hide();
        view = findViewById(R.id.view);
        setLink = findViewById(R.id.setShL);
        setLink.setTextIsSelectable(true);
        getLink = findViewById(R.id.getLink);
        Done = findViewById(R.id.Done);
        linearLayout = findViewById(R.id.LinearLayout);
        inTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button);
        type = findViewById(R.id.Bitly);
        yon = findViewById(R.id.noBitly);
        history = findViewById(R.id.history);
        setHistory = findViewById(R.id.msg);
        setHistory.setTextIsSelectable(true);
        history.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.putString("shortLinkHistory", "");
                db.putBoolean("isHistoryShortLink", isChecked);
                if (isChecked) {
                    setHistory.setVisibility(View.VISIBLE);
                } else {
                    setHistory.setVisibility(View.GONE);
                }

            }
        });
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Url = getLink.getText().toString().length();
                if (Url <= 18) {
                    ToastActivity.Toast(Shortlink.this, "لینک شما از قبل کوتاه است.", Toast.LENGTH_SHORT);
                } else if (!Main.CheckNetworkStatus(Shortlink.this)) {
                    ToastActivity.Toast(Shortlink.this, "دستگاه شما به اینترنت متصل نیست.", Toast.LENGTH_SHORT);
                } else {
                    if (getLink.getText().toString().startsWith("http://") || getLink.getText().toString().startsWith("https://")) {
                        if (type.isChecked()) {
                            Date date = new Date();
                            if (db.getInt("Bit-ly-Record", 0) > 2) {
                                Snackbar.make(v, "روزانه تنها 3 لینک با Bitly قابل کوتاه سازیست.", Snackbar.LENGTH_LONG).setAction("باشه", null).show();
                                return;
                            }
                            db.putInt("Bit-ly-Record", db.getInt("Bit-ly-Record", 0) + 1);
                            db.putLong("Bit-ly-last", date.getTime());
                        }
                        GetLink(getApiLink());
                    } else {
                        ToastActivity.Toast(Shortlink.this, "خطا! لینک را بررسی کنید.", Toast.LENGTH_SHORT);
                    }

                }
            }
        });

        Calendar();
    }

    private void Calendar() {
        Date date = new Date();
        long d = date.getTime();
        long l = db.getLong("Bit-ly-last", 0);

        if (d - l >= 86400000) {
            db.putInt("Bit-ly-Record", 0);
        }
        if (!db.getBoolean("isHistoryShortLink", true)) {
            history.setChecked(false);
            setHistory.setVisibility(View.GONE);
        } else {
            if (!db.getString("shortLinkHistory").equals("")) {
                setHistory.setText(db.getString("shortLinkHistory"));
            }
        }
    }

    void GetLink(final String url) {
        progress.show();
        view.setVisibility(View.VISIBLE);
        Done.setEnabled(false);
        getLink.setEnabled(false);
        history.setEnabled(false);
        type.setEnabled(false);
        yon.setEnabled(false);
        linearLayout.setVisibility(View.GONE);
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(final Call call, Response response) throws IOException {

                    final String myResponse = response.body().string();

                    Shortlink.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObj = new JSONObject(myResponse);
                                String Url;
                                String Status = type.isChecked() ? jsonObj.getString("status_txt") : jsonObj.getString("status");
                                if (!Status.equals("OK") && !Status.equals("true")) {
                                    ToastActivity.Toast(Shortlink.this, "خطا!", Toast.LENGTH_SHORT);
                                    return;
                                }
                                if (type.isChecked()) {
                                    JSONObject jsonObject = jsonObj.getJSONObject("data");
                                    Url = jsonObject.getString("url");
                                } else {
                                    Url = "http://yon.ir/" + jsonObj.getString("output");
                                }
                                String data = Url + " -> " + getLink.getText().toString() + "\n";
                                db.putString("shortLinkHistory", data + db.getString("shortLinkHistory"));
                                setHistory.setText(db.getString("shortLinkHistory"));
                                setLink.setText(Url);
                                linearLayout.setVisibility(View.VISIBLE);
                                linearLayout.startAnimation(inTop);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastActivity.Toast(Shortlink.this, "خطا! ، سرور ارسالی شما را رد می کند.", Toast.LENGTH_SHORT);
                            }
                            progress.setVisibility(View.GONE);
                            progress.hide();
                            view.setVisibility(View.GONE);
                            Done.setEnabled(true);
                            getLink.setEnabled(true);
                            history.setEnabled(true);
                            type.setEnabled(true);
                            yon.setEnabled(true);
                        }
                    });

                }

            });
        } catch (Exception e) {
            progress.setVisibility(View.GONE);
            progress.hide();
            view.setVisibility(View.GONE);
            Done.setEnabled(true);
            getLink.setEnabled(true);
            history.setEnabled(true);
            type.setEnabled(true);
            yon.setEnabled(true);
            ToastActivity.Toast(this, "خطای 211،تلاش مجدد", Toast.LENGTH_SHORT);
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
            case R.id.item_help:
                HelpAlert(this, "با استفاده از این بخش برنامه می توانید لینک های طولانی خود را به لینک های کوتاه تبدیل نمائید \n این امکان وجود دارد که آمار کلیک بر روی لینک کوتاه شده خود را داشته باشید،برای این کار تنها کافیست به آخر لینک کوتاه شده خود، + اضافه نمائید. \n\n توجه: هر روز تنها 3 بار مجاز به کوتاه کردن لینک توسط bitly هستید و برای تعداد دفعات بیشتر باید از گزینه (دیگر) استفاده کنید ، همچنین تمامی لینک های ایجاد شده به سئو سایت شما کمک می کنند.");
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), Shortlink.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getApiLink() {
        String Api = "";
        if (type.isChecked()) {
            Api = "https://api-ssl.bitly.com/v3/shorten?access_token=56daac6aceeec1db534462fa93990f9d2c7fbe23&longUrl=" + getLink.getText().toString();
        } else {
            Api = "http://api.yon.ir/?format=json&url=" + URLEncoder.encode(getLink.getText().toString());
        }
        return Api;
    }
}