package ghasemi.abbas.abzaar.db.tictactoe.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.bahaar.ToastActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import ghasemi.abbas.abzaar.R;

public class BluetoothController {
    private final int REQUEST_BLUETOOTH_CODE = 1733;
    private final int REQUEST_FIND_LOCATION_CODE = 1734;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver discoveryReceiver;
    private Activity activity;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private AlertDialog alertDialog;
    //

    public BluetoothController(Activity activity) {
        this.activity = activity;
        bluetoothDevices = new ArrayList<>();
        check();
    }

    private void check() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            destroy();
        }
    }

    public void startSearching() {
        if (bluetoothAdapter == null) {
            return;
        }
        if (!checkSelfPermission()) {
            return;
        }
        bluetoothDevices.clear();
        alertDialog = new ProgressDialog(activity);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("در حال جستجو ...");
        alertDialog.setMessage("لطفا مطمئن شوید بلوتوث کاربر قابل رویت بوده و کاربر وارد 'منتظر اتصال' شده است.");
        alertDialog.show();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        initDiscoveryReceiver();
        bluetoothAdapter.startDiscovery();
    }

    public boolean isEnabled() {
        return bluetoothAdapter != null;
    }

    public boolean checkSelfPermission() {
        if (!bluetoothAdapter.isEnabled()) {
            activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BLUETOOTH_CODE);
            return false;
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FIND_LOCATION_CODE);
            return false;
        }
        return true;
    }

    public boolean handelActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_BLUETOOTH_CODE) {
            if (resultCode == Activity.RESULT_OK) {
//                startSearching();
            } else {
                ToastActivity.Toast(activity, "لطفا ابتدا بلوتوث خود را روشن نمایید.", 0);
                destroy();
//                xoActionListener.bluetoothError(403);
            }
            return true;
        }
        return false;
    }

    public boolean handelPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FIND_LOCATION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startSearching();
            } else {
                ToastActivity.Toast(activity, "لطفا ابتدا مجوز درخواشت شده را تایید نمایید.", 0);
                destroy();
//                xoActionListener.bluetoothError(402);
            }
            return true;
        }
        return false;
    }

    private void initDiscoveryReceiver() {
        bluetoothDevices.clear();
        discoveryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    bluetoothDevices.add(device);
                } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    // finish searching
                    destroyDiscoveryReceiver();
                    View view = LayoutInflater.from(activity).inflate(R.layout.bluetooth_device, null);
                    bluetoothDevices.addAll(bluetoothAdapter.getBondedDevices());
                    ArrayList<String> name = new ArrayList<>();
                    for (BluetoothDevice device : bluetoothDevices) {
                        name.add(device.getName());
                    }
                    if (!name.isEmpty()) {
                        ListView listView = view.findViewById(R.id.list_view);
                        ListAdapter listAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, name);
                        listView.setAdapter(listAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                bluetoothAdapter.cancelDiscovery();
                                alertDialog.dismiss();
                                addBluetoothDevice(bluetoothDevices.get(position));
                            }
                        });
                    } else {
                        view.findViewById(R.id.devices).setVisibility(View.GONE);
                        view.findViewById(R.id.not_found).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.research).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                startSearching();
                            }
                        });
                    }

                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                        alertDialog = null;
                    }

                    alertDialog = new AlertDialog.Builder(activity)
                            .setView(view)
                            .show();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(discoveryReceiver, intentFilter);
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(discoveryReceiver, intentFilter);
    }


    public void destroy() {
        destroyDiscoveryReceiver();
        bluetoothAdapter = null;
        bluetoothDevices = null;
    }

    private void destroyDiscoveryReceiver() {
        if (discoveryReceiver != null) {
            activity.unregisterReceiver(discoveryReceiver);
            discoveryReceiver = null;
        }
    }

    public void addBluetoothDevice(BluetoothDevice bluetoothDevice){

    }
}
