package ghasemi.abbas.abzaar.db.level;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.level.orientation.OrientationProvider;
import ghasemi.abbas.abzaar.db.level.view.LevelView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

public class Level extends AppCompatActivity {
    private OrientationProvider provider;

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
                Shortcut(getIntent().getIntExtra("position", 0), Level.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textView = findViewById(R.id.activity_name);
        textView.setText("تراز");
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        LevelView levelView = new LevelView(this);
        linearLayout.addView(levelView);

        provider = new OrientationProvider(this, levelView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        provider.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (provider.isListening()) {
            provider.stopListening();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
