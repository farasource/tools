package ghasemi.abbas.abzaar.db.note;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bahaar.ToastActivity;

import ghasemi.abbas.abzaar.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class newNote extends AppCompatActivity {
    private EditText content;
    private Bundle bundle;
    private String password = null,contentText ="";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = findViewById(R.id.activity_name);

        Intent intent = getIntent();
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
            String s = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (s != null) {
                contentText = s;
                intent.putExtra("title", "یادداشت جدید");
            }
        }

        title.setText(getIntent().getStringExtra("title"));

        content = findViewById(R.id.editText);
        content.requestFocus();
        if (getIntent().getBooleanExtra("isEdit",false)) {
            bundle = getIntent().getExtras().getBundle("bundle");
            if (bundle.getString("password").isEmpty()) {
                content.setText(bundle.getString("content"));
            } else {
                dilalog(true);
            }
        } else {
            bundle = new Bundle();
            bundle.putString("password", "");
            bundle.putString("content", contentText);
            content.setText(contentText);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (content.getText().toString().trim().isEmpty()) {
                    ToastActivity.Toast(newNote.this, "متن نمی تواند خالی باشد", Toast.LENGTH_SHORT);
                    break;
                }
                if (content.getText().toString().length() < 50001) {
                    if (getIntent().getBooleanExtra("isEdit",false)) {
                        DB.getInstanse().updateIntoDB(bundle.getInt("id"), content.getText().toString(), password, -1);
                    } else {
                        bundle.putInt("id", (int) DB.getInstanse().insertIntoDB(content.getText().toString(), password));
                        getIntent().putExtra("isEdit", true);
                    }
                    bundle.putString("content", content.getText().toString());
                    password = null;
                    TextView title = findViewById(R.id.activity_name);
                    title.setText("آخرین ویرایش: به تازگی");
                    ToastActivity.Toast(newNote.this, "ذخیره شد", Toast.LENGTH_SHORT);
                }else {
                    ToastActivity.Toast(newNote.this, "حداکثر 50,000 حرف مجاز به ثبت است!" + content.getText().toString().length(), Toast.LENGTH_SHORT);
                }
                break;
            case R.id.action_share:
                Intent ahare = new Intent();
                ahare.setAction(Intent.ACTION_SEND);
                ahare.addCategory(Intent.CATEGORY_DEFAULT);
                ahare.setType("text/plain");
                ahare.putExtra(Intent.EXTRA_TEXT, content.getText());
                startActivity(Intent.createChooser(ahare, "برنامه ای را جهت ارسال انتخاب نمائید"));
                break;
            case R.id.action_lock:
                dilalog(false);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (password == null && content.getText().toString().equals(bundle.getString("content"))) {
            finish();
            return;
        }
        View inflate = getLayoutInflater().inflate(R.layout.dialog_sheet_hidden, null);
        TextView title = inflate.findViewById(R.id.title);
        title.setText("آیا می خواهید بدون ذخیره سازی خارج شوید؟");
        final BottomSheetDialog dialog = new BottomSheetDialog(newNote.this);
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
                finish();
            }
        });
        dialog.show();
    }

    private void dilalog(final boolean enterPass) {
        View inflate = getLayoutInflater().inflate(R.layout.dialog_sheet_pass_note, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        final EditText editText = inflate.findViewById(R.id.password);
        if (!enterPass) {
            editText.setText(bundle.getString("password"));
        }
        dialog.setCancelable(!enterPass);
        dialog.setCanceledOnTouchOutside(!enterPass);
        dialog.setContentView(inflate);
        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
        inflate.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (enterPass) {
                    finish();
                }
            }
        });
        inflate.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterPass) {
                    if (editText.getText().toString().equals(bundle.getString("password"))) {
                        content.setText(bundle.getString("content"));
                        dialog.dismiss();
                    } else {
                        editText.setText("");
                    }
                } else {
                    if (!editText.getText().toString().equals(bundle.getString("password"))) {
                        password = editText.getText().toString();
                        bundle.putString("password", password);
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
}
