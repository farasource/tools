package ghasemi.abbas.abzaar.db.malii;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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

import com.bumptech.glide.Glide;
import com.android.bahaar.TinyDB;

import ghasemi.abbas.abzaar.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 06/07/2018.
 */

public class MainActivityM extends AppCompatActivity {
    static String[] array_main_txt = {"سود سپرده", "محاسبات وام", "کمیسیون املاک", "درصد"};
    private static int[] array_main_pic = {R.drawable.malii_ss, R.drawable.malii_mv, R.drawable.commission, R.drawable.percentage};
    RecyclerView list_main;
    TextView main_name;
    View view;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyDB.getInstance(getApplicationContext()).putInt("version_" + MainActivityM.class.getCanonicalName(), 1);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        main_name = findViewById(R.id.main_name);
        main_name.setText("مالی");
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        list_main = findViewById(R.id.list_main);
        list_main.hasFixedSize();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int x = (int) (displayMetrics.widthPixels / displayMetrics.density / 120);
        list_main.setLayoutManager(new GridLayoutManager(MainActivityM.this, x));
        list_main.setAdapter(new MainActivityM.Adapter_Main(MainActivityM.this));
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
                Shortcut(getIntent().getIntExtra("position", 0), MainActivityM.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class Adapter_Main extends RecyclerView.Adapter<MainActivityM.Adapter_Main.contentViewHolder> {
        Context context;

        public Adapter_Main(Context c) {
            context = c;
        }

        @Override
        public MainActivityM.Adapter_Main.contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main, parent, false);
            return new MainActivityM.Adapter_Main.contentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MainActivityM.Adapter_Main.contentViewHolder holder, final int position) {
            holder.row_main_txt.setText(array_main_txt[position]);
            Glide.with(MainActivityM.this)
                    .load(array_main_pic[position])
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.row_main_img);
            holder.row_main_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == 0) {
                        Intent intent = new Intent(MainActivityM.this, SoodSep.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (position == 1) {
                        Intent intent = new Intent(MainActivityM.this, SoodVam.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (position == 2) {
                        Intent intent = new Intent(MainActivityM.this, commission.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (position == 3) {
                        Intent intent = new Intent(MainActivityM.this, percentage.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                }
            });
            if ((position == 2 && TinyDB.getInstance(getApplicationContext()).getInt("version_" + commission.class.getCanonicalName(), 0) == 0)) {
                holder.row_main_type.setImageResource(R.drawable.edit_item);
            } else {
                holder.row_main_type.setImageDrawable(null);
            }
        }


        @Override
        public int getItemCount() {
            return array_main_txt.length;
        }

        public class contentViewHolder extends RecyclerView.ViewHolder {

            CardView row_main_card;
            ImageView row_main_img,row_main_type;
            TextView row_main_txt;

            public contentViewHolder(View itemView) {
                super(itemView);

                row_main_type = itemView.findViewById(R.id.row_main_type);
                row_main_card = itemView.findViewById(R.id.row_main_card);
                row_main_img = itemView.findViewById(R.id.row_main_img);
                row_main_txt = itemView.findViewById(R.id.row_main_txt);

            }
        }

    }
}
