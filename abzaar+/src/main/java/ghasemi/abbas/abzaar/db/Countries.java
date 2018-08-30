package ghasemi.abbas.abzaar.db;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.bahaar.TinyDB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import ghasemi.abbas.abzaar.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

public class Countries extends AppCompatActivity {

    ArrayList<Bundle> bundles;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyDB.getInstance(getApplicationContext()).putInt("version_" + Countries.class.getCanonicalName(), 1);
        setContentView(R.layout.base_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        TextView title = findViewById(R.id.activity_name);
        title.setText("پیش شماره کشورها");
        bundles = new ArrayList<>();
        try {
            InputStream inputStream = getResources().getAssets().open("countries.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Bundle bundle = new Bundle();
                String[] p = line.split(";");
                bundle.putString("name", "+" + p[0]);
                bundle.putString("code", p[2] + " (" + p[1] + ")");
                bundles.add(bundle);
            }
            Collections.sort(bundles, new Comparator<Bundle>() {
                @Override
                public int compare(Bundle o1, Bundle o2) {
                    return o1.getString("code").compareTo(o2.getString("code"));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(new Countries_adapter());
        linearLayout.addView(recyclerView);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            private Drawable icon;
            private ColorDrawable background;
            private RecyclerView recyclerView;

            {
                icon = ContextCompat.getDrawable(Countries.this,
                        R.drawable.ic_call);
                background = new ColorDrawable(0xff4CAF50);
            }

            @Override
            public boolean onMove(@androidx.annotation.NonNull RecyclerView recyclerView, @androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder, @androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }


            @Override
            public void onSwiped(@androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder, int i) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("tel/*");
                    intent.setData(Uri.parse("tel:" + bundles.get(viewHolder.getAdapterPosition()).getString("name")));
                    startActivity(intent);
                } catch (Exception e) {
                }
                Vibrator vibrator = (Vibrator) Countries.this.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, 100));
                } else {
                    vibrator.vibrate(100);
                }
                recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(@androidx.annotation.NonNull Canvas c, @androidx.annotation.NonNull RecyclerView recyclerView, @androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX > 0) { // Swiping to the right
                    int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                    int iconRight = itemView.getLeft() + iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) ,
                            itemView.getBottom());
                } else if (dX < 0) { // Swiping to the left
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX),
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                    icon.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
                icon.draw(c);
                if (this.recyclerView == null) {
                    this.recyclerView = recyclerView;
                }
            }
        }).attachToRecyclerView(recyclerView);
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
                Shortcut(getIntent().getIntExtra("position", 0), Countries.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class Countries_adapter extends RecyclerView.Adapter<Countries_adapter.holder> {


        @NonNull
        @Override
        public Countries_adapter.holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.iran_city, null));
        }

        @Override
        public void onBindViewHolder(@NonNull Countries_adapter.holder holder, int i) {
            Bundle bundle = bundles.get(i);
            holder.code.setText(bundle.getString("code"));
            holder.name.setText(bundle.getString("name"));
        }

        @Override
        public int getItemCount() {
            return bundles.size();
        }

        public class holder extends RecyclerView.ViewHolder {
            TextView code;
            TextView name;

            public holder(@NonNull View itemView) {
                super(itemView);

                code = itemView.findViewById(R.id.code);
                name = itemView.findViewById(R.id.city);
                name.setGravity(Gravity.END);
            }
        }
    }
}
