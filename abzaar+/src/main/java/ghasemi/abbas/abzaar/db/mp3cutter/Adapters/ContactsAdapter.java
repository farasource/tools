package ghasemi.abbas.abzaar.db.mp3cutter.Adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.mp3cutter.Activities.ChooseContactActivity;
import ghasemi.abbas.abzaar.db.mp3cutter.Models.ContactsModel;
import ghasemi.abbas.abzaar.db.mp3cutter.Ringdroid.Utils;

import java.util.ArrayList;

/**
 * Created by REYANSH on 4/13/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ItemHolder> {

    private ChooseContactActivity mChooseContactActivity;
    private ArrayList<ContactsModel> mData;

    public ContactsAdapter(ChooseContactActivity ringdroidSelectActivity, ArrayList<ContactsModel> data) {
        mChooseContactActivity = ringdroidSelectActivity;
        mData = data;
    }


    @Override
    public ContactsAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactsAdapter.ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.arm_item_contacts, parent, false));
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ItemHolder holder, int position) {
        holder.mContactName.setText(mData.get(position).mName);
        holder.mContactID.setText(mData.get(position).mContactId);

        try {
            String letter = String.valueOf(mData.get(position).mName.charAt(0));
            holder.mOneLetter.setText(letter);
            holder.mOneLetter.setBackground(Utils.getMatColor(mChooseContactActivity.getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void updateData(ArrayList<ContactsModel> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mContactName;
        private TextView mOneLetter,mContactID;

        public ItemHolder(View itemView) {
            super(itemView);
            mContactName = (TextView) itemView.findViewById(R.id.text_view_name);
            mContactID = (TextView) itemView.findViewById(R.id.text_view_id);
            mOneLetter = (TextView) itemView.findViewById(R.id.one_letter);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mChooseContactActivity.onItemClicked(getAdapterPosition());
        }
    }
}
