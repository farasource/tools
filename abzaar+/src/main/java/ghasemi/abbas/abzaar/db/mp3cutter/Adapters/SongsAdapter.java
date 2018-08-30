package ghasemi.abbas.abzaar.db.mp3cutter.Adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.mp3cutter.Activities.RingdroidSelectActivity;
import ghasemi.abbas.abzaar.db.mp3cutter.Models.SongsModel;
import ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Utils;
import ghasemi.abbas.abzaar.db.mp3cutter.Views.BubbleTextGetter;
import com.android.bahaar.RoundedImg.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by REYANSH on 4/8/2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ItemHolder> implements BubbleTextGetter {

    private RingdroidSelectActivity mRingdroidSelectActivity;
    private ArrayList<SongsModel> mData;

    public SongsAdapter(RingdroidSelectActivity ringdroidSelectActivity, ArrayList<SongsModel> data) {
        mRingdroidSelectActivity = ringdroidSelectActivity;
        mData = data;
    }

    @Override
    public SongsAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.arm_item_songs, parent, false));
    }

    @Override
    public void onBindViewHolder(SongsAdapter.ItemHolder holder, int position) {

        holder.mSongName.setText(mData.get(position).mSongsName);
        holder.mArtistName.setText(mData.get(position).mArtistName);
        try {
            holder.mDuration.setText(Utils.makeShortTimeString(mRingdroidSelectActivity.getApplicationContext(),
                    Integer.parseInt(mData.get(position).mDuration) / 1000));
        }catch (Exception e) {
            holder.mDuration.setText("نامشخص!");
        }

        try {
            Glide.with(RingdroidSelectActivity.mContext)
                    .load(Utils.getAlbumArtUri(Long.parseLong(mData.get(position).mAlbumId)).toString())
                    .placeholder(R.drawable.default_art)
                    .into(holder.mSongsImage);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        try {
            return String.valueOf(mData.get(pos).mSongsName.charAt(0));
        } catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
    }

    public void updateData(ArrayList<SongsModel> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RoundedImageView mSongsImage;
        private TextView mSongName;
        private TextView mArtistName;
        private TextView mDuration;
        private ImageView mPopUpMenu;

        public ItemHolder(View itemView) {
            super(itemView);
            mSongsImage = (RoundedImageView) itemView.findViewById(R.id.album_art_image_view);
            mSongName = itemView.findViewById(R.id.song_name);
            mArtistName = (TextView) itemView.findViewById(R.id.artist_name);
            mDuration = (TextView) itemView.findViewById(R.id.song_duration);
            mPopUpMenu = (ImageView) itemView.findViewById(R.id.overflow);
            mPopUpMenu.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.overflow) {
                mRingdroidSelectActivity.onPopUpMenuClickListener(mSongName.getText(),getAdapterPosition());
                return;
            }
            mRingdroidSelectActivity.onItemClicked(getAdapterPosition());
        }
    }
}
