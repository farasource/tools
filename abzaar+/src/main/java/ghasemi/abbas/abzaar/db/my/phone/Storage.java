package ghasemi.abbas.abzaar.db.my.phone;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;

import java.text.DecimalFormat;

/**
 * on 04/20/2018.
 */

public class Storage  extends Fragment {
    View v ;
    View view ;
    String[] detail_storage = new String[10];
    String[] detail_name = {"حافظه رم:", "حافظه پر شده رم:", "حافظه خالی رم:", "درصد استفاده شده از رم:","حافظه داخلی:","حافظه داخلی استفاده شده:","حافظه داخلی باقی مانده:","حافظه خارجی:","حافظه خارجی استفاده شده:","حافظه خارجی باقی مانده:"};
    RecyclerView list_detail ;
    boolean isNotExternal;
    Adapter_Detail adapter_detail ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_phone, null);

        get();

        adapter_detail = new Adapter_Detail(Main.activity);
        list_detail = v.findViewById(R.id.list_main);
        list_detail.setLayoutManager(new LinearLayoutManager(Main.activity,LinearLayoutManager.VERTICAL,false));
        list_detail.hasFixedSize();
        list_detail.setAdapter(adapter_detail);

        return v;
    }

    class Adapter_Detail extends RecyclerView.Adapter<Adapter_Detail.contentViewHolder> {
        Context context;
        public Adapter_Detail(Context c) {
            context = c;
        }

        @Override
        public Adapter_Detail.contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_info_phone, parent, false);

            return new contentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(Adapter_Detail.contentViewHolder holder, final int position) {

            holder.text_detail_name.setText(detail_name[position]);
            holder.text_detail_value.setText(detail_storage[position]);
        }

        @Override
        public int getItemCount() {
            return isNotExternal ? detail_name.length - 3 : detail_name.length;
        }

        public class contentViewHolder extends RecyclerView.ViewHolder {

            TextView text_detail_name,text_detail_value;

            public contentViewHolder(View itemView) {
                super(itemView);
                text_detail_name = (TextView) itemView.findViewById(R.id.text_detail_name);
                text_detail_value = (TextView) itemView.findViewById(R.id.text_detail_value);
                CardView row_main_card = (CardView) itemView.findViewById(R.id.row_main_card);
                row_main_card.setOnClickListener(null);
            }
        }

    }


    public void get(){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ((ActivityManager) Main.activity.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(mi);
        double availableMegs = (double) (mi.availMem / 1048576);
        double percentAvail = ((double) mi.availMem) / ((double) mi.totalMem);
        detail_storage[0] = String.valueOf(mi.totalMem / 1048576);
        detail_storage[1] = String.valueOf((mi.totalMem - mi.availMem) / 1048576);
        detail_storage[2] = String.valueOf(availableMegs);
        detail_storage[3] = new StringBuilder(String.valueOf(new DecimalFormat("#.##").format(100.0d - (100.0d * percentAvail)))).append(" درصد").toString();
        DeviceInfo info = new DeviceInfo(Main.activity);
        detail_storage[4] = Formatter.formatFileSize(Main.activity, info.getTotalInternalMemorySize());
        detail_storage[5] = info.getFreeInternalMemorySize();
        detail_storage[6] = Formatter.formatFileSize(Main.activity, info.getAvailableInternalMemorySize());

        detail_storage[7] = Formatter.formatFileSize(Main.activity, info.getTotalExternalMemorySize());
        detail_storage[8] = info.getFreeExternalMemorySize();
        detail_storage[9] = Formatter.formatFileSize(Main.activity, info.getAvailableExternalMemorySize());

        isNotExternal = detail_storage[5].equals(detail_storage[8]);
    }
}

