package ghasemi.abbas.abzaar.db.foucault;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ablanco.zoomy.ZoomListener;
import com.ablanco.zoomy.Zoomy;

import ghasemi.abbas.abzaar.Application;
import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;
import com.android.bahaar.ToastActivity;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Record extends AppCompatActivity {
    RecyclerView pager;
    TextView main_name;
    View view;
    ArrayList<HashMap<String, Object>> imgLogList;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Main.activity == null){
            Main.activity = this;
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        main_name = findViewById(R.id.main_name);
        main_name.setText("تصاویر ضبط شده");
        imgLogList = TSQL.getInstanse().getImgSave();
        pager = findViewById(R.id.list_main);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int x = (int) (displayMetrics.widthPixels / displayMetrics.density / 180);
        pager.setLayoutManager(new GridLayoutManager(this, x));
        pager.setAdapter(new Adapter());
        if (imgLogList.isEmpty()) {
            ToastActivity.Toast(this, "چیزی برای نمایش وجود ندارد.", Toast.LENGTH_SHORT);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (imgLogList != null) {
            if (!imgLogList.isEmpty()) {
                getMenuInflater().inflate(R.menu.delete, menu);
            }
        } else {
            getMenuInflater().inflate(R.menu.delete, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            onBackPressed();
        } else if (item.getItemId() == R.id.item_delete) {
            View inflate = getLayoutInflater().inflate(R.layout.dialog_sheet_hidden, null);
            TextView title = inflate.findViewById(R.id.title);
            title.setText("آیا می خواهید همه ی تصاویر حذف گردد؟");
            final BottomSheetDialog dialog = new BottomSheetDialog(Record.this);
            dialog.setContentView(inflate);
            dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
            inflate.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            inflate.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    TSQL.getInstanse().deleteImgSave(-1, null);
                    recreate();
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.contentViewHolder> {
        @Override
        public contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_foucault, parent, false);
            return new contentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(contentViewHolder holder, final int position) {
            final HashMap<String, Object> hashMap = imgLogList.get(position);
            holder.row_main_img.setImageURI(Uri.parse((String) hashMap.get("img")));
            holder.row_main_txt.setText((String) hashMap.get("date"));
        }

        @Override
        public int getItemCount() {
            return imgLogList.size();
        }

        class contentViewHolder extends RecyclerView.ViewHolder {

            ImageView row_main_img;
            TextView row_main_txt;
            View view;

            contentViewHolder(View itemView) {
                super(itemView);

                view = itemView;
                row_main_img = itemView.findViewById(R.id.row_main_img);
                row_main_txt = itemView.findViewById(R.id.row_main_txt);
                Zoomy.Builder builder = new Zoomy.Builder(Record.this).target(row_main_img);
                builder.zoomListener(new ZoomListener() {
                    @Override
                    public void onViewStartedZooming(View view) {

                    }

                    @Override
                    public void onViewEndedZooming(View view) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                                }
                            }
                        },200);
                    }
                });

                builder.register();
                itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View inflate = getLayoutInflater().inflate(R.layout.dialog_sheet_hidden, null);
                        TextView title = inflate.findViewById(R.id.title);
                        title.setText("آیا می خواهید تصویر انتخابی حذف گردد؟");
                        final BottomSheetDialog dialog = new BottomSheetDialog(Record.this);
                        dialog.setContentView(inflate);
                        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
                        inflate.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        inflate.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                TSQL.getInstanse().deleteImgSave((Integer) imgLogList.get(getAdapterPosition()).get("id"), (String) imgLogList.get(getAdapterPosition()).get("img"));
                                recreate();
                            }
                        });
                        dialog.show();
                    }
                });
            }
        }

    }
}
