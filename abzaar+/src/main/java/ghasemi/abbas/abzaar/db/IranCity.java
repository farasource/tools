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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.bahaar.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ghasemi.abbas.abzaar.BuildVars;
import ghasemi.abbas.abzaar.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

public class IranCity extends AppCompatActivity {

    String city;
    JSONArray jsonArray;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyDB.getInstance(getApplicationContext()).putInt("version_" + IranCity.class.getCanonicalName(), 1);
        setContentView(R.layout.base_layout);
        city = BuildVars.ReadFromFile("iran_city_numbers.json", this);
        try {
            jsonArray = new JSONArray(city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        TextView title = findViewById(R.id.activity_name);
        title.setText("پیش شماره شهرها");
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(new city_adapter());
        linearLayout.addView(recyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            private Drawable icon;
            private ColorDrawable background;
            private RecyclerView recyclerView;

            {
                icon = ContextCompat.getDrawable(IranCity.this,
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
                    intent.setData(Uri.parse("tel:" + jsonArray.getJSONObject(viewHolder.getAdapterPosition()).getString("code")));
                    startActivity(intent);
                } catch (Exception e) {
                }
                Vibrator vibrator = (Vibrator) IranCity.this.getSystemService(Context.VIBRATOR_SERVICE);
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
                            itemView.getLeft() + ((int) dX),
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

    public class city_adapter extends RecyclerView.Adapter<city_adapter.holder> {


        @NonNull
        @Override
        public city_adapter.holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.iran_city, null));
        }

        @Override
        public void onBindViewHolder(@NonNull city_adapter.holder holder, int i) {

            try {
                JSONObject object = jsonArray.getJSONObject(i);
                holder.code.setText(object.getString("code"));
                holder.name.setText(object.getString("fa"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class holder extends RecyclerView.ViewHolder {
            TextView code;
            TextView name;

            public holder(@NonNull View itemView) {
                super(itemView);

                code = itemView.findViewById(R.id.code);
                name = itemView.findViewById(R.id.city);
            }
        }
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
                Shortcut(getIntent().getIntExtra("position", 0), IranCity.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
