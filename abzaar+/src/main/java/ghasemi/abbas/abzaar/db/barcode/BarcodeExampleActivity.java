package ghasemi.abbas.abzaar.db.barcode;

/**
 * on 07/23/2018.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ghasemi.abbas.abzaar.R;

import com.android.bahaar.FlatButton.FlatButton;
import com.android.bahaar.ToastActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

public class BarcodeExampleActivity extends AppCompatActivity {

    int _color_one = Color.WHITE;
    int _color_two = Color.BLACK;
    EditText text;
    Spinner type, color;
    FlatButton done;
    String[] name = {
            "QR CODE",
            "CODABAR",
            "CODE 39",
            "CODE 93",
            "CODE 128",
            "DATA MATRIX",
            "EAN 8",
            "EAN 13",
            "ITF",
            "MAXICODE",
            "PDF 417",
            "AZTEC",
            "RSS 14",
            "RSS EXPANDED",
            "UPC A",
            "UPC E",
            "UPC EAN EXTENSION"
    };
    String[] color_item = {"مشکی", "قرمز", "آبی", "سبز", "بنفش", "پیش فرض"};

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.barcode_writer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        text = findViewById(R.id.text);
        type = findViewById(R.id.type);
        color = findViewById(R.id.color);
        done = findViewById(R.id.done);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, name);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);
        ArrayAdapter<String> color_adaptor = new ArrayAdapter<>(this, R.layout.simple_dropdown_item_1line, color_item);
        color_adaptor.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        color.setAdapter(color_adaptor);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().equals("")) {
                    ToastActivity.Toast(BarcodeExampleActivity.this, "امکان پذیر نیست!.", Toast.LENGTH_SHORT);
                    return;
                }
                try {
                    setColorBoarcode();
                    LinearLayout l = new LinearLayout(BarcodeExampleActivity.this);
                    l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    l.setPadding(10, 10, 10, 10);
                    l.setOrientation(LinearLayout.VERTICAL);
                    TextView tv = new TextView(BarcodeExampleActivity.this);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv.setSingleLine();
                    tv.setText(text.getText().toString());

                    final Bitmap bitmap = encodeAsBitmap(text.getText().toString());
                    ImageView iv = new ImageView(BarcodeExampleActivity.this);
                    iv.setImageBitmap(bitmap);
                    l.addView(iv);
                    l.addView(tv);
                    if (bitmap != null) {
                        AlertDialog show = new AlertDialog.Builder(BarcodeExampleActivity.this,R.style.Theme_Dialog_Alert)
                                .setView(l)
                                .setCancelable(false)
                                .setNeutralButton("بستن", null)
                                .setPositiveButton("ذخیره سازی", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveBarcode(bitmap);
                                    }
                                })
                                .setNegativeButton("اشتراک", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null);
                                            Uri bitmapUri = Uri.parse(bitmapPath);
                                            Intent i = new Intent(Intent.ACTION_SEND);
                                            i.setType("image/*");
                                            i.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                                            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            startActivity(i);
                                        } catch (Exception ex) {
                                            ToastActivity.Toast(BarcodeExampleActivity.this, "خطا", Toast.LENGTH_SHORT);
                                            ex.printStackTrace();
                                        }
                                    }
                                })
                                .create();
                        show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
                        show.show();
                    } else {
                        ToastActivity.Toast(BarcodeExampleActivity.this, "متن شما با فرمت انتخابی سازگار نیست!.", Toast.LENGTH_SHORT);
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                    ToastActivity.Toast(BarcodeExampleActivity.this, "متن شما بیش از حد طولانی است.", Toast.LENGTH_SHORT);
                }
            }
        });


    }

    private void setColorBoarcode() {
        switch (color.getSelectedItemPosition()) {
            case 0:
                _color_two = Color.BLACK;
                break;
            case 1:
                _color_two = 0xFFE91E63;
                break;
            case 2:
                _color_two = 0xFF2196F3;
                break;
            case 3:
                _color_two =0xFF4CAF50;
                break;
            case 4:
                _color_two = 0xFF9C27B0;
                break;
            case 5:
                _color_two = getResources().getColor(R.color.colorPrimary);
                break;
        }
    }


    Bitmap encodeAsBitmap(String contents) throws WriterException {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            int s = type.getSelectedItemPosition();
            if (s == 0 ||
                    s == 5 ||
                    s == 11) {
                result = writer.encode(contents, BarcodeFormat(s), 512, 512, hints);
            } else {
                result = writer.encode(contents, BarcodeFormat(s), 600, 300, hints);
            }
        } catch (Exception iae) {
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? _color_two : _color_one;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private BarcodeFormat BarcodeFormat(int b) {
        if (b == 0) {
            return BarcodeFormat.QR_CODE;
        } else if (b == 1) {
            return BarcodeFormat.CODABAR;
        } else if (b == 2) {
            return BarcodeFormat.CODE_39;
        } else if (b == 3) {
            return BarcodeFormat.CODE_93;
        } else if (b == 4) {
            return BarcodeFormat.CODE_128;
        } else if (b == 5) {
            return BarcodeFormat.DATA_MATRIX;
        } else if (b == 6) {
            return BarcodeFormat.EAN_8;
        } else if (b == 7) {
            return BarcodeFormat.EAN_13;
        } else if (b == 8) {
            return BarcodeFormat.ITF;
        } else if (b == 9) {
            return BarcodeFormat.MAXICODE;
        } else if (b == 10) {
            return BarcodeFormat.PDF_417;
        } else if (b == 11) {
            return BarcodeFormat.AZTEC;
        } else if (b == 12) {
            return BarcodeFormat.RSS_14;
        } else if (b == 13) {
            return BarcodeFormat.RSS_EXPANDED;
        } else if (b == 14) {
            return BarcodeFormat.UPC_A;
        } else if (b == 15) {
            return BarcodeFormat.UPC_E;
        } else if (b == 16) {
            return BarcodeFormat.UPC_EAN_EXTENSION;
        } else {
            return BarcodeFormat.QR_CODE;
        }
    }

    public void saveBarcode(Bitmap bitmap) {
        File a = new File(Environment.getExternalStorageDirectory() + File.separator + "/abz/Barcode/");
        if (!a.exists()) {
            a.mkdirs();
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "/abz/Barcode/" + getName() + ".png");
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            ToastActivity.Toast(this, "با موفقیت در پوشه abz/Barcode ذخیره شد.", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            ToastActivity.Toast(this, "خطا", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }


    public String getName() {
        Date d = new Date();
        return String.valueOf(d.getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        menu.findItem(R.id.item_hide).setVisible(false);
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
                Shortcut(getIntent().getIntExtra("position",0), BarcodeExampleActivity.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}