package ghasemi.abbas.abzaar.db.tictactoe.game.play;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.tictactoe.control.Board;
import ghasemi.abbas.abzaar.db.tictactoe.control.Player;
import ghasemi.abbas.abzaar.db.tictactoe.control.WinnerListener;

public class BaseXO extends Fragment implements WinnerListener {
    AppCompatImageView[] imageViews = new AppCompatImageView[9];
    private TextView score1, score2;
    private TextView player1, player2;
    private ImageView message;
    private boolean allowClick = true;
    private Board board;
    //
    private FrameLayout alert_layout;
    private TextView msg;
    private LinearLayout buttons;
    private MaterialButton restart, cancel;
    private OnBackPressed onBackPressed;
    private boolean isAdmin = true;

    public void showWinner(String msg) {
        this.msg.setText(msg);
        if (isAdmin) {
            buttons.setVisibility(View.VISIBLE);
        }
        alert_layout.setVisibility(View.VISIBLE);
    }

    public Player mark(int pos) {
        return board.mark(pos);
    }

    public int getResDrawable(Player player) {
        return board.getResDrawable(player);
    }

    public void cellClick(int pos) {

    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void activeHelpPlayer(boolean playerOne) {
        if (playerOne == isAdmin) {
            player1.setBackgroundResource(R.drawable.back_player_xo);
            player1.setTextColor(0xffF57C00);
            player2.setTextColor(0xff535353);
            player2.setBackground(null);
        } else {
            player1.setBackground(null);
            player1.setTextColor(0xff535353);
            player2.setTextColor(0xffF57C00);
            player2.setBackgroundResource(R.drawable.back_player_xo);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tictactoe_main, null);
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        onBackPressed = (OnBackPressed) activity;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        player1 = view.findViewById(R.id.player_1);
        player2 = view.findViewById(R.id.player_2);
        score1 = view.findViewById(R.id.score_1);
        score2 = view.findViewById(R.id.score_2);
        alert_layout = view.findViewById(R.id.alert_layout);
        msg = view.findViewById(R.id.msg);
        buttons = view.findViewById(R.id.buttons);
        restart = view.findViewById(R.id.restart);
        cancel = view.findViewById(R.id.cancel);
        message = view.findViewById(R.id.message);
        updateNamePlayers();
        board = new Board(view.getContext());
        board.setWinnerListener(this);
        childesGridLayout(view);

        buttons();
    }

    public final void activeSendMessage() {
        message.setVisibility(View.VISIBLE);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                View view = LayoutInflater.from(getContext()).inflate(R.layout.tictactoe_message, null);
                final AppCompatEditText editText = view.findViewById(R.id.message);
                editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
                editText.setSingleLine();
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        InputMethodManager methodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        methodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        dialog.dismiss();
                        sendMessage(editText.getText().toString());
                        return false;
                    }
                });
                dialog.setView(view);
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dial) {
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager methodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                editText.requestFocus();
                                methodManager.showSoftInput(editText, 0);
                            }
                        }, 100);
                    }
                });
                dialog.show();
            }
        });
    }

    protected void sendMessage(String msg) {

    }

    @CallSuper
    public void restartGame() {
        alert_layout.setVisibility(View.GONE);
        buttons.setVisibility(View.GONE);
        for (AppCompatImageView imageView : imageViews) {
            imageView.setImageDrawable(null);
            imageView.setEnabled(true);
        }
        board.restart();
    }

    public void setAllowClick(boolean allowClick) {
        this.allowClick = allowClick;
    }

    public boolean isAllowClick() {
        return allowClick;
    }


    private void childesGridLayout(View v) {
        GridLayout gridLayout = v.findViewById(R.id.grid_layout);
        for (int i = 0; i < 9; i++) {
            imageViews[i] = (AppCompatImageView) gridLayout.getChildAt(i);
            imageViews[i].setOnClickListener(new OnClick(i));
        }
    }

    @CallSuper
    @Override
    public void winner(Player player, int sumWin) {
        if (player != null) {
            setScore(player, sumWin);
        }
    }

    private void setScore(Player player, int sumWin) {
        if (player == Player.X == isAdmin) {
            score1.setText(String.valueOf(sumWin));
        } else {
            score2.setText(String.valueOf(sumWin));
        }
    }

    private class OnClick implements View.OnClickListener {

        private int pos;

        OnClick(int p) {
            pos = p;
        }

        @Override
        public void onClick(View v) {
            cellClick(pos);
        }
    }

    private void buttons() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back
                onBackPressed();
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });
    }

    public final void onBackPressed() {
        onBackPressed.onFragmentBackPressed();
    }

    public void updateNamePlayers() {
        player1.setText(getArguments().getString("player1"));
        player2.setText(getArguments().getString("player2"));
    }

    public interface OnBackPressed {
        void onFragmentBackPressed();
    }
}
