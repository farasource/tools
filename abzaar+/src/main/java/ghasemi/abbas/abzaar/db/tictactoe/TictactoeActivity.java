package ghasemi.abbas.abzaar.db.tictactoe;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentManager;
import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.sargarmi.MainActivityS;
import ghasemi.abbas.abzaar.db.tictactoe.bluetooth.BluetoothController;

import ghasemi.abbas.abzaar.db.tictactoe.game.play.BaseXO;
import ghasemi.abbas.abzaar.db.tictactoe.game.play.Single;
import ghasemi.abbas.abzaar.db.tictactoe.game.play.Twosome;
import ghasemi.abbas.abzaar.db.tictactoe.game.play.XOBluetooth;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class TictactoeActivity extends AppCompatActivity implements BaseXO.OnBackPressed {

    private BluetoothController bluetoothController;
    private AlertDialog alertDialog;
    private boolean isActiveFragment;
    private BottomSheetDialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tictactoe_activity);
        TinyDB.getInstance(getApplicationContext()).putInt("version_" + TictactoeActivity.class.getCanonicalName(), 1);
        init();
    }

    private void init() {
        findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null && alertDialog.isShowing()){
                    return;
                }
                alertDialog = new AlertDialog.Builder(TictactoeActivity.this)
                        .setCancelable(false)
                        .setView(LayoutInflater.from(TictactoeActivity.this).inflate(R.layout.tictactoe_settings, null))
                        .show();
                alertDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                AppCompatEditText player_1 = alertDialog.findViewById(R.id.player_1);
                String name = TinyDB.getInstance(TictactoeActivity.this).getString("tic_tac_toe_player_1");
                player_1.setText(name.isEmpty() ? "player 1" : name);
                player_1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TinyDB.getInstance(TictactoeActivity.this).putString("tic_tac_toe_player_1", s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                AppCompatEditText player_2 = alertDialog.findViewById(R.id.player_2);
                name = TinyDB.getInstance(TictactoeActivity.this).getString("tic_tac_toe_player_2");
                player_2.setText(name.isEmpty() ? "player 2" : name);
                player_2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TinyDB.getInstance(TictactoeActivity.this).putString("tic_tac_toe_player_2", s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });
        findViewById(R.id.single).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Single single = new Single();
                Bundle bundle = new Bundle();
                String name = TinyDB.getInstance(TictactoeActivity.this).getString("tic_tac_toe_player_1");
                bundle.putString("player1", name.isEmpty() ? "player 1" : name);
                bundle.putString("player2", "System");
                single.setArguments(bundle);
                startFragment(single);
            }
        });
        findViewById(R.id.twosome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Twosome twosome = new Twosome();
                Bundle bundle = new Bundle();
                String name = TinyDB.getInstance(TictactoeActivity.this).getString("tic_tac_toe_player_1");
                bundle.putString("player1", name.isEmpty() ? "player 1" : name);
                name = TinyDB.getInstance(TictactoeActivity.this).getString("tic_tac_toe_player_2");
                bundle.putString("player2", name.isEmpty() ? "player 2" : name);
                twosome.setArguments(bundle);
                startFragment(twosome);
            }
        });
        findViewById(R.id.bluetooth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothController == null) {
                    bluetoothController = new BluetoothController(TictactoeActivity.this) {
                        @Override
                        public void addBluetoothDevice(BluetoothDevice bluetoothDevice) {
                            startForBluetooth(bluetoothDevice);
                            destroy();
                            bluetoothController = null;
                        }
                    };
                }
                if (!bluetoothController.isEnabled()) {
                    ToastActivity.Toast(TictactoeActivity.this, "بر روی دستگاه پشتیبانی نمی شود.", 0);
                } else if (!bluetoothController.checkSelfPermission()) {
                    ToastActivity.Toast(TictactoeActivity.this, "لطفا درخواست ها را تایید نمایید.", 0);
                } else {
                    if(dialog != null && dialog.isShowing()){
                        return;
                    }
                    View inflate = getLayoutInflater().inflate(R.layout.dialog_sheet_hidden, null);
                    TextView title = inflate.findViewById(R.id.title);
                    title.setText("یک دستگاه منتظر اتصال و دستگاه دیگر جستجو را انتحاب نماید.");
                    dialog = new BottomSheetDialog(TictactoeActivity.this);
                    dialog.setContentView(inflate);
                    dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
                    TextView cancel = inflate.findViewById(R.id.cancel);
                    cancel.setText("منتظر اتصال");
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            startForBluetooth(null);
                        }
                    });
                    TextView ok = inflate.findViewById(R.id.ok);
                    ok.setText("جستجوی دستگاه");
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            bluetoothController.startSearching();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    private void startForBluetooth(BluetoothDevice device) {
        XOBluetooth xoBluetooth = new XOBluetooth();
        Bundle bundle = new Bundle();
        String name = TinyDB.getInstance(TictactoeActivity.this).getString("tic_tac_toe_player_1");
        bundle.putString("player1", name.isEmpty() ? "player 1" : name);
        bundle.putString("player2", "connecting...");
        xoBluetooth.setArguments(bundle);
        xoBluetooth.setBluetoothDevice(device);
        startFragment(xoBluetooth);
    }

    private void startFragment(BaseXO baseXO) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.root, baseXO)
                .commit();
        isActiveFragment = true;
    }

    private void finishFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .remove(manager.findFragmentById(R.id.root))
                .commit();
        isActiveFragment = false;
    }

    @Override
    public void onBackPressed() {
        if (isActiveFragment) {
            finishFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentBackPressed() {
        if (isActiveFragment) {
            destroyBluetoothController();
            finishFragment();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (bluetoothController != null && bluetoothController.handelActivityResult(requestCode, resultCode)) {

            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (bluetoothController != null && bluetoothController.handelPermissionsResult(requestCode, grantResults)) {
            return;
        }
    }

    @Override
    protected void onDestroy() {
        destroyBluetoothController();
        super.onDestroy();
    }

    private void destroyBluetoothController() {
        if (bluetoothController != null) {
            bluetoothController.destroy();
            bluetoothController = null;
        }
    }
}
