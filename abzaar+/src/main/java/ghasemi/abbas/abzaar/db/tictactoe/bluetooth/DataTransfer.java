package ghasemi.abbas.abzaar.db.tictactoe.bluetooth;

import androidx.annotation.NonNull;

public class DataTransfer {
    public String type;
    public String arg1;
    public int arg2;

    @NonNull
    @Override
    public String toString() {
        return type + ";" + arg1 + ";" + arg2;
    }
}
