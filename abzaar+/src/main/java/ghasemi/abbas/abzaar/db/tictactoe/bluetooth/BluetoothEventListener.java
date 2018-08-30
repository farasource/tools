package ghasemi.abbas.abzaar.db.tictactoe.bluetooth;

public interface BluetoothEventListener {
    void bluetoothConnected(String name);
    void bluetoothDisconnect();
    void bluetoothError(int code);
    void bluetoothDataTransfer(DataTransfer dataTransfer);
}
