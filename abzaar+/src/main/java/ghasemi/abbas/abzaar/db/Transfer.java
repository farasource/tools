package ghasemi.abbas.abzaar.db;

import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;

import com.android.bahaar.ToastActivity;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 04/24/2018.
 */

public class Transfer extends AppCompatActivity {
    Animation inTop;
    EditText getText;
    TextView setText;
    LinearLayout linearLayout;
    Button done;
    int x = 0;
    Spinner sp1, sp2;
    String[] first = {"فارسی", "انگلیسی", "فرانسوی", "عربی", "آلمانی", "روسی", "ترکی", "چینی (s)", "کره ای", "لاتین"};
    String[] second = {"فارسی", "انگلیسی", "فرانسوی", "عربی", "آلمانی", "روسی", "ترکی", "چینی (s)", "کره ای", "لاتین"};
    String languageTransfer = "";
    String ID_3;
    RadioGroup radioGroup;
    AVLoadingIndicatorView progress;
    View view;
    private int ID_1, ID_2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progress = findViewById(R.id.progress);
        progress.hide();
        view = findViewById(R.id.view);
        inTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button);
        getText = findViewById(R.id.getText);
        setText = findViewById(R.id.setText);
        setText.setTextIsSelectable(true);
        sp1 = findViewById(R.id.spinner1);
        linearLayout = findViewById(R.id.LinearLayout);
        sp2 = findViewById(R.id.spinner2);
        done = findViewById(R.id.Done);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, first);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, second);
        adapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter1);
        sp2.setAdapter(adapter2);
        radioGroup = findViewById(R.id.radioGroup);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                ID_1 = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                ID_2 = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID_3 = getText.getText().toString();
                if (ID_1 == ID_2) {
                    ToastActivity.Toast(Transfer.this, "هر دو زبان نمی توانند یکسان باشند!", Toast.LENGTH_SHORT);
                    return;
                } else if (ID_3.equals("")) {
                    ToastActivity.Toast(Transfer.this, "متن ترجمه نمی تواند خالی باشد!", Toast.LENGTH_SHORT);
                    return;
                } else if (ID_3.length() > 1500) {
                    ToastActivity.Toast(Transfer.this, "متن شما طولانی است! لطفا " + String.valueOf(ID_3.length() - 1500) + " کاراکتر از آن کم نمایید.", Toast.LENGTH_SHORT);
                    return;
                } else if (!Main.CheckNetworkStatus(Transfer.this)) {
                    ToastActivity.Toast(Transfer.this, "دستگاه شما به شبکه متصل نیست!", Toast.LENGTH_SHORT);
                    return;
                }
                GetTrans(radioGroup.getCheckedRadioButtonId() == R.id.radioGroup1 ? 1 : 2);
            }
        });
        setText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x <= 2) {
                    x++;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            x = 0;
                        }
                    }, 2000);
                } else {
                    ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setText(languageTransfer);
                    ToastActivity.Toast(Transfer.this, "کل متن ترجمه کپی شد.", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    void GetTrans(final int type) {
        String url = "https://www.tastemylife.com/gtr.php";
        if (type == 2) {
            url = "https://translate.yandex.net/api/v1.5/tr.json/translate?text="
                    + getTextTrans() + "&lang=" + getIdLanguageFirst() + "-" + getIdLanguageSecond() + "&key=" +
                    "trnsl.1.1.20190208T084700Z.3bfe61c4f0c434b1.a49f839fbf27b8415e19a10b233177e3a561a057";
        }
        view.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        progress.show();
        getText.setEnabled(false);
        sp1.setEnabled(false);
        sp2.setEnabled(false);
        radioGroup.setEnabled(false);
        done.setEnabled(false);
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("sl", getIdLanguageFirst())
                    .addFormDataPart("tl", getIdLanguageSecond())
                    .addFormDataPart("p", "1")
                    .addFormDataPart("q", getTextTrans())
                    .build();
            OkHttpClient client = new OkHttpClient();
            Request request;
            if (type == 2) {
                request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
            }
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(final Call call, Response response) throws IOException {

                    final String myResponse = response.body().string();

                    Transfer.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(View.GONE);
                            progress.setVisibility(View.GONE);
                            progress.hide();
                            done.setEnabled(true);
                            getText.setEnabled(true);
                            sp1.setEnabled(true);
                            sp2.setEnabled(true);
                            try {
                                JSONObject jsonObj = new JSONObject(myResponse);
                                languageTransfer = "";
                                String result;
                                if (type == 1) {
                                    result = jsonObj.getString("result");
                                    languageTransfer = result;
                                } else {
                                    int code = jsonObj.getInt("code");
                                    switch (code) {
                                        case 200:
                                            result = jsonObj.getJSONArray("text").getString(0);
                                            languageTransfer = result;
                                            break;
                                        case 404:
                                        case 413:
                                            setText.setText("طول متن ترجمه بیش از اندازه است.");
                                            linearLayout.setVisibility(View.VISIBLE);
                                            linearLayout.startAnimation(inTop);
                                            return;
                                        case 422:
                                            setText.setText("این متن قابل ترجمه نیست.");
                                            linearLayout.setVisibility(View.VISIBLE);
                                            linearLayout.startAnimation(inTop);
                                            return;
                                        case 402:
                                            setText.setText("سرویس فعلا در دسترس نیست.");
                                            linearLayout.setVisibility(View.VISIBLE);
                                            linearLayout.startAnimation(inTop);
                                            return;
                                        default:
                                            setText.setText("خطا در ترجمه");
                                            linearLayout.setVisibility(View.VISIBLE);
                                            linearLayout.startAnimation(inTop);
                                            return;
                                    }
                                }
                            } catch (JSONException e) {
                                //
                            }
                            if (languageTransfer.startsWith("DOCTYPE")) {
                                setText.setText("خطا در ترجمه \n\n علت:\n کاراکتر های غیر مجاز در متن دیده شد.");
                                linearLayout.setVisibility(View.VISIBLE);
                                linearLayout.startAnimation(inTop);
                            } else if (languageTransfer.equals("")) {
                                setText.setText("خطا در ترجمه \n\n علت:\n ارتباط با سرور برقرار نشد.");
                                linearLayout.setVisibility(View.VISIBLE);
                                linearLayout.startAnimation(inTop);
                            } else {
                                setText.setText(languageTransfer);
                                linearLayout.setVisibility(View.VISIBLE);
                                linearLayout.startAnimation(inTop);
                            }
                        }
                    });

                }

            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (languageTransfer.equals("") && progress.getVisibility() == View.VISIBLE) {
                        view.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        progress.hide();
                        done.setEnabled(true);
                        getText.setEnabled(true);
                        sp1.setEnabled(true);
                        sp2.setEnabled(true);
                        ToastActivity.Toast(getApplicationContext(), "خطای 210،تلاش مجدد", Toast.LENGTH_SHORT);
                    }
                }
            }, 20000);
        } catch (Exception e) {
            view.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            progress.hide();
            done.setEnabled(true);
            getText.setEnabled(true);
            sp1.setEnabled(true);
            sp2.setEnabled(true);
            ToastActivity.Toast(this, "خطای 211،تلاش مجدد", Toast.LENGTH_SHORT);
        }
    }

    public String getIdLanguageFirst() {
        String[] LanguageFirst = {"fa", "en", "fr", "ar", "de", "ru", "tr", "zh", "ko", "la"};
        return LanguageFirst[ID_1];
    }

    public String getIdLanguageSecond() {
        String[] LanguageSecond = {"fa", "en", "fr", "ar", "de", "ru", "tr", "zh", "ko", "la"};
        return LanguageSecond[ID_2];

    }

    public String getTextTrans() {
        return ID_3;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        menu.findItem(R.id.item_record).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), Transfer.class.getCanonicalName());
                break;
            case R.id.item_help:
                HelpAlert(this,"قدرت گرفته از سایت:\n" + "- tastemylife.com server 1\n" + "- yandex.net server 2");
                break;
            case R.id.item_record:
                askSpeechInput();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "بگو ترجمه کنم...");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, "متاسفانه در دستگاه شما پشتیبانی نمی شود.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    getText.setText(result.get(0));
                }
                break;
            }

        }
    }
}