package ghasemi.abbas.abzaar.db.sargarmi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import ghasemi.abbas.abzaar.BuildVars;
import ghasemi.abbas.abzaar.R;

import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;

import ghasemi.abbas.abzaar.db.malii.MainActivityM;
import ghasemi.abbas.abzaar.db.tictactoe.TictactoeActivity;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 06/07/2018.
 */

public class MainActivityS extends AppCompatActivity {
    public static boolean isFontCamera = false;
    RecyclerView list_main;
    TextView main_name;
    String[] NAME = {};
    boolean active;
    String SITE = Environment.getExternalStorageDirectory() + "/abz/.DataAbz/ABZ Images/";


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyDB.getInstance(getApplicationContext()).putInt("version_" + MainActivityS.class.getCanonicalName(), 1);
        stopService(new Intent(getApplicationContext(), BreakService.class));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        main_name = findViewById(R.id.main_name);
        main_name.setText("سرگرمی");
        list_main = findViewById(R.id.list_main);
        list_main.hasFixedSize();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int x = (int) (displayMetrics.widthPixels / displayMetrics.density / 120);
        list_main.setLayoutManager(new GridLayoutManager(MainActivityS.this, x));
        list_main.setAdapter(new MainActivityS.Adapter_Main());
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }


    View view;
    private static int[] array_main_pic = {R.drawable.berak, R.drawable.glass,R.drawable.tic_tac_toe};
    static String[] array_main_txt = {"شکشتن نمایشگر", "زمینه شیشه ای","دوز"};

    class Adapter_Main extends RecyclerView.Adapter<MainActivityS.Adapter_Main.contentViewHolder> {
        @Override
        public MainActivityS.Adapter_Main.contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main, parent, false);
            return new MainActivityS.Adapter_Main.contentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MainActivityS.Adapter_Main.contentViewHolder holder, final int position) {
            if(position == 2 && TinyDB.getInstance(getApplicationContext()).getInt("version_" + TictactoeActivity.class.getCanonicalName(),0) == 0)
            {
                holder.row_main_type.setImageResource(R.drawable.new_item);
            }else {
                holder.row_main_type.setImageDrawable(null);
            }
            holder.row_main_txt.setText(array_main_txt[position]);
            Glide.with(MainActivityS.this)
                    .load(array_main_pic[position])
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.row_main_img);
            holder.row_main_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == 0) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (!Settings.canDrawOverlays(MainActivityS.this)) {
                                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                startActivity(myIntent);
                                return;
                            }
                        }
                        if (active) return;
                        active = true;
                        createNotification();
                        startService(new Intent(getApplicationContext(), BreakService.class));
                        ToastActivity.Toast(MainActivityS.this, "صفحه رو لمس کن :)", Toast.LENGTH_SHORT);
                        /*File s1 = new File(SITE + "screen_1.png");
                        File s2 = new File(SITE + "screen_2.png");
                        Bitmap bmp1 = BitmapFactory.decodeFile(s1.getAbsolutePath());
                        Bitmap bmp2 = BitmapFactory.decodeFile(s2.getAbsolutePath());
                        if(bmp1 == null) Picasso.with(MainActivityS.this).load("https://www.dl.dropboxusercontent.com/s/paiuf1mipx8jte3/screen_1.png").placeholder(R.drawable.ic_launcher).into(getTarget);
                        if(bmp2 == null) Picasso.with(MainActivityS.this).load("https://www.dl.dropboxusercontent.com/s/fxif7oxuqoizg06/screen_2.png").placeholder(R.drawable.ic_launcher).into(getTarget);*/
                    } else if (position == 1) {
                        AlertDialog show = new AlertDialog.Builder(MainActivityS.this,R.style.Theme_Dialog_Alert)
                                .setMessage("کدام دوربین تنظیم شود؟")
                                .setPositiveButton("دوربین جلو", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isFontCamera = true;
                                        startWallpaper();
                                    }
                                })
                                .setNegativeButton("دوربین عقب", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isFontCamera = false;
                                        startWallpaper();
                                    }
                                })
                                .create();
                        show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
                        show.show();
                    }else if(position == 2){
                        startActivity(new Intent(MainActivityS.this, TictactoeActivity.class));
                    }

                }
            });
        }


        @Override
        public int getItemCount() {
            return array_main_txt.length;
        }

        class contentViewHolder extends RecyclerView.ViewHolder {

            CardView row_main_card;
            ImageView row_main_img,row_main_type;
            TextView row_main_txt;

            contentViewHolder(View itemView) {
                super(itemView);

                row_main_card = itemView.findViewById(R.id.row_main_card);
                row_main_img = itemView.findViewById(R.id.row_main_img);
                row_main_txt = itemView.findViewById(R.id.row_main_txt);
                row_main_type = itemView.findViewById(R.id.row_main_type);

            }
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
                HelpAlert(this, "زمینه شیشه ای: می توانید پس زمینه تلفن خود را با دورین جلو یا عقب  تنطیم نمائید! بعد از انتخاب به دنبال نشان ابزار در تصاویر زنده بگردید و آن را لمس و تنطیم نمائید.");
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), MainActivityS.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startWallpaper() {
        setTransparentWallpaper();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SET_WALLPAPER);
        Intent chooser = Intent.createChooser(intent, "لطفا تصاویر زمینه یا تصاویر زمینه متحرک را انتخاب کنید");
        startActivity(chooser);
    }

    private void setTransparentWallpaper() {
        startService(new Intent(this, LiveWallpaper.class));
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivityS.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BuildVars.createChannel(this,"in_app_service","فعالیت های درون برنامه ای"));
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.berak)
                .setContentTitle("شکست صفحه فعال است")
                .setContentText("لمس برای غیر فعال سازی")
                .setAutoCancel(true);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);
    }

}
