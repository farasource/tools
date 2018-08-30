package ghasemi.abbas.abzaar.db.tictactoe.game.play;


import ghasemi.abbas.abzaar.db.tictactoe.control.Player;

public class Twosome extends BaseXO {

    @Override
    public void cellClick(int pos) {
        Player player = mark(pos);
        imageViews[pos].setEnabled(false);
        if (player != null) {
            imageViews[pos].setImageResource(getResDrawable(player));
            activeHelpPlayer(player == Player.O);
        }
    }

    @Override
    public void winner(Player player, int sumWin) {
        super.winner(player, sumWin);
        showWinner( player == null ? "بازی برابر شد." : getWinnerName(player) + " برنده بازی شد.");
    }

    private String getWinnerName(Player player) {
        if (Player.X == player) {
            return getArguments().getString("player1");
        }
        return getArguments().getString("player2");
    }

}
