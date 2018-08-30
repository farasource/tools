package ghasemi.abbas.abzaar.db.tictactoe.game.play;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.android.bahaar.ToastActivity;

import androidx.annotation.NonNull;
import ghasemi.abbas.abzaar.db.tictactoe.bluetooth.BluetoothEventListener;
import ghasemi.abbas.abzaar.db.tictactoe.bluetooth.DataTransfer;
import ghasemi.abbas.abzaar.db.tictactoe.bluetooth.XOConnectionController;
import ghasemi.abbas.abzaar.db.tictactoe.control.Player;

public class XOBluetooth extends BaseXO implements BluetoothEventListener {
    private boolean isAdmin;
    private XOConnectionController xoConnectionController;
    private BluetoothDevice bluetoothDevice;
    private String deviceName = "";


    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setAllowClick(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        xoConnectionController = new XOConnectionController(this);
        if (bluetoothDevice != null) {
            xoConnectionController.connect(bluetoothDevice);
        }
        setAdmin(isAdmin = bluetoothDevice != null);
    }

    @Override
    protected void sendMessage(String msg){
        DataTransfer dataTransfer = new DataTransfer();
        dataTransfer.type = "message";
        dataTransfer.arg1 = msg;
        xoConnectionController.sendData(dataTransfer);
    }

    @Override
    public void cellClick(int pos) {
        if (isAllowClick()) {
            setAllowClick(false);
            Player player = mark(pos);
            imageViews[pos].setEnabled(false);
            if (player != null) {
                imageViews[pos].setImageResource(getResDrawable(player));
                activeHelpPlayer(player == Player.O);
            }
            DataTransfer dataTransfer = new DataTransfer();
            dataTransfer.type = "mark";
            dataTransfer.arg2 = pos;
            xoConnectionController.sendData(dataTransfer);
        }
    }

    @Override
    public void winner(Player player, int sumWin) {
        super.winner(player, sumWin);
        showWinner(player == null ? "بازی برابر شد." : player == Player.X == isAdmin ? "شما برنده شده اید." : "شما بازنده شده اید.");
    }

    @Override
    public void restartGame() {
        super.restartGame();
        if (isAdmin) {
            DataTransfer dataTransfer = new DataTransfer();
            dataTransfer.type = "restarted";
            xoConnectionController.sendData(dataTransfer);
        }
    }

    @Override
    public void bluetoothDataTransfer(DataTransfer dataTransfer) {
        switch (dataTransfer.type) {
            case "connected":
                getArguments().putString("player2", dataTransfer.arg1);
                updateNamePlayers();
                if (isAdmin) {
                    setAllowClick(true);
                }
                activeHelpPlayer(true);
                activeSendMessage();
                ToastActivity.Toast(getContext(), "connected to " + deviceName, 0);
//                restartGame();
                break;
            case "mark":
                setAllowClick(true);
                Player player = mark(dataTransfer.arg2);
                imageViews[dataTransfer.arg2].setEnabled(false);
                if (player != null) {
                    imageViews[dataTransfer.arg2].setImageResource(getResDrawable(player));
                    activeHelpPlayer(player == Player.O);
                }
                break;
            case "message":
                ToastActivity.Toast(getContext(), dataTransfer.arg1, 1);
                break;
            case "restarted":
                restartGame();
                break;
        }
    }

    @Override
    public void bluetoothConnected(String name) {
        if (getContext() == null) {
            return;
        }
        deviceName = name;
        DataTransfer dataTransfer = new DataTransfer();
        dataTransfer.type = "connected";
        dataTransfer.arg1 = getArguments().getString("player1");
        xoConnectionController.sendData(dataTransfer);
    }

    @Override
    public void bluetoothDisconnect() {
        if (getContext() == null) {
            return;
        }
        ToastActivity.Toast(getContext(), "خطایی در ارتباط رخ داد.", 0);
        onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (xoConnectionController != null) {
            xoConnectionController.destroy();
        }
        bluetoothDevice = null;
    }

    @Override
    public void bluetoothError(int code) {
        if (getContext() == null) {
            return;
        }
        ToastActivity.Toast(getContext(), "خطائی غیر منتظره رخ داد.", 0);
        onBackPressed();
    }
}
