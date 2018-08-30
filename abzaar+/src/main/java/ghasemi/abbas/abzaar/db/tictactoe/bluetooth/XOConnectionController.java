package ghasemi.abbas.abzaar.db.tictactoe.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class XOConnectionController {

    private XOConnection xoConnection;
    private Handler handler;
    private BluetoothEventListener bluetoothEventListener;
    private BluetoothDevice bluetoothDevice;

    static final int XO_STATE_CHANGED = 0;
    static final int XO_STATE_READ = 1;
    static final int XO_STATE_DEVICE_OBJ = 2;

    public XOConnectionController(BluetoothEventListener bluetoothEventListener) {
        this.bluetoothEventListener = bluetoothEventListener;
        initHandler();
        xoConnection = new XOConnection(handler);
        connectionStart();
    }

    private void initHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case XOConnectionController.XO_STATE_CHANGED:
                        switch (msg.arg1) {
                            case XOConnection.STATE_NONE:
                                bluetoothDevice = null;
                                bluetoothEventListener.bluetoothDisconnect();
                                break;
                            case XOConnection.STATE_CONNECTED:
                                bluetoothEventListener.bluetoothConnected(bluetoothDevice.getName());
                                break;
                            case XOConnection.STATE_CONNECTING:
                            case XOConnection.STATE_LISTEN:
                                bluetoothDevice = null;
//                                connecting...
                                break;
                        }
                        break;
                    case XOConnectionController.XO_STATE_READ:
                        byte[] in = (byte[]) msg.obj;
                        try {
                            String[] part = new String(in, 0, msg.arg1).split(";");
                            DataTransfer dataTransfer = new DataTransfer();
                            dataTransfer.type = part[0];
                            dataTransfer.arg1 = part[1];
                            try {
                                dataTransfer.arg2 = Integer.parseInt(part[2]);
                            } catch (Exception e) {
                                //
                            }
                            bluetoothEventListener.bluetoothDataTransfer(dataTransfer);
                        } catch (Exception e) {
                            bluetoothEventListener.bluetoothError(401);
                        }
                        break;
                    case XOConnectionController.XO_STATE_DEVICE_OBJ:
                        bluetoothDevice = msg.getData().getParcelable("device");
//                        if (bluetoothDevice != null) {
//                            bluetoothEventListener.bluetoothConnected(bluetoothDevice.getName());
//                        }
                        break;
                }
                return false;
            }
        });
    }

    public void connect(BluetoothDevice device) {
        xoConnection.Connect(device);
    }

    public void sendData(DataTransfer dataTransfer) {
        if (xoConnection != null) {
            xoConnection.write(dataTransfer.toString().getBytes());
        }
    }

    private void connectionStart() {
//        if (xoConnection.getState() == XOConnection.STATE_NONE) {
            xoConnection.connectionStart();
//        }
    }

    public void destroy() {
        handler = null;
        bluetoothDevice = null;
        if (xoConnection != null) {
            xoConnection.connectionStop();
        }
    }
}
