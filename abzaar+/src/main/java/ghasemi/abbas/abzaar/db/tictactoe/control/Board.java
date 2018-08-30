package ghasemi.abbas.abzaar.db.tictactoe.control;

import android.content.Context;

import ghasemi.abbas.abzaar.R;

public class Board {

    private Cell[][] cells;

    private GameState state;
    private Player currentTurn;
    private WinnerListener winnerListener;

    private enum GameState {IN_PROGRESS, FINISHED}


    private int sumWinO = 0;
    private int sumWinX = 0;
    private Sound sound;

    public Board(Context context) {
        sound = new Sound(context);
        currentTurn = Player.X;
        restart();
    }

    public int getResDrawable(Player player) {
        if (player == Player.O) {
            return R.drawable.ic_xo_o;
        }
        return R.drawable.ic_xo_x;
    }

    public void setWinnerListener(WinnerListener winnerListener) {
        this.winnerListener = winnerListener;
    }

    public void restart() {
        clearCells();
        state = GameState.IN_PROGRESS;
    }

    public Player mark(int pos) {
        int row = pos % 3;
        int col = pos / 3;
        if (isValid(row, col)) {
            sound.playClickSound();
            cells[row][col].setValue(currentTurn);
            if (isWinningMoveByPlayer(currentTurn, row, col)) {
                state = GameState.FINISHED;
                winnerListener.winner(currentTurn, currentTurn == Player.X ? ++sumWinX : ++sumWinO);
                sound.playVictorySound();
            } else if (isBoardFull()) {
                state = GameState.FINISHED;
                winnerListener.winner(null, currentTurn == Player.X ? sumWinX : sumWinO);
            }
            Player p = currentTurn;
            flipCurrentTurn();
            return p;
        }
        return null;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cells[i][j].getValue() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void clearCells() {
        cells = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    private boolean isValid(int row, int col) {
        if (state == GameState.FINISHED) {
            return false;
        } else if (isOutOfBounds(row) || isOutOfBounds(col)) {
            return false;
        } else {
            return !isCellValueAlreadySet(row, col);
        }
    }

    private boolean isOutOfBounds(int idx) {
        return idx < 0 || idx > 2;
    }

    private boolean isCellValueAlreadySet(int row, int col) {
        return cells[row][col].getValue() != null;
    }

    private boolean isWinningMoveByPlayer(Player player, int currentRow, int currentCol) {

        return (cells[currentRow][0].getValue() == player         // 3-in-the-row
                && cells[currentRow][1].getValue() == player
                && cells[currentRow][2].getValue() == player
                || cells[0][currentCol].getValue() == player      // 3-in-the-column
                && cells[1][currentCol].getValue() == player
                && cells[2][currentCol].getValue() == player
                || currentRow == currentCol            // 3-in-the-diagonal
                && cells[0][0].getValue() == player
                && cells[1][1].getValue() == player
                && cells[2][2].getValue() == player
                || currentRow + currentCol == 2    // 3-in-the-opposite-diagonal
                && cells[0][2].getValue() == player
                && cells[1][1].getValue() == player
                && cells[2][0].getValue() == player);
    }

    private void flipCurrentTurn() {
        currentTurn = currentTurn == Player.X ? Player.O : Player.X;
    }

}
