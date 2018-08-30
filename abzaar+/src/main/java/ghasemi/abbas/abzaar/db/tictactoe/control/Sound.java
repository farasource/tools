package ghasemi.abbas.abzaar.db.tictactoe.control;

import android.content.Context;
import android.media.MediaPlayer;

import ghasemi.abbas.abzaar.R;

public class Sound {

    private Context context;
    private MediaPlayer mediaPlayer;

    public Sound(Context context) {
        this.context = context;
    }

    public void playClickSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.snd_click);
        mediaPlayer.start();
    }

    public void playVictorySound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.victory);
        mediaPlayer.start();
    }
}
