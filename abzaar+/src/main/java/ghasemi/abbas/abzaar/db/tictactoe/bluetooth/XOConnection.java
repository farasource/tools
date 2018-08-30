package ghasemi.abbas.abzaar.db.tictactoe.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class XOConnection {
    private final String NAME = "XOB";
    private final UUID UUID = java.util.UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    static final int STATE_NONE = 0;
    static final int STATE_LISTEN = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    private int state = STATE_NONE;
    private BluetoothAdapter bluetoothAdapter;
    private WaitingThread waitingThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private Handler handler;

    public int getState() {
        return state;
    }

    synchronized void Connect(BluetoothDevice device) {
        if (getState() == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    public void write(byte[] out) {
        ConnectedThread con;
        synchronized (this) {
            if (getState() != STATE_CONNECTED || connectedThread == null) {
                return;
            }
            con = connectedThread;
        }
        con.write(out);
    }


    private void setState(int state) {
        if (getState() == state) {
            return;
        }
        this.state = state;
        // send UI
        handler.obtainMessage(XOConnectionController.XO_STATE_CHANGED, state, -1).sendToTarget();
    }

    XOConnection(Handler handler) {
        this.handler = handler;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private class WaitingThread extends Thread {
        private BluetoothSocket socket;
        private BluetoothServerSocket serverSocket;

        WaitingThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            serverSocket = tmp;
        }

        @Override
        public void run() {
            setName("WaitingThread");
            while (XOConnection.this.getState() != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (socket != null) {
                    synchronized (XOConnection.this) {
                        switch (XOConnection.this.getState()) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // start the connected thread.
                                connected(socket.getRemoteDevice(), socket);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate
                                // new socket.
                                try {
                                    if (socket != null) {
                                        socket.close();
                                        socket = null;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                }
            }
        }

        public synchronized void cancel() {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                serverSocket = null;
                socket = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void connectFailed() {
//        connectionLost();
//        handler.obtainMessage(BluetoothController.XO_STATE_CHANGED, STATE_NONE, -1).sendToTarget();
        //
        connectionStart();
    }

    private synchronized void connected(BluetoothDevice device, BluetoothSocket socket) {
        connectionStop(false);
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        Message message = handler.obtainMessage(XOConnectionController.XO_STATE_DEVICE_OBJ);
        Bundle bundle = new Bundle();
        bundle.putParcelable("device", device);
        message.setData(bundle);
        handler.sendMessage(message);
        setState(STATE_CONNECTED);
    }

    private class ConnectThread extends Thread {

        private BluetoothSocket socket;
        private BluetoothDevice device;

        ConnectThread(BluetoothDevice bluetoothDevice) {
            device = bluetoothDevice;
            BluetoothSocket socket = null;
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.socket = socket;
        }

        @Override
        public void run() {
            setName("ConnectThread");
            bluetoothAdapter.cancelDiscovery();
            try {
                socket.connect();
            } catch (Exception e) {
                e.printStackTrace();
                cancel();
                // failed
                connectFailed();
                return;
            }

            synchronized (XOConnection.this) {
                connectThread = null;
            }

            connected(device, socket);
        }

        public synchronized void cancel() {
            try {
                if (socket != null) {
                    socket.close();
                }
                device = null;
                socket = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void connectionStart() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (bluetoothAdapter.isEnabled()) {
            setState(STATE_LISTEN);
            if (waitingThread != null) {
                waitingThread.cancel();
                waitingThread = null;
            }
            waitingThread = new WaitingThread();
            waitingThread.start();
        }
    }

    synchronized void connectionStop() {
        connectionStop(true);
    }

    private synchronized void connectionStop(boolean report) {
        if (waitingThread != null) {
            waitingThread.cancel();
            waitingThread = null;
        }
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        if (report) {
            setState(STATE_NONE);
        }
    }

    private synchronized void connectionLost() {
        handler.obtainMessage(XOConnectionController.XO_STATE_CHANGED, STATE_NONE, -1).sendToTarget();
//        //
        connectionStart();
    }

    private class ConnectedThread extends Thread {

        private BluetoothSocket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        private byte[] bytes;

        ConnectedThread(BluetoothSocket bluetoothSocket) {
            socket = bluetoothSocket;
            InputStream tmpIn = null;
            OutputStream tmoOut = null;


            try {
                tmpIn = socket.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                tmoOut = socket.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }

            inputStream = tmpIn;
            outputStream = tmoOut;
        }

        @Override
        public void run() {
            setName("ConnectedThread");
            bytes = new byte[1024];
            int len;
            while (true) {
                try {
                    len = inputStream.read(bytes);
                    // send UI
                    handler.obtainMessage(XOConnectionController.XO_STATE_READ, len, -1, bytes).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public synchronized void cancel() {
            try {
                if (socket != null) {
                    socket.close();
                }
                socket = null;
                bytes = null;
                inputStream = null;
                outputStream = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}