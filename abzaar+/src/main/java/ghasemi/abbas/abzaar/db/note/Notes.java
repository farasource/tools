package ghasemi.abbas.abzaar.db.note;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ghasemi.abbas.abzaar.R;

import com.android.bahaar.ToastActivity;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 07/10/2018.
 */

public class Notes extends AppCompatActivity {

    ArrayList<Bundle> bundles = new ArrayList<>();
    private TextView textView;
    private ad adapter;
    private AVLoadingIndicatorView progress;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = findViewById(R.id.activity_name);
        title.setText(getIntent().getExtras().getString("title"));
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        FrameLayout frameLayout = new FrameLayout(this);
        textView = new TextView(this);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/sans.ttf"));
        textView.setGravity(Gravity.CENTER);
        textView.setVisibility(View.GONE);
        frameLayout.addView(textView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(recyclerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        progress = new AVLoadingIndicatorView(this);
        progress.setIndicatorColor(Color.BLACK);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) getResources().getDimension(R.dimen.w_p), (int) getResources().getDimension(R.dimen.h_p));
        layoutParams.gravity = Gravity.CENTER;
        frameLayout.addView(progress, layoutParams);

        linearLayout.addView(frameLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        adapter = new ad();
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!getIntent().getExtras().getBoolean("isFav")) {
            getMenuInflater().inflate(R.menu.note, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), Notes.class.getCanonicalName());
                break;
            case R.id.item_fav: {
                Intent intent = new Intent(Notes.this, Notes.class);
                intent.putExtra("title", "نشان شده ها");
                intent.putExtra("isFav", true);
                startActivity(intent);
            }
            break;
            case R.id.item_new:
                Intent intent = new Intent(Notes.this, newNote.class);
                intent.putExtra("title", "یادداشت جدید");
                startActivity(intent);
                break;
            case R.id.item_save:
                dialog("آیا می خواهید فایل پشتیبانی ایجاد گردد؟", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (DB.getInstanse().backupDB()) {
                            ToastActivity.Toast(Notes.this, "انجام شد.", Toast.LENGTH_SHORT);
                        }
                    }
                });
                break;
            case R.id.item_resave:
                dialog("آیا می خواهید فایل پشتیبانی بارگذاری گردد؟", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (DB.getInstanse().importDB()) {
                            ToastActivity.Toast(Notes.this, "انجام شد.", Toast.LENGTH_SHORT);
                            onResume();
                        } else {
                            ToastActivity.Toast(Notes.this, "فایل پشتیبان پیدا نشد.", Toast.LENGTH_SHORT);
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getLastTime(String date) {
        long time = (System.currentTimeMillis() - Long.parseLong(date)) / 1000;
        if (time >= 604800) {
            long t = time / 604800;
            return String.format("آخرین ویرایش: %s هفته پیش", t);
        } else if (time >= 86400) {
            long t = time / 86400;
            return String.format("آخرین ویرایش: %s روز پیش", t);
        } else if (time >= 3600) {
            long t = time / 3600;
            return String.format("آخرین ویرایش: %s ساعت پیش", t);
        } else if (time >= 120) {
            long t = time / 60;
            return String.format("آخرین ویرایش: %s دقیقه پیش", t);
        } else {
            return "آخرین ویرایش: به تازگی";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        textView.setVisibility(View.GONE);
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                bundles = getIntent().getExtras().getBoolean("isFav") ? DB.getInstanse().getDataStarFromDB() : DB.getInstanse().getDataFromDB();
                Notes.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.hide();
                        if (bundles.isEmpty()) {
                            if (getIntent().getExtras().getBoolean("isFav")) {
                                textView.setText("لیست نشان شده ها خالی است.");
                            } else {
                                textView.setText("هنوز یادداشتی ایجاد نکرده اید.");
                            }
                            textView.setVisibility(View.VISIBLE);
                            return;
                        }
                        textView.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void dialog(String t, final View.OnClickListener clickListener) {
        View inflate = getLayoutInflater().inflate(R.layout.dialog_sheet_hidden, null);
        TextView title = inflate.findViewById(R.id.title);
        title.setText(t);
        final BottomSheetDialog dialog = new BottomSheetDialog(Notes.this);
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
                clickListener.onClick(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public class ad extends RecyclerView.Adapter<ad.holder> {
        @NonNull
        @Override
        public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ad.holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_note, null));
        }

        @Override
        public void onBindViewHolder(@NonNull final holder holder, final int position) {
            final Bundle bundle = bundles.get(position);
            if (bundle.getString("password").isEmpty()) {
                holder.content.setText(bundle.getString("content").replace("\\s+", " "));
            } else {
                holder.content.setText("Text is locked.");
            }
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(Long.parseLong(bundle.getString("date")));
            holder.date.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" +
                    c.get(Calendar.SECOND) + " " + c.get(Calendar.DATE) + "-" + c.get(Calendar.MONTH) +
                    "-" + c.get(Calendar.YEAR));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Notes.this, newNote.class);
                    intent.putExtra("title", getLastTime(bundle.getString("date")));
                    intent.putExtra("isEdit", true);
                    intent.putExtra("bundle", bundle);
                    startActivityForResult(intent, 1);
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog("آیا می خواهید یادداشت حذف گردد؟", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bundle.getString("password").isEmpty()) {
                                DB.getInstanse().deleteRow(bundle.getInt("id"));
                                bundles.remove(position);
                                adapter.notifyDataSetChanged();
                                if (bundles.isEmpty()) {
                                    textView.setText("هنوز یادداشتی ایجاد نکرده اید.");
                                    textView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                View inflate = getLayoutInflater().inflate(R.layout.dialog_sheet_pass_note, null);
                                final BottomSheetDialog dialog = new BottomSheetDialog(Notes.this);
                                final EditText editText = inflate.findViewById(R.id.password);
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
                                        if (bundle.getString("password").equals(editText.getText().toString())) {
                                            DB.getInstanse().deleteRow(bundle.getInt("id"));
                                            bundles.remove(position);
                                            adapter.notifyDataSetChanged();
                                            if (bundles.isEmpty()) {
                                                textView.setText("هنوز یادداشتی ایجاد نکرده اید.");
                                                textView.setVisibility(View.VISIBLE);
                                            }
                                            dialog.dismiss();
                                        }else {
                                            editText.setText("");
                                        }
                                    }
                                });
                                dialog.show();
                            }
                        }
                    });
                }
            });
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bundle.getString("password").isEmpty()) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, bundle.getString("content"));
                        startActivity(Intent.createChooser(intent, "برنامه ای را جهت ارسال انتخاب نمائید"));
                    } else {
                        ToastActivity.Toast(Notes.this, "Text is locked", Toast.LENGTH_SHORT);
                    }
                }
            });
            if (bundle.getInt("fav") == 0) {
                holder.fav.setImageResource(R.drawable.ic_favorite_border);
            } else {
                holder.fav.setImageResource(R.drawable.ic_favorite);
            }
            holder.fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bundle.getInt("fav") == 0) {
                        bundle.putInt("fav", 1);
                        DB.getInstanse().updateIntoDB(bundle.getInt("id"), null, null, 1);
                        holder.fav.setImageResource(R.drawable.ic_favorite);
                    } else {
                        bundle.putInt("fav", 0);
                        DB.getInstanse().updateIntoDB(bundle.getInt("id"), null, null, 0);
                        holder.fav.setImageResource(R.drawable.ic_favorite_border);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return bundles.size();
        }

        public class holder extends RecyclerView.ViewHolder {
            TextView content, date;
            ImageView delete, share, fav;

            public holder(View itemView) {
                super(itemView);

                date = itemView.findViewById(R.id.date);
                content = itemView.findViewById(R.id.content);
                delete = itemView.findViewById(R.id.delete);
                share = itemView.findViewById(R.id.share);
                fav = itemView.findViewById(R.id.fav);
            }
        }
    }
}
