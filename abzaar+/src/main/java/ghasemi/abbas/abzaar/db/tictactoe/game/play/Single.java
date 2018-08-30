package ghasemi.abbas.abzaar.db.tictactoe.game.play;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Random;

import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.tictactoe.control.Player;

public class Single extends BaseXO {
    private int[] cells = new int[9];
    private int row;
    private Handler handler;
    private boolean isFinish;
    private Runnable runnable;

    public Single() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                mark(row);
                imageViews[row].setEnabled(false);
                imageViews[row].setImageResource(R.drawable.ic_xo_o);
                activeHelpPlayer(true);
                cells[row] = 1;
                //
                setAllowClick(true);
            }
        };
    }

    @Override
    public void cellClick(int pos) {
        if (isAllowClick()) {
            setAllowClick(false);
            Player player = mark(pos);
            if (player != null) {
                cells[pos] = -1;
                imageViews[pos].setEnabled(false);
                imageViews[pos].setImageResource(R.drawable.ic_xo_x);
                activeHelpPlayer(false);
                ArrayList<Integer> integers = getRandomPos();
                if (!integers.isEmpty() && !isFinish) {
                    row = integers.get(new Random().nextInt(integers.size()));
                    handler.postDelayed(runnable, 1000);
                }
            }
        }
    }

    private ArrayList<Integer> getRandomPos() {
        ArrayList<Integer> integers = new ArrayList<>();
        // for win
        // row
        for (int i = 0; i < 9; i += 3) {
            if (cells[i] + cells[i + 1] + cells[i + 2] == 2) {
                for (int j = i; j < i + 3; j++) {
                    if (cells[j] == 0) {
                        integers.add(j);
                        return integers;
                    }
                }
            }
        }
        // col
        for (int i = 0; i < 3; i++) {
            if (cells[i] + cells[i + 3] + cells[i + 6] == 2) {
                for (int j = i; j < 9; j += 3) {
                    if (cells[j] == 0) {
                        integers.add(j);
                        return integers;
                    }
                }
            }
        }


        // for pre break
        if (cells[0] + cells[4] + cells[8] == -2) {
            for (int j = 0; j < 9; j += 4) {
                if (cells[j] == 0) {
                    integers.add(j);
                    return integers;
                }
            }
        }
        // for pre break
        if (cells[2] + cells[4] + cells[6] == -2) {
            for (int j = 2; j < 7; j += 2) {
                if (cells[j] == 0) {
                    integers.add(j);
                    return integers;
                }
            }
        }

        // row
        for (int i = 0; i < 9; i += 3) {
            if (cells[i] + cells[i + 1] + cells[i + 2] == -2) {
                for (int j = i; j < i + 3; j++) {
                    if (cells[j] == 0) {
                        integers.add(j);
                        return integers;
                    }
                }
            }
        }
        // col
        for (int i = 0; i < 3; i++) {
            if (cells[i] + cells[i + 3] + cells[i + 6] == -2) {
                for (int j = i; j < 9; j += 3) {
                    if (cells[j] == 0) {
                        integers.add(j);
                        return integers;
                    }
                }
            }
        }

        // other
        for (int j = 0; j < 9; j++) {
            if (cells[j] == 0) {
                integers.add(j);
            }
        }
        return integers;
    }

    @Override
    public void restartGame() {
        super.restartGame();
        isFinish = false;
        cells = new int[9];
        if (!isAllowClick()) {
            row = new Random().nextInt(9);
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    public void winner(Player player, int sumWin) {
        isFinish = true;
        if (player != null) {
            // شروع کننده مشخص می شود
            setAllowClick(Player.O == player);
        }
        super.winner(player, sumWin);
        showWinner( player == null ? "بازی برابر شد." : getMasseage(player));
    }

    private String getMasseage(Player player) {
        if (Player.X == player) {
            return "شما برنده شدید.";
        }
        return "بازنده شدی!.";
    }
}
