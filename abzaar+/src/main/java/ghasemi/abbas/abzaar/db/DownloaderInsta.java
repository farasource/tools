package ghasemi.abbas.abzaar.db;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import androidx.appcompat.widget.AppCompatEditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.core.app.ActivityCompat;
import ghasemi.abbas.abzaar.BuildVars;
import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;

import com.android.bahaar.ToastActivity;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 04/25/2018.
 */

public class DownloaderInsta extends AppCompatActivity {
    String VideoUrl = "";
    String Username = "";
    String PicUrl = "";
    String Type = "";
    String ProfilePicUrl = "";
    String shortcode = "";
    String Text = "";
    Animation inTop;
    TextView setType, setName, setUser, textFile;
    AppCompatEditText getLink;
    Button Done, saveFile;
    ImageView picUser, pic;
    LinearLayout linearLayout;
    int x = 0;
    private AVLoadingIndicatorView progress;
    private View view;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dl_insta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progress = findViewById(R.id.progress);
        progress.hide();
        view = findViewById(R.id.view);
        setType = findViewById(R.id.setType);
        setName = findViewById(R.id.setName);
        setUser = findViewById(R.id.setUser);
        textFile = findViewById(R.id.textFile);
        textFile.setTextIsSelectable(true);
        getLink = findViewById(R.id.getLink);
        picUser = findViewById(R.id.picUser);
        pic = findViewById(R.id.pic);
        Done = findViewById(R.id.Done);
        saveFile = findViewById(R.id.saveFile);
        linearLayout = findViewById(R.id.LinearLayout);
        inTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ToastActivity.Toast(this,"دسترسی به حافظه به برنامه داده نشده است.",0);
            Done.setEnabled(false);
            Done.setText("خطای دسترسی");
        }
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Main.CheckNetworkStatus(DownloaderInsta.this)) {
                    ToastActivity.Toast(DownloaderInsta.this, "دستگاه شما به اینترنت متصل نیست.", Toast.LENGTH_SHORT);
                } else {
                    if (getLink.getText().toString().startsWith("https://www.instagram.com/p/") || getLink.getText().toString().startsWith("http://www.instagram.com/p/")) {
                        String uei = getLink.getText().toString().replace("https://www.instagram.com/p/", "").replace("https://instagram.com/p/", "");
                        String[] code = uei.split("/");
                        shortcode = code[0];
                        GetFile();
                    } else {
                        int ecolor = Color.WHITE;
                        ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
                        getLink.setFocusableInTouchMode(true);
                        getLink.requestFocus();
                        String estring = "خطا! لینک را بررسی کنید.";
                        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
                        ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                        getLink.setError(ssbuilder);
                    }

                }
            }
        });
        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Type.equals("GraphImage")) {
                    fileDownload(PicUrl, "Images", "");
                } else {
                    fileDownload(VideoUrl, "Videos", "");
                }
            }
        });
        textFile.setOnClickListener(new View.OnClickListener() {
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
                    ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setText(Text);
                    ToastActivity.Toast(DownloaderInsta.this, "متن فایل کپی شد.", Toast.LENGTH_SHORT);

                }
            }
        });
        picUser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (BuildVars.ADAD_MARKET != 1) {
                    fileDownload(ProfilePicUrl, "Images", Username);
                }
                return false;
            }
        });

    }

    void GetFile() {
        view.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        progress.show();
        linearLayout.setVisibility(View.GONE);
        Done.setEnabled(false);
        getLink.setEnabled(false);
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://www.instagram.com/p/" + shortcode + "/?__a=1")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(final Call call, Response response) throws IOException {

                    final String myResponse = response.body().string();

                    DownloaderInsta.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObj = new JSONObject(myResponse);
                                JSONObject jsonObject = jsonObj.getJSONObject("graphql");
                                JSONObject object = jsonObject.getJSONObject("shortcode_media");
                                JSONObject owner = object.getJSONObject("owner");
                                JSONObject edge_media_to_caption = object.getJSONObject("edge_media_to_caption");
                                JSONArray edges = edge_media_to_caption.getJSONArray("edges");
                                Type = object.getString("__typename");
                                ProfilePicUrl = owner.getString("profile_pic_url");
                                Username = owner.getString("username");
                                String FullName = owner.getString("full_name");
                                PicUrl = object.getString("display_url");
                                try {
                                    JSONObject node = edges.getJSONObject(0);
                                    JSONObject text = node.getJSONObject("node");
                                    Text = text.getString("text");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    VideoUrl = object.getString("video_url");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                view.setVisibility(View.GONE);
                                progress.setVisibility(View.GONE);
                                progress.hide();
                                Done.setEnabled(true);
                                getLink.setEnabled(true);
                                if (Type.equals("GraphImage")) {
                                    setType.setText("نوع: تصویر");
                                    setName.setText("نام کاربری: " + FullName);
                                    setUser.setText("یوزر نیم: " + Username);
                                    Glide.with(DownloaderInsta.this).load(ProfilePicUrl).placeholder(R.mipmap.ic_launcher).into(picUser);
                                    Glide.with(DownloaderInsta.this).load(PicUrl).into(pic);
                                    if (!Text.equals("")) {
                                        textFile.setText("متن : \n\n" + Text);
                                    } else {
                                        textFile.setText("متن : \n\n" + "نویسنده برای این تصویر ، متنی ننوشته است!.");
                                    }
                                    linearLayout.setVisibility(View.VISIBLE);
                                    linearLayout.startAnimation(inTop);
                                } else if (Type.equals("GraphVideo")) {
                                    setType.setText("نوع : ویدئو");
                                    setName.setText("نام کاربری: " + FullName);
                                    setUser.setText("یوزر نیم: " + Username);
                                    Glide.with(DownloaderInsta.this).load(ProfilePicUrl).placeholder(R.mipmap.ic_launcher).into(picUser);
                                    Glide.with(DownloaderInsta.this).load(PicUrl).into(pic);
                                    if (!Text.equals("")) {
                                        textFile.setText("متن : \n\n" + Text);
                                    } else {
                                        textFile.setText("متن : \n\n" + "نویسنده برای این ویدئو ، متنی ننوشته است!.");
                                    }
                                    linearLayout.setVisibility(View.VISIBLE);
                                    linearLayout.startAnimation(inTop);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastActivity.Toast(DownloaderInsta.this, "خطا! اینستاگرام پاسخ نمی دهد.", Toast.LENGTH_SHORT);
                            }
                        }
                    });

                }

            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (view.getVisibility() == View.VISIBLE) {
                        view.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        progress.hide();
                        Done.setEnabled(true);
                        getLink.setEnabled(true);
                        ToastActivity.Toast(DownloaderInsta.this, "خطای 210،تلاش مجدد،نتیجه نامشخص", Toast.LENGTH_SHORT);
                    }
                }
            }, 20000);
        } catch (Exception e) {
            view.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            progress.show();
            Done.setEnabled(true);
            getLink.setEnabled(true);
            ToastActivity.Toast(this, "خطای 211،تلاش مجدد", Toast.LENGTH_SHORT);
        }
    }


    public void fileDownload(String uRl, String type, String name) {
        String t = name + ".jpg";
        if (name.equals("")) {
            if (type.equals("Videos")) {
                t = getName(uRl);
            } else {
                t = getName(uRl);
            }
        }
        try {
            File direct = new File(Environment.getExternalStorageDirectory() + "/abz/Instagram/" + type);
            if (!direct.exists()) {
                direct.mkdirs();
            }
            DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uRl));
            request.setAllowedNetworkTypes(3).setAllowedOverRoaming(true).setTitle(t).setDescription("در حال بارگیری ...").setDestinationInExternalPublicDir("/abz/Instagram/" + type, t);
            mgr.enqueue(request);
            ToastActivity.Toast(this, "دانلود آغاز شد.", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            ToastActivity.Toast(this, "خطا! لطفا دسترسی های لازم داده شود.", Toast.LENGTH_SHORT);
        }
    }

    private String getName(String link) {
        String[] t = link.split("/");
        int c = t.length;
        String s = t[c - 1];
        s = s.replace("%20", " ");
        return s;

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
                String msg = "";
                if (BuildVars.ADAD_MARKET != 1) {
                    msg = "ابتدا لینک تصویر یا ویدئو را وارد و سپس تائید نمایید،پس از بررسی ، فایل و متن آن به نمایش در می آید ، شما می توانید متنون فایل ها را با لمس کردن یا 3 بار لمس کپی نمائید،برای دانلود تصویر پروفایل تنها کافیست ، تصویر پروفایل لود شده را لمس طولانی نمائید(برای دانلود تصویر پروفایل حتما باید یک لینک از پست آن شخص را وارد نمائید،نه لینک دیگری را) \n تمامی فایل های بارگیری شده در پوشه abz ذخیره می گردند.";
                } else {
                    msg = "ابتدا لینک تصویر یا ویدئو را وارد و سپس تائید نمایید،پس از بررسی ، فایل و متن آن به نمایش در می آید ، شما می توانید متنون فایل ها را با لمس کردن یا 3 بار لمس کپی نمائید.(برای حفظ حریم خصوصی کاربران و با توجه به قوانین کافه بازار امکان دانلود تصاویر پروفایل امکان پذیر نیست) \n تمامی فایل های بارگیری شده در پوشه abz ذخیره می گردند.";
                }
                HelpAlert(this, msg);
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), DownloaderInsta.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
